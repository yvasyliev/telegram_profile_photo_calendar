package telegram.client;

import it.tdlight.common.Init;
import it.tdlight.common.TelegramClient;
import it.tdlight.common.UpdatesHandler;
import it.tdlight.common.utils.CantLoadLibrary;
import it.tdlight.jni.TdApi;
import it.tdlight.tdlight.ClientManager;
import telegram.utils.ImageCreator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOError;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Client {
    private static TelegramClient telegramClient;
    private static final ImageCreator imageCreator = new ImageCreator();
    private static final Properties properties = new Properties();

    public static void main(String[] args) throws CantLoadLibrary, ExecutionException, InterruptedException, IOException {
        String mode = args[0];
        properties.load(new FileInputStream("config.properties"));
        initTelegramClient();
        setTelegramClientParams();

        switch (mode) {
            case "login":
                login();
                break;

            case "start":
                start();
                break;

            case "logout":
                logout();
                break;
        }

        closeTelegramClient();
    }

    private static void closeTelegramClient() throws ExecutionException, InterruptedException {
        sendSynchronously(new TdApi.Close());
    }

    private static void initTelegramClient() throws CantLoadLibrary {
        Init.start();
        telegramClient = ClientManager.create((UpdatesHandler) null, null, null);
    }

    private static void setTelegramClientParams() throws ExecutionException, InterruptedException {
        telegramClient.execute(new TdApi.SetLogVerbosityLevel(0));
        if (telegramClient.execute(new TdApi.SetLogStream(new TdApi.LogStreamFile("tdlib.log", 1 << 27, false))) instanceof TdApi.Error) {
            throw new IOError(new IOException("Write access to the current directory is required"));
        }

        TdApi.TdlibParameters tdlibParameters = new TdApi.TdlibParameters();
        tdlibParameters.apiHash = properties.getProperty("api_hash");
        tdlibParameters.apiId = Integer.parseInt(properties.getProperty("api_id"));
        tdlibParameters.systemLanguageCode = "en";
        tdlibParameters.deviceModel = "Desktop";
        tdlibParameters.applicationVersion = "0.5";

        sendSynchronously(new TdApi.SetTdlibParameters(tdlibParameters));
        sendSynchronously(new TdApi.CheckDatabaseEncryptionKey());
    }

    private static TdApi.Object sendSynchronously(TdApi.Function request) throws InterruptedException, ExecutionException {
        CompletableFuture<TdApi.Object> response = new CompletableFuture<>();

        telegramClient.send(request, response::complete, response::completeExceptionally);

        try {
            return response.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void login() throws ExecutionException, InterruptedException {
        sendAuthenticationCode();
        String code = promptCode();
        login(code);
    }

    private static void sendAuthenticationCode() throws ExecutionException, InterruptedException {
        TdApi.SetAuthenticationPhoneNumber setAuthenticationPhoneNumber = new TdApi.SetAuthenticationPhoneNumber();
        setAuthenticationPhoneNumber.phoneNumber = properties.getProperty("phone_number");
        sendSynchronously(setAuthenticationPhoneNumber);
    }

    private static String promptCode() {
        System.out.print("Please enter code: ");
        return new Scanner(System.in).next();
    }

    private static void login(String code) throws ExecutionException, InterruptedException {
        sendSynchronously(new TdApi.CheckAuthenticationCode(code));
    }

    private static void logout() throws ExecutionException, InterruptedException {
        sendSynchronously(new TdApi.LogOut());
    }

    private static void start() throws IOException, ExecutionException, InterruptedException {
        deletePreviousProfilePhoto();
        File image = createImage();
        setProfilePhoto(image.getPath());
    }

    private static File createImage() throws IOException {
        return imageCreator.createImage();
    }

    private static void setProfilePhoto(String filePath) throws ExecutionException, InterruptedException {
        sendSynchronously(new TdApi.SetProfilePhoto(new TdApi.InputChatPhotoStatic(new TdApi.InputFileLocal(filePath))));
    }

    private static void deletePreviousProfilePhoto() throws ExecutionException, InterruptedException {
        TdApi.ChatPhoto[] profilePhotos = getProfilePhotos();
        if (profilePhotos != null && profilePhotos.length > 0) {
            sendSynchronously(new TdApi.DeleteProfilePhoto(profilePhotos[0].id));
        }
    }

    private static TdApi.ChatPhoto[] getProfilePhotos() throws ExecutionException, InterruptedException {
        TdApi.User user = (TdApi.User) sendSynchronously(new TdApi.GetMe());
        TdApi.GetUserProfilePhotos getUserProfilePhotos = new TdApi.GetUserProfilePhotos();
        getUserProfilePhotos.limit = 1;
        getUserProfilePhotos.userId = user.id;
        TdApi.ChatPhotos chatPhotos = (TdApi.ChatPhotos) sendSynchronously(getUserProfilePhotos);
        return chatPhotos.photos;
    }
}

[![MIT License](http://img.shields.io/badge/license-MIT-blue.svg?style=flat)](https://github.com/yvasyliev/java-vk-bots-long-poll-api/blob/master/LICENSE)
# Telegram Profile Photo Updater
A Telegram client created to automatically update your profile photo.

## Dependencies
This project uses [TDLight Java](https://github.com/tdlight-team/tdlight-java) as implementation of Telegram Client API.

## Requirements
1. JDK 8 (or higher)
2. Maven

## Quickstart
1. Clone this project.<br/>
`git clone https://github.com/yvasyliev/telegram_profile_photo_updater.git`
2. Build the application.<br/>
`mvn clean package`
3. Find in `target` folder a `TelegramProfilePhotoUpdater-1.0-SNAPSHOT-jar-with-dependencies.jar` file.<br/>
4. Create `config.properties` file in the same folder as `TelegramProfilePhotoUpdater-1.0-SNAPSHOT-jar-with-dependencies.jar`.<br/>
5. Put the following content in the `config.properties` file:<br/>
```properties
#To get api_hash and api_id see https://core.telegram.org/api/obtaining_api_id
api_hash=your_api_hash
api_id=XXXXXXX
phone_number=+XXXXXXXXXXXX #Your phone number
```
6. Login to your custom client. You will be prompted to enter authentication code.<br/>
`java -jar TelegramProfilePhotoUpdater-1.0-SNAPSHOT-jar-with-dependencies.jar login`
7. Update your profile photo.<br/>
`java -jar TelegramProfilePhotoUpdater-1.0-SNAPSHOT-jar-with-dependencies.jar start`
8. (Optional) Logout from the client.<br/>
`java -jar TelegramProfilePhotoUpdater-1.0-SNAPSHOT-jar-with-dependencies.jar logout`

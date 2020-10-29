package telegram.utils;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class ImageCreator {
    private static final List<List<Color>> COLORS = Arrays.asList(
            Arrays.asList(Color.BLACK, Color.WHITE),
            Arrays.asList(Color.ORANGE, Color.BLACK),
            Arrays.asList(Color.WHITE, Color.MAGENTA),
            Arrays.asList(Color.PINK, Color.DARK_GRAY)
    );
    private static final Font FONT_LARGE = new Font("Arial", Font.PLAIN, 130);
    private static final Font FONT_SMALL = new Font("Arial", Font.PLAIN, 30);
    private static final File IM = new File("im.png");
    private static final LocalDate startDate = LocalDate.of(2020, 10, 27);
    private static final int LINE_1 = FONT_LARGE.getSize() + 80;
    private static final int LINE_2 = LINE_1 + FONT_LARGE.getSize() + 20;
    private static final int LINE_3 = LINE_2 + FONT_SMALL.getSize() + 60;
    private static final int LINE_4 = LINE_3 + FONT_SMALL.getSize() + 10;
    private static final int IM_SIZE = 500;

    public File createImage() throws IOException {
        BufferedImage bufferedImage = createBufferedImage();
        ImageIO.write(bufferedImage, "png", IM);
        return IM;
    }

    private BufferedImage createBufferedImage() {
        BufferedImage bufferedImage = new BufferedImage(IM_SIZE, IM_SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
        List<Color> nextColours = getNextColours();
        graphics.setColor(nextColours.get(0));
        graphics.fillRect(0, 0, IM_SIZE, IM_SIZE);
        graphics.setColor(nextColours.get(1));
        graphics.setFont(FONT_LARGE);
        printCentered(graphics, "DAY", LINE_1);
        printCentered(graphics, getDays(), LINE_2);
        graphics.setFont(FONT_SMALL);
        printCentered(graphics, "Waiting for Cyberpunk 2077.", LINE_4);
        graphics.dispose();
        return bufferedImage;
    }

    private void printCentered(Graphics2D graphics, String text, int y) {
        TextLayout textLayout = new TextLayout(text, graphics.getFont(), graphics.getFontRenderContext());
        double textWidth = textLayout.getBounds().getWidth();

        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        graphics.drawString(text, IM_SIZE / 2 - (int) textWidth / 2, y);
    }

    /**
     * Every single day returns the new color pair from {@link ImageCreator#COLORS} list.
     *
     * @return pair of colors.
     */
    private List<Color> getNextColours() {
        return COLORS.get(((int) (System.currentTimeMillis() / (1000 * 3600 * 24))) % COLORS.size());
    }

    private String getDays() {
        return String.valueOf(Duration.between(startDate.atStartOfDay(), LocalDate.now().atStartOfDay()).toDays());
    }
}

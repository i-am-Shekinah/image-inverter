package tasks;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageInverterActor extends AbstractActor {

    public static Props props() {
        return Props.create(ImageInverterActor.class);
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(InvertImageMessage.class, this::handleInvert)
                .build();
    }

    private void handleInvert(InvertImageMessage msg) {
        try {
            BufferedImage image = ImageIO.read(msg.inputFile);
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int rgb = image.getRGB(x, y);
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;

                    // Red → Green, Green → Blue, Blue → Red
                    int newRGB = (green << 16) | (blue << 8) | red;
                    image.setRGB(x, y, newRGB);
                }
            }

            ImageIO.write(image, "png", msg.outputFile);
            sender().tell(msg.outputFile.getAbsolutePath(), self());
        } catch (IOException e) {
            sender().tell(e, self());
        }
    }

    public static class InvertImageMessage {
        public final File inputFile;
        public final File outputFile;

        public InvertImageMessage(File inputFile, File outputFile) {
            this.inputFile = inputFile;
            this.outputFile = outputFile;
        }
    }
}

package sk.upjs.ics.utilities;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.UUID;

public class QRCodeUitl {
    public static void generateQRCode(String text, String filePath, int width, int height) {
        try {
            // Create a QR Code Writer
            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            // Generate the QR code as a BitMatrix
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

            // Specify the file path to save the QR code
            Path path = FileSystems.getDefault().getPath(filePath);

            // Write the BitMatrix to a file as a PNG image
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

        } catch (WriterException | IOException e) {
            System.err.println("Error generating QR Code: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String uniqueID = UUID.randomUUID().toString();; // Text to encode in QR Code
        String filePath = "src/main/resources/sk/upjs/ics/qr_codes/qrcode.png";  // Output file path
        int width = 300;                 // QR Code width
        int height = 300;                // QR Code height

        generateQRCode(uniqueID, filePath, width, height);
    }

    public static String readQRCode(String filePath) {
        try {
            // Read the image file into a BufferedImage
            File qrCodeFile = new File(filePath);
            BufferedImage bufferedImage = ImageIO.read(qrCodeFile);

            // Create a BinaryBitmap from the BufferedImage
            LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            // Decode the QR code using the ZXing library
            Result result = new MultiFormatReader().decode(bitmap);

            // Return the decoded text
            return result.getText();
        } catch (NotFoundException e) {
            System.err.println("QR Code not found in the image: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error reading the image file: " + e.getMessage());
        }
        return null;
    }
}
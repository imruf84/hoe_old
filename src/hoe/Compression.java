package hoe;

import static hoe.servlets.ContentServlet.TILE_IMAGE_FORMAT;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class Compression {

    public static String compress(String s) {
        Deflater def = new Deflater(9);
        byte[] sbytes = s.getBytes(StandardCharsets.UTF_8);
        def.setInput(sbytes);
        def.finish();
        byte[] buffer = new byte[sbytes.length];
        int n = def.deflate(buffer);
        return new String(buffer, 0, n, StandardCharsets.ISO_8859_1)
                + "*" + sbytes.length;
    }

    public static String decompress(String s) {
        int pos = s.lastIndexOf('*');
        int len = Integer.parseInt(s.substring(pos + 1));
        s = s.substring(0, pos);

        Inflater inf = new Inflater();
        byte[] buffer = s.getBytes(StandardCharsets.ISO_8859_1);
        byte[] decomp = new byte[len];
        inf.setInput(buffer);
        try {
            inf.inflate(decomp, 0, len);
            inf.end();
        } catch (DataFormatException e) {
            throw new IllegalArgumentException(e);
        }
        return new String(decomp, StandardCharsets.UTF_8);
    }

    public static byte[] compress(byte[] data) throws IOException {
        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION, true);
        deflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        deflater.finish();
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        byte[] output = outputStream.toByteArray();
        return output;
    }

    public static byte[] decompress(byte[] data) throws IOException, DataFormatException {
        Inflater inflater = new Inflater(true);
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!inflater.finished()) {
            int count = inflater.inflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        byte[] output = outputStream.toByteArray();
        return output;
    }

    public static byte[] imageToByteArray(BufferedImage image) throws IOException {

        byte[] iba;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, TILE_IMAGE_FORMAT, baos);
            baos.flush();
            iba = baos.toByteArray();
        }

        return iba;
    }

    public static byte[] imageToByteArray(BufferedImage image, float quality) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageWriter writer = ImageIO.getImageWritersByFormatName(TILE_IMAGE_FORMAT).next();
        ImageOutputStream ios = ImageIO.createImageOutputStream(out);
        writer.setOutput(ios);

        ImageWriteParam param = writer.getDefaultWriteParam();
        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);
        }

        writer.write(null, new IIOImage(image, null, null), param);

        out.close();
        ios.close();
        writer.dispose();

        return out.toByteArray();
    }

    public static BufferedImage byteArrayToImage(byte[] byteArray) throws IOException {

        BufferedImage image;
        try (InputStream in = new ByteArrayInputStream(byteArray)) {
            image = ImageIO.read(in);
        }

        return image;
    }
}

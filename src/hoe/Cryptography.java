package hoe;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.BadPaddingException;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Cryptography {

    private static SecretKeySpec secretKey;
    private static byte[] key;

    static class C implements Serializable {

        long i;
        String s;

        public C(int i, String s) {
            this.i = i;
            this.s = s;
        }

        @Override
        public String toString() {
            return "C{" + "i=" + i + ", s=" + s + '}';
        }

    }

    public static void setKey(String myKey) {
        MessageDigest sha;
        try {
            key = myKey.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e) {
            Log.error(e);
        }
    }

    public static String encryptObject(final Serializable object) {
        return encryptString(convertToString(object));
    }

    public static <T extends Serializable> T decryptObject(final String objectAsString) {
        return convertFromString(decryptString(objectAsString));
    }

    public static String encryptString(String strToEncrypt) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().withoutPadding().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8))).replaceAll("\\+", "-").replaceAll("/", "_");
        } catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            Log.error("Error while encrypting: ", e);
        }
        return null;
    }

    public static String decryptString(String strToDecrypt) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt.replaceAll("-", "\\+").replaceAll("_", "/"))));
        } catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            Log.error("Error while decrypting: ", e);
        }
        return null;
    }

    static String convertToString(final Serializable object) {
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(object);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (final IOException e) {
            Log.error(e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    static <T extends Serializable> T convertFromString(final String objectAsString) {
        final byte[] data = Base64.getDecoder().decode(objectAsString);
        try (final ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            return (T) ois.readObject();
        } catch (final IOException | ClassNotFoundException e) {
            Log.error(e);
            return null;
        }
    }

    public static void sample(String[] args) throws IOException {

        final String k = "szupertitkosjelszó";
        setKey(k);

        C c1 = new C(1, "oneklfjdlkégj dflkéjddfklé");
        System.out.println(c1.toString());
        String encryptedString = Cryptography.encryptObject(c1);
        System.out.println(encryptedString);
        System.out.println(encryptedString.length());
        C c2 = Cryptography.decryptObject(encryptedString);
        System.out.println(c2.toString());
    }
}

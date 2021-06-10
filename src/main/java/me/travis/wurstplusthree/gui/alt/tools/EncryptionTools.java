package me.travis.wurstplusthree.gui.alt.tools;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

/**
 * @author Madmegsox1
 * @since 23/05/2021
 */

public class EncryptionTools {
    /*
    public static final String DEFAULT_ENCODING = "UTF-8";
    private static Base64.Encoder encoder = Base64.getEncoder();
    private static Base64.Decoder decoder = Base64.getDecoder();
    private static MessageDigest sha512 = getSha512Hasher();
    private static KeyGenerator keyGen = getAESGenerator();
    private static String secretSalt = "${secretSalt}";

    public static String decodeOld(String text) {
        try {
            return new String(decoder.decode(text), DEFAULT_ENCODING);
        } catch (IOException e) {
            return null;
        }
    }

    public static String encode(String text) {
        try {
            byte[] data = text.getBytes(DEFAULT_ENCODING);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());

            return new String(encoder.encode(cipher.doFinal(data)));
        } catch (BadPaddingException e) {
            throw new RuntimeException("The password does not match", e);
        } catch (IllegalBlockSizeException | InvalidKeyException | IOException | NoSuchAlgorithmException
                | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decode(String text) {
        try {
            byte[] data = decoder.decode(text);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey());

            return new String(cipher.doFinal(data), DEFAULT_ENCODING);
        } catch (BadPaddingException e) {
            throw new RuntimeException("The password does not match", e);
        } catch (IllegalBlockSizeException | InvalidKeyException | IOException | NoSuchAlgorithmException
                | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String generatePassword() {
        keyGen.init(256);
        return new String(encoder.encode(keyGen.generateKey().getEncoded()));
    }

    private static MessageDigest getSha512Hasher() {
        try {
            return MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static KeyGenerator getAESGenerator() {
        try {
            return KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    private static SecretKeySpec getSecretKey() {
        try {
            String password = secretSalt + Standards.getPassword() + secretSalt;
            byte[] key = Arrays.copyOf(sha512.digest(password.getBytes(DEFAULT_ENCODING)), 16);

            return new SecretKeySpec(key, "AES");
       } catch (UnsupportedEncodingException e) {
           throw new RuntimeException(e);
        }
    }
     */
}


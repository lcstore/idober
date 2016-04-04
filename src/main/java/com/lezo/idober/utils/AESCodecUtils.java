package com.lezo.idober.utils;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class AESCodecUtils {
    private static byte[] iv = "0000000000000000".getBytes();
    private static final String DEFAULT_DIGEST_ALGORITHM = "SHA-256";
    private static final String DEFAULT_SECRETKEY_ALGORITHM = "AES";
    private static final String DEFAULT_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String DEFAULT_KEY = "idober20160403";
    private static SecretKeySpec secretKey = null;

    private static SecretKeySpec getSecretKey() throws Exception {
        if (secretKey == null) {
            synchronized (AESCodecUtils.class) {
                if (secretKey == null) {
                    secretKey = newSecretKey(DEFAULT_KEY);
                }
            }
        }
        return secretKey;
    }

    private static SecretKeySpec newSecretKey(String key) throws Exception {
        byte[] keyb = key.getBytes("utf-8");
        MessageDigest md = MessageDigest.getInstance(DEFAULT_DIGEST_ALGORITHM);
        byte[] thedigest = md.digest(keyb);
        SecretKeySpec skey = new SecretKeySpec(thedigest, DEFAULT_SECRETKEY_ALGORITHM);
        return skey;
    }

    public static String decrypt(String encrypted) throws Exception {
        SecretKeySpec skc = getSecretKey();
        return decrypt(encrypted, skc);
    }

    public static String decrypt(String encrypted, String key) throws Exception {
        SecretKeySpec skc = newSecretKey(key);
        return decrypt(encrypted, skc);
    }

    public static String decrypt(String encrypted, SecretKeySpec skey) throws Exception {
        Cipher dcipher = Cipher.getInstance(DEFAULT_TRANSFORMATION);
        dcipher.init(Cipher.DECRYPT_MODE, skey, new IvParameterSpec(iv));

        byte[] clearbyte = dcipher.doFinal(DatatypeConverter
                .parseHexBinary(encrypted));
        return new String(clearbyte);
    }

    public static String encrypt(String content) throws Exception {
        SecretKeySpec skc = getSecretKey();
        return encrypt(content, skc);
    }

    public static String encrypt(String content, String key) throws Exception {
        SecretKeySpec skc = newSecretKey(key);
        return encrypt(content, skc);
    }

    public static String encrypt(String content, SecretKeySpec skc) throws Exception {
        byte[] input = content.getBytes("utf-8");
        Cipher cipher = Cipher.getInstance(DEFAULT_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, skc, new IvParameterSpec(iv));

        byte[] cipherText = new byte[cipher.getOutputSize(input.length)];
        int ctLength = cipher.update(input, 0, input.length, cipherText, 0);
        ctLength += cipher.doFinal(cipherText, ctLength);
        return DatatypeConverter.printHexBinary(cipherText).toLowerCase();
    }

    public static void main(String[] args) throws Exception {
        String data = "1234567890";
        String key = "idober0403";
        String cipher = AESCodecUtils.encrypt(data, key);
        String decipher = AESCodecUtils.decrypt(cipher, key);
        System.out.println(cipher);
        System.out.println(decipher);
        cipher = AESCodecUtils.encrypt(data);
        decipher = AESCodecUtils.decrypt(cipher);
        System.out.println(cipher);
        System.out.println(decipher);
    }
}

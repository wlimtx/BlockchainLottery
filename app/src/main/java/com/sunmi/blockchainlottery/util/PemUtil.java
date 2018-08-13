package com.sunmi.blockchainlottery.util;

import org.apache.commons.compress.utils.IOUtils;
import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

public class PemUtil {



    public static byte[] rsaWithSha256(byte[] plainText, RSAPrivateKey key) throws Exception {
        return encrypt(sha256(plainText), key);
    }

    public static byte[] base64ToDecode(String base64) {
        return org.bouncycastle.util.encoders.Base64.decode(base64);
    }

    public static String base64Encode(byte[] bytes) {
        return org.bouncycastle.util.encoders.Base64.toBase64String(bytes);
    }

    public static String loadPemBase64(File file) throws Exception {
        return new String(IOUtils.toByteArray(new FileInputStream(file)))
                .replaceFirst("^([^\r\n]++)", "")
                .replaceAll("[\r\n]", "")
                .replaceFirst("-[\\s\\S]++$", "");
    }

    public static RSAPublicKey loadPublicKey(byte[] key) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key);
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    public static RSAPrivateKey loadPrivateKey(byte[] encodeKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodeKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    public static byte[] encrypt(byte[] content, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(content);
    }

    public static byte[] decrypt(byte[] content, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(content);
    }

    public static String SHA256(byte[] raw) throws NoSuchAlgorithmException {
        return Hex.toHexString(MessageDigest.getInstance("SHA-256").digest(raw));
    }

    public static byte[] sha256(byte[] bytes) {

        System.out.println(Base64.toBase64String(bytes));
        try {
            return MessageDigest.getInstance("SHA-256").digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new byte[]{};
    }

    public static byte[] sign(byte[] plainText, RSAPrivateKey key) {
        try {
            Signature signature = Signature.getInstance("SHA256WithRSA");
            signature.initSign(key);
            signature.update(plainText);
            return signature.sign();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean doCheck(byte[] content, byte[] sign, RSAPublicKey publicKey)
    {
        try
        {
            Signature signature = Signature
                    .getInstance("SHA256WithRSA");

            signature.initVerify(publicKey);
            signature.update(content);
            return signature.verify(sign);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }

    public static int randomNumber() {
        return new SecureRandom().nextInt(10_000_000);
    }
    public static byte[] randomBytes() {
        SecureRandom sec = new SecureRandom();
        byte[] random = new byte[sec.nextInt(32) + 1];
        sec.nextBytes(random);
        return random;
    }

    public static byte[] nTimesOfSha256(int n, byte[] bytes) {
        for (int i = n; i > 0; i--)
            bytes = PemUtil.sha256(bytes);
        return bytes;
    }

    public static String fixTo(String hex){
        return fixTo(hex, 64);
    }

    public static String fixTo(String hex, int length) {
        StringBuilder hexBuilder = new StringBuilder(hex);
        for (int i = length - hexBuilder.length(); i > 0; i--) hexBuilder.insert(0, '0');
        return hexBuilder.toString();
    }

    public static byte[] sha3(byte[] input) {
        return new Keccak.Digest256().digest(input);
    }

    public static String pkToEthAddr(byte[] pk) {
        return "0x" + Hex.toHexString(sha3(pk)).substring(24);
    }


}


package com.sunmi.blockchainlottery.util;

import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;


public class ECDSAUtil {


    public static PrivateKey base64ToPrivateKey(String base64Key) throws Exception {
        return rawToPrivateKey(Base64.decode(base64Key));
    }

    public static PrivateKey hexToPrivateKey(String hex) throws Exception {
        return rawToPrivateKey(Hex.decode(hex));
    }

    public static PrivateKey rawToPrivateKey(byte[] raw) throws Exception {
        return KeyFactory.getInstance("EC").generatePrivate(new PKCS8EncodedKeySpec(raw));
    }

    public static PublicKey base64ToPublicKey(String base64Key) throws Exception {
        return rawToPublicKey(Base64.decode(base64Key));
    }

    public static PublicKey hexToPublicKey(String hex) throws Exception {
        return rawToPublicKey(Hex.decode(hex));
    }

    public static PublicKey rawToPublicKey(byte[] raw) throws Exception {
        return KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(raw));
    }

    public static KeyPair generateKey() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        keyGen.initialize(256, random); //256 bit key size
        return keyGen.generateKeyPair();
    }

    public static byte[] signMsg(String msg, PrivateKey priv) throws Exception {
        Signature ecdsa = Signature.getInstance("SHA1withECDSA");
        ecdsa.initSign(priv);
        byte[] strByte = msg.getBytes("UTF-8");
        ecdsa.update(strByte);
        return ecdsa.sign();
    }

    public static boolean verifySignature(PublicKey pubKey, String msg, byte[] signature) throws Exception {
        byte[] message = msg.getBytes("UTF-8");
        Signature ecdsa = Signature.getInstance("SHA1withECDSA");
        ecdsa.initVerify(pubKey);
        ecdsa.update(message);
        return ecdsa.verify(signature);
    }

    public static BigInteger extractR(byte[] signature) throws Exception {
        int startR = (signature[1] & 0x80) != 0 ? 3 : 2;
        int lengthR = signature[startR + 1];
        return new BigInteger(Arrays.copyOfRange(signature, startR + 2, startR + 2 + lengthR));
    }

    public static BigInteger extractS(byte[] signature) throws Exception {
        int startR = (signature[1] & 0x80) != 0 ? 3 : 2;
        int lengthR = signature[startR + 1];
        int startS = startR + 2 + lengthR;
        int lengthS = signature[startS + 1];
        return new BigInteger(Arrays.copyOfRange(signature, startS + 2, startS + 2 + lengthS));
    }
}

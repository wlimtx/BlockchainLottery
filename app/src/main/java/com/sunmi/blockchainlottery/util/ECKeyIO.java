package com.sunmi.blockchainlottery.util;

import android.content.Context;

import com.sunmi.blockchainlottery.MainActivity;
import com.sunmi.blockchainlottery.bean.Account;

import org.bouncycastle.util.encoders.Hex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECPoint;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class ECKeyIO {

    public static void main(String[] args) throws Exception {
        String orgPath = "orgMsp/";
        String usrPath = "usrMsp/";
        File orgParent = new File(Constant.Path, orgPath);
        File usrParent = new File(Constant.Path, usrPath);
        File org1 = new File(orgParent, "org1/");
        File org2 = new File(orgParent, "org2/");


//        File[] usrs = new File[4];
//        for (int i = usrs.length - 1; i >= 0; i--) usrs[i] = new File(usrParent, String.valueOf(i) + "/");
//        save(org1, generateKey());
//        save(org2, generateKey());
//        for (File usr : usrs) save(usr, generateKey());

//        File parent = new File("./test_key");
//        System.out.println(parent.getAbsolutePath());
//        save(parent, generateKey());
//        KeyPair kp = read(parent, parent.listFiles()[0].getName());
//
//        token(kp);
    }

    private static void test(KeyPair kp) throws Exception {

        PrivateKey sk = kp.getPrivate();
        PublicKey pk = kp.getPublic();
        System.out.println("sk: " + PemUtil.fixTo(((ECPrivateKey) sk).getS().toString(16)));
        ECPoint w = ((ECPublicKey) pk).getW();
        String pK = PemUtil.fixTo(w.getAffineX().toString(16)) + PemUtil.fixTo(w.getAffineY().toString(16));
        System.out.println("pk: " + pK);
        byte[] signature = ECDSAUtil.signMsg("abc", sk);
        System.out.println("R: " + PemUtil.fixTo(ECDSAUtil.extractR(signature).toString(16)));
        System.out.println("S: " + PemUtil.fixTo(ECDSAUtil.extractS(signature).toString(16)));
        System.out.println("sig: " + PemUtil.fixTo(Hex.toHexString(signature)));
        boolean res = ECDSAUtil.verifySignature(pk, "abc", signature);
        System.out.println(res);
    }


    public static void save(KeyPair keyPair) throws FileNotFoundException {
        save(Constant.Path, keyPair);
    }

    public static void save(File parent, KeyPair keyPair) throws FileNotFoundException {
        ECPublicKey pk = (ECPublicKey) keyPair.getPublic();
        ECPoint w = pk.getW();
        String pK = PemUtil.fixTo(w.getAffineX().toString(16)) + PemUtil.fixTo(w.getAffineY().toString(16));
        String sK = PemUtil.fixTo(((ECPrivateKey) keyPair.getPrivate()).getS().toString(16));
        String address = Hex.toHexString(PemUtil.sha256(Hex.decode(pK)));
        if (!parent.exists()) {
            parent.mkdirs();
        }
        PrintStream out = new PrintStream(new File(parent, address));
        out.println(sK);
        out.println(pK);
        out.close();
    }

    public static KeyPair read(String address) throws Exception {
        return read(Constant.Path, address);
    }

    public static KeyPair read(File parent, String address) throws Exception {
        File file = new File(parent, address);
        Scanner scanner = new Scanner(file);
        String sK = scanner.nextLine();
        String pK = scanner.nextLine();
        scanner.close();
        return new KeyPair(ECDSAUtil.hexToPublicKey(Constant.BasePkPrefix + pK), ECDSAUtil.hexToPrivateKey(Constant.BaseSkPrefix + sK));
    }


    public static List<File> getKeyList(File parent) {
        File[] files = parent.listFiles();
        if (files == null) return null;
        List<File> list = new ArrayList<>();
        for (File file : files) if (file.getName().matches("[a-fA-F0-9]++")) list.add(file);
        return list;
    }

    public static String[] next(List<File> I) throws Exception {
        if (I == null || I.isEmpty()) return null;
        File next = I.remove(I.size() - 1);
        Scanner scanner = new Scanner(next);
        String sK = scanner.nextLine();
        String pK = scanner.nextLine();
        scanner.close();
        return new String[]{next.getName(), pK, sK};
    }

    public static PrivateKey wrap(String hexSk) throws Exception {
        return ECDSAUtil.hexToPrivateKey(Constant.BaseSkPrefix + hexSk);
    }

    public static String[] split(KeyPair kp) {
        ECPublicKey pk = (ECPublicKey) kp.getPublic();
        ECPoint w = pk.getW();
        String pK = PemUtil.fixTo(w.getAffineX().toString(16)) + PemUtil.fixTo(w.getAffineY().toString(16));
        String sK = PemUtil.fixTo(((ECPrivateKey) kp.getPrivate()).getS().toString(16));
        String address = Hex.toHexString(PemUtil.sha256(Hex.decode(pK)));
        return new String[]{address, pK, sK};
    }


    public static void save(Account account, Context context) throws FileNotFoundException {
        File parent = context.getFilesDir().getAbsoluteFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        File file = new File(parent, String.valueOf(System.currentTimeMillis()) + "#" + account.getName());
        System.out.println("save in " + file.getAbsolutePath());
        PrintStream out = new PrintStream(file);
        out.println(account.getName());
        out.println(account.getAddress());
        out.println(account.getPk());
        out.println(account.getSk());
        out.close();
    }

    public static List<Account> readAll(Context context) throws FileNotFoundException {
        File parent = context.getFilesDir().getAbsoluteFile();
        List<Account> accounts = new ArrayList<>();
        for (File file : parent.listFiles()) accounts.add(read(file));
        return accounts;
    }

    public static Account read(File file) throws FileNotFoundException {
        try (Scanner scanner = new Scanner(file)) {
            return new Account(scanner.nextLine(),
                    scanner.nextLine(),
                    scanner.nextLine(),
                    scanner.nextLine());
        }
    }

    public static Account readFirst(Context context) throws Exception {
        File[] files = context.getFilesDir().getAbsoluteFile().listFiles();
        if (files == null || files.length == 0) {
            String[] keys = split(ECDSAUtil.generateKey());
            Account account = new Account(RandomString.nextName().toString(), keys[0], keys[1], keys[2]);
            save(account, context);
            return account;
        } else {
            return read(files[0]);
        }
    }

    public static Account readByName(Context context, String name) throws Exception {
        File[] files = context.getFilesDir().getAbsoluteFile().listFiles();
        if (files == null || files.length == 0) {
            String[] keys = split(ECDSAUtil.generateKey());
            Account account = new Account(RandomString.nextName().toString(), keys[0], keys[1], keys[2]);
            save(account, context);
            return account;
        } else {
            for (File file : files) if (file.getName().endsWith("#" + name)) return read(file);
            return read(files[0]);
        }
    }
}

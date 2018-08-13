package com.sunmi.blockchainlottery.util;



import java.io.File;

public class Constant {

    public static final String BaseSkPrefix = "3041020100301306072a8648ce3d020106082a8648ce3d030107042730250201010420";
    public static final String BasePkPrefix = "3059301306072a8648ce3d020106082a8648ce3d03010703420004";
    public static final File Path = new File("/Users/liumingxing/IdeaProjects/FabricFinal/fabric_key");
    public static final String KeyRoot = "fabric_key";
//    String keyDir = getApplicationContext().getFilesDir().getAbsolutePath() + "/" + Constant.KeyRoot;


    public static final String[][] genesisKps;

    static {
        File parent = new File(Path, "./orgMsp/");
        if (!parent.exists()) {
            System.out.println("require genesis key");
            System.exit(1);
        }
        File[] dirs = parent.listFiles();
        if (dirs.length < 2) {
            System.out.println("require genesis key");
            System.exit(2);
        }
        genesisKps = new String[dirs.length][];
        int i = 0;
        for (File file : dirs) {
            System.out.println(file.getAbsolutePath() + file.isDirectory());

            if (file.isDirectory()) {
                try {

                    File f = file.listFiles()[0];
                    if (f.getName().matches("[0-9a-f]++")) {
                        genesisKps[i++] = ECKeyIO.split(ECKeyIO.read(f.getParentFile(), f.getName()));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(4);
                }
            }

        }
        if (genesisKps[0] == null || genesisKps[1] == null) {
            System.out.println("require genesis key");
            System.exit(3);
        }
    }

}

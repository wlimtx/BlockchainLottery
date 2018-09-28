package com.sunmi.blockchainlottery.util;

import java.util.Random;

public class RandomString extends Random {
    private static final RandomString randomString = new RandomString();

    private RandomString() {
    }

    public static char nextAlpha(boolean upper) {
        return (char) (randomString.nextInt(26) + (upper ? 'A' : 'a'));
    }

    public static StringBuilder nextWord(StringBuilder sb, int length) {
        sb.append(nextAlpha(true));
        for (length--; length > 0; length--) sb.append(nextAlpha(false));
        return sb;
    }

    public static StringBuilder nextName(int n) {
        StringBuilder sb = nextWord(new StringBuilder(), randomString.nextInt(5) + 2);
        for (n--; n > 0; n--) {
            sb.append(' ');
            nextWord(sb, randomString.nextInt(3) + 2);
        }
        return sb;
    }
    public static StringBuilder nextName() {
//        return nextName(randomString.nextInt(2) + 1);
        return nextName(randomString.nextInt(3) + 1);
    }

}

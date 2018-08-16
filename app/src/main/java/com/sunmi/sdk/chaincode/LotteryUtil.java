package com.sunmi.sdk.chaincode;




import com.sunmi.blockchainlottery.util.ECDSAUtil;
import com.sunmi.blockchainlottery.util.ECKeyIO;

import org.bouncycastle.util.encoders.Hex;

import java.io.File;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LotteryUtil {

    public static String[] transfer(String asset, String hexReceiverAddress, String hexPk, String hexSk) throws Exception {
        String message = asset + hexReceiverAddress;
        String hexSig = Hex.toHexString(ECDSAUtil.signMsg(message, ECKeyIO.wrap(hexSk)));
        return new String[]{
                "transfer", asset, hexReceiverAddress, hexSig, hexPk
        };

    }

    public static String[] bet(String wager, String hexNumber, String hexPk, String hexSk) throws Exception {
        String message = wager + hexNumber + hexPk;
        String hexSig = Hex.toHexString(ECDSAUtil.signMsg(message, ECKeyIO.wrap(hexSk)));
        return new String[]{
                "bet", wager, hexNumber, hexSig, hexPk
        };
    }

    public static BigDecimal sumBet(String resultAll) {
        BigDecimal sum = BigDecimal.ZERO;
        Matcher matcher = Pattern.compile("(?:bet_asset:\\s*+)([^,\\s]++)").matcher(resultAll);
        while (matcher.find()) sum = sum.add(new BigDecimal(matcher.group(1)));
        return sum;
    }
}

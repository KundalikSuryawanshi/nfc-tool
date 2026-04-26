package com.kor.tool;

import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MyHostApduService extends HostApduService {

    private static final byte[] SELECT_APDU =
            hexToBytes("00A4040007F001020304050600");

    private static final byte[] GET_DATA_APDU =
            hexToBytes("00CA000000");

    private static final byte[] SUCCESS =
            hexToBytes("9000");

    private static final byte[] NOT_FOUND =
            hexToBytes("6A82");

    private static final byte[] UNKNOWN_INS =
            hexToBytes("6D00");

    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {

        if (commandApdu == null) {
            return UNKNOWN_INS;
        }

        // SELECT AID
        if (Arrays.equals(commandApdu, SELECT_APDU)) {
            return appendStatus(
                    "HCE_READY".getBytes(StandardCharsets.UTF_8),
                    SUCCESS
            );
        }

        // GET SAVED CARD DATA
        if (Arrays.equals(commandApdu, GET_DATA_APDU)) {
            String cardData = getSharedPreferences(HCEActivity.PREF_NAME, MODE_PRIVATE)
                    .getString(HCEActivity.KEY_CARD_DATA, "EMPTY");

            return appendStatus(
                    cardData.getBytes(StandardCharsets.UTF_8),
                    SUCCESS
            );
        }

        return NOT_FOUND;
    }

    @Override
    public void onDeactivated(int reason) {
        // Called when NFC link is lost or another AID is selected
    }

    private static byte[] appendStatus(byte[] data, byte[] status) {
        byte[] out = new byte[data.length + status.length];

        System.arraycopy(data, 0, out, 0, data.length);
        System.arraycopy(status, 0, out, data.length, status.length);

        return out;
    }

    private static byte[] hexToBytes(String hex) {
        hex = hex.replace(" ", "")
                .replace("\n", "")
                .replace("0x", "")
                .replace("0X", "");

        byte[] data = new byte[hex.length() / 2];

        for (int i = 0; i < data.length; i++) {
            int index = i * 2;
            data[i] = (byte) Integer.parseInt(hex.substring(index, index + 2), 16);
        }

        return data;
    }
}
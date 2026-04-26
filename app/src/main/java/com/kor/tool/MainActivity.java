package com.kor.tool;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.nio.charset.Charset;
import java.util.*;

public class MainActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;

    private TextView statusText, uidText, techText, resultText, sectionTitleText;
    private EditText inputData;

    private Tag currentTag;

    enum Mode {
        READ, WRITE, HEX, MIFARE, NDEF, APDU, FORMAT
    }

    private Mode currentMode = Mode.READ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        statusText = findViewById(R.id.statusText);
        uidText = findViewById(R.id.uidText);
        techText = findViewById(R.id.techText);
        resultText = findViewById(R.id.resultText);
        sectionTitleText = findViewById(R.id.sectionTitleText);
        inputData = findViewById(R.id.inputData);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        findViewById(R.id.readButton).setOnClickListener(v -> setMode(Mode.READ));
        findViewById(R.id.writeButton).setOnClickListener(v -> setMode(Mode.WRITE));
        findViewById(R.id.hexButton).setOnClickListener(v -> setMode(Mode.HEX));
        findViewById(R.id.mifareButton).setOnClickListener(v -> setMode(Mode.MIFARE));
        findViewById(R.id.apduButton).setOnClickListener(v -> setMode(Mode.APDU));
        findViewById(R.id.formatButton).setOnClickListener(v -> setMode(Mode.FORMAT));

        findViewById(R.id.hceReadButton).setOnClickListener(v -> {
            setMode(Mode.APDU);
            inputData.setText("00A4040007F001020304050600\n" + "00CA000000");
        });

        findViewById(R.id.hceActButton).setOnClickListener(v -> {
            startActivity(new Intent(this, HCEActivity.class));
        });

        handleIntent(getIntent());
    }

    private void setMode(Mode mode) {
        currentMode = mode;
        statusText.setText("Mode: " + mode.name());
    }

    @Override
    protected void onResume() {
        super.onResume();
        PendingIntent pi = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_MUTABLE);

        nfcAdapter.enableForegroundDispatch(this, pi, null, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag == null) return;

        currentTag = tag;

        uidText.setText(bytesToHex(tag.getId()));
        techText.setText(Arrays.toString(tag.getTechList()));

        switch (currentMode) {
            case READ:
                inspectTag(tag);
                break;
            case WRITE:
                writeNdef(tag);
                break;
            case HEX:
                showHex(tag);
                break;
            case MIFARE:
                readMifare(tag);
                break;
            case APDU:
                sendApdu(tag);
                break;
            case FORMAT:
                formatTag(tag);
                break;
        }
    }

    // ================= HEX =================
    private void showHex(Tag tag) {
        resultText.setText("UID HEX:\n" + bytesToHex(tag.getId()));
    }

    // ================= READ =================
    private void inspectTag(Tag tag) {
        StringBuilder out = new StringBuilder();
        out.append("UID: ").append(bytesToHex(tag.getId())).append("\n\n");

        Ndef ndef = Ndef.get(tag);
        if (ndef != null) {
            try {
                ndef.connect();
                NdefMessage msg = ndef.getCachedNdefMessage();

                if (msg != null) {
                    for (NdefRecord r : msg.getRecords()) {
                        out.append(parseNdef(r)).append("\n\n");
                    }
                }
            } catch (Exception e) {
                out.append(e.getMessage());
            }
        }

        resultText.setText(out.toString());
    }

    // ================= WRITE =================
    private void writeNdef(Tag tag) {
        try {
            String text = inputData.getText().toString();

            NdefMessage msg = new NdefMessage(
                    new NdefRecord[]{
                            NdefRecord.createTextRecord("en", text)
                    });

            Ndef ndef = Ndef.get(tag);
            ndef.connect();
            ndef.writeNdefMessage(msg);

            resultText.setText("WRITE OK");

        } catch (Exception e) {
            resultText.setText(e.getMessage());
        }
    }

    // ================= FORMAT =================
    private void formatTag(Tag tag) {
        try {
            NdefFormatable format = NdefFormatable.get(tag);
            if (format != null) {
                format.connect();
                format.format(new NdefMessage(new NdefRecord[]{new NdefRecord(
                        NdefRecord.TNF_EMPTY, new byte[0], new byte[0], new byte[0]
                )}));
                resultText.setText("Formatted");
            }
        } catch (Exception e) {
            resultText.setText(e.getMessage());
        }
    }

    // ================= APDU =================
    private void sendApdu(Tag tag) {
        try {
            IsoDep iso = IsoDep.get(tag);
            iso.connect();

            String[] cmds = inputData.getText().toString().split("\\n");

            StringBuilder out = new StringBuilder();

            for (String c : cmds) {
                byte[] apdu = hexToBytes(c);
                byte[] resp = iso.transceive(apdu);

                out.append("TX: ").append(c).append("\n");
                out.append("RX: ").append(bytesToHex(resp)).append("\n\n");
            }

            resultText.setText(out.toString());

        } catch (Exception e) {
            resultText.setText(e.getMessage());
        }
    }

    // ================= MIFARE =================
    private void readMifare(Tag tag) {
        try {
            MifareClassic m = MifareClassic.get(tag);
            m.connect();

            StringBuilder out = new StringBuilder();

            for (int s = 0; s < m.getSectorCount(); s++) {

                //with keys
//                boolean ok = m.authenticateSectorWithKeyA(s, new byte[]{-1,-1,-1,-1,-1,-1});

                //without keys
                boolean ok = authSector(m, s);

                if (!ok) continue;

                int block = m.sectorToBlock(s);
                int count = m.getBlockCountInSector(s);

                out.append("Sector ").append(s).append("\n");

                for (int i = 0; i < count; i++) {
                    byte[] data = m.readBlock(block + i);

                    out.append("B").append(block+i)
                            .append(": ")
                            .append(bytesToHex(data))
                            .append("\n");

                    if (i == count - 1) {
                        out.append(decodeAccessBits(data)).append("\n");
                    }
                }
            }

            resultText.setText(out.toString());

        } catch (Exception e) {
            resultText.setText(e.getMessage());
        }
    }

    private boolean authSector(MifareClassic m, int sector) {
        byte[][] keys = new byte[][]{
                MifareClassic.KEY_DEFAULT,      // FF FF FF FF FF FF
                MifareClassic.KEY_MIFARE_APPLICATION_DIRECTORY, // A0 A1 A2 A3 A4 A5
                MifareClassic.KEY_NFC_FORUM,    // D3 F7 D3 F7 D3 F7
                new byte[]{0,0,0,0,0,0}
        };

        for (byte[] key : keys) {
            try {
                if (m.authenticateSectorWithKeyA(sector, key)) return true;
                if (m.authenticateSectorWithKeyB(sector, key)) return true;
            } catch (Exception ignored) {}
        }

        return false;
    }


    // ================= ACCESS BIT DECODER =================
    private String decodeAccessBits(byte[] d) {
        byte b6 = d[6], b7 = d[7], b8 = d[8];

        return "Access Bits: " +
                String.format("%02X %02X %02X", b6, b7, b8);
    }

    // ================= NDEF PARSER =================
    private String parseNdef(NdefRecord r) {
        byte[] payload = r.getPayload();

        if (Arrays.equals(r.getType(), NdefRecord.RTD_TEXT)) {
            return "TEXT: " + new String(payload);
        }

        if (Arrays.equals(r.getType(), NdefRecord.RTD_URI)) {
            return "URL: " + new String(payload);
        }

        return "RAW: " + bytesToHex(payload);
    }

    // ================= UTIL =================
    private String bytesToHex(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte x : b) sb.append(String.format("%02X ", x));
        return sb.toString();
    }

    private byte[] hexToBytes(String s) {
        s = s.replace(" ", "");
        byte[] d = new byte[s.length()/2];
        for(int i=0;i<d.length;i++){
            d[i]=(byte)Integer.parseInt(s.substring(i*2,i*2+2),16);
        }
        return d;
    }
}
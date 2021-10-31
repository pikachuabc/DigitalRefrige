package com.example.digitalrefrige.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.widget.Toast;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;

public class NfcUtils {
    private NfcAdapter mNfcAdapter;
    private IntentFilter[] mIntentFilter = null;
    private PendingIntent mPendingIntent = null;
    private String[][] mTechList = null;
    private final Activity activity;

    public NfcUtils(Activity activity) {
        this.activity = activity;
        check();
        init();
    }

    private void check() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(activity);
//        if (mNfcAdapter == null) {
//            Toast.makeText(activity, "Device not support NFC", Toast.LENGTH_SHORT).show();
//        }
    }

    private void init() {
        Intent intent = new Intent(activity,activity.getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mPendingIntent = PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_MUTABLE);

        IntentFilter ndefFilter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndefFilter.addDataType("text/plain");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }

        IntentFilter techFilter = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        mIntentFilter = new IntentFilter[]{ndefFilter, techFilter};
        mTechList = new String[][]{
                {MifareClassic.class.getName()},
                {NfcA.class.getName()},
                {Ndef.class.getName()},
                {NdefFormatable.class.getName()}};
    }


    public NfcAdapter getmNfcAdapter() {
        return mNfcAdapter;
    }

    //Monitor NFC
    public void enableForegroundDispatch() {
        if (mNfcAdapter != null && mNfcAdapter.isEnabled()) {
            mNfcAdapter.enableForegroundDispatch(activity, mPendingIntent, mIntentFilter, mTechList);
        }
    }

    //Cancel NFC monitor
    public void disableForegroundDispatch() {
        if (mNfcAdapter != null && mNfcAdapter.isEnabled()) {
            mNfcAdapter.disableForegroundDispatch(activity);
        }
    }

    public void writeNFCToTag(String text, Intent intent) throws IOException, FormatException {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        NdefRecord record = NdefRecord.createTextRecord("en",text);
        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{record});
        int size = ndefMessage.toByteArray().length;
        Ndef ndef = Ndef.get(tag);

        if (ndef != null) {
            ndef.connect();
            if (!ndef.isWritable()) {

                return;
            }
            if (ndef.getMaxSize() < size) {
                return;
            }
            ndef.writeNdefMessage(ndefMessage);
        } else {
            NdefFormatable format = NdefFormatable.get(tag);
            if (format != null) {
                format.connect();
                format.format(ndefMessage);
            }
        }
    }

}

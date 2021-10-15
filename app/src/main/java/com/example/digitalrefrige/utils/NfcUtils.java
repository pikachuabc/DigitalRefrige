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
    private Activity activity;

    public NfcUtils(Activity activity) {
        this.activity = activity;
        check();
        init();
    }

    private void check() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(activity);
        if (mNfcAdapter == null) {
            Toast.makeText(activity, "Device not support NFC", Toast.LENGTH_SHORT).show();
        }
    }

    private void init() {
        Intent intent = new Intent(activity,activity.getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mPendingIntent = PendingIntent.getActivity(activity, 0, intent, 0);

        IntentFilter ndefFilter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            //文本类型
            ndefFilter.addDataType("text/plain");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }

        //intentFilter过滤----非ndef
        IntentFilter techFilter = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        //intentFilter过滤器列表
        mIntentFilter = new IntentFilter[]{ndefFilter, techFilter};
        //匹配的数据格式列表
        mTechList = new String[][]{
                {MifareClassic.class.getName()},
                {NfcA.class.getName()},
                {Ndef.class.getName()},
                {NdefFormatable.class.getName()}};
    }

    /**
     * Nfc监听intent
     */
    public void enableForegroundDispatch() {
        if (mNfcAdapter != null && mNfcAdapter.isEnabled()) {
            mNfcAdapter.enableForegroundDispatch(activity, mPendingIntent, mIntentFilter, mTechList);
        }
    }

    /**
     * 取消监听Nfc
     */
    public void disableForegroundDispatch() {
        if (mNfcAdapter != null && mNfcAdapter.isEnabled()) {
            mNfcAdapter.disableForegroundDispatch(activity);
        }
    }

    public Tag getNFCTag(Intent intent) {return intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);}

    public void writeNFCToTag(String text, Intent intent) throws IOException, FormatException {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        NdefRecord record = NdefRecord.createTextRecord("en",text);
        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{record});
        int size = ndefMessage.toByteArray().length;
        Ndef ndef = Ndef.get(tag);

        if (ndef != null) {
            ndef.connect();
            //判断是否支持可写
            if (!ndef.isWritable()) {

                return;
            }
            //判断标签的容量是否够用
            if (ndef.getMaxSize() < size) {
                return;
            }
            //3.写入数据
            ndef.writeNdefMessage(ndefMessage);
        } else {
            //当我们买回来的NFC标签是没有格式化的，或者没有分区的执行此步
            //Ndef格式类
            NdefFormatable format = NdefFormatable.get(tag);
            //判断是否获得了NdefFormatable对象，有一些标签是只读的或者不允许格式化的
            if (format != null) {
                //连接
                format.connect();
                //格式化并将信息写入标签
                format.format(ndefMessage);
            } else {
            }
        }
    }

}

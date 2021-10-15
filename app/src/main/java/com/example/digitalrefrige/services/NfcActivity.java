package com.example.digitalrefrige.services;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;

import com.example.digitalrefrige.R;
import com.example.digitalrefrige.utils.Converters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class NfcActivity extends AppCompatActivity {
    TextView nfcTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            Parcelable[] rawMessages =
                    getIntent().getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMessages != null) {
                NdefMessage[] messages = new NdefMessage[rawMessages.length];
                for (int i = 0; i < rawMessages.length; i++) {
                    messages[i] = (NdefMessage) rawMessages[i];
                }
                // Process the messages array.
                NdefMessage message = (NdefMessage) messages[0];
                NdefRecord record = message.getRecords()[0];
                String payload = new String(record.getPayload());
                long dayDifferences = calDateDifferenceWithCurrent(payload.substring(3));
                nfcTextView = findViewById(R.id.nfc_textview);
                nfcTextView.setText("The item is expiring in "+ Long.toString(dayDifferences)+" days\n"+payload.substring(3));
            }
        }
    }

    private long calDateDifferenceWithCurrent(String selectDateString){
        Date selectDate = Converters.strToDate(selectDateString);
        Date currentDate = new Date();
        long differenceInTime = selectDate.getTime() - currentDate.getTime();
        long difference_In_Days = TimeUnit.MILLISECONDS.toDays(differenceInTime) % 365;
        return difference_In_Days;
    }


}
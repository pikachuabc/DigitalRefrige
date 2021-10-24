package com.example.digitalrefrige.services;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.digitalrefrige.R;
import com.example.digitalrefrige.utils.Converters;
import com.example.digitalrefrige.views.itemList.ItemDetailActivity;
import com.example.digitalrefrige.views.itemList.ItemDetailActivityArgs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class NfcActivity extends AppCompatActivity {
    TextView nfcTextView;
    Button nfcButton;

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
                long itemID = Long.parseLong(payload.substring(3));
                nfcTextView = findViewById(R.id.nfc_textview);
                nfcTextView.setText("Click the button to view item");
                nfcButton = findViewById(R.id.nfc_button);
                nfcButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent =  new Intent(NfcActivity.this, ItemDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putLong("itemID",itemID);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();

                    }
                });
            }
        }
    }


}
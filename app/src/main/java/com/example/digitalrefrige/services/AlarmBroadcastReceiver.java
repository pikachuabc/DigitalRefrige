package com.example.digitalrefrige.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LiveData;

import com.example.digitalrefrige.MainActivity;
import com.example.digitalrefrige.R;
import com.example.digitalrefrige.model.ItemRepository;
import com.example.digitalrefrige.model.dataHolder.Item;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AlarmBroadcastReceiver extends BroadcastReceiver {
    private final String CHANNEL_ID = "expiry";
    private Context context;
    @Inject
    ItemRepository itemRepository;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        List<Item> expiringItems = itemRepository.getExpiringItems(7);

        if (expiringItems!= null && !expiringItems.isEmpty()) {
                if (intent.getAction().equals("NOTIFY")) {
                    Intent mIntent = new Intent(context, MainActivity.class);
                    PendingIntent mPendingIntent = PendingIntent.getActivity(context, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    createNotificationChannel();
                    NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_document)
                            .setContentTitle("Expiring")
                            .setContentText("You have expiring item(s)")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setContentIntent(mPendingIntent)
                            .setAutoCancel(true);
                    manager.notify(0, builder.build());
                }
        }

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}

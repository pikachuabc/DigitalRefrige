package com.example.digitalrefrige.views.login;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.digitalrefrige.MainActivity;
import com.example.digitalrefrige.R;
import com.example.digitalrefrige.databinding.FragmentExpireBinding;
import com.example.digitalrefrige.services.AlarmBroadcastReceiver;

import java.util.Calendar;
import java.util.Locale;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;


public class ExpireFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{
    private FragmentExpireBinding binding;
    private AlarmManager alarmManager;
    private Calendar calendar;
    private EditTextPreference dayExpireEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        dayExpireEditText = findPreference("expire_day");
        dayExpireEditText.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
            @Override
            public void onBindEditText(@NonNull EditText editText) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }



    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("notifications")) {
            if (sharedPreferences.getBoolean(key, false)){
                // Setting Alarm for daily notification
                alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                // Set the alarm to start at approximately time.
                calendar = Calendar.getInstance(Locale.getDefault());
                setCalendarHour(9, calendar);

                Intent alarmIntent = new Intent(getContext(), AlarmBroadcastReceiver.class);
                alarmIntent.setAction("NOTIFY");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, alarmIntent, PendingIntent.FLAG_MUTABLE);
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pendingIntent);
                Toast.makeText(getContext(),"Notification enabled",Toast.LENGTH_SHORT).show();
            }
        }
        if (key.equals("expire_day")) {
            Toast.makeText(getContext(),"Expiring deadline update to "+sharedPreferences.getString(key,"7"),Toast.LENGTH_SHORT).show();
        }


    }

    private void setCalendarHour(int hour, Calendar calendar){
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }
}
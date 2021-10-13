package com.example.digitalrefrige.views.common;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class picSelectorDialogFragment extends DialogFragment {
    DialogInterface.OnClickListener listener;
    String[] availableMode;

    public picSelectorDialogFragment( String[] availableMode,DialogInterface.OnClickListener listener) {
        this.listener = listener;
        this.availableMode = availableMode;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("select photo or take one")
                .setItems(availableMode, listener);
        return builder.create();
    }
}

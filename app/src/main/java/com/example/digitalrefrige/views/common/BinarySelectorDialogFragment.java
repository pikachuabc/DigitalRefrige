package com.example.digitalrefrige.views.common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.digitalrefrige.model.dataHolder.Label;

import java.util.List;

public class BinarySelectorDialogFragment extends DialogFragment {

    private OnShakeOptionChosenListener listener;
    private String title;

    public interface OnShakeOptionChosenListener {
        void onPositiveClicked();
        void onNegativeClicked();
    }

    public BinarySelectorDialogFragment(OnShakeOptionChosenListener listener, String title) {
        this.listener = listener;
        this.title = title;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onPositiveClicked();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onNegativeClicked();
            }
        });
        return builder.create();

    }
}

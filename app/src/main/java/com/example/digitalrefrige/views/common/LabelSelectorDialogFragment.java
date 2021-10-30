package com.example.digitalrefrige.views.common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.digitalrefrige.R;
import com.example.digitalrefrige.model.dataHolder.Label;

import java.util.ArrayList;
import java.util.List;


public class LabelSelectorDialogFragment extends DialogFragment {

    List<Label> allLabels;
    List<Label> preSelectedLabels;
    List<Label> selectedLabel = new ArrayList<>();
    String dialogTitle;


    public interface OnLabelsChosenListener {
        void onPositiveClicked(List<Label> selectedLabels);
    }

    OnLabelsChosenListener listener;

    public LabelSelectorDialogFragment(List<Label> allLabels, List<Label> preSelectedLabels, OnLabelsChosenListener listener, String title) {
        this.allLabels = allLabels;
        this.listener = listener;
        this.preSelectedLabels = preSelectedLabels;
        selectedLabel.addAll(preSelectedLabels);
        dialogTitle = title;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String[] allLabelStr = new String[allLabels.size()];
        boolean[] checkStatus = new boolean[allLabels.size()];
        for (int i = 0; i < allLabels.size(); i++) {
            Label temp = allLabels.get(i);
            allLabelStr[i] = temp.getTitle();
            if (preSelectedLabels.contains(temp)) {
                checkStatus[i] = true;
            }
        }
        builder.setTitle(dialogTitle).setMultiChoiceItems(allLabelStr, checkStatus, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                if (b) {
                    selectedLabel.add(allLabels.get(i));
                } else {
                    selectedLabel.remove(allLabels.get(i));
                }
            }
        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onPositiveClicked(selectedLabel);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        return builder.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_label_selector_dialog, container, false);
    }
}
package com.example.digitalrefrige.views.common;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.digitalrefrige.R;
import com.example.digitalrefrige.databinding.DialogProgressBinding;

import org.w3c.dom.Text;

public class ProgressDialog extends DialogFragment {
    Dialog dialog;

    String message;

    public ProgressDialog(String progressingMes) {
        message = progressingMes;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_progress, null);
        TextView progressingMessageTextView = view.findViewById(R.id.progress_message);
        progressingMessageTextView.setText(message);
        builder.setView(view);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }


}

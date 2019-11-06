package com.example.bledemo;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;


public class WriteDialogFragment extends DialogFragment {
    private EditText editMessage;
    private WriteDialogListener listener;
    Fragment fragment;

    public WriteDialogFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog, null);

        builder.setView(view)
                .setTitle("Message")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String username = editMessage.getText().toString();
                        listener.applyText(username);
                    }
                });

        editMessage = view.findViewById(R.id.message);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (WriteDialogListener) fragment;
    }

    public interface WriteDialogListener {
        void applyText(String message);
    }
}
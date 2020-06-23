package com.example.storagemanager.fragments.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.storagemanager.R;

import java.util.Objects;

public class DeleteDialog extends DialogFragment {

    public interface DeleteListener {
        void delete(String id);
    }

    private final String mMessage;
    private final String mId;
    private DeleteListener mListener;

    public DeleteDialog(String id, String message) {
        mId = id;
        mMessage = message;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog
                .Builder(requireActivity())
                .setMessage(mMessage)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    mListener.delete(mId);
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    Objects.requireNonNull(DeleteDialog.this.getDialog()).cancel();
                })
                .create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (DeleteListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DeleteDialogListener");
        }
    }
}

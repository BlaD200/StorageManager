package com.example.storagemanager.fragments.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.storagemanager.R;

import java.util.Objects;

public class DeleteDialog<E> extends DialogFragment {

    public interface DeleteListener<E> {
        void delete(E entity);
    }

    private DeleteListener<E> mListener;
    private final E mEntity;
    private final String mMessage;

    public DeleteDialog(E goodEntity, String message) {
        mEntity = goodEntity;
        mMessage = message;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog
                .Builder(requireActivity())
                .setMessage(mMessage)
                .setPositiveButton(R.string.delete, (dialog, which) ->
                        mListener.delete(mEntity))
                .setNegativeButton(R.string.cancel, (dialog, which) ->
                        Objects.requireNonNull(DeleteDialog.this.getDialog()).cancel())
                .create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            //noinspection unchecked
            mListener = (DeleteListener<E>) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DeleteDialogListener");
        }
    }
}

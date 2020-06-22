package com.example.storagemanager.fragments.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.example.storagemanager.R;
import com.example.storagemanager.databinding.DialogCreateGroupBinding;

import java.util.Objects;

public class CreateGroupDialog extends DialogFragment {

    public interface CreateGroupDialogListener {
        void getGroupData(String name, String description);
    }

    private CreateGroupDialogListener mListener;

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DialogCreateGroupBinding binding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()),
                R.layout.dialog_create_group, null, false);

        return new AlertDialog
                .Builder(requireActivity())
                .setView(binding.getRoot())
                .setPositiveButton(R.string.create, (dialog, id) -> {
                    String name = binding.editName.getText().toString();
                    String description = binding.editDescription.getText().toString();

                    mListener.getGroupData(name, description);
                })
                .setNegativeButton(R.string.cancel, (dialog, id) ->
                        Objects.requireNonNull(CreateGroupDialog.this.getDialog()).cancel())
                .create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (CreateGroupDialogListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement CreateGroupDialogListener");
        }
    }
}


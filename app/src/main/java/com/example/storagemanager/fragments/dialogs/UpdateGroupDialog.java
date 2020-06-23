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
import com.example.storagemanager.databinding.DialogGroupBinding;
import com.example.storagemanager.entities.GroupEntity;

import java.util.Objects;

public class UpdateGroupDialog extends DialogFragment {

    public interface UpdateGroupDialogListener {
        void updateGroupData(String name, String description);
    }

    private UpdateGroupDialogListener mListener;
    private final GroupEntity mGroupEntity;

    public UpdateGroupDialog(@NonNull GroupEntity groupEntity) {
        mGroupEntity = groupEntity;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DialogGroupBinding binding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()),
                R.layout.dialog_group, null, false);

        binding.setGroup(mGroupEntity);

        return new AlertDialog
                .Builder(requireActivity())
                .setView(binding.getRoot())
                .setPositiveButton(R.string.update, (dialog, id) -> {
                    String name = binding.editName.getText().toString();
                    String description = binding.editDescription.getText().toString();

                    mListener.updateGroupData(name, description);
                })
                .setNegativeButton(R.string.cancel, (dialog, id) ->
                        Objects.requireNonNull(UpdateGroupDialog.this.getDialog()).cancel())
                .create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (UpdateGroupDialogListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement UpdateGroupDialogListener");
        }
    }
}



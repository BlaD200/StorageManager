package com.example.storagemanager.fragments.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
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

public class GroupDialog extends DialogFragment {

    public interface CreateGroupListener {
        void createGroupData(String name, String description);
    }

    public interface UpdateGroupListener {
        void updateGroupData(String name, String description);
    }

    private CreateGroupListener mCreateListener;
    private UpdateGroupListener mUpdateListener;

    private GroupEntity mGroupEntity;

    public GroupDialog(CreateGroupListener createListener, GroupEntity groupEntity) {
        mCreateListener = createListener;
        mGroupEntity = groupEntity;
    }

    public GroupDialog(UpdateGroupListener updateListener, GroupEntity groupEntity) {
        mUpdateListener = updateListener;
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
                .setPositiveButton(mCreateListener != null ?
                        R.string.create : R.string.update, (dialog, id) -> {
                    String name = binding.editName.getText().toString();
                    String description = binding.editDescription.getText().toString();

                    if (mCreateListener != null)
                        mCreateListener.createGroupData(name, description);
                    else
                        mUpdateListener.updateGroupData(name, description);
                })
                .setNegativeButton(R.string.cancel, (dialog, id) ->
                        Objects.requireNonNull(GroupDialog.this.getDialog()).cancel())
                .create();
    }
}


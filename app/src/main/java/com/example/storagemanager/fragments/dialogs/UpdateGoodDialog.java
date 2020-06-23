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
import com.example.storagemanager.databinding.DialogGoodBinding;
import com.example.storagemanager.entities.GoodEntity;

import java.util.Objects;

public class UpdateGoodDialog extends DialogFragment {

    public interface UpdateGoodDialogListener {
        void updateGoodData(String name, String group, String description,
                            String producer, int amount, int price);
    }

    private UpdateGoodDialogListener mListener;
    private final GoodEntity mGoodEntity;

    public UpdateGoodDialog(GoodEntity goodEntity) {
        mGoodEntity = goodEntity;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DialogGoodBinding binding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()),
                R.layout.dialog_good, null, false);

        binding.setGood(mGoodEntity);

        return new AlertDialog
                .Builder(requireActivity())
                .setView(binding.getRoot())
                .setPositiveButton(R.string.create, (dialog, id) -> {
                    String name = binding.editName.getText().toString();
                    String group = binding.spinnerGroup.getSelectedItem().toString();
                    String description = binding.editDescription.getText().toString();
                    String producer = binding.spinnerProducer.getSelectedItem().toString();

                    int amount = -1;
                    int price = -1;
                    try {
                        amount = Integer.parseInt(binding.editAmount.getText().toString());
                        price = Integer.parseInt(binding.editPrice.getText().toString());
                    } catch (NumberFormatException ignored) {
                    }

                    mListener.updateGoodData(name, group, description, producer, amount, price);
                })
                .setNegativeButton(R.string.cancel, (dialog, id) ->
                        Objects.requireNonNull(UpdateGoodDialog.this.getDialog()).cancel())
                .create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (UpdateGoodDialogListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement UpdateGoodDialogListener");
        }
    }
}


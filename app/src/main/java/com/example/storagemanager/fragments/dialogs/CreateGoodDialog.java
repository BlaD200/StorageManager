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
import com.example.storagemanager.databinding.DialogCreateGoodBinding;

import java.util.Objects;

public class CreateGoodDialog extends DialogFragment {

    public interface CreateGoodDialogListener {
        void getGoodData(String name, String group, String description,
                         String producer, int amount, int price);
    }

    private CreateGoodDialogListener mListener;

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DialogCreateGoodBinding binding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()),
                R.layout.dialog_create_good, null, false);

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

                    mListener.getGoodData(name, group, description, producer, amount, price);
                })
                .setNegativeButton(R.string.cancel, (dialog, id) ->
                        Objects.requireNonNull(CreateGoodDialog.this.getDialog()).cancel())
                .create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (CreateGoodDialog.CreateGoodDialogListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement CreateGoodDialogListener");
        }
    }
}

package com.example.storagemanager.fragments.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.example.storagemanager.R;
import com.example.storagemanager.databinding.DialogGoodBinding;
import com.example.storagemanager.entities.GoodEntity;

import java.util.Objects;

public class GoodDialog extends DialogFragment {

    public interface CreateGoodListener {
        void createGoodData(String name, String group, String description,
                            String producer, int amount, int price);
    }

    public interface UpdateGoodListener {
        void updateGoodData(String name, String group, String description,
                            String producer, int amount, int price);
    }

    private CreateGoodListener mCreateListener;
    private UpdateGoodListener mUpdateListener;

    private final GoodEntity mGoodEntity;

    public GoodDialog(CreateGoodListener createListener, GoodEntity goodEntity) {
        mCreateListener = createListener;
        mGoodEntity = goodEntity;
    }

    public GoodDialog(UpdateGoodListener updateListener, GoodEntity goodEntity) {
        mUpdateListener = updateListener;
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
                .setPositiveButton(mCreateListener != null ?
                        R.string.create : R.string.update, (dialog, id) -> {
                    String name = binding.editName.getText().toString();
                    String group = binding.spinnerGroup.getSelectedItem().toString();
                    String description = binding.editDescription.getText().toString();
                    String producer = binding.spinnerProducer.getSelectedItem().toString();

                    try {
                        int amount = Integer.parseInt(binding.editAmount.getText().toString());
                        int price = Integer.parseInt(binding.editPrice.getText().toString());

                        if (mCreateListener != null)
                            mCreateListener.createGoodData(name, group, description,
                                    producer, amount, price);
                        else
                            mUpdateListener.updateGoodData(name, group, description,
                                    producer, amount, price);
                    } catch (NumberFormatException ignored) {
                        Toast.makeText(requireContext(), "Amount and price cannot be empty",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel, (dialog, id) ->
                        Objects.requireNonNull(GoodDialog.this.getDialog()).cancel())
                .create();
    }
}

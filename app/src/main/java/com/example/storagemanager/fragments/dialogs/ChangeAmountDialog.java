package com.example.storagemanager.fragments.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.example.storagemanager.R;
import com.example.storagemanager.databinding.DialogChangeAmountBinding;
import com.example.storagemanager.entities.GoodEntity;

import java.util.Objects;

public class ChangeAmountDialog extends DialogFragment {

    public interface ChangeAmountListener {
        void addAmount(GoodEntity goodEntity, int amount);

        void removeAmount(GoodEntity goodEntity, int amount);
    }

    private ChangeAmountListener mListener;
    private final GoodEntity mGoodEntity;
    private final boolean mToAdd;

    public ChangeAmountDialog(GoodEntity goodEntity, boolean toAdd) {
        mGoodEntity = goodEntity;
        mToAdd = toAdd;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DialogChangeAmountBinding binding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()),
                R.layout.dialog_change_amount, null, false);

        binding.setAmount(mGoodEntity.getAmount());
        binding.setToAdd(mToAdd);

        return new AlertDialog
                .Builder(requireActivity())
                .setView(binding.getRoot())
                .setPositiveButton(mToAdd ? R.string.add : R.string.remove, (dialog, id) -> {
                    try {
                        int amount = Integer.parseInt(binding.editChangeValue.getText().toString());

                        if (mToAdd)
                            mListener.addAmount(mGoodEntity, amount);
                        else
                            mListener.removeAmount(mGoodEntity, amount);
                    } catch (NumberFormatException ignored) {
                        Toast.makeText(requireContext(), "Amount cannot be empty",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel, (dialog, id) ->
                        Objects.requireNonNull(ChangeAmountDialog.this.getDialog()).cancel())
                .create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (ChangeAmountListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement ChangeAmountListener");
        }
    }
}

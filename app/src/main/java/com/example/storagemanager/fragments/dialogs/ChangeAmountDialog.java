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

import java.util.Objects;

public class ChangeAmountDialog extends DialogFragment {

    public interface ChangeAmountDialogListener {
        void addAmount(int amount);

        void removeAmount(int amount);
    }

    private ChangeAmountDialogListener mListener;
    private final int mAmount;
    private final boolean mToAdd;

    public ChangeAmountDialog(int amount, boolean toAdd) {
        mAmount = amount;
        mToAdd = toAdd;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DialogChangeAmountBinding binding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()),
                R.layout.dialog_change_amount, null, false);

        binding.setAmount(mAmount);
        binding.setToAdd(mToAdd);

        return new AlertDialog
                .Builder(requireActivity())
                .setView(binding.getRoot())
                .setPositiveButton(mToAdd ? R.string.add : R.string.remove, (dialog, id) -> {
                    try {
                        int amount = Integer.parseInt(binding.editChangeValue.getText().toString());

                        if (mToAdd)
                            mListener.addAmount(amount);
                        else
                            mListener.removeAmount(amount);
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
            mListener = (ChangeAmountDialogListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement ChangeAmountDialogListener");
        }
    }
}

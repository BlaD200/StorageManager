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
import com.example.storagemanager.databinding.DialogProducerBinding;

import java.util.Objects;

public class ProducerDialog extends DialogFragment {

    public interface CreateProducerListener {
        void createProducerData(String producer);
    }

    public interface UpdateProducerListener {
        void updateProducerData(String producerBefore, String producerAfter);
    }

    private CreateProducerListener mCreateListener;
    private UpdateProducerListener mUpdateListener;

    private String mProducer;

    public ProducerDialog(CreateProducerListener createListener, String producer) {
        mCreateListener = createListener;
        mProducer = producer;
    }

    public ProducerDialog(UpdateProducerListener updateListener, String producer) {
        mUpdateListener = updateListener;
        mProducer = producer;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DialogProducerBinding binding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()),
                R.layout.dialog_producer, null, false);

        binding.setProducer(mProducer);

        return new AlertDialog
                .Builder(requireActivity())
                .setView(binding.getRoot())
                .setPositiveButton(mCreateListener != null ?
                        R.string.create : R.string.update, (dialog, id) -> {
                    String name = binding.editName.getText().toString();

                    if (mCreateListener != null)
                        mCreateListener.createProducerData(name);
                    else
                        mUpdateListener.updateProducerData(mProducer, name);
                })
                .setNegativeButton(R.string.cancel, (dialog, id) ->
                        Objects.requireNonNull(ProducerDialog.this.getDialog()).cancel())
                .create();
    }
}


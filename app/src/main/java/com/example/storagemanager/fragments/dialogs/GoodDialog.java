package com.example.storagemanager.fragments.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.storagemanager.R;
import com.example.storagemanager.Utils;
import com.example.storagemanager.backend.entity.Group;
import com.example.storagemanager.databinding.DialogGoodBinding;
import com.example.storagemanager.entities.GoodEntity;
import com.example.storagemanager.viewmodels.GoodsViewModel;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class GoodDialog extends DialogFragment {

    private final GoodEntity mGoodEntity;
    private CreateGoodListener mCreateListener;
    private UpdateGoodListener mUpdateListener;
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

        GoodsViewModel mViewModel = new ViewModelProvider(this)
                .get(GoodsViewModel.class);

        List<String> groups = new ArrayList<>();
        mViewModel.getGroups()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(reply -> {
                    try {
                        List<Group> groupList = mViewModel.getMapper().readValue(reply, new TypeReference<List<Group>>() {
                        });
                        groups.addAll(groupList.stream().filter(Objects::nonNull).map(Group::getName).collect(Collectors.toList()));
                        binding.spinnerGroup.setAdapter(new ArrayAdapter<>(
                                requireContext(), R.layout.item_spinner_black, groups));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        List<String> producers = new ArrayList<>();
        mViewModel.getProducers()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(reply -> {
                    try {
                        List<String> producersList = mViewModel.getMapper().readValue(reply, new TypeReference<List<String>>() {
                        });
                        producers.addAll(producersList.stream().filter(Objects::nonNull).collect(Collectors.toList()));
                        binding.spinnerProducer.setAdapter(new ArrayAdapter<>(
                                requireContext(), R.layout.item_spinner_black, producers));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        binding.setGood(mGoodEntity);

        return new AlertDialog
                .Builder(requireActivity())
                .setView(binding.getRoot())
                .setPositiveButton(mCreateListener != null ?
                        R.string.create : R.string.update, (dialog, id) -> {
                    String name = binding.editName.getText() != null ? binding.editName.getText().toString() : null;
                    String group = binding.spinnerGroup.getSelectedItem() != null ? binding.spinnerGroup.getSelectedItem().toString() : null;
                    String description = binding.editDescription.getText().toString();
                    String producer = binding.spinnerProducer.getSelectedItem().toString();
                    Integer amount = Utils.getIntOrNull(binding.editAmount);
                    Integer price = Utils.getIntOrNull(binding.editPrice);

                    if (mCreateListener != null)
                        mCreateListener.createGoodData(name, group, description,
                                producer, amount, price);
                    else
                        mUpdateListener.updateGoodData(name, group, description,
                                producer, amount, price);
                })
                .setNegativeButton(R.string.cancel, (dialog, id) ->
                        Objects.requireNonNull(GoodDialog.this.getDialog()).cancel())
                .create();
    }

    public interface CreateGoodListener {
        void createGoodData(String name, String group, String description,
                            String producer, Integer amount, Integer price);
    }

    public interface UpdateGoodListener {
        void updateGoodData(String name, String group, String description,
                            String producer, Integer amount, Integer price);
    }
}

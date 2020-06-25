package com.example.storagemanager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.storagemanager.R;
import com.example.storagemanager.backend.entity.Good;
import com.example.storagemanager.databinding.FragmentGoodBinding;
import com.example.storagemanager.entities.GoodEntity;
import com.example.storagemanager.viewmodels.GoodViewModel;

import java.io.IOException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class GoodFragment extends Fragment {

    private FragmentGoodBinding mBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_good,
                container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GoodViewModel viewModel = new ViewModelProvider(this)
                .get(GoodViewModel.class);

        String goodName = GoodFragmentArgs.fromBundle(requireArguments()).getGoodName();
        viewModel.getGoodByName(goodName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(reply -> {
                    try {
                        Good good = viewModel.getMapper().readValue(reply, Good.class);
                        mBinding.setGood(new GoodEntity(good));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        mBinding.textGroup.setOnClickListener(v -> {
            NavDirections action = GoodFragmentDirections
                    .actionGoodFragmentToGroupFragment(goodName);
            Navigation.findNavController(v).navigate(action);
        });
    }
}
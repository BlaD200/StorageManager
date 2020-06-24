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
import com.example.storagemanager.databinding.FragmentGoodBinding;
import com.example.storagemanager.entities.GoodEntity;
import com.example.storagemanager.viewmodels.GoodViewModel;

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
        GoodEntity goodEntity = viewModel.getGoodByName(goodName);
        mBinding.setGood(goodEntity);

        mBinding.textGroup.setOnClickListener(v -> {
            NavDirections action = GoodFragmentDirections
                    .actionGoodFragmentToGroupFragment(goodName);
            Navigation.findNavController(v).navigate(action);
        });
    }
}
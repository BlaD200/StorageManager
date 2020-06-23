package com.example.storagemanager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.storagemanager.R;
import com.example.storagemanager.databinding.FragmentGoodBinding;

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

        String goodId = GoodFragmentArgs.fromBundle(requireArguments()).getGoodId();
        // TODO setup data
        //  mBinding.setGood();

        mBinding.textGroup.setOnClickListener(v -> {
            // TODO pass id
            NavDirections action = GoodFragmentDirections
                    .actionGoodFragmentToGroupFragment("fake id");
            Navigation.findNavController(v).navigate(action);
        });
    }
}
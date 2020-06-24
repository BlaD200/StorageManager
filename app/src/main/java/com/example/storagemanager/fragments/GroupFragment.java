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
import androidx.navigation.Navigation;

import com.example.storagemanager.R;
import com.example.storagemanager.databinding.FragmentGroupBinding;
import com.example.storagemanager.entities.GroupEntity;
import com.example.storagemanager.viewmodels.GroupViewModel;

public class GroupFragment extends Fragment {

    private FragmentGroupBinding mBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_group,
                container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GroupViewModel viewModel = new ViewModelProvider(this)
                .get(GroupViewModel.class);

        String groupName = GroupFragmentArgs.fromBundle(requireArguments()).getGroupName();

        GroupEntity groupEntity = viewModel.getGroupByName(groupName);
        mBinding.setGroup(groupEntity);

        int groupTotalPrice = viewModel.getGroupTotalPrice(groupEntity);
        mBinding.setTotalPrice(groupTotalPrice);

        mBinding.cardGoods.setOnClickListener(v -> {
            GroupFragmentDirections.ActionGroupFragmentToGoodsFragment action =
                    GroupFragmentDirections.actionGroupFragmentToGoodsFragment();
            action.setGroupName(groupName);
            Navigation.findNavController(v).navigate(action);
        });
    }
}
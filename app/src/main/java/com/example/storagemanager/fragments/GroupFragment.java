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
import com.example.storagemanager.backend.entity.Group;
import com.example.storagemanager.databinding.FragmentGroupBinding;
import com.example.storagemanager.entities.GroupEntity;
import com.example.storagemanager.viewmodels.GroupViewModel;

import java.io.IOException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

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

        viewModel.getGroupByName(groupName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(reply -> {
                    try {
                        Group good = viewModel.getMapper().readValue(reply, Group.class);
                        GroupEntity groupEntity = new GroupEntity(good.getName(), good.getDescription());
                        mBinding.setGroup(groupEntity);

                        viewModel.getGroupTotalPrice(groupEntity)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(reply2 -> {
                                    mBinding.setTotalPrice((int) Double.parseDouble(reply2));
                                });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        mBinding.cardGoods.setOnClickListener(v -> {
            GroupFragmentDirections.ActionGroupFragmentToGoodsFragment action =
                    GroupFragmentDirections.actionGroupFragmentToGoodsFragment();
            action.setGroupName(groupName);
            Navigation.findNavController(v).navigate(action);
        });
    }
}
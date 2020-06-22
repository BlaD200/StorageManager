package com.example.storagemanager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storagemanager.R;
import com.example.storagemanager.databinding.FragmentGoodsBinding;
import com.example.storagemanager.databinding.ItemGoodBinding;
import com.example.storagemanager.entities.GoodEntity;
import com.example.storagemanager.fragments.dialogs.CreateGoodDialog;

import java.util.LinkedList;
import java.util.List;

public class GoodsFragment extends Fragment implements CreateGoodDialog.CreateGoodDialogListener {

    private FragmentGoodsBinding mBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_goods,
                container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = mBinding.goodsList;

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(new GoodsFragment.Adapter(testData()));

        mBinding.fab.setOnClickListener(v -> {
            CreateGoodDialog dialog = new CreateGoodDialog();
            dialog.show(GoodsFragment.this.getChildFragmentManager(), CREATE_GOOD_DIALOG_TAG);
        });
    }

    @Override
    public void getGoodData(String name, String group, String description, String producer, int amount, int price) {
        String message;

        if (name.isEmpty() || group.isEmpty() || description.isEmpty())
            message = "Name, group, description or producer cannot be empty";
        else if (amount == -1 || price == -1)
            message = "Amount or price cannot be empty";
        else
            message = new GoodEntity(name, group, description, producer, amount, price).toString();

        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    static class Adapter extends RecyclerView.Adapter<Adapter.GoodViewHolder> {

        private List<GoodEntity> mData;

        public Adapter(@NonNull List<GoodEntity> data) {
            mData = data;
        }

        @NonNull
        @Override
        public GoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                 int viewType) {
            ItemGoodBinding binding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.item_good, parent, false);

            return new GoodViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(GoodViewHolder holder, int position) {
            GoodEntity goodEntity = mData.get(position);
            holder.bind(goodEntity);
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public static class GoodViewHolder extends RecyclerView.ViewHolder {

            private final ItemGoodBinding mBinding;

            public GoodViewHolder(ItemGoodBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }

            public void bind(GoodEntity goodEntity) {
                mBinding.setGood(goodEntity);
                mBinding.executePendingBindings();
            }
        }
    }

    private List<GoodEntity> testData() {
        List<GoodEntity> goodEntities = new LinkedList<>();

        goodEntities.add(new GoodEntity("Goody good 0", "Good",
                "Good Description", "Good Company", 1000, 1000));
        goodEntities.add(new GoodEntity("Goody good 1", "Good",
                "Good Description", "Good Company", 1000, 1000));
        goodEntities.add(new GoodEntity("Goody good 2", "Good",
                "Good Description", "Good Company", 1000, 1000));
        goodEntities.add(new GoodEntity("Goody good 3", "Good",
                "Good Description", "Good Company", 1000, 1000));
        goodEntities.add(new GoodEntity("Goody good 4", "Good",
                "Good Description", "Good Company", 1000, 1000));
        goodEntities.add(new GoodEntity("Goody good 5", "Good",
                "Good Description", "Good Company", 1000, 1000));
        goodEntities.add(new GoodEntity("Bady good 0", "Bad",
                "Bad Description", "Bad Company", 10, 100));
        goodEntities.add(new GoodEntity("Bady good 1", "Bad",
                "Bad Description", "Bad Company", 10, 100));
        goodEntities.add(new GoodEntity("Bady good 2", "Bad",
                "Bad Description", "Bad Company", 10, 100));
        goodEntities.add(new GoodEntity("Bady good 3", "Bad",
                "Bad Description", "Bad Company", 10, 100));
        goodEntities.add(new GoodEntity("Bady good 4", "Bad",
                "Bad Description", "Bad Company", 10, 100));
        goodEntities.add(new GoodEntity("Bady good 5", "Bad",
                "Bad Description", "Bad Company", 10, 100));

        return goodEntities;
    }

    private static final String CREATE_GOOD_DIALOG_TAG = "CREATE_GOOD_DIALOG";
}
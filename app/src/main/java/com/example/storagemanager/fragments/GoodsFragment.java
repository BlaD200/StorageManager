package com.example.storagemanager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storagemanager.R;
import com.example.storagemanager.databinding.FragmentGoodsBinding;
import com.example.storagemanager.databinding.ItemGoodBinding;
import com.example.storagemanager.entities.GoodEntity;
import com.example.storagemanager.fragments.dialogs.CreateGoodDialog;
import com.example.storagemanager.fragments.dialogs.DeleteDialog;
import com.example.storagemanager.fragments.dialogs.UpdateGoodDialog;
import com.google.android.material.appbar.AppBarLayout;

import java.util.LinkedList;
import java.util.List;

public class GoodsFragment extends Fragment implements
        CreateGoodDialog.CreateGoodDialogListener,
        UpdateGoodDialog.UpdateGoodDialogListener,
        DeleteDialog.DeleteDialogListener {

    private FragmentGoodsBinding mBinding;
    private AppBarLayout mAppBarLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_goods,
                container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAppBarLayout = mBinding.appBarLayout;
        mAppBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) ->
                mIsExpanded = verticalOffset == 0);

        RecyclerView recyclerView = mBinding.goodsList;

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(new GoodsFragment.Adapter(testData()));

        mBinding.fab.setOnClickListener(v -> {
            CreateGoodDialog dialog = new CreateGoodDialog();
            dialog.show(GoodsFragment.this.getChildFragmentManager(), CREATE_GOOD_DIALOG_TAG);
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_search).setVisible(true);
        menu.findItem(R.id.action_logout).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            mAppBarLayout.setExpanded(!mIsExpanded);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void createGoodData(String name, String group, String description, String producer, int amount, int price) {
        String message;

        if (name.isEmpty() || group.isEmpty() || description.isEmpty())
            message = "Name, group, description or producer cannot be empty";
        else if (amount == -1 || price == -1)
            message = "Amount or price cannot be empty";
        else
            message = new GoodEntity(name, group, description, producer, amount, price).toString();

        Toast.makeText(requireContext(), "Create: " + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateGoodData(String name, String group, String description, String producer, int amount, int price) {
        String message;

        if (name.isEmpty() || group.isEmpty() || description.isEmpty())
            message = "Name, group, description or producer cannot be empty";
        else if (amount == -1 || price == -1)
            message = "Amount or price cannot be empty";
        else
            message = new GoodEntity(name, group, description, producer, amount, price).toString();

        Toast.makeText(requireContext(), "Update: " + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void delete(String id) {
        Toast.makeText(requireContext(), "Delete good: " + id, Toast.LENGTH_SHORT).show();
    }

    class Adapter extends RecyclerView.Adapter<Adapter.GoodViewHolder> {

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

        public class GoodViewHolder extends RecyclerView.ViewHolder implements
                View.OnClickListener, View.OnLongClickListener {

            private final ItemGoodBinding mBinding;
            private GoodEntity mGoodEntity;

            public GoodViewHolder(ItemGoodBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
                binding.getRoot().setOnClickListener(this);
                binding.getRoot().setOnLongClickListener(this);
            }

            public void bind(GoodEntity goodEntity) {
                mGoodEntity = goodEntity;
                mBinding.setGood(goodEntity);
                mBinding.executePendingBindings();
            }

            @Override
            public void onClick(View v) {
                // TODO pass id
                NavDirections action = GoodsFragmentDirections
                        .actionGoodsFragmentToGoodFragment("fake id");
                Navigation.findNavController(v).navigate(action);
            }

            @Override
            public boolean onLongClick(View v) {
                PopupMenu popup = new PopupMenu(requireContext(), v);

                popup.inflate(R.menu.menu_list_item_popup);

                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.action_edit) {
                        UpdateGoodDialog dialog = new UpdateGoodDialog(mGoodEntity);
                        dialog.show(GoodsFragment.this.getChildFragmentManager(), CREATE_GOOD_DIALOG_TAG);
                    } else {
                        DeleteDialog dialog = new DeleteDialog(mGoodEntity.getName(), "Delete Good");
                        dialog.show(GoodsFragment.this.getChildFragmentManager(), DELETE_GOOD_DIALOG_TAG);
                    }

                    return true;
                });

                popup.show();

                return true;
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

    private boolean mIsExpanded = true;

    private static final String CREATE_GOOD_DIALOG_TAG = "CREATE_GOOD_DIALOG";
    private static final String DELETE_GOOD_DIALOG_TAG = "DELETE_GOOD_DIALOG";
}
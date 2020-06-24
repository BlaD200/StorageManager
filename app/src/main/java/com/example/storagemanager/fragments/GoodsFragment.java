package com.example.storagemanager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storagemanager.R;
import com.example.storagemanager.Utils;
import com.example.storagemanager.databinding.FragmentGoodsBinding;
import com.example.storagemanager.databinding.ItemGoodBinding;
import com.example.storagemanager.entities.GoodEntity;
import com.example.storagemanager.entities.GroupEntity;
import com.example.storagemanager.exceptions.EntityException;
import com.example.storagemanager.fragments.dialogs.ChangeAmountDialog;
import com.example.storagemanager.fragments.dialogs.DeleteDialog;
import com.example.storagemanager.fragments.dialogs.GoodDialog;
import com.example.storagemanager.viewmodels.GoodsViewModel;
import com.google.android.material.appbar.AppBarLayout;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class GoodsFragment extends Fragment implements
        GoodDialog.CreateGoodListener,
        GoodDialog.UpdateGoodListener,
        ChangeAmountDialog.ChangeAmountListener,
        DeleteDialog.DeleteListener<GoodEntity> {

    private FragmentGoodsBinding mBinding;
    private GoodsViewModel mViewModel;
    private AppBarLayout mAppBarLayout;
    private Adapter mAdapter;

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

        mViewModel = new ViewModelProvider(this)
                .get(GoodsViewModel.class);

        mAppBarLayout = mBinding.appBarLayout;
        mAppBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) ->
                mIsExpanded = verticalOffset == 0);

        RecyclerView recyclerView = mBinding.goodsList;
        mAdapter = new Adapter(new LinkedList<>());

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(mAdapter);

        mBinding.fab.setOnClickListener(v -> {
            GoodDialog dialog = new GoodDialog((GoodDialog.CreateGoodListener) this, null);
            dialog.show(GoodsFragment.this.getChildFragmentManager(), GOOD_DIALOG_TAG);
        });

        setupFilter();

        mBinding.searchArea.btnSearch.callOnClick();
    }

    private void setupFilter() {
        EditText editQuery = mBinding.searchArea.editQuery;
        Spinner spinnerGroup = mBinding.searchArea.spinnerGroup;
        Spinner spinnerProducer = mBinding.searchArea.spinnerProducer;
        EditText editAmountMin = mBinding.searchArea.editAmountMin;
        EditText editAmountMax = mBinding.searchArea.editAmountMax;
        EditText editPriceMin = mBinding.searchArea.editPriceMin;
        EditText editPriceMax = mBinding.searchArea.editPriceMax;

        List<String> groups = mViewModel.getGroups()
                .stream()
                .map(GroupEntity::getName)
                .collect(Collectors.toList());
        List<String> producers = mViewModel.getProducers();

        spinnerGroup.setAdapter(
                new ArrayAdapter<>(requireContext(), R.layout.item_spinner, groups));
        spinnerProducer.setAdapter(
                new ArrayAdapter<>(requireContext(), R.layout.item_spinner, producers));

        if (getArguments() != null && GoodsFragmentArgs
                .fromBundle(getArguments()).getGroupName() != null) {
            String groupName = GoodsFragmentArgs.fromBundle(getArguments()).getGroupName();

            for (int i = 0; i < groups.size(); i++)
                if (groups.get(i).equals(groupName))
                    mBinding.searchArea.spinnerGroup.setSelection(i);
        }

        mBinding.searchArea.btnSearch.setOnClickListener(v -> {
            String query = editQuery.getText().toString();
            String group = spinnerGroup.getSelectedItem().toString();
            String producer = spinnerProducer.getSelectedItem().toString();
            Integer amountMin = Utils.getIntOrNull(editAmountMin);
            Integer amountMax = Utils.getIntOrNull(editAmountMax);
            Integer priceMin = Utils.getIntOrNull(editPriceMin);
            Integer priceMax = Utils.getIntOrNull(editPriceMax);

            List<GoodEntity> goodEntities = mViewModel.getGoods(query, group, producer,
                    amountMin, amountMax, priceMin, priceMax);

            mAdapter.setData(goodEntities);
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
    public void createGoodData(String name, String group, String description,
                               String producer, Integer amount, Integer price) {
        try {
            GoodEntity goodEntity = new GoodEntity(name, group, description, producer, amount, price);
            mViewModel.createGood(goodEntity);
        } catch (EntityException e) {
            Toast.makeText(requireContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void updateGoodData(String name, String group, String description,
                               String producer, Integer amount, Integer price) {
        try {
            GoodEntity goodEntity = new GoodEntity(name, group, description, producer, amount, price);
            mViewModel.updateGood(goodEntity);
        } catch (EntityException e) {
            Toast.makeText(requireContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void delete(GoodEntity goodEntity) {
        mViewModel.deleteGood(goodEntity);
    }

    @Override
    public void addAmount(GoodEntity goodEntity, int amount) {
        mViewModel.changeAmount(goodEntity, amount);
    }

    @Override
    public void removeAmount(GoodEntity goodEntity, int amount) {
        mViewModel.changeAmount(goodEntity, -amount);
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

        public void setData(List<GoodEntity> data) {
            mData = data;
            notifyDataSetChanged();
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

                binding.btnPlus.setOnClickListener(v -> {
                    ChangeAmountDialog dialog = new ChangeAmountDialog(mGoodEntity, true);
                    dialog.show(GoodsFragment.this.getChildFragmentManager(), CHANGE_AMOUNT_DIALOG_TAG);
                });

                binding.btnMinus.setOnClickListener(v -> {
                    ChangeAmountDialog dialog = new ChangeAmountDialog(mGoodEntity, false);
                    dialog.show(GoodsFragment.this.getChildFragmentManager(), CHANGE_AMOUNT_DIALOG_TAG);
                });
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
                        GoodDialog dialog = new GoodDialog((GoodDialog.UpdateGoodListener) GoodsFragment.this, mGoodEntity);
                        dialog.show(GoodsFragment.this.getChildFragmentManager(), GOOD_DIALOG_TAG);
                    } else {
                        DeleteDialog<GoodEntity> dialog = new DeleteDialog<>(mGoodEntity, "Delete Good");
                        dialog.show(GoodsFragment.this.getChildFragmentManager(), DELETE_GOOD_DIALOG_TAG);
                    }

                    return true;
                });

                popup.show();

                return true;
            }
        }
    }

    private boolean mIsExpanded = true;

    private static final String GOOD_DIALOG_TAG = "GOOD_DIALOG";
    private static final String DELETE_GOOD_DIALOG_TAG = "DELETE_GOOD_DIALOG";
    private static final String CHANGE_AMOUNT_DIALOG_TAG = "CHANGE_AMOUNT_DIALOG";
}
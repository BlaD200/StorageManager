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
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storagemanager.R;
import com.example.storagemanager.databinding.FragmentProducersBinding;
import com.example.storagemanager.databinding.ItemProducerBinding;
import com.example.storagemanager.fragments.dialogs.DeleteDialog;
import com.example.storagemanager.fragments.dialogs.ProducerDialog;
import com.example.storagemanager.viewmodels.ProducersViewModel;

import java.util.LinkedList;
import java.util.List;

public class ProducersFragment extends Fragment implements
        ProducerDialog.CreateProducerListener,
        ProducerDialog.UpdateProducerListener,
        DeleteDialog.DeleteListener<String> {

    private FragmentProducersBinding mBinding;
    private ProducersViewModel mViewModel;
    private Adapter mAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_producers,
                container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(this)
                .get(ProducersViewModel.class);

        RecyclerView recyclerView = mBinding.producersList;
        mAdapter = new Adapter(new LinkedList<>());

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(mAdapter);

        List<String> producers = mViewModel.getProducers(null);
        mAdapter.setData(producers);

        mBinding.fab.setOnClickListener(v -> {
            ProducerDialog dialog = new ProducerDialog((ProducerDialog.CreateProducerListener) ProducersFragment.this, null);
            dialog.show(ProducersFragment.this.getChildFragmentManager(), PRODUCER_DIALOG_TAG);
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_logout).setVisible(true);

        MenuItem actionSearchView = menu.findItem(R.id.action_search_view);
        actionSearchView.setVisible(true);

        SearchView searchView = (SearchView) actionSearchView.getActionView();
        searchView.setQueryHint(getResources().getString(R.string.producer_search_view_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                List<String> producers = mViewModel.getProducers(query);
                mAdapter.setData(producers);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void createProducerData(String producer) {
        if (!producer.isEmpty())
            mViewModel.createProducer(producer);
        else
            Toast.makeText(requireContext(), "Producer must not be empty", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateProducerData(String producerBefore, String producerAfter) {
        if (!producerAfter.isEmpty())
            mViewModel.updateProducer(producerBefore, producerAfter);
        else
            Toast.makeText(requireContext(), "Producer must not be empty", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void delete(String entity) {
        mViewModel.deleteProducer(entity);
    }

    class Adapter extends RecyclerView.Adapter<Adapter.ProducerViewHolder> {

        private List<String> mData;

        public Adapter(@NonNull List<String> data) {
            mData = data;
        }

        @NonNull
        @Override
        public ProducerViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                     int viewType) {
            ItemProducerBinding binding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.item_producer, parent, false);

            return new ProducerViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(ProducerViewHolder holder, int position) {
            String producer = mData.get(position);
            holder.bind(producer);
        }

        public void setData(List<String> data) {
            mData = data;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public class ProducerViewHolder extends RecyclerView.ViewHolder implements
                View.OnLongClickListener {

            private final ItemProducerBinding mBinding;
            private String mProducer;

            public ProducerViewHolder(ItemProducerBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
                binding.getRoot().setOnLongClickListener(this);
            }

            public void bind(String producer) {
                mProducer = producer;
                mBinding.setProducer(producer);
                mBinding.executePendingBindings();
            }

            @Override
            public boolean onLongClick(View v) {
                PopupMenu popup = new PopupMenu(requireContext(), v);

                popup.inflate(R.menu.menu_list_item_popup);

                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.action_edit) {
                        ProducerDialog dialog = new ProducerDialog((ProducerDialog.UpdateProducerListener) ProducersFragment.this, mProducer);
                        dialog.show(ProducersFragment.this.getChildFragmentManager(), PRODUCER_DIALOG_TAG);
                    } else {
                        DeleteDialog<String> dialog = new DeleteDialog<>(mProducer, "Delete Producer");
                        dialog.show(ProducersFragment.this.getChildFragmentManager(), DELETE_PRODUCER_DIALOG_TAG);
                    }

                    return true;
                });

                popup.show();

                return true;
            }
        }
    }

    public static final String PRODUCER_DIALOG_TAG = "PRODUCER_DIALOG";
    public static final String DELETE_PRODUCER_DIALOG_TAG = "DELETE_PRODUCER_DIALOG";
}
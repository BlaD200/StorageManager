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
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storagemanager.R;
import com.example.storagemanager.backend.entity.Group;
import com.example.storagemanager.databinding.FragmentGroupsBinding;
import com.example.storagemanager.databinding.ItemGroupBinding;
import com.example.storagemanager.entities.GroupEntity;
import com.example.storagemanager.exceptions.EntityException;
import com.example.storagemanager.fragments.dialogs.DeleteDialog;
import com.example.storagemanager.fragments.dialogs.GroupDialog;
import com.example.storagemanager.viewmodels.GroupsViewModel;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class GroupsFragment extends Fragment implements
        GroupDialog.CreateGroupListener,
        GroupDialog.UpdateGroupListener,
        DeleteDialog.DeleteListener<GroupEntity> {

    public static final String GROUP_DIALOG_TAG = "GROUP_DIALOG";
    public static final String DELETE_GROUP_DIALOG_TAG = "DELETE_GROUP_DIALOG";
    private FragmentGroupsBinding mBinding;
    private GroupsViewModel mViewModel;
    private Adapter mAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_groups,
                container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(this)
                .get(GroupsViewModel.class);

        RecyclerView recyclerView = mBinding.groupsList;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<GroupEntity> groupEntities = new ArrayList<>();
        mViewModel.getGroups(null)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(reply -> {
                    try {
                        List<Group> groupList = mViewModel.getMapper().readValue(reply, new TypeReference<List<Group>>() {
                        });
                        groupEntities.addAll(groupList.stream().filter(Objects::nonNull).map(GroupEntity::new).collect(Collectors.toList()));
                        recyclerView.setAdapter(new Adapter(groupEntities));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        mBinding.fab.setOnClickListener(v -> {
            GroupDialog dialog = new GroupDialog((GroupDialog.CreateGroupListener) this, null);
            dialog.show(GroupsFragment.this.getChildFragmentManager(), GROUP_DIALOG_TAG);
        });
    }

    @Override
    public void createGroupData(String name, String description) {
        try {
            GroupEntity groupEntity = new GroupEntity(name, description);
            mViewModel.createGroup(groupEntity);
        } catch (EntityException e) {
            Toast.makeText(requireContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void updateGroupData(String name, String description) {
        try {
            GroupEntity groupEntity = new GroupEntity(name, description);
            mViewModel.updateGroup(groupEntity);
        } catch (EntityException e) {
            Toast.makeText(requireContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void delete(GroupEntity groupEntity) {
        mViewModel.deleteGroup(groupEntity);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_logout).setVisible(true);

        MenuItem actionSearchView = menu.findItem(R.id.action_search_view);
        actionSearchView.setVisible(true);

        SearchView searchView = (SearchView) actionSearchView.getActionView();
        searchView.setQueryHint(getResources().getString(R.string.search_view_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                List<GroupEntity> groups = new ArrayList<>();
                mViewModel.getGroups(query)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(reply -> {
                            try {
                                List<Group> groupList = mViewModel.getMapper().readValue(reply, new TypeReference<List<Group>>() {
                                });
                                groups.addAll(groupList.stream().filter(Objects::nonNull).map(GroupEntity::new).collect(Collectors.toList()));
                                mAdapter.setData(groups);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        super.onPrepareOptionsMenu(menu);
    }

    class Adapter extends RecyclerView.Adapter<Adapter.GroupViewHolder> {

        private List<GroupEntity> mData;

        public Adapter(@NonNull List<GroupEntity> data) {
            mData = data;
        }

        @NonNull
        @Override
        public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                  int viewType) {
            ItemGroupBinding binding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.item_group, parent, false);

            return new GroupViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(GroupViewHolder holder, int position) {
            GroupEntity groupEntity = mData.get(position);
            holder.bind(groupEntity);
        }

        public void setData(List<GroupEntity> data) {
            mData = data;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public class GroupViewHolder extends RecyclerView.ViewHolder implements
                View.OnClickListener, View.OnLongClickListener {

            private final ItemGroupBinding mBinding;
            private GroupEntity mGroupEntity;

            public GroupViewHolder(ItemGroupBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
                binding.getRoot().setOnClickListener(this);
                binding.getRoot().setOnLongClickListener(this);
            }

            public void bind(GroupEntity groupEntity) {
                mGroupEntity = groupEntity;
                mBinding.setGroup(groupEntity);
                mBinding.executePendingBindings();
            }

            @Override
            public void onClick(View v) {
                NavDirections action = GroupsFragmentDirections
                        .actionGroupsFragmentToGroupFragment(mGroupEntity.getName());
                Navigation.findNavController(v).navigate(action);
            }

            @Override
            public boolean onLongClick(View v) {
                PopupMenu popup = new PopupMenu(requireContext(), v);

                popup.inflate(R.menu.menu_list_item_popup);

                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.action_edit) {
                        GroupDialog dialog = new GroupDialog((GroupDialog.UpdateGroupListener) GroupsFragment.this, mGroupEntity);
                        dialog.show(GroupsFragment.this.getChildFragmentManager(), GROUP_DIALOG_TAG);
                    } else {
                        DeleteDialog<GroupEntity> dialog = new DeleteDialog<>(mGroupEntity, "Delete Group");
                        dialog.show(GroupsFragment.this.getChildFragmentManager(), DELETE_GROUP_DIALOG_TAG);
                    }

                    return true;
                });

                popup.show();

                return true;
            }
        }
    }
}
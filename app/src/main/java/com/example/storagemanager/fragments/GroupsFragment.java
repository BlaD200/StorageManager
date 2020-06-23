package com.example.storagemanager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storagemanager.R;
import com.example.storagemanager.databinding.FragmentGroupsBinding;
import com.example.storagemanager.databinding.ItemGroupBinding;
import com.example.storagemanager.entities.GroupEntity;
import com.example.storagemanager.fragments.dialogs.CreateGroupDialog;
import com.example.storagemanager.fragments.dialogs.DeleteDialog;
import com.example.storagemanager.fragments.dialogs.UpdateGroupDialog;

import java.util.LinkedList;
import java.util.List;

public class GroupsFragment extends Fragment implements
        CreateGroupDialog.CreateGroupDialogListener,
        UpdateGroupDialog.UpdateGroupDialogListener,
        DeleteDialog.DeleteDialogListener {

    private FragmentGroupsBinding mBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_groups,
                container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = mBinding.groupsList;

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(new Adapter(testData()));

        mBinding.fab.setOnClickListener(v -> {
            CreateGroupDialog dialog = new CreateGroupDialog();
            dialog.show(GroupsFragment.this.getChildFragmentManager(), CREATE_GROUP_DIALOG_TAG);
        });
    }

    @Override
    public void createGroupData(String name, String description) {
        String message;

        if (name.isEmpty() || description.isEmpty())
            message = "Name or description cannot be empty";
        else
            message = new GroupEntity(name, description).toString();

        Toast.makeText(requireContext(), "Create: " + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateGroupData(String name, String description) {
        String message;

        if (name.isEmpty() || description.isEmpty())
            message = "Name or description cannot be empty";
        else
            message = new GroupEntity(name, description).toString();

        Toast.makeText(requireContext(), "Update: " + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void delete(String id) {
        Toast.makeText(requireContext(), "Delete Group: " + id, Toast.LENGTH_SHORT).show();
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
                // TODO navigation
            }

            @Override
            public boolean onLongClick(View v) {
                PopupMenu popup = new PopupMenu(requireContext(), v);

                popup.inflate(R.menu.menu_list_item_popup);

                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.action_edit) {
                        UpdateGroupDialog dialog = new UpdateGroupDialog(mGroupEntity);
                        dialog.show(GroupsFragment.this.getChildFragmentManager(), CREATE_GROUP_DIALOG_TAG);
                    } else {
                        DeleteDialog dialog = new DeleteDialog(mGroupEntity.getName(), "Delete Group");
                        dialog.show(GroupsFragment.this.getChildFragmentManager(), DELETE_GROUP_DIALOG_TAG);
                    }

                    return true;
                });

                popup.show();

                return true;
            }
        }
    }

    private List<GroupEntity> testData() {
        List<GroupEntity> groupEntities = new LinkedList<>();

        groupEntities.add(new GroupEntity("First Ever Created Group",
                "And its awesome description!"));
        groupEntities.add(new GroupEntity("Second Ever Created Group",
                "And its awesome description"));
        groupEntities.add(new GroupEntity("Third Ever Created Group",
                "And its awesome description.."));
        groupEntities.add(new GroupEntity("Another created group",
                "And yet again another description"));
        groupEntities.add(new GroupEntity("Group Im getting tired of it",
                "Really tired description"));

        return groupEntities;
    }

    public static final String CREATE_GROUP_DIALOG_TAG = "CREATE_GROUP_DIALOG";
    public static final String DELETE_GROUP_DIALOG_TAG = "DELETE_GROUP_DIALOG";
}
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
import com.example.storagemanager.databinding.FragmentGroupsBinding;
import com.example.storagemanager.databinding.ItemGroupBinding;
import com.example.storagemanager.entities.GroupEntity;
import com.example.storagemanager.fragments.dialogs.CreateGroupDialog;

import java.util.LinkedList;
import java.util.List;

public class GroupsFragment extends Fragment
        implements CreateGroupDialog.CreateGroupDialogListener {

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
    public void onCreate(String name, String description) {
        if (name.isEmpty() || description.isEmpty())
            Toast.makeText(requireContext(),
                    "Name or description cannot be empty",
                    Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(requireContext(),
                    new GroupEntity(name, description).toString(),
                    Toast.LENGTH_SHORT).show();
    }


    static class Adapter extends RecyclerView.Adapter<Adapter.GroupViewHolder> {

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
            GroupEntity challenge = mData.get(position);
            holder.bind(challenge);
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public static class GroupViewHolder extends RecyclerView.ViewHolder {

            private final ItemGroupBinding mBinding;

            public GroupViewHolder(ItemGroupBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }

            public void bind(GroupEntity groupEntity) {
                mBinding.setGroup(groupEntity);
                mBinding.executePendingBindings();
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
}
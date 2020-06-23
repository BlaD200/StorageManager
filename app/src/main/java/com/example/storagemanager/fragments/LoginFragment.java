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
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.storagemanager.R;
import com.example.storagemanager.databinding.FragmentLoginBinding;
import com.example.storagemanager.entities.LoginEntity;
import com.example.storagemanager.viewmodels.LoginViewModel;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding mBinding;
    private LoginViewModel mLoginViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login,
                container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLoginViewModel = new ViewModelProvider(requireActivity())
                .get(LoginViewModel.class);

        mBinding.btnLogin.setOnClickListener(v -> {
            String login = mBinding.editLogin.getText().toString();
            String password = mBinding.editPassword.getText().toString();

            LoginEntity loginEntity = new LoginEntity(login, password);

            if (mLoginViewModel.authenticate(loginEntity)) {
                mLoginViewModel.save(loginEntity);

                NavDirections action = LoginFragmentDirections.actionLoginFragmentToGoodsFragment();
                Navigation.findNavController(view).navigate(action);
            } else {
                Snackbar.make(requireView(), "Wrong Login or Password",
                        BaseTransientBottomBar.LENGTH_LONG).show();
            }
        });
    }
}
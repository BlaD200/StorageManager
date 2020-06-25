package com.example.storagemanager.fragments;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.storagemanager.R;
import com.example.storagemanager.backend.client.StoreClientTCP;
import com.example.storagemanager.backend.cryptography.AsymmetricCryptography;
import com.example.storagemanager.backend.cryptography.SymmetricCryptography;
import com.example.storagemanager.databinding.FragmentLoginBinding;
import com.example.storagemanager.entities.LoginEntity;
import com.example.storagemanager.viewmodels.LoginViewModel;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

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

            mLoginViewModel.authenticate(loginEntity)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(isLoggedIn -> {
                        if (Boolean.parseBoolean(isLoggedIn)) {
                            mLoginViewModel.save(loginEntity);

                            NavDirections action = LoginFragmentDirections.actionLoginFragmentToGoodsFragment();
                            Navigation.findNavController(view).navigate(action);
                        } else {
                            Snackbar.make(requireView(), "Wrong Login or Password",
                                    BaseTransientBottomBar.LENGTH_LONG).show();
                        }
                    });
        });
    }
}
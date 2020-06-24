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
import com.example.storagemanager.databinding.FragmentLoginBinding;
import com.example.storagemanager.entities.LoginEntity;
import com.example.storagemanager.viewmodels.LoginViewModel;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

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

        UserLoginTask mAuthTask = new UserLoginTask();
        mAuthTask.execute();
    }

    // ASYNCRONUS NETWORK PROCESS

    public class UserLoginTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
        }


        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected String doInBackground(String... params) {
            StoreClientTCP storeClientTCP = new StoreClientTCP();
            int connectionAttempts = 10;
            while (connectionAttempts >= 0) {
                try {
                    if (storeClientTCP.connect())
                        try {
                            connectionAttempts = 10;
                            storeClientTCP.conversation();
                        } catch (IOException e) {
                            System.err.println("CONNECTION LOST. TRYING RECONECT.");
                        }
                } catch (IOException e) {
                    System.err.println("COULD NOT ESTABLISH CONNECTION(" + (10 - connectionAttempts) + ").");
                    System.err.println(e.getMessage());
                } finally {
                    storeClientTCP.disconnect();
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignored) {
                }
                --connectionAttempts;
            }
            System.err.println("Closing org.vsynytsyn.client.");
            return "";
        }

        protected void onPostExecute(String success) {
            Log.i(success, "");
            //attemptLogin();
        }
    }
}
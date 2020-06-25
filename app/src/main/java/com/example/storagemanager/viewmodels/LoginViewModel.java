package com.example.storagemanager.viewmodels;

import android.content.SharedPreferences;

import com.example.storagemanager.backend.dto.UserDTO;
import com.example.storagemanager.backend.entity.CommandType;
import com.example.storagemanager.entities.LoginEntity;

import org.apache.commons.codec.digest.DigestUtils;

import io.reactivex.rxjava3.core.Observable;

public class LoginViewModel extends BaseViewModel {

    private final SharedPreferences mPreferences;
    private LoginEntity mLoginEntity;

    public LoginViewModel(SharedPreferences preferences) {
        mPreferences = preferences;
    }

    public @io.reactivex.rxjava3.annotations.NonNull Observable<String> authenticate(LoginEntity loginEntity) {
        return Observable.defer(() -> {
            String reply = clientConnection.conversation(
                    CommandType.LOGIN,
                    mapper.writeValueAsString(
                            UserDTO.builder()
                                    .login(loginEntity.getLogin())
                                    .build())
            ).getMessageText();
            try {
                UserDTO user = mapper.readValue(reply, UserDTO.class);
                if (user.getPassword().equals(DigestUtils.md5Hex(loginEntity.getPassword())))
                    return Observable.just(Boolean.toString(true));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Observable.just(Boolean.toString(false));
        });
    }

    public boolean isAuthenticated() {
        if (mLoginEntity == null) {
            String login = mPreferences.getString(LOGIN, null);
            String password = mPreferences.getString(PASSWORD, null);

            if (login == null || password == null)
                return false;

            mLoginEntity = new LoginEntity(login, password);
        }

        return true;
    }

    public void save(LoginEntity loginEntity) {
        SharedPreferences.Editor editor = mPreferences.edit();

        editor.putString(LOGIN, loginEntity.getLogin());
        editor.putString(PASSWORD, loginEntity.getPassword());

        editor.apply();
    }

    public void logout() {
        mPreferences.edit().clear().apply();
        mLoginEntity = null;
    }

    private static final String LOGIN = "LOGIN";
    private static final String PASSWORD = "PASSWORD";
}

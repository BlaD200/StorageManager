package com.example.storagemanager.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class UserCredential {
    @NonNull
    private String login;
    @NonNull
    private String password;
}

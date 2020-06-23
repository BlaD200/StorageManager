package com.example.storagemanager.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginEntity {
    private String login;
    private String password;
}

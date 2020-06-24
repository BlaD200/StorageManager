package com.example.storagemanager;

import android.widget.EditText;

public class Utils {

    public static Integer getIntOrNull(EditText editText) {
        try {
            return Integer.parseInt(editText.getText().toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

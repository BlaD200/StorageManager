package com.example.storagemanager.viewmodels;

import com.example.storagemanager.backend.dto.GoodDTO;
import com.example.storagemanager.backend.entity.CommandType;

import io.reactivex.rxjava3.core.Observable;
import lombok.SneakyThrows;

public class GoodViewModel extends BaseViewModel {

    public @io.reactivex.rxjava3.annotations.NonNull Observable<String> getGoodByName(String name) {
        return Observable.defer(() -> {
            String reply = clientConnection.conversation(
                    CommandType.GET_GOOD,
                    mapper.writeValueAsString(
                            GoodDTO.builder()
                                    .name(name)
                                    .build())
            ).getMessageText();
            return Observable.just(reply);
        });
    }
}

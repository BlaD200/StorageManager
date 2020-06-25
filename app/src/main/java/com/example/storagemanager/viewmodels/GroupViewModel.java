package com.example.storagemanager.viewmodels;

import com.example.storagemanager.backend.dto.GroupDTO;
import com.example.storagemanager.backend.entity.CommandType;
import com.example.storagemanager.entities.GroupEntity;

import io.reactivex.rxjava3.core.Observable;
import lombok.SneakyThrows;

public class GroupViewModel extends BaseViewModel {

    @SneakyThrows
    public @io.reactivex.rxjava3.annotations.NonNull Observable<String> getGroupByName(String name) {
        return Observable.defer(() -> {
            String reply = clientConnection.conversation(
                    CommandType.GET_GROUP,
                    mapper.writeValueAsString(
                            GroupDTO.builder()
                                    .name(name)
                                    .build())
            ).getMessageText();
            return Observable.just(reply);
        });
    }

    public @io.reactivex.rxjava3.annotations.NonNull Observable<String> getGroupTotalPrice(GroupEntity groupEntity) {
        return Observable.defer(() -> {
            String reply = clientConnection.conversation(
                    CommandType.GET_GROUP_TOTAL,
                    mapper.writeValueAsString(
                            GroupDTO.builder()
                                    .name(groupEntity.getName())
                                    .build())
            ).getMessageText();
            return Observable.just(reply);
        });
    }
}

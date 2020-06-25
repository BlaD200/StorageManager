package com.example.storagemanager.viewmodels;

import com.example.storagemanager.backend.dto.GroupDTO;
import com.example.storagemanager.backend.dto.PagingDTO;
import com.example.storagemanager.backend.dto.criteria.Criteria;
import com.example.storagemanager.backend.entity.CommandType;
import com.example.storagemanager.entities.GroupEntity;

import io.reactivex.rxjava3.core.Observable;

public class GroupsViewModel extends BaseViewModel {

    public @io.reactivex.rxjava3.annotations.NonNull Observable<String> getGroups(String query) {
        return Observable.defer(() -> {
            String reply = clientConnection.conversation(
                    CommandType.GET_GROUPS,
                    mapper.writeValueAsString(
                            PagingDTO.builder()
                            .size(-1)
                            .criteria(Criteria.builder().name(query).build())
                            .build()
                    )
            ).getMessageText();
            return Observable.just(reply);
        });
    }

    public @io.reactivex.rxjava3.annotations.NonNull Observable<String> createGroup(GroupEntity groupEntity) {
        return Observable.defer(() -> {
            String reply = clientConnection.conversation(
                    CommandType.CREATE_GROUP,
                    mapper.writeValueAsString(
                            GroupDTO.builder()
                                    .name(groupEntity.getName())
                                    .description(groupEntity.getDescription())
                                    .build())
            ).getMessageText();
            return Observable.just(reply);
        });
    }

    public @io.reactivex.rxjava3.annotations.NonNull Observable<String> updateGroup(GroupEntity groupEntity) {
        // TODO update group
//        return Observable.defer(() -> {
//            String reply1 = clientConnection.conversation(
//                    CommandType.,
//                    mapper.writeValueAsString(
//                            GroupDTO.builder()
//                                    .name(groupEntity.getName())
//                                    .description(groupEntity.getDescription())
//                                    .build())
//            ).getMessageText();
//
//
//            return Observable.just(reply1);
//        });
        return null;
    }

    public @io.reactivex.rxjava3.annotations.NonNull Observable<String> deleteGroup(GroupEntity groupEntity) {
        return Observable.defer(() -> {
            String reply = clientConnection.conversation(
                    CommandType.DELETE_GROUP,
                    mapper.writeValueAsString(
                            GroupDTO.builder()
                                    .name(groupEntity.getName())
                                    .build())
            ).getMessageText();
            return Observable.just(reply);
        });
    }
}

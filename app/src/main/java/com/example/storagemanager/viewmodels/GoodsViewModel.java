package com.example.storagemanager.viewmodels;

import com.example.storagemanager.backend.dto.GoodDTO;
import com.example.storagemanager.backend.dto.PagingDTO;
import com.example.storagemanager.backend.dto.criteria.Criteria;
import com.example.storagemanager.backend.entity.CommandType;
import com.example.storagemanager.entities.GoodEntity;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import lombok.SneakyThrows;

public class GoodsViewModel extends BaseViewModel {

    @SneakyThrows
    public @NonNull Observable<String> getGoods(String query, String groupName, String producer,
                                                Integer amountMin, Integer amountMax,
                                                Integer priceMin, Integer priceMax) {
        return Observable.defer(() -> {
            String reply = clientConnection.conversation(
                    CommandType.GET_GOODS,
                    mapper.writeValueAsString(
                            PagingDTO.builder()
                                    .size(1000)
                                    .criteria(new Criteria(
                                            query, producer,
                                            amountMin, amountMax,
                                            priceMin, priceMax,
                                            groupName))
                                    .build())
            ).getMessageText();
            return Observable.just(reply);
        });
    }

    @SneakyThrows
    public @NonNull Observable<String> getGroups() {
        return Observable.defer(() -> {
            String reply = clientConnection.conversation(
                    CommandType.GET_GROUPS,
                    mapper.writeValueAsString(
                            PagingDTO.builder()
                                    .size(-1)
                                    .build())
            ).getMessageText();
            return Observable.just(reply);
        });
    }

    public @NonNull Observable<String> getProducers() {
        return Observable.defer(() -> {
            String reply = clientConnection.conversation(
                    CommandType.GET_PRODUCERS,
                    mapper.writeValueAsString(
                            PagingDTO.builder()
                                    .size(-1)
                                    .build())
            ).getMessageText();
            return Observable.just(reply);
        });
    }

    public @NonNull Observable<String> changeAmount(GoodEntity goodEntity, int amount) {
        return Observable.defer(() -> {
            String reply = clientConnection.conversation(
                    amount > 0 ? CommandType.PUT_GOOD_AMOUNT : CommandType.RETIRE_GOOD_AMOUNT,
                    mapper.writeValueAsString(
                            GoodDTO.builder()
                                    .name(goodEntity.getName())
                                    .amount(amount)
                                    .build())
            ).getMessageText();
            return Observable.just(reply);
        });
    }

    public @NonNull Observable<String> createGood(GoodEntity goodEntity) {
        return Observable.defer(() -> {
            String reply = clientConnection.conversation(
                    CommandType.CREATE_GOOD,
                    mapper.writeValueAsString(
                            GoodDTO.builder()
                                    .name(goodEntity.getName())
                                    .amount(goodEntity.getAmount())
                                    .price(Double.valueOf(goodEntity.getPrice()))
                                    .description(goodEntity.getDescription())
                                    .producer(goodEntity.getProducer())
                                    .group(goodEntity.getGroup())
                                    .build())
            ).getMessageText();
            return Observable.just(reply);
        });
    }

    public @NonNull Observable<String> updateGood(GoodEntity goodEntity) {
        return Observable.defer(() -> {
            String reply1 = clientConnection.conversation(
                    CommandType.SET_GOOD_PRICE,
                    mapper.writeValueAsString(
                            GoodDTO.builder()
                                    .name(goodEntity.getName())
                                    .price(Double.valueOf(goodEntity.getPrice()))
                                    .build())
            ).getMessageText();

            return Observable.just(reply1);
        });
    }

    public @NonNull Observable<String> deleteGood(GoodEntity goodEntity) {
        return Observable.defer(() -> {
            String reply = clientConnection.conversation(
                    CommandType.DELETE_GOOD,
                    mapper.writeValueAsString(
                            GoodDTO.builder()
                                    .name(goodEntity.getName())
                                    .build())
            ).getMessageText();
            return Observable.just(reply);
        });
    }
}

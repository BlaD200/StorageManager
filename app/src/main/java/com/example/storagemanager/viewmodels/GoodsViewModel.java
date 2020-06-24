package com.example.storagemanager.viewmodels;

import androidx.lifecycle.ViewModel;

import com.example.storagemanager.entities.GoodEntity;
import com.example.storagemanager.entities.GroupEntity;

import java.util.LinkedList;
import java.util.List;

import lombok.SneakyThrows;

public class GoodsViewModel extends ViewModel {

    @SneakyThrows
    public List<GoodEntity> getGoods(String query, String groupName, String producer,
                                     Integer amountMin, Integer amountMax,
                                     Integer priceMin, Integer priceMax) {
        // TODO get goods
        List<GoodEntity> goodEntities = new LinkedList<>();

        goodEntities.add(new GoodEntity("Goody good 0", "Good",
                "Good Description", "Good Company", 1000, 1000));
        goodEntities.add(new GoodEntity("Goody good 1", "Good",
                "Good Description", "Good Company", 1000, 1000));
        goodEntities.add(new GoodEntity("Goody good 2", "Good",
                "Good Description", "Good Company", 1000, 1000));
        goodEntities.add(new GoodEntity("Goody good 3", "Good",
                "Good Description", "Good Company", 1000, 1000));
        goodEntities.add(new GoodEntity("Goody good 4", "Good",
                "Good Description", "Good Company", 1000, 1000));
        goodEntities.add(new GoodEntity("Goody good 5", "Good",
                "Good Description", "Good Company", 1000, 1000));
        goodEntities.add(new GoodEntity("Bady good 0", "Bad",
                "Bad Description", "Bad Company", 10, 100));
        goodEntities.add(new GoodEntity("Bady good 1", "Bad",
                "Bad Description", "Bad Company", 10, 100));
        goodEntities.add(new GoodEntity("Bady good 2", "Bad",
                "Bad Description", "Bad Company", 10, 100));
        goodEntities.add(new GoodEntity("Bady good 3", "Bad",
                "Bad Description", "Bad Company", 10, 100));
        goodEntities.add(new GoodEntity("Bady good 4", "Bad",
                "Bad Description", "Bad Company", 10, 100));
        goodEntities.add(new GoodEntity("Bady good 5", "Bad",
                "Bad Description", "Bad Company", 10, 100));

        return goodEntities;
    }

    public List<GroupEntity> getGroups() {
        // TODO get groups
        List<GroupEntity> groupEntities = new LinkedList<>();

        groupEntities.add(new GroupEntity("Group A", "Group A"));
        groupEntities.add(new GroupEntity("Group B", "Group B"));
        groupEntities.add(new GroupEntity("Group C", "Group C"));

        return groupEntities;
    }

    public List<String> getProducers() {
        // TODO get producers
        List<String> groupEntities = new LinkedList<>();

        groupEntities.add("Producer A");
        groupEntities.add("Producer B");
        groupEntities.add("Producer C");

        return groupEntities;
    }

    public void changeAmount(GoodEntity goodEntity, int amount) {
        // TODO change amount
    }

    public void createGood(GoodEntity goodEntity) {
        // TODO create good
    }

    public void updateGood(GoodEntity goodEntity) {
        // TODO update good
    }

    public void deleteGood(GoodEntity goodEntity) {
        // TODO delete good
    }
}

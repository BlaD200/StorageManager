package com.example.storagemanager.viewmodels;

import androidx.lifecycle.ViewModel;

import java.util.LinkedList;
import java.util.List;

public class ProducersViewModel extends ViewModel {

    public List<String> getProducers(String query) {
        // TODO get producers
        List<String> producers = new LinkedList<>();

        producers.add("Producer 1");
        producers.add("Producer 2");
        producers.add("Producer 3");

        return producers;
    }


    public void createProducer(String producer) {
        // TODO create producer
    }

    public void updateProducer(String producerBefore, String producerAfter) {
        // TODO update producer
    }

    public void deleteProducer(String entity) {
        // TODO delete producer
    }
}

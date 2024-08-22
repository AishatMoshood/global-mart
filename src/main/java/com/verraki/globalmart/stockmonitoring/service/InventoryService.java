package com.verraki.globalmart.stockmonitoring.service;

import com.verraki.globalmart.stockmonitoring.entity.Inventory;

import java.util.concurrent.CompletableFuture;


public interface InventoryService {

    void monitorAndReorder();

    CompletableFuture<String> processInventory(Inventory inventory);

    String triggerReorder(Inventory inventory);

    int calculateReorderThreshold(Inventory inventory);

    void processOrder(int reorderQuantity, Inventory inventory);
}

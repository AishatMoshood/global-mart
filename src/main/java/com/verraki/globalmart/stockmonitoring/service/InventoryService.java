package com.verraki.globalmart.stockmonitoring.service;

import com.verraki.globalmart.stockmonitoring.entity.Inventory;


public interface InventoryService {

    void monitorAndReorder();

    void processInventory(Inventory inventory);

    int calculateReorderThreshold(Inventory inventory);

    void triggerReorder(Inventory inventory);

}

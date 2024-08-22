package com.verraki.globalmart.stockmonitoring.config;

import com.verraki.globalmart.stockmonitoring.service.InventoryService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    private final InventoryService inventoryService;

    public SchedulerConfig(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Scheduled(fixedRate = 60000) //every minute
    public void scheduleInventoryCheck() {
        inventoryService.monitorAndReorder();
    }
}

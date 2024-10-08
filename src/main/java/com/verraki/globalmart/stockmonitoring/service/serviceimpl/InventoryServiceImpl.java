package com.verraki.globalmart.stockmonitoring.service.serviceimpl;

import com.verraki.globalmart.stockmonitoring.entity.Inventory;
import com.verraki.globalmart.stockmonitoring.entity.Supplier;
import com.verraki.globalmart.stockmonitoring.repository.InventoryRepository;
import com.verraki.globalmart.stockmonitoring.service.EncryptionService;
import com.verraki.globalmart.stockmonitoring.service.InventoryService;
import com.verraki.globalmart.stockmonitoring.service.SupplierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    private final SupplierService supplierService;

    private final EncryptionService encryptionService;

    private final RedisTemplate<String, Object> redisTemplate;

    private final KafkaTemplate<String, String> kafkaTemplate;

    public InventoryServiceImpl(InventoryRepository inventoryRepository,
                                SupplierService supplierService,
                                EncryptionService encryptionService,
                                RedisTemplate<String, Object> redisTemplate,
                                KafkaTemplate<String, String> kafkaTemplate) {
        this.inventoryRepository = inventoryRepository;
        this.supplierService = supplierService;
        this.encryptionService = encryptionService;
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }


    @Override
    public void monitorAndReorder() {
        try {
            log.info("Starting inventory monitoring process...");
            List<Inventory> inventories = inventoryRepository.findAll();
            inventories.parallelStream().forEach(this::processInventory);
            log.info("Inventory monitoring process completed.");
        } catch (Exception e) {
            log.error("Error monitoring and reordering inventory", e);
        }
    }


    @Override
    public CompletableFuture<String> processInventory(Inventory inventory) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (inventory.getStockLevel() < calculateReorderThreshold(inventory)) {
                    return triggerReorder(inventory); // Trigger reorder and wait for result
                }
                return "No reorder needed";
            } catch (Exception e) {
                log.error("Error processing inventory for product: {}", inventory.getProduct().getName(), e);
                return "Failed to process inventory";
            }
        });
    }


    @Override
    public String triggerReorder(Inventory inventory) {
        if(inventory == null){
            log.warn("No inventory provided {}", inventory);
            return "No inventory provided";
        }

        try {
            Supplier supplier = supplierService.findAvailableSupplier(inventory.getProduct(), inventory.getLeadTime());
            if (supplier == null) {
                log.warn("No available supplier found for product: {} with required lead time: {} days",
                        inventory.getProduct().getName(), inventory.getLeadTime());
                return "No supplier provided.Please provide one";
            } else {
                //use redis caching
                String cacheKey = "product_" + inventory.getProduct().getId();
                Integer cachedQuantity = (Integer) redisTemplate.opsForValue().get(cacheKey);
                int reorderQuantity = cachedQuantity != null ? cachedQuantity : calculateReorderQuantity(inventory);
                redisTemplate.opsForValue().set(cacheKey, reorderQuantity);

                //process order
                processOrder(reorderQuantity, inventory);
            }
        } catch (Exception e) {
            log.error("Error triggering reorder for inventory", e);
            return "Error triggering reorder";
        }
        return null;
    }


    @Override
    public int calculateReorderThreshold(Inventory inventory) {
        return (int) (inventory.getReorderThreshold() * inventory.getRegionalDemandMultiplier());
    }

    @Override
    public void processOrder(int reorderQuantity, Inventory inventory){
        //process order
        String message = "Reorder " + reorderQuantity + " units of " + inventory.getProduct().getName()
                + " for region " + inventory.getWarehouse().getRegion();
        String encryptedMessage = encryptionService.encrypt(message);
        kafkaTemplate.send("reorder-topic", encryptedMessage);
        log.info("Reorder triggered for product: {} with quantity: {} for region: {}",
                inventory.getProduct().getName(), reorderQuantity, inventory.getWarehouse().getRegion());
    }


    private int calculateReorderQuantity(Inventory inventory) {
        return (int) (inventory.getReorderQuantity() * inventory.getRegionalDemandMultiplier());
    }


}
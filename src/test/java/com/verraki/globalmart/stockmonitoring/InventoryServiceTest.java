package com.verraki.globalmart.stockmonitoring;

import com.verraki.globalmart.stockmonitoring.entity.Inventory;
import com.verraki.globalmart.stockmonitoring.entity.Product;
import com.verraki.globalmart.stockmonitoring.entity.Supplier;
import com.verraki.globalmart.stockmonitoring.entity.Warehouse;
import com.verraki.globalmart.stockmonitoring.repository.InventoryRepository;
import com.verraki.globalmart.stockmonitoring.service.EncryptionService;
import com.verraki.globalmart.stockmonitoring.service.InventoryService;
import com.verraki.globalmart.stockmonitoring.service.SupplierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class InventoryServiceTest {

    @InjectMocks
    private InventoryService inventoryService;

    @Mock
    private SupplierService supplierService;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private EncryptionService encryptionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testMonitorAndReorder() {
        // Arrange
        Inventory inventory = new Inventory();
        when(inventoryRepository.findAll()).thenReturn(Arrays.asList(inventory));

        // Act
        inventoryService.monitorAndReorder();

        // Assert
        verify(inventoryRepository).findAll();
        verify(inventoryService).processInventory(any(Inventory.class));
    }

    @Test
    public void testProcessInventoryReorderNeeded() {
        // Arrange
        Inventory inventory = new Inventory();
        inventory.setStockLevel(5);
        inventory.setReorderThreshold(10);
        when(inventoryService.calculateReorderThreshold(inventory)).thenReturn(10);
        when(inventoryService.triggerReorder(inventory)).thenReturn(String.valueOf("Reorder triggered"));

        // Act
        CompletableFuture<String> result = inventoryService.processInventory(inventory);

        // Assert
        assertEquals("Reorder triggered", result.join());
    }

    @Test
    public void testProcessInventoryNoReorderNeeded() {
        // Arrange
        Inventory inventory = new Inventory();
        inventory.setStockLevel(15);
        inventory.setReorderThreshold(10);

        // Act
        CompletableFuture<String> result = inventoryService.processInventory(inventory);

        // Assert
        assertEquals("No reorder needed", result.join());
    }

    @Test
    public void testTriggerReorderNoInventory() {
        // Act
        String result = inventoryService.triggerReorder(null);

        // Assert
        assertEquals("No inventory provided", result);
    }

    @Test
    public void testTriggerReorderNoSupplier() {
        // Arrange
        Inventory inventory = new Inventory();
        when(supplierService.findAvailableSupplier(any(), anyInt())).thenReturn(null);

        // Act
        String result = inventoryService.triggerReorder(inventory);

        // Assert
        assertEquals("No supplier provided. Please provide one.", result);
    }

    @Test
    public void testTriggerReorderWithSupplier() {
        // Arrange
        Inventory inventory = new Inventory();
        Supplier supplier = new Supplier();
        when(supplierService.findAvailableSupplier(any(), anyInt())).thenReturn(supplier);
        when(redisTemplate.opsForValue().get(anyString())).thenReturn(null);
        when(encryptionService.encrypt(anyString())).thenReturn("encryptedMessage");

        // Act
        String result = inventoryService.triggerReorder(inventory);

        // Assert
        assertNull(result);
        verify(kafkaTemplate).send(eq("reorder-topic"), anyString());
    }

    @Test
    public void testCalculateReorderThreshold() {
        // Arrange
        Inventory inventory = new Inventory();
        inventory.setReorderThreshold(10);
        inventory.setRegionalDemandMultiplier(1.5);

        // Act
        int threshold = inventoryService.calculateReorderThreshold(inventory);

        // Assert
        assertEquals(15, threshold);
    }

    @Test
    public void testProcessOrder() {
        // Arrange
        Inventory inventory = new Inventory();
        inventory.setProduct(new Product(1L,"Product1"));
        List<Inventory> inventoryList = new ArrayList<>();
        inventoryList.add(inventory);
        inventory.setWarehouse(new Warehouse(1L, "Region1", inventoryList));

        when(encryptionService.encrypt(anyString())).thenReturn("encryptedMessage");

        // Act
        inventoryService.processOrder(100, inventory);

        // Assert
        verify(kafkaTemplate).send(eq("reorder-topic"), eq("encryptedMessage"));
    }
}

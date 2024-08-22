package com.verraki.globalmart.stockmonitoring.repository;

import com.verraki.globalmart.stockmonitoring.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}

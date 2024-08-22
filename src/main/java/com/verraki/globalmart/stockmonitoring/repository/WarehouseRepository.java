package com.verraki.globalmart.stockmonitoring.repository;

import com.verraki.globalmart.stockmonitoring.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
}

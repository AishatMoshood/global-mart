package com.verraki.globalmart.stockmonitoring.repository;

import com.verraki.globalmart.stockmonitoring.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    List<Supplier> findByProductAndLeadTime(Long id, int requiredLeadTime);
}


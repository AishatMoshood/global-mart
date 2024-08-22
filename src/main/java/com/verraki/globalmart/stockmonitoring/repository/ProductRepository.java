package com.verraki.globalmart.stockmonitoring.repository;

import com.verraki.globalmart.stockmonitoring.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}

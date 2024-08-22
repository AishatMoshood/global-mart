package com.verraki.globalmart.stockmonitoring.service;

import com.verraki.globalmart.stockmonitoring.entity.Product;

public interface ProductService {
    Product saveProduct(Product product);
    Product getProductById(Long id);
}


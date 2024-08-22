package com.verraki.globalmart.stockmonitoring.service;

import com.verraki.globalmart.stockmonitoring.entity.Product;
import com.verraki.globalmart.stockmonitoring.entity.Supplier;


public interface SupplierService {

    Supplier findAvailableSupplier(Product product, int requiredLeadTime);
}

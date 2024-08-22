package com.verraki.globalmart.stockmonitoring.service.serviceimpl;


import com.verraki.globalmart.stockmonitoring.entity.Product;
import com.verraki.globalmart.stockmonitoring.entity.Supplier;
import com.verraki.globalmart.stockmonitoring.repository.SupplierRepository;
import com.verraki.globalmart.stockmonitoring.service.SupplierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierServiceImpl(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }


    @Override
    public Supplier findAvailableSupplier(Product product, int requiredLeadTime) {
        try {
            List<Supplier> suppliers = supplierRepository.findByProductAndLeadTime(product.getId(), requiredLeadTime);
            return suppliers.stream().filter(Supplier::isAvailable).findFirst().orElse(null);
        } catch (Exception e) {
            log.error("Error finding available supplier for product: {}", product.getName(), e);
            return null;
        }
    }
}

package com.verraki.globalmart.stockmonitoring.service.serviceimpl;

import com.verraki.globalmart.stockmonitoring.entity.Product;
import com.verraki.globalmart.stockmonitoring.repository.ProductRepository;
import com.verraki.globalmart.stockmonitoring.service.ProductService;
import org.springframework.stereotype.Service;


@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

}

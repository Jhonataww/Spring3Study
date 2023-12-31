package com.example.springboot.services.impl;

import com.example.springboot.dtos.ProductForm;
import com.example.springboot.models.ProductModel;
import com.example.springboot.repositories.ProductRepository;
import com.example.springboot.services.ProductService;
import com.example.springboot.util.GenericSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository productRepository;

    @Override
    public ArrayList<ProductModel> productFilter(String name, BigDecimal value) {

        Specification<ProductModel> spec = (root, query, criteriaBuilder) -> {
            if (name != null) {
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
            }
            if (value != null) {
                return criteriaBuilder.equal(root.get("value"), value);
            }
            return null;
        };

        return (ArrayList<ProductModel>) productRepository.findAll(spec);
    }

    @Override
    public Page<ProductModel> productFilterPage(String name, BigDecimal value, Pageable pageable) {

        Specification<ProductModel> spec = (root, query, criteriaBuilder) -> {
            if (name != null) {
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
            }
            if (value != null) {
                return criteriaBuilder.equal(root.get("value"), value);
            }
            return null;
        };

        return productRepository.findAll(spec, pageable );
    }

    @Override
    public Page<ProductModel> findAllFilterGeneric(ProductForm filter, Pageable pageable) {
        Specification<ProductForm> spec = new GenericSpecification<>(filter);
        return productRepository.findAll(((GenericSpecification) spec), pageable);
    }

    @Override
    public ArrayList<ProductModel> findAll() {
        return (ArrayList<ProductModel>) productRepository.findAll();
    }

    @Override
    public Page<ProductModel> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public ProductModel save(ProductModel productModel) {
        return productRepository.save(productModel);
    }

    @Override
    public void delete(ProductModel productModel) {
        productRepository.delete(productModel);
    }

    @Override
    public ProductModel findById(UUID id) {
        return productRepository.findById(id).get();
    }


}

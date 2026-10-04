package dss.practicas.practica_1.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import dss.practicas.practica_1.model.Product;
import dss.practicas.practica_1.repository.ProductRepo;

@Service 
public class ProductService {
    private final ProductRepo productRepo;

    public ProductService(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    public Iterable<Product> getAllProducts() {
        return productRepo.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepo.findById(id);
    }

    public Product saveProduct(Product product) {
        return productRepo.save(product);
    }

    public void deleteProduct(Long id) {
        productRepo.deleteById(id);
    }

}
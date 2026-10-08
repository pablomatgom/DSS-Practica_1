package dss.practicas.practica_1.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import dss.practicas.practica_1.model.Product;
import dss.practicas.practica_1.repository.ProductRepo;

@Service
public class ProductService {
    private final ProductRepo productRepo;

    public ProductService(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    public List<Product> searchProducts(String name, Double minPrice, Double maxPrice, boolean inStockOnly) {
        boolean hasName = name != null && !name.isBlank();
        boolean hasPriceOrStock = minPrice != null || maxPrice != null || inStockOnly;

        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            Double swap = minPrice;
            minPrice = maxPrice;
            maxPrice = swap;
        }

        // Consulta 1: por nombre (solo si el usuario ha escrito algo)
        List<Product> byName = null;
        
        if (hasName) {
            byName = productRepo.findByNameContainingIgnoreCaseOrderByIdAsc(name.trim());
        }

        // Consulta 2: por rango de precio y stock.
        List<Product> byPriceAndStock = null;

        if (hasPriceOrStock) {
            double lowest = (minPrice == null) ? -Double.MAX_VALUE : minPrice;
            double highest = (maxPrice == null) ? Double.MAX_VALUE : maxPrice;
            int minStock = inStockOnly ? 1 : Integer.MIN_VALUE;
            byPriceAndStock = productRepo.findByPriceBetweenAndStockGreaterThanEqualOrderByIdAsc(lowest, highest, minStock);
        }

        // Combinar los resultados (Inner Join)
        if (byName != null && byPriceAndStock != null) {
            // Intersección por id
            Set<Long> idsPriceAndStock = byPriceAndStock.stream()
                .map(Product -> Product.getId())
                .collect(Collectors.toSet());
            return byName.stream()
                .filter(p -> idsPriceAndStock.contains(p.getId()))
                .toList();
        }

        if (byName != null) {
            return byName;
        }

        if (byPriceAndStock != null) {
            return byPriceAndStock;
        }

        return productRepo.findAllByOrderByIdAsc();
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
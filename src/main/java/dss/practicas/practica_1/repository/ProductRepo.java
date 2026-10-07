package dss.practicas.practica_1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dss.practicas.practica_1.model.Product;

public interface ProductRepo extends JpaRepository<Product, Long> {

    List<Product> findByNameContainingIgnoreCaseOrderByIdAsc(String name);
        // SELECT id, name, price, stock
        // FROM product
        // WHERE UPPER(name) LIKE UPPER(value1) ESCAPE '\'
        // ORDER BY id ASC;
    
    List<Product> findByPriceBetweenAndStockGreaterThanEqualOrderByIdAsc(Double minPrice, Double maxPrice, int stock);
        // SELECT id, name, price, stock
        // FROM product
        // WHERE price BETWEEN value1 AND value2
        // AND stock >= value3
        // ORDER BY id ASC;

    List<Product> findAllByOrderByIdAsc();
    
}

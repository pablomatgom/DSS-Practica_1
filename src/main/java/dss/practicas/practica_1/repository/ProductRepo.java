package dss.practicas.practica_1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dss.practicas.practica_1.model.Product;

public interface ProductRepo extends JpaRepository<Product, Long> {
    
}

package dss.practicas.practica_1.service;

import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;

import dss.practicas.practica_1.model.Product;
import dss.practicas.practica_1.repository.ProductRepo;

@Service 
public class DatabaseExportService {

    private final ProductRepo productRepo;

    public DatabaseExportService(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    public byte[] exportDatabaseToSql() {
        StringBuilder sql = new StringBuilder();
        
        for (Product product : productRepo.findAll()) {
            
            String name = product.getName() == null
                ? "NULL"
                : "'" + product.getName().replace("'", "''") + "'";

            sql.append("INSERT INTO product (id, name, price) VALUES (")
                .append(product.getId()).append(", ")
                .append(name).append(", ")
                .append(product.getPrice())
                .append(");\n");
    }

    return sql.toString().getBytes(StandardCharsets.UTF_8);
    }
}

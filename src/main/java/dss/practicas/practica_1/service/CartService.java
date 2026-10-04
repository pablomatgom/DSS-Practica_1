package dss.practicas.practica_1.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import dss.practicas.practica_1.model.Product;

@Service 
@SessionScope 
public class CartService {
    
    private final List<Product> cartItems = new ArrayList<>();

    public void addProduct(Product product) {
        cartItems.add(product);
    }

    public void removeProduct(Long productId) {
        cartItems.removeIf(p -> p.getId().equals(productId));
    }

    public List<Product> getProducts() {
        return cartItems;
    }

}

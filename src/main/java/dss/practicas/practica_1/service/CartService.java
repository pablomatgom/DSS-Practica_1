package dss.practicas.practica_1.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import dss.practicas.practica_1.model.Product;

@Service 
@SessionScope 
public class CartService {
    
    private final List<Long> cartItemsIds = new ArrayList<>();
    private final ProductService productService;

    public CartService (ProductService productService){
        this.productService = productService;
    }

    public void addProduct(Long productId) {
        cartItemsIds.add(productId);
    }

    public void removeProduct(Long productId) {
        cartItemsIds.removeIf(x -> productId.equals(x));
    }

    // Revisar luego con findAllById en caso de implementar stock de productos
    public List<Product> getProducts() {
        return cartItemsIds.stream()
                .map(id -> productService.getProductById(id))
                .flatMap(opt -> opt.stream())
                .toList();
    }

}

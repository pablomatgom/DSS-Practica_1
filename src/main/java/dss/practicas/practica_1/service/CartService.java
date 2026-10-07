package dss.practicas.practica_1.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import dss.practicas.practica_1.model.Product;

@Service
@SessionScope
public class CartService {

    private final Map<Long, Integer> quantities = new LinkedHashMap<>();
    private final ProductService productService;

    public CartService(ProductService productService) {
        this.productService = productService;
    }

    public synchronized int addProduct(Product product, int quantity) {
        int inCart = quantities.getOrDefault(product.getId(), 0);
        int available = Math.max(0, product.getStock() - inCart);
        int added = Math.min(Math.max(quantity, 0), available);
        
        if (added > 0) {
            quantities.put(product.getId(), inCart + added);
        }
        
        return added;
    }

    public synchronized int setQuantity(Long productId, int quantity) {
        if (!quantities.containsKey(productId)) {
            return 0;
        }

        Optional<Product> product = productService.getProductById(productId);
        if (product.isEmpty() || quantity <= 0) {
            quantities.remove(productId);
            return 0;
        }

        int newQuantity = Math.min(quantity, Math.max(0, product.get().getStock()));
        if (newQuantity == 0) {
            quantities.remove(productId);
        } else {
            quantities.put(productId, newQuantity);
        }

        return newQuantity;
    }

    public synchronized void removeProduct(Long productId) {
        quantities.remove(productId);
    }

    public synchronized List<CartItem> getItems() {
        List<CartItem> items = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : quantities.entrySet()) {
            productService.getProductById(entry.getKey())
                    .ifPresent(product -> items.add(new CartItem(product, entry.getValue())));
        }

        return items;
    }

    public static class CartItem {

        private final Product product;
        private final int quantity;

        public CartItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public Product getProduct() {
            return product;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getSubtotal() {
            return product.getPrice() * quantity;
        }

        public boolean isExceedsStock() {
            return quantity > product.getStock();
        }
    }
}

package dss.practicas.practica_1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import dss.practicas.practica_1.service.CartService;
import dss.practicas.practica_1.service.ProductService;

@Controller
@RequestMapping ("/cart")
public class CartController {
    
    private final CartService cartService;
    private final ProductService productService;

    public CartController(CartService cartService, ProductService productService) {
        this.cartService = cartService;
        this.productService = productService;
    }

    @GetMapping
    public String viewCart(Model model) {
        model.addAttribute("cartItems", cartService.getProducts());
        return "cart";
    }

    @PostMapping ("/add/{productId}")
    public String addProductToCart(@PathVariable Long productId) {
        productService.getProductById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found"));
        cartService.addProduct(productId);
        return "redirect:/cart";
    }

    @PostMapping ("/remove/{productId}")
    public String removeProductFromCart(@PathVariable Long productId) {
        cartService.removeProduct(productId);
        return "redirect:/cart";
    }
}

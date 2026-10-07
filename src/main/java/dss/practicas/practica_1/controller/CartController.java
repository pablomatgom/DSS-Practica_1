package dss.practicas.practica_1.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dss.practicas.practica_1.model.Product;
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
        model.addAttribute("cartItems", cartService.getItems());
        return "cart";
    }

    @PostMapping ("/add/{productId}")
    public String addProductToCart(@PathVariable Long productId,
                                    @RequestParam int quantity,
                                    RedirectAttributes redirectAttributes) {
        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        if (quantity < 1) {
            redirectAttributes.addFlashAttribute("error", "La cantidad debe ser al menos 1.");
        } else if (product.getStock() <= 0) {
            redirectAttributes.addFlashAttribute("error", "«" + product.getName() + "» está agotado.");
        } else {
            
            int added = cartService.addProduct(product, quantity);
            
            if (added == 0) {
                redirectAttributes.addFlashAttribute("error",
                        "Ya tienes en el carrito todas las unidades disponibles de «" + product.getName() + "».");
            } else if (added < quantity) {
                redirectAttributes.addFlashAttribute("warning",
                        "Solo se han añadido " + added + " unidades de «" + product.getName() + "» que es todo el stock disponible.");
            } else {
                redirectAttributes.addFlashAttribute("message",
                        "Añadido al carrito: " + added + " × «" + product.getName() + "».");
            }
        }
        return "redirect:/cart";
    }

    @PostMapping ("/update/{productId}")
    public String updateQuantity(@PathVariable Long productId,
                                 @RequestParam int quantity,
                                 RedirectAttributes redirectAttributes) {
        int result = cartService.setQuantity(productId, quantity);
        
        if (quantity > 0 && result == 0) {
            redirectAttributes.addFlashAttribute("error", "Ese producto ya no está disponible.");
        } else if (quantity > 0 && result < quantity) {
            redirectAttributes.addFlashAttribute("warning", "Solo hay " + result + " unidades disponibles.");
        }
        
        return "redirect:/cart";
    }

    @PostMapping ("/remove/{productId}")
    public String removeProductFromCart(@PathVariable Long productId) {
        cartService.removeProduct(productId);
        return "redirect:/cart";
    }
}

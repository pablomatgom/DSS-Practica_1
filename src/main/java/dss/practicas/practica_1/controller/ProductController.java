package dss.practicas.practica_1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import dss.practicas.practica_1.model.Product;
import dss.practicas.practica_1.service.ProductService;

@Controller
@RequestMapping ("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @GetMapping
    public String viewProducts(@RequestParam(required = false) String searcher,
                               @RequestParam(required = false) Double minPrice,
                               @RequestParam(required = false) Double maxPrice,
                               @RequestParam(defaultValue = "false") boolean inStock,
                               Model model) {
        model.addAttribute("products", productService.searchProducts(searcher, minPrice, maxPrice, inStock));

        model.addAttribute("searcher", searcher);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("inStock", inStock);
        model.addAttribute("filtered", (searcher != null && !searcher.isBlank()) || minPrice != null || maxPrice != null || inStock);
        return "products";
    }

    @GetMapping ("/add")
    public String add(Model model) {
        model.addAttribute("product", new Product());
        return "product_form";
    }

    @GetMapping ("/edit/{productId}")
    public String edit(@PathVariable Long productId, Model model) {
        Product product = productService.getProductById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found"));
        model.addAttribute("product", product);
        return "product_form";
    }

    @PostMapping ("/save")
    public String save(Product product) {
        productService.saveProduct(product);
        return "redirect:/products";
    }

    @PostMapping ("/delete/{productId}")
    public String delete(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return "redirect:/products";
    }
    
}

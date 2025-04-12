package com.rayvision.POS;

import com.rayvision.POS.domain.Product;
import com.rayvision.POS.domain.Sale;
import com.rayvision.POS.service.PosService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class PosController {
    private final PosService posService;

    public PosController(PosService posService) {
        this.posService = posService;
    }

    // Show the index page with product list and sales
    @GetMapping("/")
    public String showIndex(Model model) {
        List<Product> products = posService.getAllProducts();
        List<Sale> allSales = posService.getAllSales();
        
        model.addAttribute("products", products);
        model.addAttribute("sales", allSales);
        model.addAttribute("newSale", new Sale());
        model.addAttribute("newProduct", new Product());
        
        return "index"; // => index.html in templates folder
    }

    // Process the form submission from index.html
    @PostMapping("/sales")
    public String createSale(Sale sale) {
        posService.createSale(sale);
        return "redirect:/";
    }
    
    // REST API endpoint for integration with other systems
    @PostMapping(value = "/sales", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Sale> createSaleApi(@RequestBody Sale sale) {
        Sale createdSale = posService.createSale(sale);
        return ResponseEntity.ok(createdSale);
    }
    
    // Create a new product
    @PostMapping(value = "/api/products", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Product> createProductApi(@RequestBody Product product) {
        Product createdProduct = posService.createProduct(product);
        return ResponseEntity.ok(createdProduct);
    }
    
    // Get all products as JSON (for AJAX calls)
    @GetMapping("/api/products")
    @ResponseBody
    public List<Product> getProducts() {
        return posService.getAllProducts();
    }
    
    // Get a specific product by posCode
    @GetMapping("/api/products/{posCode}")
    @ResponseBody
    public ResponseEntity<Product> getProductByPosCode(@PathVariable String posCode) {
        return posService.getProductByPosCode(posCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

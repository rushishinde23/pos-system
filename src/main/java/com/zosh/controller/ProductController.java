package com.zosh.controller;

import com.zosh.modal.Product;
import com.zosh.modal.User;
import com.zosh.payload.dto.ProductDTO;
import com.zosh.payload.responce.ApiResponse;
import com.zosh.service.ProductService;
import com.zosh.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ProductDTO> create(@RequestBody ProductDTO productDTO, @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.getUserFromJwtToken(jwt);
        return ResponseEntity.ok(productService.createProduct(productDTO, user));
    }

    @GetMapping("/store/{storeID}")
    public ResponseEntity<List<ProductDTO>> getByStoreID(@PathVariable Long storeID, @RequestHeader("Authorization") String jwt) throws Exception {

        return ResponseEntity.ok(productService.getProductsByStoreID(storeID));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductDTO> update(@PathVariable Long id, @RequestBody ProductDTO productDTO, @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.getUserFromJwtToken(jwt);
        return ResponseEntity.ok(productService.updateProduct(id, productDTO, user));
    }

    @GetMapping("/store/{storeID}/search")
    public ResponseEntity<List<ProductDTO>> searchByKeyword(@PathVariable Long storeID, @RequestParam String keyword, @RequestHeader("Authorization") String jwt) throws Exception {

        return ResponseEntity.ok(productService.searchByKeyword(storeID, keyword));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id, @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.getUserFromJwtToken(jwt);
        productService.deleteProduct(id, user);

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Product deleted successfully");
        return ResponseEntity.ok(apiResponse);
    }
}

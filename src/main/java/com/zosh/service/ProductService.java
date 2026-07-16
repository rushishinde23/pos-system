package com.zosh.service;

import com.zosh.modal.Product;
import com.zosh.modal.User;
import com.zosh.payload.dto.ProductDTO;

import java.util.List;

public interface ProductService {
    ProductDTO createProduct(ProductDTO productDTO, User user) throws Exception;
    ProductDTO updateProduct(Long id, ProductDTO productDTO, User user) throws Exception;
    void deleteProduct(Long id, User user) throws Exception;
    List<ProductDTO> getProductsByStoreID(Long storeID);
    List<ProductDTO> searchByKeyword(Long storeID, String keyword);
}

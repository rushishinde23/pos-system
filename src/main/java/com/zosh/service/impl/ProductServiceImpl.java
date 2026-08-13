package com.zosh.service.impl;

import com.zosh.mapper.ProductMapper;
import com.zosh.modal.Category;
import com.zosh.modal.Product;
import com.zosh.modal.Store;
import com.zosh.modal.User;
import com.zosh.payload.dto.ProductDTO;
import com.zosh.repository.CategoryRepository;
import com.zosh.repository.ProductRepository;
import com.zosh.repository.StoreRepository;
import com.zosh.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final StoreRepository storeRepository;

    @Override
    public ProductDTO createProduct(ProductDTO productDTO, User user) throws Exception {
        Store store = storeRepository.findById(productDTO.getStoreID()).orElseThrow(
                () -> new Exception("Store Not Found")
        );

        Category category = categoryRepository.findById(productDTO.getCategoryID()).orElseThrow(
                () -> new Exception("Category not exist")
        );
        Product product = ProductMapper.toEntity(productDTO, store, category);
        Product savedProduct = productRepository.save(product);
        return ProductMapper.toDTO(savedProduct);
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO productDTO, User user)
            throws Exception {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new Exception("Product not found")
        );

        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setSku(productDTO.getSku());
        product.setImage(productDTO.getImage());
        product.setMrp(product.getMrp());
        product.setSellingPrice(productDTO.getSellingPrice());
        product.setBrand(productDTO.getBrand());
        product.setUpdatedAt(LocalDateTime.now());
        product.setCreatedAt(productDTO.getCreatedAt());

        if(productDTO.getCategoryID()!=null) {
            Category category = categoryRepository.findById(productDTO.getCategoryID()).orElseThrow(
                    () -> new Exception("Category not exist")
            );
            product.setCategory(category);
        }

        Product savedProduct = productRepository.save(product);
        return ProductMapper.toDTO(savedProduct);
    }

    @Override
    public void deleteProduct(Long id, User user) throws Exception {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new Exception("Product not found")
        );
        productRepository.delete(product);
    }

    @Override
    public List<ProductDTO> getProductsByStoreID(Long storeID) {
        List<Product> products = productRepository.findByStoreId(storeID);
        return products.stream().map(ProductMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> searchByKeyword(Long storeID, String keyword) {
        List<Product> products = productRepository.searchByKeyword(storeID, keyword);
        return products.stream().map(ProductMapper::toDTO).collect(Collectors.toList());
    }
}

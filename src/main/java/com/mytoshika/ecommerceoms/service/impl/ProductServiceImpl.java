package com.mytoshika.ecommerceoms.service.impl;

import com.mytoshika.ecommerceoms.dto.ProductRequest;
import com.mytoshika.ecommerceoms.dto.ProductResponse;
import com.mytoshika.ecommerceoms.entity.Inventory;
import com.mytoshika.ecommerceoms.entity.Product;
import com.mytoshika.ecommerceoms.entity.User;
import com.mytoshika.ecommerceoms.exception.ProductNameConflictException;
import com.mytoshika.ecommerceoms.exception.ResourceNotFoundException;
import com.mytoshika.ecommerceoms.repository.InventoryRepository;
import com.mytoshika.ecommerceoms.repository.ProductRepository;
import com.mytoshika.ecommerceoms.repository.UserRepository;
import com.mytoshika.ecommerceoms.service.interfaces.ProductService;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;


@Slf4j
@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ModelMapper mapper;
    private final ProductRepository productRepo;
    private final InventoryRepository inventoryRepo;
    private final UserRepository userRepository;


    @Cacheable(
            cacheNames = "products",
            key = "#page+'-'+#size+'-'+#sortBy+'-'+#direction+'-'+#keyword+'-'+#minPrice+'-'+#maxPrice"
    )
    @Override
    public Page<ProductResponse> getAllProducts(int page,
                                                int size,
                                                String sortBy,
                                                 String direction,
                                                  String keyword,
                                                Double minPrice,
                                                Double maxPrice) {

        validatePagination(page, size);
        Sort sort = createSort(sortBy, direction);
        Pageable pageable = PageRequest.of(page,size,sort);
        return  productRepo.searchProducts(keyword,minPrice,maxPrice,pageable
        ).map(product -> {
            Inventory inventory = getInventoryByProduct(product);
            return mapToProductResponse(product,inventory);
        });
    }

    @CacheEvict(
            cacheNames = "products",
            allEntries = true
    )
    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest dto) {
        log.info("Creating product with name: {}", dto.getName());
        validateNameExist(dto.getName());
        Product product = createProductEntity(dto);
        Inventory inventory = createInventoryEntity(dto, product);
        return mapToProductResponse(product,inventory);
    }


    @Caching(
            put = {@CachePut(cacheNames = "product",key="#id"),},
            evict = {@CacheEvict(cacheNames = "products",allEntries = true)}
    )
    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest dto) {
        Product product = getProductById(id);
        validateProductUpdate(dto.getName(), id);
        updateProductDetails(product, dto);
        Inventory inventory = getInventoryByProduct(product);
        updateInventory(inventory, dto);
        log.info("Product updated successfully. Product Id: {}", id);
        return mapToProductResponse(product, inventory);
    }

    @Cacheable(
            cacheNames = "product",
            key = "#id"
    )
    @Override
    public ProductResponse getProduct(Long id) {
        log.info("Fetching product details for product id: {}", id);
        Product product =  getProductById(id);
        Inventory inventory = getInventoryByProduct(product);
        log.info("Product details fetched successfully for product id: {}", id);
        return mapToProductResponse(product,inventory);
    }

    @Caching(
            evict = {
                    @CacheEvict(cacheNames="product", key="#id"),
                    @CacheEvict(cacheNames="products", allEntries=true)
            }
    )
    @Transactional
    @Override
    public String deleteProduct(Long id){
        Long userId = getLoggedInUserKeycloakId();
        productRepo.softDeleteProduct(id,userId);
        inventoryRepo.softDeleteInventory(id, userId);
        log.info("Product soft deleted successfully. Product Id: {}",id);
        return "Product deleted successfully";
    }


    @Caching(
            evict = {
                    @CacheEvict(cacheNames = "products", allEntries = true),
                    @CacheEvict(cacheNames = "product", key="#id")
            }
    )
    @Transactional
    @Override
    public ProductResponse restoreProduct(Long id) {
        Product product = getProductIncludingDeleted(id);
        Inventory inventory = getInventoryIncludingDeleted(product);
        validateStatus(id);
        validateName(product.getName(), id);
        validateInventory(inventory);
        productRepo.restoreProduct(id);
        inventoryRepo.restoreInventory(id);
        log.info("Product restored successfully. Product Id: {}",product.getId());
        return mapToProductResponse(product,inventory);
    }

    @Override
    public List<ProductResponse> getDeletedProducts() {
        return  productRepo.findDeletedProducts()
                .stream()
                .map(product ->{
                    Inventory inventory = getInventoryIncludingDeleted(product);
                    return mapToProductResponse(product,inventory);
                }
                ).toList();
    }

    private Product createProductEntity(ProductRequest dto){
        Product product = mapper.map(dto, Product.class);
        product = productRepo.save(product);

        log.info("Product saved successfully with id: {}", product.getId());
        return product;
    }

    private Inventory createInventoryEntity(ProductRequest dto, Product product){
        Inventory inventory = mapper.map(dto,Inventory.class);
        inventory.setProduct(product);
        inventory.setReservedQuantity(0);
        inventoryRepo.save(inventory);

        log.info("Inventory created successfully for product id: {}", product.getId());
        return inventory;
    }

    private Product getProductById(Long id){

        return productRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("Product not found. Product Id: {}", id);
                    return new ResourceNotFoundException("Product not found");
                });
    }
    private Product getProductIncludingDeleted(Long id){

        return productRepo.findByIdIncludingDeleted(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found"));
    }

    private Inventory getInventoryByProduct(Product product){

        return inventoryRepo.findByProduct(product)
                .orElseThrow(() -> {
                    log.error("Inventory not found. Product Id: {}", product.getId());
                    return new ResourceNotFoundException("Inventory not found");
                });
    }

    private Inventory getInventoryIncludingDeleted(Product product){

        return inventoryRepo.findByProductIncludingDeleted(product.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Inventory not found"));
    }

    private void updateProductDetails(Product product, ProductRequest dto){

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());

        productRepo.save(product);
    }

    private void updateInventory(Inventory inventory, ProductRequest dto){

        inventory.setAvailableQuantity(dto.getAvailableQuantity());
        inventoryRepo.save(inventory);
    }

    private void validateProductUpdate(String name, Long id){
            if(productRepo.existsByNameIgnoreCaseAndIdNot(name,id)){
            log.warn("Product name already exists. Name: {}", name);
            throw new ProductNameConflictException("Product name already exists");
        }
    }

    private ProductResponse mapToProductResponse(Product product, Inventory inventory){

        ProductResponse response = mapper.map(product, ProductResponse.class);
        response.setAvailableQuantity(inventory.getAvailableQuantity());

        return response;
    }

    private void validatePagination(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page can not be negative");
        }

        if (size <= 0 || size > 100) {
            throw new IllegalArgumentException("Page size must be between 1 to 100");
        }
    }

    private void validateStatus(Long id){
        boolean deleted = productRepo.getDeletedStatus(id);

        if(!deleted){
            throw new IllegalStateException(
                    "Product is already active"
            );
        }
    }
    private void validateInventory(Inventory inventory){

        if(inventory.getAvailableQuantity() < 0){
            throw new IllegalStateException(
                    "Invalid inventory quantity"
            );
        }
    }

    private void validateName(String name, long id){
        if(productRepo.existsByNameIgnoreCaseAndIdNot(name, id)){
            log.error("Restore failed due to product name conflict. Product Id: {}, Product Name: {}",
                    id, name);
            throw new ProductNameConflictException("Name Conflict, Can not restore");
        }
    }

    private Sort createSort(String sortBy, String direction) {

        Set<String> allowedSortFields = Set.of("id", "name", "price");

        if (!allowedSortFields.contains(sortBy)) {
            throw new IllegalArgumentException("Invalid sort field " + sortBy);
        }

        return direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
    }

    private long getLoggedInUserKeycloakId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String keycloakUserId = ((Jwt) authentication.getPrincipal()).getSubject();
        User user = userRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return user.getId();
    }
    private void validateNameExist(String name) {
        if(productRepo.existsByNameIgnoreCase(name)){
            log.error("Product already exists with name: {}", name);
            throw new ProductNameConflictException("Product name already exists");
        }
    }
}

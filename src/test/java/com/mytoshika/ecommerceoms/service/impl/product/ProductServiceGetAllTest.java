package com.mytoshika.ecommerceoms.service.impl.product;

import com.mytoshika.ecommerceoms.dto.ProductResponse;
import com.mytoshika.ecommerceoms.entity.Inventory;
import com.mytoshika.ecommerceoms.entity.Product;
import com.mytoshika.ecommerceoms.repository.InventoryRepository;
import com.mytoshika.ecommerceoms.repository.ProductRepository;
import com.mytoshika.ecommerceoms.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceGetAllTest {

    @Mock
    private ModelMapper mapper;

    @Mock
    private ProductRepository productRepo;

    @Mock
    private InventoryRepository inventoryRepo;

    @InjectMocks
    private ProductServiceImpl productService;


    @Test
    void shouldGetAllProductsSuccessfully() {

        // Arrange
        int page = 0;
        int size = 10;
        String sortBy = "name";
        String direction = "asc";
        String keyword = null;
        Double minPrice = null;
        Double maxPrice = null;

        Product product = createProduct();
        Inventory inventory = createInventory(product);
        ProductResponse productResponse = createProductResponse();
        Page<Product> productPage = new PageImpl<>(List.of(product));

        when(productRepo.searchProducts(eq(keyword), eq(minPrice), eq(maxPrice),
                any())).thenReturn(productPage);
        when(inventoryRepo.findByProduct(product)).thenReturn(Optional.of(inventory));
        when(mapper.map(product, ProductResponse.class)).thenReturn(productResponse);

        // Act
        Page<ProductResponse> response =
                productService.getAllProducts(
                        page, size, sortBy, direction, keyword, minPrice, maxPrice);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals("Laptop",response.getContent().get(0).getName());
        assertEquals(20, response.getContent().get(0).getAvailableQuantity());

        verify(productRepo).searchProducts(eq(keyword), eq(minPrice), eq(maxPrice), any());
        verify(inventoryRepo).findByProduct(product);
        verify(mapper).map(product, ProductResponse.class);
    }

    @Test
    void shouldThrowExceptionWhenPageIsNegative(){
        // Arrange
        int page = -1;
        int size = 10;
        String sortBy = "name";
        String direction = "asc";
        String keyword = null;
        Double minPrice = null;
        Double maxPrice = null;

        // Act
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        ()-> productService.getAllProducts(
                                page, size, sortBy, direction, keyword, minPrice, maxPrice));

        //Assert
        assertEquals("Page can not be negative", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPageSizeIsZero(){
        // Arrange
        int page = 0;
        int size = 101;
        String sortBy = "name";
        String direction = "asc";
        String keyword = null;
        Double minPrice = null;
        Double maxPrice = null;

        // Act
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        ()-> productService.getAllProducts(
                                page, size, sortBy, direction, keyword, minPrice, maxPrice));

        //Assert
        assertEquals("Page size must be between 1 to 100", exception.getMessage());
    }
    @Test
    void shouldThrowExceptionWhenPageSizeExceedsLimit(){
        // Arrange
        int page = 0;
        int size = 0;
        String sortBy = "name";
        String direction = "asc";
        String keyword = null;
        Double minPrice = null;
        Double maxPrice = null;

        // Act
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        ()-> productService.getAllProducts(
                                page, size, sortBy, direction, keyword, minPrice, maxPrice));

        //Assert
        assertEquals("Page size must be between 1 to 100", exception.getMessage());
    }
    @Test
    void shouldThrowExceptionWhenSortFieldIsInvalid(){
        // Arrange
        int page = 0;
        int size = 10;
        String sortBy = "power";
        String direction = "asc";
        String keyword = null;
        Double minPrice = null;
        Double maxPrice = null;

        // Act
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        ()-> productService.getAllProducts(
                                page, size, sortBy, direction, keyword, minPrice, maxPrice));

        //Assert
        assertEquals("Invalid sort field power", exception.getMessage());
    }
    // Helper Methods

    private Product createProduct() {

        return Product.builder()
                .id(1L)
                .name("Laptop")
                .description("Gaming Laptop")
                .price(new BigDecimal("50000"))
                .build();
    }


    private Inventory createInventory(Product product) {

        return Inventory.builder()
                .product(product)
                .availableQuantity(20)
                .reservedQuantity(0)
                .build();
    }


    private ProductResponse createProductResponse() {

        return ProductResponse.builder()
                .id(1L)
                .name("Laptop")
                .price(new BigDecimal("50000"))
                .availableQuantity(20)
                .build();
    }
}
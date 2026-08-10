package com.mytoshika.ecommerceoms.service.impl.product;

import com.mytoshika.ecommerceoms.dto.ProductRequest;
import com.mytoshika.ecommerceoms.dto.ProductResponse;
import com.mytoshika.ecommerceoms.entity.Inventory;
import com.mytoshika.ecommerceoms.entity.Product;
import com.mytoshika.ecommerceoms.exception.ProductNameConflictException;
import com.mytoshika.ecommerceoms.repository.InventoryRepository;
import com.mytoshika.ecommerceoms.repository.ProductRepository;
import com.mytoshika.ecommerceoms.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceCreateProductTest {
    @Mock private ModelMapper mapper;
    @Mock private ProductRepository productRepo;
    @Mock private InventoryRepository inventoryRepo;
    @InjectMocks private ProductServiceImpl productService;


    @Test
    void shouldCreateProductSuccessfully() {
        //Arrange
        ProductRequest request = createProductRequest();
        Product product = createProduct();
        Inventory inventory = createInventory(product);
        ProductResponse response = createProductResponse();

        when(productRepo.existsByNameIgnoreCase(request.getName())).thenReturn(false);
        when(mapper.map(request,Product.class)).thenReturn(product);
        when(productRepo.save(product)).thenReturn(product);
        when(mapper.map(request,Inventory.class)).thenReturn(inventory);
        when(inventoryRepo.save(inventory)).thenReturn(inventory);
        when(mapper.map(product, ProductResponse.class)).thenReturn(response);

        //Act
        ProductResponse productResponse = productService.createProduct(request);

        //Assert
        assertNotNull(productResponse);
        assertEquals("Laptop", productResponse.getName());
        assertEquals(new BigDecimal("9000"), productResponse.getPrice());
        assertEquals(100, productResponse.getAvailableQuantity());

        verify(productRepo).existsByNameIgnoreCase(request.getName());
        verify(mapper).map(request, Product.class);
        verify(productRepo).save(product);
        verify(mapper).map(request, Inventory.class);
        verify(inventoryRepo).save(inventory);
        verify(mapper).map(product, ProductResponse.class);
    }
    @Test
    void shouldThrowExceptionWhenProductNameAlreadyExists() {
        //Arrange
        ProductRequest request = createProductRequest();

        when(productRepo.existsByNameIgnoreCase(request.getName())).thenReturn(true);

        //Act
        ProductNameConflictException exception = assertThrows(
                ProductNameConflictException.class,
                ()->productService.createProduct(request));

        //Assert
        assertEquals("Product name already exists", exception.getMessage());
        verify(productRepo).existsByNameIgnoreCase(request.getName());

    }
    //Helper Method
    private Product createProduct() {

        return Product.builder()
                .id(1L)
                .name("Laptop")
                .description("Gaming Laptop")
                .price(new BigDecimal("9000"))
                .build();
    }


    private Inventory createInventory(Product product) {
        return Inventory.builder()
                .product(product)
                .availableQuantity(100)
                .reservedQuantity(0)
                .build();
    }
    private ProductRequest createProductRequest(){
        return ProductRequest.builder()
                .name("Laptop")
                .price(new BigDecimal("9000"))
                .availableQuantity(100)
                .build();
    }
    private ProductResponse createProductResponse(){
        return ProductResponse.builder()
                .name("Laptop")
                .price(new BigDecimal("9000"))
                .availableQuantity(100)
                .build();
    }
}

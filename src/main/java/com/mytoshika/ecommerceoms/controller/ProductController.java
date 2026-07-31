package com.mytoshika.ecommerceoms.controller;

import com.mytoshika.ecommerceoms.dto.*;
import com.mytoshika.ecommerceoms.service.interfaces.ProductService;
import com.mytoshika.ecommerceoms.swagger.CommonApiResponses;
import com.mytoshika.ecommerceoms.swagger.ValidationApiResponses;
import com.mytoshika.ecommerceoms.util.JsonIdGenerator;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Product API",
        description = "Product management APIs"
)
@Slf4j
@RestController
@RequestMapping("api/v1/")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final JsonIdGenerator idGenerator;

    @ValidationApiResponses
    @GetMapping("/products")
    public ResponseEntity<APIResponse> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice
            ){
        Page<ProductResponse> products = productService.getAllProducts(
                page,
                size,
                sortBy,
                direction,
                keyword,
                minPrice,
                maxPrice);
        return buildResponse(HttpStatus.OK,null,"Products Fetched",products);
    }

    @CommonApiResponses
    @SecurityRequirement(name = "bearer-key")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/products")
    public ResponseEntity<APIResponse>  createProduct(
            @Valid @RequestBody APIRequest<ProductRequest> dto){
        log.info("Create product request received, product name: {}", dto.getData().getName());
        ProductResponse productResponse = productService.createProduct(dto.getData());
        log.info("Product created successfully, product id: {}", productResponse.getId());
        return buildResponse(HttpStatus.CREATED,idGenerator.responseId(),"Product Created Successfully",productResponse);
    }

    @CommonApiResponses
    @SecurityRequirement(name = "bearer-key")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/products/{id}")
    public ResponseEntity<APIResponse>  updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest dto){
        log.info("Update product request received for product id: {}", id);
        ProductResponse productResponse = productService.updateProduct(id,dto);
        log.info("Product updated successfully, product id: {}", id);
        return buildResponse(HttpStatus.OK,null, "Product Updated Successfully",productResponse);
    }

    @ValidationApiResponses
    @GetMapping("/products/{id}")
    public ResponseEntity<APIResponse> getProduct(@PathVariable Long id){
        log.info("Get product request received for product id: {}", id);
        ProductResponse productResponse = productService.getProduct(id);
        log.info("Product fetched successfully, product id: {}", id);
        return buildResponse(HttpStatus.OK,null,"Product Fetched Successfully",productResponse);
    }

    @CommonApiResponses
    @SecurityRequirement(name = "bearer-key")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/products/{id}")
    public ResponseEntity<APIResponse> deleteProduct(@PathVariable Long id){
        log.info("Delete product request received. Product Id: {}", id);
        String message = productService.deleteProduct(id);
        log.info("Delete product request completed. Product Id: {}", id);
        return buildResponse(HttpStatus.OK,null,message,null);
    }

    @CommonApiResponses
    @SecurityRequirement(name = "bearer-key")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/admin/products/{id}")
    public ResponseEntity<APIResponse> restoreProduct(@PathVariable Long id){
        log.info("Restore product request received. Product Id: {}", id);
        ProductResponse productResponse = productService.restoreProduct(id);
        log.info("Restore product request completed. Product Id: {}", id);
        return buildResponse(HttpStatus.OK,null,"Product Restored Successfully", productResponse);
    }

    @CommonApiResponses
    @SecurityRequirement(name = "bearer-key")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/products/deleted")
    public ResponseEntity<APIResponse> deletedProducts(){
        log.info("Fetch deleted products request received.");
        List<ProductResponse> response = productService.getDeletedProducts();
        log.info("Fetch deleted products request completed. Total deleted products: {}", response.size());
        return buildResponse(HttpStatus.OK,null,"Deleted Product Fetched Successfully", response);
    }

    private ResponseEntity<APIResponse> buildResponse(HttpStatus status, String jsonId,String message, Object response){
        System.out.println(response);
        return ResponseEntity.status(status)
                .body(APIResponse.builder()
                        .success(true)
                        .jsonId(jsonId)
                        .message(message)
                        .data(response)
                        .build());
    }
}

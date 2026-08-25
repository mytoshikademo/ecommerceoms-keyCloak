package com.mytoshika.ecommerceoms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    private long totalElements;
    private int totalPages;
    private int size;
    private int number;
    private List<T> content;
}
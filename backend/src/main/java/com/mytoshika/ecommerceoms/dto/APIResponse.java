package com.mytoshika.ecommerceoms.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class APIResponse {
    private String jsonId;
    private Boolean success;
    private String message;
    private Object data;
}

package com.mytoshika.ecommerceoms.swagger;


import com.mytoshika.ecommerceoms.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
        @ApiResponse(
                responseCode = "400",
                description = "Validation Error",
                content = @Content(
                        schema = @Schema(
                                implementation = ErrorResponse.class
                        )
                )
        )
public @interface ValidationApiResponses {
}

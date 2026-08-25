package com.mytoshika.ecommerceoms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class LoginRequest {

    @Schema(
            description = "Registered user email",
            example ="abhishek@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Schema(
            description = "User password",
            example = "abhishek@123"
    )
    @NotBlank(message = "Password is required")
    private String password;
}
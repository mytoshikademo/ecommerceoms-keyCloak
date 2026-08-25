package com.mytoshika.ecommerceoms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.Length;


@Data
public class UserRequest {


    @Schema(
            description = "User's full name",
            example = "Admin Sharma",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @Length(min=2, max=100, message = "Name should be between 2 - 100 character")
    private String name;


    @Schema(
            description = "User's email address",
            example = "admin@example.com"
    )
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;


    @Schema(
            description = "User password",
            example = "admin@123",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[0-9]{2,})(?=.*[A-Z])(?=.*[a-z])(?=.*[@#$%^&+=]).{8,}$",
                message = "Password must be at least 8 characters with uppercase, lowercase,number, and special character")
    private String password;

}

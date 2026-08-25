package com.mytoshika.ecommerceoms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.apache.logging.log4j.message.Message;


@Getter
@Setter
public class APIRequest<T> {

    @Schema(
            description = "Unique request identifier",
            example = "abc312"
    )
    private String jsonid;


    @Valid
    @NotNull(message = "Request data can not be null")
    @Schema(
            description = "Request Payload"
    )
    private T data;
}

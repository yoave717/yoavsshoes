package com.shoestore.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for logout
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Logout response")
public class LogoutResponse {

    @Schema(description = "Success message")
    private String message;

    @Schema(description = "Timestamp of logout")
    private LocalDateTime timestamp;
}

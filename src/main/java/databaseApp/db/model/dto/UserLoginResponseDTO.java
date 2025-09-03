package databaseApp.db.model.dto;

import java.util.List;

public record UserLoginResponseDTO(
        String uNumber,
        String email,
        List<String> roles,
        String message
) {}
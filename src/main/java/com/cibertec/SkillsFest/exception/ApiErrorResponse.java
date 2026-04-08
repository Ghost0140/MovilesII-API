package com.cibertec.SkillsFest.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String detalle,
        Map<String, String> validaciones
) {
}
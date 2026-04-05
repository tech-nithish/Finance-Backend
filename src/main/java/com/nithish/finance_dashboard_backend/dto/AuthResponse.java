package com.nithish.finance_dashboard_backend.dto;

import com.nithish.finance_dashboard_backend.model.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token;
    private String id;
    private String name;
    private String email;
    private Role role;
}

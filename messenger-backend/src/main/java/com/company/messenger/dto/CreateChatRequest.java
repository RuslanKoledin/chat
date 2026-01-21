package com.company.messenger.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateChatRequest {
    private String name;

    @NotNull(message = "Chat type is required")
    private String type;

    @NotEmpty(message = "Members list cannot be empty")
    private List<Long> memberIds;
}

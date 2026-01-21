package com.company.messenger.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendMessageRequest {
    @NotNull(message = "Chat ID is required")
    private Long chatId;

    @NotBlank(message = "Message content is required")
    private String content;

    private Long replyToId;
}

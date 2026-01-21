package com.company.messenger.dto;

import com.company.messenger.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private Long id;
    private String content;
    private Long chatId;
    private UserDto sender;
    private Long replyToId;
    private LocalDateTime createdAt;

    public static MessageDto fromEntity(Message message) {
        return new MessageDto(
            message.getId(),
            message.getContent(),
            message.getChat().getId(),
            UserDto.fromEntity(message.getSender()),
            message.getReplyTo() != null ? message.getReplyTo().getId() : null,
            message.getCreatedAt()
        );
    }
}

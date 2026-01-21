package com.company.messenger.dto;

import com.company.messenger.entity.Chat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatDto {
    private Long id;
    private String name;
    private String type;
    private LocalDateTime createdAt;
    private List<UserDto> members;

    public static ChatDto fromEntity(Chat chat) {
        return new ChatDto(
            chat.getId(),
            chat.getName(),
            chat.getType().name(),
            chat.getCreatedAt(),
            chat.getMembers().stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList())
        );
    }
}

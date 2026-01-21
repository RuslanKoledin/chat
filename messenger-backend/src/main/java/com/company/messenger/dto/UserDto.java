package com.company.messenger.dto;

import com.company.messenger.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String department;
    private String role;

    public static UserDto fromEntity(User user) {
        return new UserDto(
            user.getId(),
            user.getUsername(),
            user.getFullName(),
            user.getEmail(),
            user.getDepartment(),
            user.getRole().name()
        );
    }
}

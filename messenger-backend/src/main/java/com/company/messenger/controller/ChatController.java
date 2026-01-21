package com.company.messenger.controller;

import com.company.messenger.dto.ChatDto;
import com.company.messenger.dto.CreateChatRequest;
import com.company.messenger.entity.User;
import com.company.messenger.repository.UserRepository;
import com.company.messenger.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<ChatDto> createChat(@Valid @RequestBody CreateChatRequest request,
                                               Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));

        ChatDto chatDto = chatService.createChat(request, user.getId());
        return ResponseEntity.ok(chatDto);
    }

    @GetMapping
    public ResponseEntity<List<ChatDto>> getUserChats(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));

        List<ChatDto> chats = chatService.getUserChats(user.getId());
        return ResponseEntity.ok(chats);
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<ChatDto> getChatById(@PathVariable Long chatId, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));

        ChatDto chatDto = chatService.getChatById(chatId, user.getId());
        return ResponseEntity.ok(chatDto);
    }

    @PostMapping("/{chatId}/members/{memberId}")
    public ResponseEntity<ChatDto> addMember(@PathVariable Long chatId,
                                              @PathVariable Long memberId,
                                              Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));

        ChatDto chatDto = chatService.addMember(chatId, memberId, user.getId());
        return ResponseEntity.ok(chatDto);
    }

    @DeleteMapping("/{chatId}/members/{memberId}")
    public ResponseEntity<ChatDto> removeMember(@PathVariable Long chatId,
                                                 @PathVariable Long memberId,
                                                 Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));

        ChatDto chatDto = chatService.removeMember(chatId, memberId, user.getId());
        return ResponseEntity.ok(chatDto);
    }

    @DeleteMapping("/{chatId}")
    public ResponseEntity<Void> deleteChat(@PathVariable Long chatId, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));

        chatService.deleteChat(chatId, user.getId());
        return ResponseEntity.noContent().build();
    }
}

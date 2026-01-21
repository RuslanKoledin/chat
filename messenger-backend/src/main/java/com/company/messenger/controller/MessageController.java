package com.company.messenger.controller;

import com.company.messenger.dto.MessageDto;
import com.company.messenger.dto.SendMessageRequest;
import com.company.messenger.entity.User;
import com.company.messenger.repository.UserRepository;
import com.company.messenger.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<MessageDto> sendMessage(@Valid @RequestBody SendMessageRequest request,
                                                   Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));

        MessageDto messageDto = messageService.sendMessage(request, user.getId());
        return ResponseEntity.ok(messageDto);
    }

    @GetMapping("/chat/{chatId}")
    public ResponseEntity<List<MessageDto>> getChatMessages(@PathVariable Long chatId,
                                                             Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));

        List<MessageDto> messages = messageService.getChatMessages(chatId, user.getId());
        return ResponseEntity.ok(messages);
    }
}

@Controller
class WebSocketMessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserRepository userRepository;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload SendMessageRequest request, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));

        messageService.sendMessage(request, user.getId());
    }
}

package com.company.messenger.service;

import com.company.messenger.dto.MessageDto;
import com.company.messenger.dto.SendMessageRequest;
import com.company.messenger.entity.Chat;
import com.company.messenger.entity.Message;
import com.company.messenger.entity.User;
import com.company.messenger.repository.ChatRepository;
import com.company.messenger.repository.MessageRepository;
import com.company.messenger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.Hibernate;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Transactional
    public MessageDto sendMessage(SendMessageRequest request, Long senderId) {
        logger.info("=== SEND MESSAGE DEBUG ===");
        logger.info("Request chatId: {}", request.getChatId());
        logger.info("SenderId from JWT: {}", senderId);

        Chat chat = chatRepository.findByIdWithMembers(request.getChatId())
            .orElseThrow(() -> new RuntimeException("Chat not found"));

        // Refresh entity to reload members from database, bypassing first-level cache
        entityManager.refresh(chat);
        // Force initialization of members collection
        Hibernate.initialize(chat.getMembers());

        logger.info("Chat found, ID: {}, Name: {}", chat.getId(), chat.getName());
        logger.info("Chat members count: {}", chat.getMembers().size());
        chat.getMembers().forEach(member ->
            logger.info("  Member: ID={}, Username={}", member.getId(), member.getUsername())
        );

        User sender = userRepository.findById(senderId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        logger.info("Sender found: ID={}, Username={}", sender.getId(), sender.getUsername());

        boolean isMember = chat.getMembers().stream()
            .anyMatch(member -> member.getId().equals(senderId));

        logger.info("Is member check result: {}", isMember);

        if (!isMember) {
            logger.error("ACCESS DENIED: User {} is not a member of chat {}", senderId, chat.getId());
            throw new RuntimeException("Access denied");
        }

        Message message = new Message();
        message.setContent(request.getContent());
        message.setChat(chat);
        message.setSender(sender);

        if (request.getReplyToId() != null) {
            Message replyTo = messageRepository.findById(request.getReplyToId())
                .orElseThrow(() -> new RuntimeException("Reply message not found"));
            message.setReplyTo(replyTo);
        }

        Message savedMessage = messageRepository.save(message);
        MessageDto messageDto = MessageDto.fromEntity(savedMessage);

        messagingTemplate.convertAndSend("/topic/chat/" + chat.getId(), messageDto);

        return messageDto;
    }

    public List<MessageDto> getChatMessages(Long chatId, Long userId) {
        Chat chat = chatRepository.findByIdWithMembers(chatId)
            .orElseThrow(() -> new RuntimeException("Chat not found"));

        // Refresh entity to reload members from database
        entityManager.refresh(chat);
        // Force initialization of members collection
        Hibernate.initialize(chat.getMembers());

        boolean isMember = chat.getMembers().stream()
            .anyMatch(member -> member.getId().equals(userId));

        if (!isMember) {
            throw new RuntimeException("Access denied");
        }

        List<Message> messages = messageRepository.findAllByChatId(chatId);
        return messages.stream()
            .map(MessageDto::fromEntity)
            .collect(Collectors.toList());
    }
}

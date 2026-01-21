package com.company.messenger.service;

import com.company.messenger.dto.ChatDto;
import com.company.messenger.dto.CreateChatRequest;
import com.company.messenger.entity.Chat;
import com.company.messenger.entity.User;
import com.company.messenger.repository.ChatRepository;
import com.company.messenger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Hibernate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public ChatDto createChat(CreateChatRequest request, Long currentUserId) {
        Chat chat = new Chat();
        chat.setType(Chat.ChatType.valueOf(request.getType()));
        chat.setName(request.getName());

        Set<User> members = new HashSet<>();
        for (Long memberId : request.getMemberIds()) {
            User user = userRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("User not found: " + memberId));
            members.add(user);
        }

        User currentUser = userRepository.findById(currentUserId)
            .orElseThrow(() -> new RuntimeException("Current user not found"));
        members.add(currentUser);

        chat.setMembers(members);

        if (chat.getType() == Chat.ChatType.DIRECT && chat.getName() == null) {
            List<User> membersList = members.stream()
                .filter(u -> !u.getId().equals(currentUserId))
                .collect(Collectors.toList());
            if (!membersList.isEmpty()) {
                chat.setName(membersList.get(0).getFullName());
            }
        }

        Chat savedChat = chatRepository.save(chat);
        return ChatDto.fromEntity(savedChat);
    }

    public List<ChatDto> getUserChats(Long userId) {
        List<Chat> chats = chatRepository.findAllByUserId(userId);
        return chats.stream()
            .map(ChatDto::fromEntity)
            .collect(Collectors.toList());
    }

    public ChatDto getChatById(Long chatId, Long userId) {
        Chat chat = chatRepository.findByIdWithMembers(chatId)
            .orElseThrow(() -> new RuntimeException("Chat not found"));

        entityManager.refresh(chat);
        Hibernate.initialize(chat.getMembers());

        boolean isMember = chat.getMembers().stream()
            .anyMatch(member -> member.getId().equals(userId));

        if (!isMember) {
            throw new RuntimeException("Access denied");
        }

        return ChatDto.fromEntity(chat);
    }

    @Transactional
    public ChatDto addMember(Long chatId, Long memberIdToAdd, Long currentUserId) {
        Chat chat = chatRepository.findByIdWithMembers(chatId)
            .orElseThrow(() -> new RuntimeException("Chat not found"));

        entityManager.refresh(chat);
        Hibernate.initialize(chat.getMembers());

        // Only group chats can add members
        if (chat.getType() != Chat.ChatType.GROUP) {
            throw new RuntimeException("Cannot add members to direct chat");
        }

        // Check if current user is a member
        boolean isCurrentUserMember = chat.getMembers().stream()
            .anyMatch(member -> member.getId().equals(currentUserId));
        if (!isCurrentUserMember) {
            throw new RuntimeException("Access denied");
        }

        // Check if user to add already exists
        boolean alreadyMember = chat.getMembers().stream()
            .anyMatch(member -> member.getId().equals(memberIdToAdd));
        if (alreadyMember) {
            throw new RuntimeException("User is already a member");
        }

        User userToAdd = userRepository.findById(memberIdToAdd)
            .orElseThrow(() -> new RuntimeException("User not found"));

        chat.getMembers().add(userToAdd);
        Chat savedChat = chatRepository.save(chat);

        return ChatDto.fromEntity(savedChat);
    }

    @Transactional
    public ChatDto removeMember(Long chatId, Long memberIdToRemove, Long currentUserId) {
        Chat chat = chatRepository.findByIdWithMembers(chatId)
            .orElseThrow(() -> new RuntimeException("Chat not found"));

        entityManager.refresh(chat);
        Hibernate.initialize(chat.getMembers());

        // Only group chats can remove members
        if (chat.getType() != Chat.ChatType.GROUP) {
            throw new RuntimeException("Cannot remove members from direct chat");
        }

        // Check if current user is a member
        boolean isCurrentUserMember = chat.getMembers().stream()
            .anyMatch(member -> member.getId().equals(currentUserId));
        if (!isCurrentUserMember) {
            throw new RuntimeException("Access denied");
        }

        // Find and remove the user
        boolean removed = chat.getMembers().removeIf(member -> member.getId().equals(memberIdToRemove));
        if (!removed) {
            throw new RuntimeException("User is not a member of this chat");
        }

        // Don't allow removing the last member
        if (chat.getMembers().isEmpty()) {
            throw new RuntimeException("Cannot remove the last member");
        }

        Chat savedChat = chatRepository.save(chat);
        return ChatDto.fromEntity(savedChat);
    }

    @Transactional
    public void deleteChat(Long chatId, Long currentUserId) {
        Chat chat = chatRepository.findByIdWithMembers(chatId)
            .orElseThrow(() -> new RuntimeException("Chat not found"));

        entityManager.refresh(chat);
        Hibernate.initialize(chat.getMembers());

        // Check if current user is a member
        boolean isMember = chat.getMembers().stream()
            .anyMatch(member -> member.getId().equals(currentUserId));
        if (!isMember) {
            throw new RuntimeException("Access denied");
        }

        chatRepository.delete(chat);
    }
}

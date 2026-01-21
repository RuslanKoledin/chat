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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChatService {

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

        boolean isMember = chat.getMembers().stream()
            .anyMatch(member -> member.getId().equals(userId));

        if (!isMember) {
            throw new RuntimeException("Access denied");
        }

        return ChatDto.fromEntity(chat);
    }
}

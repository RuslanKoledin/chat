package com.company.messenger.repository;

import com.company.messenger.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId ORDER BY m.createdAt ASC")
    List<Message> findAllByChatId(@Param("chatId") Long chatId);
}

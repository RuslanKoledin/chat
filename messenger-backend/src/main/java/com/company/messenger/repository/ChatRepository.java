package com.company.messenger.repository;

import com.company.messenger.entity.Chat;
import com.company.messenger.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("SELECT c FROM Chat c JOIN c.members m WHERE m.id = :userId ORDER BY c.createdAt DESC")
    List<Chat> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM Chat c JOIN c.members m1 JOIN c.members m2 " +
           "WHERE c.type = 'DIRECT' AND m1.id = :user1Id AND m2.id = :user2Id")
    Optional<Chat> findDirectChatBetweenUsers(@Param("user1Id") Long user1Id,
                                               @Param("user2Id") Long user2Id);

    @EntityGraph(attributePaths = {"members"})
    @Query("SELECT c FROM Chat c WHERE c.id = :id")
    Optional<Chat> findByIdWithMembers(@Param("id") Long id);
}

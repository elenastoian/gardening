package com.ai.gardening.repository;

import com.ai.gardening.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByChannelId(long channelId);

    @Query(value = "SELECT id FROM post WHERE channel = :channelId", nativeQuery = true)
    List<Long> findAllPostIdsByChannelId(long channelId);
}

package com.ai.gardening.repository;

import com.ai.gardening.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long> {

     @Query(value = "SELECT * FROM channel WHERE creator_id = ?1", nativeQuery = true)
     List<Channel> findAllByAppUserId(int userId);

     Channel findByName(String channelName);
}

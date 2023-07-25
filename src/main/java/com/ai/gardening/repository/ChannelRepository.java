package com.ai.gardening.repository;

import com.ai.gardening.entity.AppUser;
import com.ai.gardening.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long> {

     List<Channel> findAllByOwner(AppUser appUser);

     Optional<Channel> findByName(String channelName);

     List<Channel> findAllByJoinedAppUsers(AppUser appUser);
}

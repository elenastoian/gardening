package com.ai.gardening.repository;

import com.ai.gardening.entity.AppUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class AppUserRepositoryTest {
    @Autowired
    private AppUserRepository appUserRepository;

    @Test
    void AppUserRepository_findByEmail_UserIsSavedAndItCanBeFoundInTheDB() {
        // create a new user entity and save it to the repository
        AppUser user = new AppUser();
        user.setEmail("test@example.com");
        user.setPassword("password");
        appUserRepository.save(user);

        // call the findByEmail method and assert the result
        Optional<AppUser> result = appUserRepository.findByEmail("test@example.com");
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
        assertThat(result.get().getPassword()).isEqualTo("password");
    }
}
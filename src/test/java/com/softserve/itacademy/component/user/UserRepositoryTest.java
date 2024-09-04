package com.softserve.itacademy.component.user;

import com.softserve.itacademy.model.User;
import com.softserve.itacademy.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class UserRepositoryTest {

    private final UserRepository userRepository;

    @Autowired
    public UserRepositoryTest(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Test
    void testGetByEmail_1() {
        User user1 = new User();
        user1.setFirstName("Mike");
        user1.setLastName("Green");
        user1.setEmail("mike@mail.com");
        user1.setPassword("1111");
        userRepository.save(user1);

        User user2 = new User();
        user2.setFirstName("Nick");
        user2.setLastName("Brown");
        user2.setEmail("nick@mail.com");
        user2.setPassword("2222");

        User expected = userRepository.save(user2);
        User actual = userRepository.findByEmail("nick@mail.com").orElseThrow();

        assertEquals(expected, actual);
    }

    @Test
    void testGetByEmail_2() {
        User user = new User();
        user.setFirstName("Mike");
        user.setLastName("Green");
        user.setEmail("mike@mail.com");
        user.setPassword("1111");
        userRepository.save(user);

        Optional<User> actual = userRepository.findByEmail("nick@mail.com");
        
        assertThat(actual).isEmpty();
    }
}

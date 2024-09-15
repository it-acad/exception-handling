package com.softserve.itacademy.component.user;

import com.softserve.itacademy.dto.userDto.UpdateUserDto;
import com.softserve.itacademy.dto.userDto.UserDto;
import com.softserve.itacademy.dto.userDto.UserDtoConverter;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.model.UserRole;
import com.softserve.itacademy.repository.UserRepository;
import com.softserve.itacademy.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDtoConverter userDtoConverter;

    @InjectMocks
    private UserService userService;

    private User expected;

    @BeforeEach
    public void setUp() {
        expected = new User();
        expected.setId(1L);
        expected.setFirstName("Mike");
        expected.setLastName("Green");
        expected.setEmail("green@mail.com");
        expected.setPassword("Qwerty1!");
        expected.setRole(UserRole.USER);
    }

    @AfterEach
    public void tearDown() {
        expected = null;
    }

    @Test
    void testCorrectCreate() {
        when(userRepository.save(expected)).thenReturn(expected);
        User actual = userService.create(expected);
        assertEquals(expected, actual);
        verify(userRepository, times(1)).save(expected);
    }

    @Test
    void testExceptionCreate() {
        Exception exception = assertThrows(RuntimeException.class, () -> userService.create(null));
        assertEquals("User cannot be null", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testCorrectReadById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(expected));
        User actual = userService.readById(anyLong());
        assertEquals(expected, actual);
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void testExceptionReadById() {
        Exception exception = assertThrows(EntityNotFoundException.class, () -> userService.readById(-1L));
        assertEquals("User with id -1 not found", exception.getMessage());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void testUpdateWithAdminRole() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setRole(UserRole.ADMIN);
        existingUser.setFirstName("John");
        existingUser.setLastName("Doe");
        existingUser.setEmail("john.doe@mail.com");
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setId(1L);
        updateUserDto.setRole(UserRole.USER);
        when(userRepository.findById(updateUserDto.getId())).thenReturn(Optional.of(existingUser));

        doNothing().when(userDtoConverter).fillFields(existingUser, updateUserDto);
        when(userRepository.save(existingUser)).thenReturn(existingUser);
        when(userDtoConverter.toDto(existingUser)).thenReturn(new UserDto());

        userService.update(updateUserDto);
        assertEquals(UserRole.USER, existingUser.getRole());
        verify(userRepository, times(1)).save(existingUser);
        verify(userRepository, times(1)).findById(updateUserDto.getId());
        verify(userDtoConverter, times(1)).fillFields(existingUser, updateUserDto);
        verify(userDtoConverter, times(1)).toDto(existingUser);
    }


    @Test
    void testDelete() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        doNothing().when(userRepository).delete(any(User.class));
        userService.delete(1L);

        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).delete(any(User.class));
    }

    @Test
    void testGetAll() {
        List<User> expectedUsers = List.of(new User(), new User(), new User());

        when(userRepository.findAll()).thenReturn(expectedUsers);
        List<User> actual = userService.getAll();
        assertEquals(expectedUsers, actual);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testFindByUsername() {
        when(userRepository.findByEmail("green@mail.com")).thenReturn(Optional.of(expected));
        Optional<User> actual = userService.findByUsername("green@mail.com");
        assertThat(actual).isPresent();
        assertEquals(expected, actual.get());
        verify(userRepository, times(1)).findByEmail("green@mail.com");
    }

    @Test
    void testFindById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(expected));
        when(userDtoConverter.toDto(expected)).thenReturn(new UserDto());

        Optional<UserDto> actual = userService.findById(1L);
        assertThat(actual).isPresent();
        verify(userRepository, times(1)).findById(anyLong());
        verify(userDtoConverter, times(1)).toDto(expected);
    }

    @Test
    void testFindAll() {
        List<User> users = List.of(expected);
        when(userRepository.findAll()).thenReturn(users);
        when(userDtoConverter.toDto(expected)).thenReturn(new UserDto());

        List<UserDto> actual = userService.findAll();
        assertEquals(1, actual.size());
        verify(userRepository, times(1)).findAll();
        verify(userDtoConverter, times(1)).toDto(expected);
    }
}

package com.softserve.itacademy.service;

import com.softserve.itacademy.dto.userDto.UpdateUserDto;
import com.softserve.itacademy.dto.userDto.UserDto;
import com.softserve.itacademy.dto.userDto.UserDtoConverter;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.model.UserRole;
import com.softserve.itacademy.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserDtoConverter userDtoConverter;

    public User create(User user) {
        log.info("Creating a new user: {}", user);
        if (user == null) {
            log.error("User creation failed: User is null");
            throw new RuntimeException("User cannot be null");
        }
        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {}", savedUser.getId());
        return savedUser;
    }

    public User readById(long id) {
        log.info("Reading user with ID: {}", id);
        return userRepository.findById(id).orElseThrow(() -> {
            log.error("User with ID {} not found", id);
            return new EntityNotFoundException("User with id " + id + " not found");
        });
    }

    public UserDto update(UpdateUserDto updateUserDto) {
        log.info("Updating user with ID: {}", updateUserDto.getId());
        User user = userRepository.findById(updateUserDto.getId()).orElseThrow(() -> {
            log.error("User with ID {} not found", updateUserDto.getId());
            return new EntityNotFoundException("User with id " + updateUserDto.getId() + " not found");
        });

        if (user.getRole() == UserRole.ADMIN) {
            log.debug("Updating role for user with ID: {}", user.getId());
            user.setRole(updateUserDto.getRole());
        }
        userDtoConverter.fillFields(user, updateUserDto);
        User updatedUser = userRepository.save(user);
        log.info("User with ID {} updated successfully", updatedUser.getId());
        return userDtoConverter.toDto(updatedUser);
    }

    public void delete(long id) {
        log.info("Deleting user with ID: {}", id);
        User user = readById(id);
        userRepository.delete(user);
        log.info("User with ID {} deleted successfully", id);
    }

    public List<User> getAll() {
        log.info("Fetching all users");
        List<User> users = userRepository.findAll();
        log.debug("Fetched users: {}", users);
        return users;
    }

    public Optional<User> findByUsername(String username) {
        log.info("Finding user by username: {}", username);
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isPresent()) {
            log.debug("User found: {}", user.get());
        } else {
            log.warn("User with username {} not found", username);
        }
        return user;
    }

    public Optional<UserDto> findById(long id) {
        log.info("Finding user by ID: {}", id);
        Optional<UserDto> userDto = userRepository.findById(id).map(userDtoConverter::toDto);
        if (userDto.isPresent()) {
            log.debug("User DTO found: {}", userDto.get());
        } else {
            log.warn("User DTO with ID {} not found", id);
        }
        return userDto;
    }

    public UserDto findByIdThrowing(long id) {
        log.info("Finding user by ID with exception if not found: {}", id);
        return userRepository.findById(id).map(userDtoConverter::toDto).orElseThrow(() -> {
            log.error("User with ID {} not found", id);
            return new EntityNotFoundException("User with id " + id + " not found");
        });
    }

    public List<UserDto> findAll() {
        log.info("Fetching all user DTOs");
        List<UserDto> userDtos = userRepository.findAll().stream().map(userDtoConverter::toDto).toList();
        log.debug("Fetched user DTOs: {}", userDtos);
        return userDtos;
    }
}

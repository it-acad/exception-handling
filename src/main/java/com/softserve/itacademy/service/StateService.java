package com.softserve.itacademy.service;

import com.softserve.itacademy.model.State;
import com.softserve.itacademy.dto.StateDto;
import com.softserve.itacademy.repository.StateRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class StateService {

    private final StateRepository stateRepository;

    public State create(State state) {
        if (state == null){
            throw new RuntimeException("State cannot be null");
        }
        state = stateRepository.save(state);
        return state;
    }

    public State readById(long id) {
        State state = stateRepository.findById(id)
                .orElseThrow(() -> {
                    return new RuntimeException("State with id " + id + " not found");
                });
        return state;
    }

    public State update(State state) {
        if (state == null){
            throw new RuntimeException("State cannot be null");
        }
        readById(state.getId());
        state = stateRepository.save(state);
        return state;

    }

    public void delete(long id) {
        State state = readById(id);
        stateRepository.delete(state);
    }

    public List<State> getAll() {
        List<State> states = stateRepository.findAllByOrderById();
        return states;
    }

    public State getByName(String name) {
        State state = stateRepository.findByName(name);

        if (state != null) {
            return state;
        }
        throw new RuntimeException("State with name '" + name + "' not found");
    }

    public List<StateDto> findAll() {
        List<StateDto> dtos = stateRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
        return dtos;
    }

    private StateDto toDto(State state) {
        StateDto dto = StateDto.builder()
                .name(state.getName())
                .build();
        return dto;
    }
}

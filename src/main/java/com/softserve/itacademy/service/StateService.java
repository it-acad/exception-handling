package com.softserve.itacademy.service;

import com.softserve.itacademy.model.State;
import com.softserve.itacademy.dto.StateDto;
import com.softserve.itacademy.repository.StateRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StateService {

    private final StateRepository stateRepository;

    public State create(State state) {
        log.debug("Creating state {}", state);
        if (state == null){
            throw new RuntimeException("State cannot be null");
        }

        state = stateRepository.save(state);
        log.debug("State {} was created", state);
        return state;
    }

    public State readById(long id) {
        log.debug("Fetching state with id: {}", id);
        State state = stateRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("State with id {} doesn't exists", id);
                    return new RuntimeException("State with id " + id + " not found");
                });
        log.debug("Get state {}", state);
        return state;
    }

    public State update(State state) {
        log.debug("Updating state {}", state);
        if (state == null){
            throw new RuntimeException("State cannot be null");
        }
        readById(state.getId());
        state = stateRepository.save(state);
        log.debug("State {} was updated", state);
        return state;

    }

    public void delete(long id) {
        log.debug("Deleting state with id {}", id);
        State state = readById(id);
        stateRepository.delete(state);
        log.debug("State {} was deleted", state);
    }

    public List<State> getAll() {
        log.debug("Fetching all states");
        List<State> states = stateRepository.findAllByOrderById();
        log.debug("Get all states ");
        return states;
    }

    public State getByName(String name) {
        log.debug("Fetching state with name {}", name);
        State state = stateRepository.findByName(name);

        if (state != null) {
            log.debug("State found: {}", state);
            return state;
        }
        log.warn("No state found with name: {}", name);
        throw new RuntimeException("State with name '" + name + "' not found");
    }

    public List<StateDto> findAll() {
        log.debug("Fetching all states as StateDto}");
        List<StateDto> dtos = stateRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
        log.debug("Get all states as StateDto");
        return dtos;
    }

    private StateDto toDto(State state) {
        log.debug("Converting {} from State object to StateDto object", state);
        StateDto dto = StateDto.builder()
                .name(state.getName())
                .build();
        log.debug("Converted {} from State object to StateDto object", state);
        return dto;
    }
}

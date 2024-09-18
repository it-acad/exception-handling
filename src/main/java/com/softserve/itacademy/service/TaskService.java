package com.softserve.itacademy.service;

import com.softserve.itacademy.dto.TaskTransformer;
import com.softserve.itacademy.dto.TaskDto;
import com.softserve.itacademy.model.Task;
import com.softserve.itacademy.repository.StateRepository;
import com.softserve.itacademy.repository.ToDoRepository;
import com.softserve.itacademy.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ToDoRepository toDoRepository;
    private final StateRepository stateRepository;
    private final TaskTransformer taskTransformer;

    public TaskDto create(TaskDto taskDto) {
        log.info("Creating task with details: {}", taskDto);
        if (taskDto == null) {
            log.error("Failed to create task: taskDto is null");
            throw new RuntimeException("Task cannot be null");
        }
        Task task = taskTransformer.fillEntityFields(
                new Task(),
                taskDto,
                toDoRepository.findById(taskDto.getTodoId()).orElseThrow(() -> {
                    log.error("ToDo with id {} not found", taskDto.getTodoId());
                    return new RuntimeException("ToDo not found");
                }),
                stateRepository.findByName("New")
        );

        Task savedTask = taskRepository.save(task);
        log.info("Task with id {} created successfully", savedTask.getId());
        return taskTransformer.convertToDto(savedTask);
    }

    public Task readById(long id) {
        log.info("Reading task with id {}", id);
        return taskRepository.findById(id).orElseThrow(() -> {
            log.error("Task with id {} not found", id);
            return new RuntimeException("Task with id " + id + " not found");
        });
    }

    public TaskDto update(TaskDto taskDto) {
        log.info("Updating task with id {}", taskDto.getId());
        if (taskDto == null) {
            log.error("Failed to update task: taskDto is null");
            throw new RuntimeException("Task cannot be 'null'");
        }

        Task existingTask = taskRepository.findById(taskDto.getId()).orElseThrow(() -> {
            log.error("Task with id {} not found", taskDto.getId());
            return new RuntimeException("Task with id " + taskDto.getId() + " not found");
        });

        Task updatedTask = taskTransformer.fillEntityFields(
                existingTask,
                taskDto,
                toDoRepository.findById(taskDto.getTodoId()).orElseThrow(() -> {
                    log.error("ToDo with id {} not found", taskDto.getTodoId());
                    return new RuntimeException("ToDo not found");
                }),
                stateRepository.findById(taskDto.getStateId()).orElseThrow(() -> {
                    log.error("State with id {} not found", taskDto.getStateId());
                    return new RuntimeException("State not found");
                })
        );

        Task savedTask = taskRepository.save(updatedTask);
        log.info("Task with id {} updated successfully", savedTask.getId());
        return taskTransformer.convertToDto(savedTask);
    }

    public void delete(long id) {
        log.info("Deleting task with id {}", id);
        Task task = readById(id);
        taskRepository.delete(task);
        log.info("Task with id {} was deleted", id);
    }

    public List<Task> getAll() {
        log.info("Retrieving all tasks");
        return taskRepository.findAll();
    }

    public List<Task> getByTodoId(long todoId) {
        log.info("Retrieving tasks for ToDo with id {}", todoId);
        return taskRepository.getByTodoId(todoId);
    }
}

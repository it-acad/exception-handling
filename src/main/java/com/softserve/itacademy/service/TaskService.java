package com.softserve.itacademy.service;

import com.softserve.itacademy.dto.TaskTransformer;
import com.softserve.itacademy.dto.TaskDto;
import com.softserve.itacademy.model.Task;
import com.softserve.itacademy.repository.StateRepository;
import com.softserve.itacademy.repository.ToDoRepository;
import com.softserve.itacademy.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ToDoRepository toDoRepository;
    private final StateRepository stateRepository;
    private final TaskTransformer taskTransformer;

    public TaskDto create(TaskDto taskDto) {
        if (taskDto == null){
            throw new RuntimeException("Task cannot be null");
        }
        Task task = taskTransformer.fillEntityFields(
                new Task(),
                taskDto,
                toDoRepository.findById(taskDto.getTodoId()).orElseThrow(),
                stateRepository.findByName("New")
        );

        Task savedTask = taskRepository.save(task);
        return taskTransformer.convertToDto(savedTask);
    }


    public Task readById(long id) {

        return taskRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Task with id " + id + " not found"));
    }

    public Task update(Task task) {
            readById(task.getId());
            return taskRepository.save(task);
    }

    public void delete(long id) {
        Task task = readById(id);
        taskRepository.delete(task);
    }

    public List<Task> getAll() {
        return taskRepository.findAll();
    }

    public List<Task> getByTodoId(long todoId) {
        return taskRepository.getByTodoId(todoId);
    }
}
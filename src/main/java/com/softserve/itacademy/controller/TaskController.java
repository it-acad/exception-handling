package com.softserve.itacademy.controller;

import com.softserve.itacademy.dto.TaskDto;
import com.softserve.itacademy.model.TaskPriority;
import com.softserve.itacademy.dto.TaskTransformer;
import com.softserve.itacademy.service.StateService;
import com.softserve.itacademy.service.TaskService;
import com.softserve.itacademy.service.ToDoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final ToDoService todoService;
    private final StateService stateService;
    private final TaskTransformer taskTransformer;

    @GetMapping("/create/todos/{todo_id}")
    public String create(@PathVariable("todo_id") long todoId, Model model) {
        log.info("Received request to get todo with id {} for creating new task", todoId);
        model.addAttribute("task", new TaskDto());
        model.addAttribute("todo", todoService.readById(todoId));
        model.addAttribute("priorities", TaskPriority.values());
        System.out.println("Returning todo with id" + todoId + " for creating new task for this todo");
        return "create-task";
    }

    @PostMapping("/create/todos/{todo_id}")
    public String create(@PathVariable("todo_id") long todoId, Model model,
                         @Validated @ModelAttribute("task") TaskDto taskDto, BindingResult result) {
        System.out.println("Received request to create task " + taskDto);
        if (result.hasErrors()) {
            model.addAttribute("todo", todoService.readById(todoId));
            model.addAttribute("priorities", TaskPriority.values());
            System.out.println("Validation error for " + taskDto);
            return "create-task";
        }

        taskService.create(taskDto);
        System.out.println("Task " + taskDto + " was created");

        return "redirect:/todos/" + todoId + "/tasks";
    }

    @GetMapping("/{task_id}/update/todos/{todo_id}")
    public String taskUpdateForm(@PathVariable("task_id") long taskId, @PathVariable("todo_id") long todoId, Model model) {
        System.out.println("Received request to get task with id " + taskId + "from todo with id " + todoId + "for updating this task");
        TaskDto taskDto = taskTransformer.convertToDto(taskService.readById(taskId));
        model.addAttribute("task", taskDto);
        model.addAttribute("priorities", TaskPriority.values());
        model.addAttribute("states", stateService.getAll());
        System.out.println("Task " + taskDto + " was loaded");
        return "update-task";
    }

    @PostMapping("/{task_id}/update/todos/{todo_id}")
    public String update(@PathVariable("task_id") long taskId, @PathVariable("todo_id") long todoId, Model model,
                         @Validated @ModelAttribute("task") TaskDto taskDto, BindingResult result) {
        System.out.println("Received request to update task " + taskDto);
        if (result.hasErrors()) {
            model.addAttribute("priorities", TaskPriority.values());
            model.addAttribute("states", stateService.getAll());
            return "update-task";
        }

        taskService.update(taskDto);
        System.out.println("Task " + taskDto + " was updated");
        return "redirect:/todos/" + todoId + "/tasks";
    }

    @GetMapping("/{task_id}/delete/todos/{todo_id}")
    public String delete(@PathVariable("task_id") long taskId, @PathVariable("todo_id") long todoId) {
        System.out.println("Received request to delete task with id " + taskId);
        taskService.delete(taskId);
        System.out.println("Task with id " + taskId + " was deleted");
        return "redirect:/todos/" + todoId + "/tasks";
    }
}

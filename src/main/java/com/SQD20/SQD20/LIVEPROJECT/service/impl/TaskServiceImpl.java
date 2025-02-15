package com.SQD20.SQD20.LIVEPROJECT.service.impl;

import com.SQD20.SQD20.LIVEPROJECT.domain.entites.AppUser;
import com.SQD20.SQD20.LIVEPROJECT.domain.entites.Task;
import com.SQD20.SQD20.LIVEPROJECT.domain.entites.TaskList;
import com.SQD20.SQD20.LIVEPROJECT.infrastructure.exception.TaskListNotFoundException;
import com.SQD20.SQD20.LIVEPROJECT.infrastructure.exception.TaskNotFoundException;
import com.SQD20.SQD20.LIVEPROJECT.infrastructure.exception.UsernameNotFoundException;
import com.SQD20.SQD20.LIVEPROJECT.payload.response.TasksResponse;
import com.SQD20.SQD20.LIVEPROJECT.repository.TaskListRepository;
import com.SQD20.SQD20.LIVEPROJECT.repository.TaskRepository;
import com.SQD20.SQD20.LIVEPROJECT.repository.UserRepository;
import com.SQD20.SQD20.LIVEPROJECT.service.TaskService;
import lombok.RequiredArgsConstructor;
import com.SQD20.SQD20.LIVEPROJECT.payload.request.TaskRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    private final TaskListRepository taskListRepository;

    @Transactional
    @Override
    public void updateTask(Long taskId, TaskRequest updateRequest) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + taskId));

        if (updateRequest.getTitle() != null) task.setTitle(updateRequest.getTitle());
        if (updateRequest.getDescription() != null) task.setDescription(updateRequest.getDescription());
        if (updateRequest.getDeadline() != null) task.setDeadline(updateRequest.getDeadline());
        if (updateRequest.getPriorityLevel() != null) task.setPriorityLevel(updateRequest.getPriorityLevel());
        if (updateRequest.getStatus() != null) task.setStatus(updateRequest.getStatus());

        taskRepository.save(task);
    }

    @Override
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new TaskNotFoundException("Task Not Found"));

        taskRepository.delete(task);
    }

    @Override
    public TaskRequest createTask(Long userId, Long taskListId, TaskRequest createRequest) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User with " + userId + " not found"));

        TaskList taskList = taskListRepository.findById(taskListId).orElse(null);
        if (taskList == null) {
            taskList = taskListRepository.findById(1L)
                    .orElseThrow(() -> new TaskListNotFoundException("No task list found"));
        }

            Task task = new Task();
            task.setTitle(createRequest.getTitle());
            task.setDescription(createRequest.getDescription());
            task.setDeadline(createRequest.getDeadline());
            task.setPriorityLevel(createRequest.getPriorityLevel());
            task.setStatus(createRequest.getStatus());
            task.setUser(user);
            task.setTaskList(taskList);
            taskRepository.save(task);
            return createRequest;
    }

    @Override
    public List<TasksResponse> getTasksByTaskListId(Long taskListId) {
        List<Task> tasks = taskRepository.findByTaskListId(taskListId);
        return tasks.stream()
                .map(this::convertToTaskResponse)
                .collect(Collectors.toList());
    }

    private TasksResponse convertToTaskResponse(Task task) {
        return new TasksResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDeadline(),
                task.getPriorityLevel(),
                task.getStatus()
        );
    }

}


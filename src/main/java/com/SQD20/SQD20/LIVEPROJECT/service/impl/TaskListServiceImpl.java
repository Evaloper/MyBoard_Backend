package com.SQD20.SQD20.LIVEPROJECT.service.impl;

import com.SQD20.SQD20.LIVEPROJECT.domain.entites.AppUser;
import com.SQD20.SQD20.LIVEPROJECT.domain.entites.Task;
import com.SQD20.SQD20.LIVEPROJECT.domain.entites.TaskList;
import com.SQD20.SQD20.LIVEPROJECT.infrastructure.exception.TaskListNotFoundException;
import com.SQD20.SQD20.LIVEPROJECT.infrastructure.exception.TaskNotFoundException;
import com.SQD20.SQD20.LIVEPROJECT.infrastructure.exception.UserNotFoundException;
import com.SQD20.SQD20.LIVEPROJECT.payload.request.TaskListRequest;
import com.SQD20.SQD20.LIVEPROJECT.payload.request.TaskRequest;
import com.SQD20.SQD20.LIVEPROJECT.payload.response.TaskResponse;
import com.SQD20.SQD20.LIVEPROJECT.repository.TaskListRepository;
import com.SQD20.SQD20.LIVEPROJECT.repository.TaskRepository;
import com.SQD20.SQD20.LIVEPROJECT.repository.UserRepository;
import com.SQD20.SQD20.LIVEPROJECT.service.TaskListService;
import com.SQD20.SQD20.LIVEPROJECT.service.UserService;
import com.SQD20.SQD20.LIVEPROJECT.utils.TaskListUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskListServiceImpl implements TaskListService {
    private  final UserRepository userRepository;

    private final TaskRepository taskRepository;
    private final TaskListRepository taskListRepository;

    @Transactional
    @Override
    public TaskList updateTaskListByUserId(Long taskListId, TaskListRequest taskListRequest) {
        TaskList taskList = taskListRepository.findById(taskListId)
                .orElseThrow(() -> new TaskListNotFoundException("Task list not found with id " + taskListId));

        // Update task list fields
        if (taskListRequest.getTitle() != null) {
            taskList.setTitle(taskListRequest.getTitle()); // Update the title field of the fetched TaskList entity
        }
        if (taskListRequest.getDescription() != null){
            taskList.setDescription(taskListRequest.getDescription()); // Update the description field of the fetched TaskList entity
        }

        return taskListRepository.save(taskList); // Save the updated TaskList entity
    }

    @Override
    public TaskResponse createTaskList(Long userId, TaskListRequest request) {
        AppUser user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User Not Found with ID : " + userId));

        TaskList newTaskList = new TaskList();
        newTaskList.setTitle(request.getTitle());
        newTaskList.setDescription(request.getDescription());
        newTaskList.setUser(user);

        taskListRepository.save(newTaskList);
        return TaskResponse.builder()
                .responseCode(TaskListUtils.TASK_LIST_CREATION_SUCCESS_CODE)
                .responseMessage(TaskListUtils.TASK_LIST_CREATION_MESSAGE)
                .title(newTaskList.getTitle())
                .description(newTaskList.getDescription())
                .build();
    }


    @Override
    public TaskList deleteTask(long id) {
        Optional<TaskList> taskListOptional = taskListRepository.findById(id);
        if (taskListOptional.isPresent()){
            TaskList taskList = taskListOptional.get();
            List<Task> tasks = taskList.getTasks();
            taskRepository.deleteAll(tasks);
            taskListRepository.delete(taskList);
            return taskList;
        }
        throw new TaskNotFoundException("Task not found");
    }

}

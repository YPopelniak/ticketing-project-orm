package com.cydeo.service.impl;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.TaskDTO;
import com.cydeo.entity.Project;
import com.cydeo.entity.Task;
import com.cydeo.entity.User;
import com.cydeo.enums.Status;
import com.cydeo.mapper.ProjectMapper;
import com.cydeo.mapper.TaskMapper;
import com.cydeo.repository.TaskRepository;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.TaskService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskServiceImp implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final ProjectMapper projectMapper;
    private final UserRepository userRepository;
    public TaskServiceImp(TaskRepository taskRepository, ProjectMapper projectMapper, TaskMapper taskMapper, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.projectMapper = projectMapper;
        this.taskMapper = taskMapper;
        this.userRepository = userRepository;
    }

    @Override
    public TaskDTO findById(Long id) {

        Optional<Task> task = taskRepository.findById(id);
        if(task.isPresent()){
            return taskMapper.convertToDTO(task.get());
        }
        return null ;
    }

    @Override
    public List<TaskDTO> listAllTasks() {
        List<Task> taskList = taskRepository.findAll();
        return taskList.stream().map(taskMapper::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public void save(TaskDTO dto) {
        dto.setTaskStatus(Status.OPEN);
        Task task = taskMapper.convertToEntity(dto);
        taskRepository.save(task);
    }

    @Override
    public void update(TaskDTO dto) {

        Optional<Task> task = taskRepository.findById(dto.getId());

        Task convertTask = taskMapper.convertToEntity(dto);

        if (task.isPresent()) {
            convertTask.setId(task.get().getId());
        }
        convertTask.setTaskStatus(dto.getTaskStatus() == null ? task.get().getTaskStatus() :dto.getTaskStatus() );
        convertTask.setAssignedDate(task.get().getAssignedDate());

        taskRepository.save(convertTask);
    }

    @Override
    public void delete(Long id) {
        Optional<Task> foundTask = taskRepository.findById(id);

        if(foundTask.isPresent()){
            foundTask.get().setIsDeleted(true);
            taskRepository.save(foundTask.get());
        }
    }

    @Override
    public int totalNonCompletedTasks(String projectCode) {
        return taskRepository.totalNonCompletedTasks(projectCode);
    }

    @Override
    public int totalCompletedTasks(String projectCode) {
        return taskRepository.totalCompletedTasks(projectCode);
    }

    @Override
    public void deleteByProject(ProjectDTO project) {
        List<TaskDTO> list = listAllByProject(project);
        list.forEach(taskDTO -> delete(taskDTO.getId()));
    }

    @Override
    public void completeByProject(ProjectDTO project) {
        List<TaskDTO> list = listAllByProject(project);
        list.forEach(taskDTO -> {
            taskDTO.setTaskStatus(Status.COMPLETE);
            update(taskDTO);
        });
    }

    @Override
    public List<TaskDTO> listAllTasksByStatusIsNot(Status status) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User loggedInUser = userRepository.findByUserName(username);
        List<Task> list = taskRepository.findAllByTaskStatusIsNotAndAssignedEmployee(status, loggedInUser);
        return list.stream().map(taskMapper::convertToDTO).collect(Collectors.toList());

    }

    @Override
    public void updateStatus(TaskDTO dto) {
        Optional<Task> task = taskRepository.findById(dto.getId());

        if (task.isPresent()) {
            task.get().setTaskStatus(dto.getTaskStatus());
            taskRepository.save(task.get());
        }

    }

    @Override
    public List<TaskDTO> listAllTasksByStatus(Status status) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User loggedInUser = userRepository.findByUserName(username);
        List<Task> list = taskRepository.findAllByTaskStatusAndAssignedEmployee(status, loggedInUser);
        return list.stream().map(taskMapper::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> readAllByAssignedEmployee(User assignedEmployee) {
        List<Task> list = taskRepository.findAllByAssignedEmployee(assignedEmployee);
        return list.stream().map(taskMapper::convertToDTO).collect(Collectors.toList());
    }

    private List<TaskDTO> listAllByProject(ProjectDTO project) {
        List<Task> list = taskRepository.findAllByProject(projectMapper.convertToEntity(project));
        return list.stream().map(taskMapper::convertToDTO).collect(Collectors.toList());
    }


}

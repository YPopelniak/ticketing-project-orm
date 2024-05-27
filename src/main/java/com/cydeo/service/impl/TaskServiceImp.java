package com.cydeo.service.impl;

import com.cydeo.dto.TaskDTO;
import com.cydeo.entity.Project;
import com.cydeo.entity.Task;
import com.cydeo.enums.Status;
import com.cydeo.mapper.TaskMapper;
import com.cydeo.repository.TaskRepository;
import com.cydeo.service.TaskService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskServiceImp implements TaskService {

    private final TaskRepository taskRepository;

    private final TaskMapper taskMapper;
    public TaskServiceImp(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
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
        convertTask.setTaskStatus(task.get().getTaskStatus());
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
}

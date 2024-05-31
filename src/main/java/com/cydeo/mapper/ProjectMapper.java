package com.cydeo.mapper;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.RoleDTO;
import com.cydeo.entity.Project;
import com.cydeo.entity.Role;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

@Component
public class ProjectMapper {

    private final ModelMapper modelMapper;

    public ProjectMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Project convertToEntity(ProjectDTO dto){

        return modelMapper.map(dto,Project.class);
    }

    public ProjectDTO convertToDTO(Project entity){
        return modelMapper.map(entity,ProjectDTO.class);
    }

    public <T> T convert(Object objectToBeConverted, T convertedObject) {
        return modelMapper.map(objectToBeConverted, (Type) convertedObject.getClass());
    }
}

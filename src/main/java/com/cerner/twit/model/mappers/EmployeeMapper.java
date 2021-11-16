package com.cerner.twit.model.mappers;

import com.cerner.twit.domain.Employee;
import com.cerner.twit.model.EmployeeDTO;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmployeeMapper extends EntityMapper<EmployeeDTO, Employee> {
    List<EmployeeDTO> toDto(List<Employee> entityList);

    default Employee fromId(Long id) {
        if (id == null) {
            return null;
        }
        Employee employee = new Employee();
        employee.setId(id);
        return employee;
    }
}

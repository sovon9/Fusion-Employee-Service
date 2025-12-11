package com.sovon9.employee_service.controller;

import com.sovon9.employee_service.entities.Employee;
import com.sovon9.employee_service.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @QueryMapping("employees")
    public List<Employee> employees(@Argument Map<String, Object> where)
    {
        return employeeRepository.findAll();
    }

}

package com.sovon9.employee_service.controller;

import com.sovon9.employee_service.entities.Employee;
import com.sovon9.employee_service.entities.Node;
import com.sovon9.employee_service.repository.EmployeeRepository;
import com.sovon9.employee_service.util.GlobalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.query.ScrollSubrange;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @QueryMapping("employees")
    public Window<Employee> employees(@Argument Map<String, Object> where, ScrollSubrange subrange)
    {
        // Logic to determine offset from the opaque 'after' cursor
        ScrollPosition position = subrange.position().orElse(ScrollPosition.offset());

        Limit limit = Limit.of(subrange.count().orElse(10));
        Sort sort = Sort.by(Sort.Order.asc("eid"));
        // Spring Data JPA returns a Page object
        return employeeRepository.findBy(position, limit, sort);
    }

    @QueryMapping("node")
    public Node node(@Argument String id)
    {
        String[] decode = GlobalUtil.fromGlobalId(id);
        String type = decode[0];
        Long eid = Long.parseLong(decode[1]);
        if(!type.equals("Employee"))
        {
            return null;
        }
        return employeeRepository.findById(eid).orElse(null);
    }

}

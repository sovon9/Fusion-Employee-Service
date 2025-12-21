package com.sovon9.employee_service.repository;

import com.sovon9.employee_service.entities.Employee;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Window;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.graphql.data.query.ScrollSubrange;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>{

    Window<Employee> findBy(ScrollPosition position, Limit limit, Sort sort);

}

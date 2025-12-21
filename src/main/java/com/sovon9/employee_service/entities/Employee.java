package com.sovon9.employee_service.entities;

import com.sovon9.employee_service.util.GlobalUtil;
import jakarta.persistence.*;

@Entity
@Table
public class Employee implements Node{

    @Transient
    private String id; // for relay pagination global id

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long eid;
    @Column
    private String name;
    @Column
    private Double salary;

    @ManyToOne
    @JoinColumn(name = "dept_id")
    private Department department;

    public Long getEid() {
        return eid;
    }

    public void setEid(Long eid) {
        this.eid = eid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @Override
    public String getId() {
        return GlobalUtil.toGlobalId("Employee", eid);
    }

    @Override
    public void setId(String id) {
        this.id=id;
    }

//    @PostLoad
//    public void postLoadProcess()
//    {
//        this.id =
//    }
}

package com.csit321g5.learnsprint.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "faculty")
@DiscriminatorValue("FACULTY")
public class Faculty extends User {

    @Column(length = 30)
    private String employeeNo;

    @Column(length = 80)
    private String department;

    public String getEmployeeNo() {
        return employeeNo;
    }

    public void setEmployeeNo(String employeeNo) {
        this.employeeNo = employeeNo;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}

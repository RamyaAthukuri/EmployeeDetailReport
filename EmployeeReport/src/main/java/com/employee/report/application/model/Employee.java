package com.employee.report.application.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class Employee {
    private String id;
    private String firstName;
    private String lastName;
    private double salary;
    private String managerId;
    private List<Employee> subordinates = new ArrayList<>();

}
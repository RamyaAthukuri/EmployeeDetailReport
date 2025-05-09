package com.employee.report.application.service;

import com.employee.report.application.model.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;
import java.io.*;

@Service
@Slf4j
public class EmployeeAnalyzerService {

    private Map<String, Employee> employeeMap = new HashMap<>();

    private Employee ceo;

    public void loadDataFromFile(MultipartFile file) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        String line = reader.readLine(); // skip header
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            Employee emp = new Employee(parts[0], parts[1], parts[2],
                    Double.parseDouble(parts[3]), parts.length > 4 ? parts[4] : null, new ArrayList<>());
            employeeMap.put(emp.getId(), emp);
        }

        for (Employee emp : employeeMap.values()) {
            if (emp.getManagerId() != null && employeeMap.containsKey(emp.getManagerId())) {
                employeeMap.get(emp.getManagerId()).getSubordinates().add(emp);
            } else {
                ceo = emp;
            }
        }
    }

    public void analyzeManagerSalaries() {
        try {
            for (Employee manager : employeeMap.values()) {
                if (!manager.getSubordinates().isEmpty()) {
                    double avg = manager.getSubordinates().stream().mapToDouble(Employee::getSalary).average().orElse(0);
                    double lowerBound = avg * 1.2;
                    double upperBound = avg * 1.5;

                    if (manager.getSalary() < lowerBound) {
                        log.info("Manager {} earns LESS than Subordinates by {} (Lower Bound: {})",
                                manager.getFirstName(), lowerBound - manager.getSalary(), lowerBound);
                    } else if (manager.getSalary() > upperBound) {
                        log.info("Manager {} earns MORE than Subordinates by {} (Upper Bound: {})",
                                manager.getFirstName(), manager.getSalary() - upperBound, upperBound);
                    }
                }
            }
        } catch (Exception e) {
            log.error("An error occurred while analyzing manager salaries: {}", e.getMessage(), e);
        }
    }

    public void analyzeReportingHierarchyDepth() {
        for (Employee emp : employeeMap.values()) {
            int depth = getHierarchyDepth(emp);
            if (depth > 4) {
                log.info("Employee {} has reporting line too long: {} levels",emp.getFirstName()depth);
            }
        }

    }

    private int getHierarchyDepth(Employee emp) {
        int depth = 0;
        while (emp.getManagerId() != null && employeeMap.containsKey(emp.getManagerId())) {
            emp = employeeMap.get(emp.getManagerId());
            if (emp.getManagerId() == null)
                break;
            depth++;
        }
        return depth;
    }
}				
				

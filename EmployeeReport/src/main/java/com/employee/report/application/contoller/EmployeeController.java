package com.employee.report.application.contoller;

import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeAnalyzerService employeeAnalyzerService;

    public EmployeeController(EmployeeAnalyzerService employeeAnalyzerService) {
        this.employeeAnalyzerService = employeeAnalyzerService;
    }


    @PostMapping("/load")
    public String loadFile(@RequestParam("file") MultipartFile file) {
        try {
            employeeAnalyzerService.loadDataFromFile(file);
            return "File loaded successfully.";
        } catch (IOException e) {
            return "Failed to load file: " + e.getMessage();
        }
    }

    @GetMapping("/salary-report")
    public String analyzeSalaries()  {
        employeeAnalyzerService.analyzeManagerSalaries();
        return "Salary analysis completed. Check logs for output.";
    }

    @GetMapping("/hierarchy-report")
    public String analyzeHierarchy(){
        employeeAnalyzerService.analyzeReportingHierarchyDepth();
        return "Hierarchy analysis completed. Check logs for output.";
    }
}


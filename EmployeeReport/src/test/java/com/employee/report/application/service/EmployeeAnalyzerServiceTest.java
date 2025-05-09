package com.employee.report.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.logging.LoggerFactory;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmployeeAnalyzerServiceTest {

    @InjectMocks
    private EmployeeAnalyzerService employeeAnalyzerService;

    @Mock
    private ClassPathResource classPathResource;

    @Mock
    private Logger logger;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        employeeAnalyzerService = spy(new EmployeeAnalyzerService());


    }

    public Logger getLogger() {
        return LoggerFactory.getLogger(EmployeeAnalyzerService.class);
    }


    @Test
    void testAnalyzeReportingHierarchyDepth() throws IOException {
        FileInputStream inputStream = new FileInputStream("src/test/resources/Employees.csv");
        MockMultipartFile file = new MockMultipartFile("file""Employees.csv""text/csv"inputStream);

        doNothing().when(logger).info(anyString()any()any());

        employeeAnalyzerService.loadDataFromFile(file);
        employeeAnalyzerService.analyzeReportingHierarchyDepth();
        verify(employeeAnalyzerService, times(1)).loadDataFromFile(file);
    }

    @Test
    void testAnalyzeManagerSalaries() throws IOException {
        FileInputStream inputStream = new FileInputStream("src/test/resources/Employees.csv");
        MockMultipartFile file = new MockMultipartFile("file", "Employees.csv", "text/csv", inputStream);

        doNothing().when(logger).info(anyString(), any(), any(), any());

        employeeAnalyzerService.loadDataFromFile(file);

        employeeAnalyzerService.analyzeManagerSalaries();
        verify(employeeAnalyzerService, times(1)).loadDataFromFile(file);

    }

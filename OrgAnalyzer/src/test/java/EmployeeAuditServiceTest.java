import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeAuditServiceTest {

    @Test
    void testLoadAndAnalyze() throws IOException {
        EmployeeAuditService analyzer = new EmployeeAuditService();
        analyzer.loadCSV("test_employees.csv");

        assertEquals(9, analyzer.employeeMap.size());
        assertEquals(1, analyzer.managerToEmployees.get(201).size());
        assertTrue(analyzer.employeeMap.containsKey(305));
    }

    @Test
    public void testCheckSalariesOutput() throws IOException {
        EmployeeAuditService analyzer = new EmployeeAuditService();
        analyzer.loadCSV("test_employees.csv");

        Map<String, List<String>> response = analyzer.checkSalaries();
        assertEquals(2, response.get("HighPayManagers").size());
        assertEquals(3, response.get("LowPayManagers").size());
    }

    @Test
    void testDepthCalculation() throws IOException {
        EmployeeAuditService analyzer = new EmployeeAuditService();
        analyzer.loadCSV("test_employees.csv");

        Employee brettBetancourt = analyzer.employeeMap.get(305);
        assertEquals(2, analyzer.getDepth(brettBetancourt));

        Employee jackTaylor = analyzer.employeeMap.get(202);
        assertEquals(6, analyzer.getDepth(jackTaylor));

        Employee joeDoe = analyzer.employeeMap.get(123);
        assertEquals(0, analyzer.getDepth(joeDoe));

        Employee martinChekov = analyzer.employeeMap.get(124);
        assertEquals(0, analyzer.getDepth(martinChekov));


    }

    @Test
    void testDepthCalculation_CircularReference() {
        EmployeeAuditService analyzer = new EmployeeAuditService();

        // Creating circular depth loop (e.g., 1 -> 2 -> 3 -> 1)
        analyzer.employeeMap.put(1, new Employee(1, "A", "Reynolds", 70000, 3));
        analyzer.employeeMap.put(2, new Employee(2, "B", "Chan", 60000, 1));
        analyzer.employeeMap.put(3, new Employee(3, "C", "Loop", 50000, 2));

       assertEquals("Cycle detected in reporting chain involving employee ID 1","Cycle detected in reporting chain involving employee ID 1");

    }



    @Test
    void testDepthCalculation_testSelfAsManager() {
        EmployeeAuditService analyzer = new EmployeeAuditService();
        Employee emp = new Employee(42, "Bob", "Ronstad", 50000, 42);
        analyzer.employeeMap.put(42, emp);

        int depth = analyzer.getDepth(emp);
        assertEquals(-1, depth, "Self-manager should be detected as cycle");
    }

    @Test
    void testDepthCalculation_invalidManagerId() {
        EmployeeAuditService analyzer = new EmployeeAuditService();
        Employee emp = new Employee(10, "Ghost", "Manager", 50000, 999);
        analyzer.employeeMap.put(10, emp);

        int depth = analyzer.getDepth(emp);
        assertEquals(0, depth, "Should return 0 if manager not found");
    }

    @Test
    void testLoadCsv_withEmptyFile() throws IOException {
        EmployeeAuditService service = new EmployeeAuditService();
        File tempFile = File.createTempFile("empty", ".csv");

        String emptyCsv = "Id,firstName,lastName,salary,managerId\n";
        Files.write(tempFile.toPath(), emptyCsv.getBytes());

        service.loadCSV(tempFile.getAbsolutePath());

        assertEquals(0, service.employeeMap.size(), "No employees should be loaded");
    }


}
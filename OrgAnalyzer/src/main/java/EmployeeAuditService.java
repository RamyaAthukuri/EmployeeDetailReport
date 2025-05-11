import java.io.*;
import java.util.*;

public class EmployeeAuditService {
    Map<Integer, Employee> employeeMap = new HashMap<>();
    Map<Integer, List<Employee>> managerToEmployees = new HashMap<>();

    public void loadCSV(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        reader.readLine(); // skip header

        while ((line = reader.readLine()) != null) {
            String[] tokens = line.split(",");
            int id = Integer.parseInt(tokens[0]);
            String firstName = tokens[1];
            String lastName = tokens[2];
            double salary = Double.parseDouble(tokens[3]);
            Integer managerId = null;
            if (tokens.length <= 4) {
                managerId = null;
            } else {
                managerId = Integer.parseInt(tokens[4]);
            }

            Employee emp = new Employee(id, firstName, lastName, salary, managerId);
            employeeMap.put(id, emp);
            if (managerId != null) {
                managerToEmployees.computeIfAbsent(managerId, k -> new ArrayList<>()).add(emp);
            }
        }
        reader.close();
    }

    public Map<String,List<String>> checkSalaries() {
        Map<String,List<String>> response = new HashMap<>();
        List<String> highPayManagers = new ArrayList<>();
        List<String> lowPayManagers = new ArrayList<>();
        for (Integer managerId : managerToEmployees.keySet()) {
            Employee manager = employeeMap.get(managerId);
            List<Employee> subordinates = managerToEmployees.get(managerId);
            double avgSubSalary = subordinates.stream().mapToDouble(e -> e.salary).average().orElse(0);

            double minRequired = avgSubSalary * 1.2;
            double maxAllowed = avgSubSalary * 1.5;

            if (manager.salary < minRequired) {
                System.out.printf(
                        "Manager %s earns less. Required minimum: %.2f, Actual: %.2f, Shortfall: %.2f%n",
                        manager.getFullName(), minRequired, manager.salary, minRequired - manager.salary
                );
                lowPayManagers.add(manager.getFullName());
            } else if (manager.salary > maxAllowed) {
                System.out.printf(
                        "Manager %s earns more. Maximum allowed: %.2f, Actual: %.2f, Excess: %.2f%n",
                        manager.getFullName(), maxAllowed, manager.salary, manager.salary - maxAllowed
                );
                highPayManagers.add(manager.getFullName());
            }
        }
        response.put("HighPayManagers", highPayManagers);
        response.put("LowPayManagers", lowPayManagers);
        System.out.println("checkSalaries : "  + response);
        return response;
    }

    public void checkReportingChainLength() {
        System.out.println("Checking Reporting Chain Length for the employees");

        for (Employee emp : employeeMap.values()) {
            int depth = getDepth(emp);

            if (depth == -1) {
                System.out.printf("Skipping employee ID %d due to invalid reporting structure (e.g., cycle)\n", emp.id);
                continue;
            }

            if (depth > 4) {
                System.out.printf("Employee %s empId %s has reporting depth of %d (exceeds by %d)\n",
                        emp.getFullName(), emp.id, depth, depth - 4);
            }
        }
    }

    public int getDepth(Employee emp) {
        int depth = 0;
        Set<Integer> visited = new HashSet<>();

        while (emp.managerId != null) {
            if (emp.managerId.equals(emp.id)) {
                System.out.printf("Employee ID %d is self-managed (cycle detected)\n", emp.id);
                return -1;
            }

            if (visited.contains(emp.managerId)) {
                System.out.printf("Reporting cycle found at Employee ID %d\n", emp.id);
                return -1;
            }

            visited.add(emp.managerId);

            Employee manager = employeeMap.get(emp.managerId);
            if (manager == null) {
                System.out.printf("Manager with ID %d not found for employee ID %d\n", emp.managerId, emp.id);
                break;
            }

            emp = manager;
            if(emp.managerId==null){
                break;
            }
            depth++;
        }

        return depth;
    }

    public static void main(String[] args) throws IOException {
        EmployeeAuditService analyzer = new EmployeeAuditService();
        analyzer.loadCSV("employees.csv");
        analyzer.checkSalaries();
        analyzer.checkReportingChainLength();
    }
}
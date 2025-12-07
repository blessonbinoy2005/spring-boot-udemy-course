package com.luv2code.springboot.cruddemo.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.luv2code.springboot.cruddemo.entity.Employee;
import com.luv2code.springboot.cruddemo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


/*
Client / Browser / REST Client → Controller → Service → Repository → Database

1️⃣ Employee (Entity)
    This is your data model.
    Represents a table in the database.
    Each field = a column.
    Spring/JPA will automatically map rows to Employee objects.
    ✔ Used by: Repository, Service, Controller
    ❌ Doesn’t call anything by itself.
    Think: “What does an Employee look like in the system?”

2️⃣ EmployeeRepository (DAO Layer)
    This interface extends JpaRepository.
    Because of that, Spring auto-creates all CRUD database methods:
    findAll()
    findById()
    save()
    deleteById()
    etc.
    ✔ Called by: Service
    ❌ Does not call service or controller.
    Think: “This is the database toolbox.”
    You never write SQL—Spring Data does it for you.

3️⃣ EmployeeService (Service Layer Interface)
    This defines what operations your app supports, such as:
    findAll()
    findById()
    save()
    deleteById()
    It’s just the blueprint — no logic here.
    ✔ Implemented by: EmployeeServiceImpl
    ✔ Called by: Controller
    Think: “These are the features the app provides.”

4️⃣ EmployeeServiceImpl (Service Implementation)
    This class implements the service interface.
    What does it actually do?
    Uses the repository to talk to the database.
    Contains business logic (if you had any).
    Example flow:
    Controller → Service → Repository → DB
    and back.
    ✔ Calls Repository
    ✔ Called by Controller
    Think: “This is the middleman between controller and database.”

5️⃣ EmployeeRestController (REST API Layer)

    This exposes API endpoints like:
    GET /api/employees
    GET /api/employees/{id}
    POST /api/employees
    PUT /api/employees
    DELETE /api/employees/{id}
    It receives HTTP requests → uses Service → returns results.
    ✔ Calls Service
    ❌ Never touches database directly.
    Think: “This is what the outside world (Postman/browser) talks to.”

    ----

    (You / Browser / Postman)
            ↓
   EmployeeRestController
            ↓
       EmployeeService
            ↓
   EmployeeServiceImpl
            ↓
     EmployeeRepository
            ↓
 */

@RestController
@RequestMapping("/api")
public class EmployeeRestController {

    private EmployeeService employeeService;

    private ObjectMapper objectMapper;

    @Autowired
    public EmployeeRestController(EmployeeService theEmployeeService, ObjectMapper theObjectMapper) {
        employeeService = theEmployeeService;
        objectMapper = theObjectMapper;
    }

    // expose "/employees" and return a list of employees
    @GetMapping("/employees")
    public List<Employee> findAll() {
        return employeeService.findAll();
    }

    // add mapping for GET /employees/{employeeId}

    @GetMapping("/employees/{employeeId}")
    public Employee getEmployee(@PathVariable int employeeId) {

        Employee theEmployee = employeeService.findById(employeeId);

        if (theEmployee == null) {
            throw new RuntimeException("Employee id not found - " + employeeId);
        }

        return theEmployee;
    }

    // add mapping for POST /employees - add new employee

    @PostMapping("/employees")
    public Employee addEmployee(@RequestBody Employee theEmployee) {

        // also just in case they pass an id in JSON ... set id to 0
        // this is to force a save of new item ... instead of update

        theEmployee.setId(0);

        Employee dbEmployee = employeeService.save(theEmployee);

        return dbEmployee;
    }

    // add mapping for PUT /employees - update existing employee

    @PutMapping("/employees")
    public Employee updateEmployee(@RequestBody Employee theEmployee) {

        Employee dbEmployee = employeeService.save(theEmployee);

        return dbEmployee;
    }

    // add mapping for PATCH /employees/{employeeId} - patch employee ... partial update

    @PatchMapping("/employees/{employeeId}")
    public Employee patchEmployee(@PathVariable int employeeId,
                                  @RequestBody Map<String, Object> patchPayload) {

        Employee tempEmployee = employeeService.findById(employeeId);

        // throw exception if null
        if (tempEmployee == null) {
            throw new RuntimeException("Employee id not found - " + employeeId);
        }

        // throw exception if request body contains "id" key
        if (patchPayload.containsKey("id")) {
            throw new RuntimeException("Employee id not allowed in request body - " + employeeId);
        }

        Employee patchedEmployee = apply(patchPayload, tempEmployee);

        Employee dbEmployee = employeeService.save(patchedEmployee);

        return dbEmployee;
    }

    private Employee apply(Map<String, Object> patchPayload, Employee tempEmployee) {

        // Convert employee object to a JSON object node
        ObjectNode employeeNode = objectMapper.convertValue(tempEmployee, ObjectNode.class);

        // Convert the patchPayload map to a JSON object node
        ObjectNode patchNode = objectMapper.convertValue(patchPayload, ObjectNode.class);

        // Merge the patch updates into the employee node
        employeeNode.setAll(patchNode);

        return objectMapper.convertValue(employeeNode, Employee.class);
    }

    // add mapping for DELETE /employees/{employeeId} - delete employee

    @DeleteMapping("/employees/{employeeId}")
    public String deleteEmployee(@PathVariable int employeeId) {

        Employee tempEmployee = employeeService.findById(employeeId);

        // throw exception if null

        if (tempEmployee == null) {
            throw new RuntimeException("Employee id not found - " + employeeId);
        }

        employeeService.deleteById(employeeId);

        return "Deleted employee id - " + employeeId;
    }

}















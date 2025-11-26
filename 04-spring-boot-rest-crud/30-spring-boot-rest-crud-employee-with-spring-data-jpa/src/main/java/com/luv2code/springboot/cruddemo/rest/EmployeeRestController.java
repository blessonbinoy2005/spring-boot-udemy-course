package com.luv2code.springboot.cruddemo.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.luv2code.springboot.cruddemo.entity.Employee;
import com.luv2code.springboot.cruddemo.service.EmployeeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class EmployeeRestController {

    private EmployeeService employeeService;
    private ObjectMapper objectMapper;

    // quick and dirty: inject employee dao (use constructor injection)
    public EmployeeRestController(EmployeeService theEmployeeDAO, ObjectMapper theObjectMapper) {
        employeeService = theEmployeeDAO;
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
        Employee theEmployee =  employeeService.findById(employeeId);
        if (theEmployee == null) {
            throw new RuntimeException("Employee with id " + employeeId + " not found");
        } else {
            System.out.println("Employee with id " + employeeId + " found and sending to webApp.");
        }
        return theEmployee;
    }

    // add mapping for POST /employees - add new employees
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

    //add mapping for PATCH /employees/{employeeID} - path employee ... partial update
    @PatchMapping("/employees/{employeeID}")
    public Employee patchEmployee(@PathVariable int employeeID, @RequestBody Map<String, Object> patchPayload) {
        Employee tempEmployee = employeeService.findById(employeeID);

        //throw exception if null
        if (tempEmployee == null) {
            throw new RuntimeException("Employee with id " + employeeID + " not found");
        }

        //throw exception if request body contains "id" key
        if (patchPayload.containsKey("id")) {
            throw new RuntimeException("Employee id not allowed in request body - " + employeeID);
        }

        //applying the patching here
        Employee patchedEmployee = apply(patchPayload, tempEmployee);

        //saving it back to the database
        Employee dbEmployee = employeeService.save(patchedEmployee);

        return dbEmployee;
    }

    private Employee apply(Map<String, Object> patchPayload, Employee tempEmployee) {
        //Convert employee object to a JSON object node
        ObjectNode employeeNode = objectMapper.convertValue(tempEmployee, ObjectNode.class);

        //Convert the patchPayload map to a JSON object node
        ObjectNode patchNode = objectMapper.convertValue(patchPayload, ObjectNode.class);

        //Merge the patch updates into the employees node
        employeeNode.setAll(patchNode);

        //Convert JSON object node back to Employee object and return it
        return objectMapper.convertValue(employeeNode, Employee.class);
    }


    //add mapping for DELETE /employees/{employees} - delete employee
    @DeleteMapping("/employees/{employeeId}")
    public String deleteEmployee(@PathVariable int employeeId) {
        Employee tempEmployee = employeeService.findById(employeeId);

        //throw exception if null
        if (tempEmployee == null) {
            throw new RuntimeException("Employee with id " + employeeId + " not found");
        }
        employeeService.deleteById(employeeId);
        return "Employee with id " + employeeId + " deleted successfully";
    }


}









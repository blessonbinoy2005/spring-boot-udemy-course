package com.luv2code.demo.rest;

import com.luv2code.demo.entity.Student;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class StudentRestController {

    // define endpoint for "/students" - return a list of students

    @GetMapping("/students")
    public List<Student> getStudent() {

        List<Student> theStudents = new ArrayList<>();

        theStudents.add(new Student("1", "A"));
        theStudents.add(new Student("2", "B"));
        theStudents.add(new Student("3", "C"));

        return theStudents;
    }

}

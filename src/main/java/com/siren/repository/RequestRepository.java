package com.siren.repository;

import com.siren.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByDepartment(String department);

    List<Request> findByStatus(String status);

    List<Request> findByDepartmentAndStatus(String department, String status);
}
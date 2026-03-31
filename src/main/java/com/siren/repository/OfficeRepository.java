package com.siren.repository;

import com.siren.model.Office;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfficeRepository extends JpaRepository<Office, Long> {
    List<Office> findByDepartmentId(Long departmentId);



//    List<Office> findByDepartment(String department);
}
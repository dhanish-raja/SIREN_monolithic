package com.siren.service;

import com.siren.model.Office;
import com.siren.repository.OfficeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoutingService {

    @Autowired
    private OfficeRepository officeRepository;

    public Office findNearestOffice(String department, double userLat, double userLon) {

        System.out.println("Finding nearest office");

        Long departmentId = mapDepartmentToId(department);

        List<Office> offices = officeRepository.findByDepartmentId(departmentId);

        if (offices == null || offices.isEmpty()) {
            throw new RuntimeException("No offices found for department: " + department);
        }

        Office nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Office office : offices) {
            double distance = haversine(
                    userLat,
                    userLon,
                    office.getLatitude(),
                    office.getLongitude()
            );

            if (distance < minDistance) {
                minDistance = distance;
                nearest = office;
            }
        }

        return nearest;
    }

    // Haversine formula (distance in KM)
    private double haversine(double lat1, double lon1, double lat2, double lon2) {

        final int R = 6371; // Earth radius in KM

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    private Long mapDepartmentToId(String department) {
        return switch (department.toUpperCase()) {
            case "POLICE" -> 1L;
            case "FIRE" -> 2L;
            case "MEDICAL" -> 3L;
            default -> throw new RuntimeException("Unknown department: " + department);
        };
    }
}
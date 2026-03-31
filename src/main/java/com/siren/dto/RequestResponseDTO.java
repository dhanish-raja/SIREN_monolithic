package com.siren.dto;

public class RequestResponseDTO {

    private Long id;
    private String type;
    private String description;
    private String department;
    private String summary;
    private String status;

    private Double latitude;
    private Double longitude;

    private OfficeDTO assignedOffice;

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getDepartment() {
        return department;
    }

    public String getSummary() {
        return summary;
    }

    public String getStatus() {
        return status;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public OfficeDTO getAssignedOffice() {
        return assignedOffice;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setAssignedOffice(OfficeDTO assignedOffice) {
        this.assignedOffice = assignedOffice;
    }
}
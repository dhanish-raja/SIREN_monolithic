package com.siren.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type; // TEXT, IMAGE, AUDIO, VIDEO

    private String mediaUrl;

    @Column(length = 1000)
    private String description;

    private Double latitude;
    private Double longitude;

    private String department; // POLICE, FIRE, MEDICAL

    @Column(length = 500)
    private String summary;

    private String status; // OPEN, IN_PROGRESS, RESOLVED

    private LocalDateTime createdAt;

    // ✅ FIXED RELATION (ONLY THIS, no duplicate field)
    @ManyToOne
    @JoinColumn(name = "assigned_office_id")
    private Office assignedOffice;

    // ---------------- GETTERS & SETTERS ----------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Office getAssignedOffice() { return assignedOffice; }


    public void setAssignedOffice(Office assignedOffice) {
        this.assignedOffice = assignedOffice;
    }
}
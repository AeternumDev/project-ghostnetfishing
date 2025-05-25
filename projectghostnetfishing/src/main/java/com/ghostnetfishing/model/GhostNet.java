package com.ghostnetfishing.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "ghost_net")
public class GhostNet implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double latitude;
    
    @Column(nullable = false)
    private double longitude;
    
    @Column(name = "estimated_size", nullable = false, length = 255)
    private String estimatedSize;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NetStatus status;

    @ManyToOne
    @JoinColumn(name = "reporting_person_id")
    private Person reportingPerson; // Can be null for anonymous reports

    @ManyToOne
    @JoinColumn(name = "recovering_person_id")
    private Person recoveringPerson; // Person assigned to recover the net

    // Constructors
    public GhostNet() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getEstimatedSize() {
        return estimatedSize;
    }

    public void setEstimatedSize(String estimatedSize) {
        this.estimatedSize = estimatedSize;
    }

    public NetStatus getStatus() {
        return status;
    }

    public void setStatus(NetStatus status) {
        this.status = status;
    }

    public Person getReportingPerson() {
        return reportingPerson;
    }

    public void setReportingPerson(Person reportingPerson) {
        this.reportingPerson = reportingPerson;
    }

    public Person getRecoveringPerson() {
        return recoveringPerson;
    }

    public void setRecoveringPerson(Person recoveringPerson) {
        this.recoveringPerson = recoveringPerson;
    }

    @Override
    public String toString() {
        return "GhostNet{" +
               "id=" + id +
               ", latitude=" + latitude +
               ", longitude=" + longitude +
               ", estimatedSize='" + estimatedSize + '\'' +
               ", status=" + status +
               '}';
    }
}

package com.ghostnetfishing.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "person")
public class Person implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;
    
    @Column(name = "phone_number", length = 50)
    private String phoneNumber; // Optional for reporting persons

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private PersonType type; // REPORTER, RECOVERER

    @OneToMany(mappedBy = "reportingPerson")
    private List<GhostNet> reportedNets = new ArrayList<>();

    @OneToMany(mappedBy = "recoveringPerson")
    private List<GhostNet> netsToRecover = new ArrayList<>();

    // Constructors
    public Person() {
    }

    public Person(String name, String phoneNumber, PersonType type) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.type = type;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public PersonType getType() {
        return type;
    }

    public void setType(PersonType type) {
        this.type = type;
    }

    public List<GhostNet> getReportedNets() {
        return reportedNets;
    }

    public void setReportedNets(List<GhostNet> reportedNets) {
        this.reportedNets = reportedNets;
    }

    public List<GhostNet> getNetsToRecover() {
        return netsToRecover;
    }

    public void setNetsToRecover(List<GhostNet> netsToRecover) {
        this.netsToRecover = netsToRecover;
    }

    @Override
    public String toString() {
        return "Person{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", phoneNumber='" + phoneNumber + '\'' +
               ", type=" + type +
               '}';
    }
}

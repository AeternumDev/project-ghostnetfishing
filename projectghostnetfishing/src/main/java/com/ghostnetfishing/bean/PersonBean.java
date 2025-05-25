package com.ghostnetfishing.bean;

import com.ghostnetfishing.model.Person;
import com.ghostnetfishing.model.PersonType;
import com.ghostnetfishing.service.GhostNetService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class PersonBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private GhostNetService ghostNetService; // Assuming Person creation might be in GhostNetService or a dedicated PersonService

    private Person newPerson;
    private List<Person> recoverers;

    @PostConstruct
    public void init() {
        newPerson = new Person();
        newPerson.setType(PersonType.RECOVERER); // Default to creating a recoverer
        loadRecoverers();
    }

    public void createRecoverer() {
        if (newPerson.getName() == null || newPerson.getName().trim().isEmpty() ||
            newPerson.getPhoneNumber() == null || newPerson.getPhoneNumber().trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Name and Phone Number are required."));
            return;
        }
        try {
            newPerson.setType(PersonType.RECOVERER);
            ghostNetService.createPerson(newPerson);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Success", "Recoverer registered successfully."));
            newPerson = new Person(); // Reset form
            newPerson.setType(PersonType.RECOVERER);
            loadRecoverers(); // Refresh the list
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not register recoverer: " + e.getMessage()));
        }
    }

    public void loadRecoverers() {
        recoverers = ghostNetService.getAllRecoverers();
    }

    // Getters and Setters
    public Person getNewPerson() {
        return newPerson;
    }

    public void setNewPerson(Person newPerson) {
        this.newPerson = newPerson;
    }

    public List<Person> getRecoverers() {
        return recoverers;
    }

    public void setRecoverers(List<Person> recoverers) {
        this.recoverers = recoverers;
    }
}

package com.ghostnetfishing.bean;

import com.ghostnetfishing.model.GhostNet;
import com.ghostnetfishing.model.NetStatus;
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
public class GhostNetBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private GhostNetService ghostNetService;

    private GhostNet newNet;
    private Person reportingPerson;
    private boolean anonymousReport = true;

    private List<GhostNet> netsToRecover;
    private List<GhostNet> allNets;
    private List<Person> availableRecoverers;

    private GhostNet selectedNet;
    private Long selectedRecovererId;
    private Person lostReportPerson; // For marking as lost

    @PostConstruct
    public void init() {
        newNet = new GhostNet();
        reportingPerson = new Person();
        reportingPerson.setType(PersonType.REPORTER);
        lostReportPerson = new Person();
        lostReportPerson.setType(PersonType.REPORTER); // Or a generic user type if needed
        loadNetsToRecover();
        loadAllNets();
        loadAvailableRecoverers();
    }

    public void reportNet() {
        try {
            Person reporterToSave = null;
            if (!anonymousReport) {
                if (reportingPerson.getName() == null || reportingPerson.getName().trim().isEmpty() ||
                    reportingPerson.getPhoneNumber() == null || reportingPerson.getPhoneNumber().trim().isEmpty()) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Name and Phone Number are required for non-anonymous reports."));
                    return;
                }
                reporterToSave = reportingPerson;
            }
            ghostNetService.reportNet(newNet, reporterToSave);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Success", "Ghost net reported successfully."));
            newNet = new GhostNet(); // Reset form
            reportingPerson = new Person();
            reportingPerson.setType(PersonType.REPORTER);
            anonymousReport = true;
            loadAllNets(); // Refresh lists
            loadNetsToRecover();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not report net: " + e.getMessage()));
        }
    }

    public void assignNetToRecoverer() {
        if (selectedNet != null && selectedRecovererId != null) {
            try {
                Person recoverer = ghostNetService.findPersonById(selectedRecovererId);
                if (recoverer != null && recoverer.getType() == PersonType.RECOVERER) {
                    ghostNetService.assignRecovery(selectedNet.getId(), recoverer);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Success", "Net assigned for recovery."));
                    loadNetsToRecover();
                    loadAllNets();
                    selectedNet = null;
                    selectedRecovererId = null;
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", "Selected person is not a valid recoverer."));
                }
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not assign net: " + e.getMessage()));
            }
        }
    }

    public void markAsRecovered(GhostNet net) {
        // For simplicity, assuming the logged-in user is the one recovering.
        // In a real app, you'd get the current user from session.
        // Here, we might need to select the recoverer again or ensure it's the assigned one.
        if (net.getRecoveringPerson() == null) {
             FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Net must be assigned to a recoverer first."));
            return;
        }
        try {
            ghostNetService.markAsRecovered(net.getId(), net.getRecoveringPerson());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Success", "Net marked as recovered."));
            loadNetsToRecover();
            loadAllNets();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not mark as recovered: " + e.getMessage()));
        }
    }

    public void markAsLost(GhostNet net) {
         if (lostReportPerson.getName() == null || lostReportPerson.getName().trim().isEmpty() ||
            lostReportPerson.getPhoneNumber() == null || lostReportPerson.getPhoneNumber().trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Name and Phone Number are required to report a net as lost."));
            return;
        }
        try {
            // Ensure the person is saved if new
            Person actualReporter = ghostNetService.createPerson(lostReportPerson); 
            ghostNetService.markAsLost(net.getId(), actualReporter);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Success", "Net marked as lost."));
            loadNetsToRecover();
            loadAllNets();
            lostReportPerson = new Person(); // Reset
            lostReportPerson.setType(PersonType.REPORTER);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not mark as lost: " + e.getMessage()));
        }
    }

    public void loadNetsToRecover() {
        netsToRecover = ghostNetService.getNetsToRecover();
    }

    public void loadAllNets() {
        allNets = ghostNetService.getAllNets();
    }

    public void loadAvailableRecoverers() {
        availableRecoverers = ghostNetService.getAllRecoverers();
    }

    // Getters and Setters
    public GhostNet getNewNet() {
        return newNet;
    }

    public void setNewNet(GhostNet newNet) {
        this.newNet = newNet;
    }

    public Person getReportingPerson() {
        return reportingPerson;
    }

    public void setReportingPerson(Person reportingPerson) {
        this.reportingPerson = reportingPerson;
    }

    public boolean isAnonymousReport() {
        return anonymousReport;
    }

    public void setAnonymousReport(boolean anonymousReport) {
        this.anonymousReport = anonymousReport;
    }

    public List<GhostNet> getNetsToRecover() {
        return netsToRecover;
    }

    public List<GhostNet> getAllNets() {
        return allNets;
    }

    public GhostNet getSelectedNet() {
        return selectedNet;
    }

    public void setSelectedNet(GhostNet selectedNet) {
        this.selectedNet = selectedNet;
    }

    public Long getSelectedRecovererId() {
        return selectedRecovererId;
    }

    public void setSelectedRecovererId(Long selectedRecovererId) {
        this.selectedRecovererId = selectedRecovererId;
    }

    public List<Person> getAvailableRecoverers() {
        return availableRecoverers;
    }

    public NetStatus[] getNetStatuses() {
        return NetStatus.values();
    }

    public Person getLostReportPerson() {
        return lostReportPerson;
    }

    public void setLostReportPerson(Person lostReportPerson) {
        this.lostReportPerson = lostReportPerson;
    }
}

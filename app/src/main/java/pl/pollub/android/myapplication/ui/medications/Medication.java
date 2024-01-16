package pl.pollub.android.myapplication.ui.medications;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;
import java.util.List;

public class Medication implements Serializable {
    @DocumentId
    String documentId;
    String form;
    String name;
    String unit;
    List<ScheduleItem> plans;

    public Medication() {
        // Empty constructor needed for Firestore deserialization
    }

    public Medication(String documentId, String form, String name, String unit, List<ScheduleItem> plans) {
        this.documentId = documentId;
        this.form = form;
        this.name = name;
        this.unit = unit;
        this.plans = plans;
    }

    // Add appropriate getters and setters
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public List<ScheduleItem> getPlans() {
        return plans;
    }

    public void setPlans(List<ScheduleItem> plans) {
        this.plans = plans;
    }
}

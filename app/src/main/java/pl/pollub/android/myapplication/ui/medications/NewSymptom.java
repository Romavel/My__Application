package pl.pollub.android.myapplication.ui.medications;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;

import java.util.List;

public class NewSymptom {
    private Timestamp day;
    private List<String> symptom_arr;
    @DocumentId
    private String documentId; // Nowe pole
    public NewSymptom() {
        // Konstruktor bezargumentowy
    }

    public Timestamp getDay() {
        return day;
    }

    public void setDay(Timestamp day) {
        this.day = day;
    }

    public List<String> getSymptom_arr() {
        return symptom_arr;
    }

    public void setSymptom_arr(List<String> symptom_arr) {
        this.symptom_arr = symptom_arr;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}


package pl.pollub.android.myapplication.ui.diet;

import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.List;
import java.util.Map;

// NewDiet.java
public class NewDiet {
    private String documentId;
    private Timestamp day;
    private List<Map<String, Object>> intakeArr;

    public NewDiet() {
        // Pusty konstruktor potrzebny do deserializacji Firestore
    }

    public NewDiet(String documentId, Timestamp day, List<Map<String, Object>> intakeArr) {
        this.documentId = documentId;
        this.day = day;
        this.intakeArr = intakeArr;
    }

    // Dodaj odpowiednie gettery i settery
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public Timestamp getDay() {
        return day;
    }

    public void setDay(Timestamp day) {
        this.day = day;
    }

    public List<Map<String, Object>> getIntakeArr() {
        return intakeArr;
    }

    public void setIntakeArr(List<Map<String, Object>> intakeArr) {
        this.intakeArr = intakeArr;
    }
}

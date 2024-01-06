package pl.pollub.android.myapplication.ui.diet;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

// NewDiet.java
public class NewDiet implements Serializable  {
    @DocumentId
    String documentId;
    private Timestamp day;
    private List<DietEntry> intakeArr;

    public NewDiet() {
        // Pusty konstruktor potrzebny do deserializacji Firestore
    }

    public NewDiet(String documentId, Timestamp day, List<DietEntry> intakeArr) {
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

    public List<DietEntry> getIntakeArr() {
        return intakeArr;
    }

    public void setIntakeArr(List<DietEntry> intakeArr) {
        this.intakeArr = intakeArr;
    }
}

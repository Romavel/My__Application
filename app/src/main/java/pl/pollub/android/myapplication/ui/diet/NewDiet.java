package pl.pollub.android.myapplication.ui.diet;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

// NewDiet.java
public class NewDiet implements Serializable {
    @DocumentId
    String documentId;
    private Timestamp day;
    private List<DietEntry> intake_arr;

    public NewDiet() {
        // Pusty konstruktor potrzebny do deserializacji Firestore
    }

    public NewDiet(String documentId, Timestamp day, List<DietEntry> intake_arr) {
        this.documentId = documentId;
        this.day = day;
        this.intake_arr = intake_arr;
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

    public List<DietEntry> getIntake_arr() {
        return intake_arr;
    }

    public void setIntake_arr(List<DietEntry> intake_arr) {
        this.intake_arr = intake_arr;
    }
}

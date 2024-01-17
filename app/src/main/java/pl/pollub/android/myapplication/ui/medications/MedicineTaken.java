package pl.pollub.android.myapplication.ui.medications;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;

public class MedicineTaken implements Serializable {
    @DocumentId
    private String documentId;
    private Timestamp date;
    private boolean isTaken;

    public MedicineTaken(Timestamp date, boolean isTaken) {
        this.date = date;
        this.isTaken = isTaken;
    }

    public MedicineTaken() {
        // Pusty konstruktor wymagany do deserializacji z Firebase Firestore
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public boolean getIsTaken() {
        return isTaken;
    }

    public void setTaken(boolean taken) {
        isTaken = taken;
    }
}

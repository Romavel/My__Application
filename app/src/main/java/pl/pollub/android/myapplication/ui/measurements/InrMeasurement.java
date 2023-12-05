package pl.pollub.android.myapplication.ui.measurements;

import com.google.firebase.Timestamp;

public class InrMeasurement {
    private double value;
    private String document_id;
    private Timestamp time;

    // Konstruktor bezargumentowy wymagany do korzystania z Firebase
    public InrMeasurement() {
    }

    // Konstruktor z argumentami
    public InrMeasurement(double value, String document_id, Timestamp time) {
        this.value = value;
        this.time = time;
        this.document_id = document_id;

    }

    // Gettery i settery
    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getDocument_id() { return document_id; }

    public void setDocument_id(String documentId) { this.document_id = documentId; }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}


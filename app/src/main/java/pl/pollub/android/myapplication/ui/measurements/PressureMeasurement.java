package pl.pollub.android.myapplication.ui.measurements;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class PressureMeasurement implements Serializable {
    private int systolic_pressure;
    private int diastolic_pressure;
    private int pulse;
    private String document_id;
    private Timestamp time;

    public PressureMeasurement() {
        // Konstruktor domyślny wymagany przez Firebase
    }

    public PressureMeasurement(int systolic_pressure, int diastolic_pressure, int pulse, String document_id,Timestamp time) {
        this.systolic_pressure = systolic_pressure;
        this.diastolic_pressure = diastolic_pressure;
        this.pulse = pulse;
        this.document_id = document_id;
        this.time = time;
    }

    public int getSystolic_pressure() {
        return systolic_pressure;
    }

    public void setSystolic_pressure(int systolic_pressure) {
        this.systolic_pressure = systolic_pressure;
    }

    public int getDiastolic_pressure() {
        return diastolic_pressure;
    }

    public void setDiastolic_pressure(int diastolic_pressure) {
        this.diastolic_pressure = diastolic_pressure;
    }

    public int getPulse() {
        return pulse;
    }

    public void setPulse(int pulse) {
        this.pulse = pulse;
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

package pl.pollub.android.myapplication.ui.pomiary;

import com.google.firebase.Timestamp;

public class InrMeasurement {
    private double value;
    //private String time;

    private Timestamp time;

    // Konstruktor bezargumentowy wymagany do korzystania z Firebase
    public InrMeasurement() {
    }

    // Konstruktor z argumentami
    public InrMeasurement(double value, Timestamp time) {
        this.value = value;
        this.time = time;
    }

    // Gettery i settery

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }



    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}


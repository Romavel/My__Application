package pl.pollub.android.myapplication.ui.pomiary;

public class InrMeasurement {
    private double inrValue;
    private String measurementTime;

    // Konstruktor bezargumentowy wymagany do korzystania z Firebase
    public InrMeasurement() {
    }

    // Konstruktor z argumentami
    public InrMeasurement(double inrValue, String measurementTime) {
        this.inrValue = inrValue;
        this.measurementTime = measurementTime;
    }

    // Gettery i settery

    public double getInrValue() {
        return inrValue;
    }

    public void setInrValue(double inrValue) {
        this.inrValue = inrValue;
    }

    public String getMeasurementTime() {
        return measurementTime;
    }

    public void setMeasurementTime(String measurementTime) {
        this.measurementTime = measurementTime;
    }
}


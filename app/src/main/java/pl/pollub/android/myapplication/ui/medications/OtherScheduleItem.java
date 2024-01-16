package pl.pollub.android.myapplication.ui.medications;

import java.io.Serializable;
import java.util.List;

public class OtherScheduleItem implements Serializable {
    double dose;
    List<Integer> daysOfWeek;


    public OtherScheduleItem() {
        // Empty constructor needed for Firestore deserialization
    }

    public OtherScheduleItem(double dose, List<Integer> daysOfWeek) {
        this.dose = dose;
        this.daysOfWeek = daysOfWeek;
    }

    // Add appropriate getters and setters
    public double getDose() {
        return dose;
    }

    public void setDose(double dose) {
        this.dose = dose;
    }

    public List<Integer> getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(List<Integer> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

}
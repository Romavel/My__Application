package pl.pollub.android.myapplication.ui.medications;

import java.io.Serializable;
import java.util.List;

public class ScheduleItem implements Serializable {
    double dose;
    List<Integer> daysOfWeek;
    String time;

    public ScheduleItem() {
        // Empty constructor needed for Firestore deserialization
    }

    public ScheduleItem(double dose, List<Integer> daysOfWeek, String time) {
        this.dose = dose;
        this.daysOfWeek = daysOfWeek;
        this.time = time;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
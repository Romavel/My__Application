package pl.pollub.android.myapplication.ui.medications;

import java.util.List;

public class ScheduleItem {
    private double dose;
    private List<Integer> daysOfWeek;
    private String time;
    private String unit;

    public ScheduleItem() {
    }

    public ScheduleItem(double dose, List<Integer> daysOfWeek, String time, String unit) {
        this.dose = dose;
        this.daysOfWeek = daysOfWeek;
        this.time = time;
        this.unit = unit;
    }

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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
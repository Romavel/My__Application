package pl.pollub.android.myapplication.ui.medications;

import java.util.List;
import java.util.Map;

public class Medication {

    private String name;
    private String form;
    private List<ScheduleItem> plans;

    // Konstruktor
    public Medication() {
        // Pusty konstruktor
    }

    public Medication(String name, String form, List<ScheduleItem> plans) {
        this.name = name;
        this.form = form;
        this.plans = plans;
    }

    // Gettery i settery

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public List<ScheduleItem> getPlans() {
        return plans;
    }

    public void setPlans(List<ScheduleItem> plans) {
        this.plans = plans;
    }
}

package pl.pollub.android.myapplication.ui.medications;

import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pl.pollub.android.myapplication.R;
import pl.pollub.android.myapplication.TimePickerFragment;

public class ScheduleItemFragment extends Fragment implements TimePickerFragment.OnTimeSetListener {

    private EditText doseEditText;
    private Spinner unitSpinner;
    private Spinner daysOfWeekSpinner;
    private TextView notificationTimeTextView;
    private Button openTimePickerButton;

    private AddMedicationDialogFragment parentFragment;
    private Map<String, Object> schedule;

    public ScheduleItemFragment() {
        // Pusty konstruktor wymagany przez fragment
    }

    // Dodaj drugi parametr do konstruktora
    public ScheduleItemFragment(View rootView, AddMedicationDialogFragment parentFragment) {
        this.parentFragment = parentFragment;
        doseEditText = rootView.findViewById(R.id.editTextDose);
        unitSpinner = rootView.findViewById(R.id.spinnerUnit);
        daysOfWeekSpinner = rootView.findViewById(R.id.spinnerDaysOfWeek);
        notificationTimeTextView = rootView.findViewById(R.id.textViewNotificationTime);
        openTimePickerButton = rootView.findViewById(R.id.buttonOpenTimePicker);

        schedule = new HashMap<>();

        openTimePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Otwarcie TimePickerFragment
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.setOnTimeSetListener(ScheduleItemFragment.this);
                timePickerFragment.show(parentFragment.getChildFragmentManager(), "timePicker"); // Użyj parentFragment.getChildFragmentManager()
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule_item, container, false);
    }

    // Usuń argument z metody convertDaysOfWeek
    public List<Integer> convertDaysOfWeek(String daysOfWeekString) {
        List<Integer> daysOfWeek = new ArrayList<>();

        if (daysOfWeekString != null) {
            if (daysOfWeekString.contains("1")) {
                daysOfWeek.add(Calendar.MONDAY);
            }
            if (daysOfWeekString.contains("2")) {
                daysOfWeek.add(Calendar.TUESDAY);
            }
            if (daysOfWeekString.contains("3")) {
                daysOfWeek.add(Calendar.WEDNESDAY);
            }
            if (daysOfWeekString.contains("4")) {
                daysOfWeek.add(Calendar.THURSDAY);
            }
            if (daysOfWeekString.contains("5")) {
                daysOfWeek.add(Calendar.FRIDAY);
            }
            if (daysOfWeekString.contains("6")) {
                daysOfWeek.add(Calendar.SATURDAY);
            }
            if (daysOfWeekString.contains("7")) {
                daysOfWeek.add(Calendar.SUNDAY);
            }
        }

        return daysOfWeek;
    }

    // Usuń argument z metody getSchedule
    public Map<String, Object> getSchedule() {
        String dose = doseEditText.getText().toString();
        String unit = unitSpinner.getSelectedItem().toString();
        String daysOfWeek = daysOfWeekSpinner.getSelectedItem().toString();

        // Sprawdź, czy dawka nie jest pusta
        if (!dose.isEmpty()) {
            // Ustaw harmonogram
            schedule.put("dose", dose);
            schedule.put("unit", unit);
            schedule.put("days_of_week", daysOfWeek);

            return schedule;
        }

        // Zwróć pusty harmonogram, jeśli dawka jest pusta
        return new HashMap<>();
    }

    @Override
    public void onTimeSet(String formattedTime) {
        // Ustawienie sformatowanego czasu w TextView
        notificationTimeTextView.setText(formattedTime);
        // Dodaj czas do obiektu harmonogramu
        schedule.put("time", formattedTime);
    }
}
package pl.pollub.android.myapplication.ui.medications;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pl.pollub.android.myapplication.R;
import pl.pollub.android.myapplication.TimePickerFragment;

public class ScheduleItemFragment extends Fragment implements TimePickerFragment.OnTimeSetListener {

    private EditText doseEditText;
    private Spinner unitSpinner;
    private TextView tvDaysOfWeek; // Zmieniono na TextView
    private TextView notificationTimeTextView;
    private Button openTimePickerButton;

    private AddMedicationDialogFragment parentFragment;
    private Map<String, Object> schedule;
    private boolean[] selectedDays; // Nowa zmienna do przechowywania stanu zaznaczenia dni

    public ScheduleItemFragment() {
        // Pusty konstruktor wymagany przez fragment
    }

    // Dodaj drugi parametr do konstruktora
    public ScheduleItemFragment(View rootView, AddMedicationDialogFragment parentFragment) {
        this.parentFragment = parentFragment;
        doseEditText = rootView.findViewById(R.id.editTextDose);
        unitSpinner = rootView.findViewById(R.id.spinnerUnit);
        tvDaysOfWeek = rootView.findViewById(R.id.textViewDaysOfWeek); // Zmieniono na TextView
        notificationTimeTextView = rootView.findViewById(R.id.textViewNotificationTime);
        openTimePickerButton = rootView.findViewById(R.id.buttonOpenTimePicker);

        schedule = new HashMap<>();
        selectedDays = new boolean[7]; // Inicjalizacja tablicy na 7 dni

        openTimePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Otwarcie TimePickerFragment
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.setOnTimeSetListener(ScheduleItemFragment.this);
                timePickerFragment.show(parentFragment.getChildFragmentManager(), "timePicker"); // Użyj parentFragment.getChildFragmentManager()
            }
        });

        tvDaysOfWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDaysOfWeekDialog();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule_item, container, false);
    }

    // Zastąp spinnerDaysOfWeek obsługą kliknięcia w TextView
    private void showDaysOfWeekDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(parentFragment.requireContext());
        builder.setTitle("Wybierz dni");
        builder.setCancelable(false);

        builder.setMultiChoiceItems(
                new String[]{"Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek", "Sobota", "Niedziela"},
                selectedDays,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        selectedDays[which] = isChecked;
                    }
                });

        builder.setPositiveButton("Potwierdź", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateSelectedDaysText();
            }
        });

        builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setNeutralButton("Wyczyść", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clearSelectedDays();
            }
        });

        builder.show();
    }

    private void updateSelectedDaysText() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int j = 0; j < selectedDays.length; j++) {
            if (selectedDays[j]) {
                stringBuilder.append(getDayName(j));

                if (j != selectedDays.length - 1) {
                    stringBuilder.append(", ");
                }
            }
        }
        tvDaysOfWeek.setText(stringBuilder.toString());
        // Ustaw harmonogram
        schedule.put("days_of_week", stringBuilder.toString());
    }

    private void clearSelectedDays() {
        for (int j = 0; j < selectedDays.length; j++) {
            selectedDays[j] = false;
        }
        tvDaysOfWeek.setText("");
        // Usuń dni tygodnia z harmonogramu
        schedule.remove("days_of_week");
    }

    private String getDayName(int dayIndex) {
        String[] dayArray = {"Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek", "Sobota", "Niedziela"};
        return dayArray[dayIndex];
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
        String daysOfWeek = tvDaysOfWeek.getText().toString(); // Zmieniono na pobieranie z TextView

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

package pl.pollub.android.myapplication.ui.medications;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import pl.pollub.android.myapplication.MainActivity;
import pl.pollub.android.myapplication.R;
import pl.pollub.android.myapplication.TimePickerFragment;

public class OtherScheduleLayout extends LinearLayout {

    private TextView textViewDose;
    private EditText editTextDose;
    private TextView textViewSelectDaysOfWeek;
    private TextView textViewDaysOfWeek;
    private TextView textViewSelectNotificationTime;
    private Button buttonOpenTimePicker;
    private TextView textViewNotificationTime;
    private Button btnDeleteSchedule;

    private boolean isDeleteClicked = false;
    private OtherScheduleItem otherScheduleItem;

    public OtherScheduleLayout(Context context) {
        super(context);
        initializeViews(context);
    }

    public OtherScheduleLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public OtherScheduleLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_schedule, this);

        textViewDose = findViewById(R.id.textViewDose);
        editTextDose = findViewById(R.id.editTextDose);
        textViewSelectDaysOfWeek = findViewById(R.id.textViewSelectDaysOfWeek);
        textViewDaysOfWeek = findViewById(R.id.textViewDaysOfWeek);
        btnDeleteSchedule = findViewById(R.id.btnDeleteSchedule);

        setupUI();
    }

    private void setupUI() {
        // Set initial text values
        textViewDose.setText("Dawka:");
        textViewSelectDaysOfWeek.setText("Wybierz dni tygodnia:");


        // Set up Days of Week dialog
        textViewDaysOfWeek.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDaysOfWeekDialog();
            }
        });

        // Set up Delete button
        btnDeleteSchedule.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ScheduleLayout", "ScheduleItem will be nulled");
                isDeleteClicked = true;
                removeFromParent();
            }
        });
    }

    private void showDaysOfWeekDialog() {
        final String[] daysOfWeekArray = getResources().getStringArray(R.array.days_of_week_array);
        final boolean[] checkedItems = new boolean[daysOfWeekArray.length];

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Wybierz dni tygodnia");
        builder.setMultiChoiceItems(daysOfWeekArray, checkedItems, (dialog, which, isChecked) -> {
            // Handle item selection
            checkedItems[which] = isChecked;
            //Log.d("ScheduleLayout", "CheckedItems: " + checkedItems);
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            StringBuilder selectedDays = new StringBuilder();
            for (int i = 0; i < daysOfWeekArray.length; i++) {
                if (checkedItems[i]) {
                    selectedDays.append(daysOfWeekArray[i]).append(", ");
                    //Log.d("ScheduleLayout", "selectedDays: " + selectedDays);
                }
            }

            if (selectedDays.length() > 0) {
                selectedDays.deleteCharAt(selectedDays.length() - 2); // Remove trailing comma and space
            }

            //Log.d("ScheduleLayout", "selectedDays after delete: " + selectedDays);

            textViewDaysOfWeek.setText(selectedDays.toString());
        });

        builder.setNegativeButton("Anuluj", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public OtherScheduleItem getScheduleItem() {
        // Get data from UI and populate the ScheduleItem
        try {
            double dose = Double.parseDouble(editTextDose.getText().toString());
            List<Integer> selectedDays = getSelectedDays();
            //Log.d("ScheduleLayout", "selectedDays inside getScheduleItem(): " + selectedDays);
            String time = textViewNotificationTime.getText().toString();

            if (isDeleteClicked) {
                otherScheduleItem = null;
            } else {
                otherScheduleItem = new OtherScheduleItem(dose, selectedDays);
            }
            return otherScheduleItem;
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Wprowadź poprawną dawkę.", Toast.LENGTH_SHORT).show();
            return null;
        }
    }


    private List<Integer> getSelectedDays() {
        List<Integer> selectedDays = new ArrayList<>();

        String selectedDaysText = textViewDaysOfWeek.getText().toString();
        //Log.d("ScheduleLayout", "selectedDaysText inside getSelectedDays(): " + selectedDaysText);
        String[] selectedDaysArray = selectedDaysText.split(", ");
        for (int j=0; j<selectedDaysArray.length; j++){
            //Log.d("ScheduleLayout", "selectedDaysArray for" + j + " inside getSelectedDays(): " + selectedDaysArray[j]);
            if (selectedDaysArray[j] != null) {
                if (selectedDaysArray[j].contains("Poniedziałek")) {
                    selectedDays.add(1);
                }
                if (selectedDaysArray[j].contains("Wtorek")) {
                    selectedDays.add(2);
                }
                if (selectedDaysArray[j].contains("Środa")) {
                    selectedDays.add(3);
                }
                if (selectedDaysArray[j].contains("Czwartek")) {
                    selectedDays.add(4);
                }
                if (selectedDaysArray[j].contains("Piątek")) {
                    selectedDays.add(5);
                }
                if (selectedDaysArray[j].contains("Sobota")) {
                    selectedDays.add(6);
                }
                if (selectedDaysArray[j].contains("Niedziela")) {
                    selectedDays.add(7);
                }
            }
        }
        //Log.d("ScheduleLayout", "selectedDays inside getSelectedDays() after Ifs: " + selectedDays);

        return selectedDays;
    }

    private void removeFromParent() {
        ((ViewGroup) getParent()).removeView(this);
    }



}

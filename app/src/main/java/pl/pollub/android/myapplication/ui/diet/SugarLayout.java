package pl.pollub.android.myapplication.ui.diet;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import pl.pollub.android.myapplication.R;

public class SugarLayout extends LinearLayout {
    private TextView textSugar;
    private NumberPicker numberPickerSugar;
    private TextView textSugarUnit;

    public SugarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.layout_sugar, this);

        textSugar = findViewById(R.id.textSugar);
        numberPickerSugar = findViewById(R.id.numberPickerSugar);
        textSugarUnit = findViewById(R.id.textSugarUnit);

        // Konfiguracja NumberPicker
        final String[] displayedValues = getDisplayedValues(); // Pobierz wartości do wyświetlenia
        numberPickerSugar.setMinValue(0);
        numberPickerSugar.setMaxValue(displayedValues.length - 1);
        numberPickerSugar.setWrapSelectorWheel(false);
        numberPickerSugar.setDisplayedValues(displayedValues);
        numberPickerSugar.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPickerSugar.setValue(0); // Domyślna wartość

        // Dodaj te dwie linie poniżej, aby sprawdzić, czy to rozwiązuje problem
        textSugar.setText("Dzisiejsze spożycie cukru: ");
        textSugarUnit.setText("g");
    }

    private String[] getDisplayedValues() {
        // Pomnóż wszystkie wartości przez 10
        int max = 250; // Maksymalna wartość
        String[] values = new String[max + 1];
        for (int i = 0; i <= max; i++) {
            values[i] = String.valueOf(i * 10);
        }
        return values;
    }
    public void setFormDataFromDatabase(DietEntry formData) {
        // Ustaw wartość NumberPicker na podstawie danych z bazy danych
        int amount = (formData.getAmount());
        numberPickerSugar.setValue(amount / 10);
    }

    // Metoda do pobierania danych wprowadzonych przez użytkownika
    public Map<String, Object> getFormData() {
        Map<String, Object> formData = new HashMap<>();
        formData.put("name", "Sugar");
        formData.put("amount", numberPickerSugar.getValue() * 10);
        return formData;
    }
}

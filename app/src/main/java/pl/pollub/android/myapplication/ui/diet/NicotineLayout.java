package pl.pollub.android.myapplication.ui.diet;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import pl.pollub.android.myapplication.R;

public class NicotineLayout extends LinearLayout {
    private TextView textNicotine;
    private NumberPicker numberPickerNicotine;
    private TextView textNicotineUnit;
    private TextView textNicotineComparison;

    public NicotineLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.layout_nicotine, this);

        textNicotine = findViewById(R.id.textNicotine);
        numberPickerNicotine = findViewById(R.id.numberPickerNicotine);
        textNicotineUnit = findViewById(R.id.textNicotineUnit);
        textNicotineComparison = findViewById(R.id.textNicotineComparison);

        // Konfiguracja NumberPicker
        final String[] displayedValues = getDisplayedValues(); // Pobierz wartości do wyświetlenia
        numberPickerNicotine.setMinValue(0);
        numberPickerNicotine.setMaxValue(displayedValues.length - 1);
        numberPickerNicotine.setWrapSelectorWheel(false);
        numberPickerNicotine.setDisplayedValues(displayedValues);
        numberPickerNicotine.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPickerNicotine.setValue(0); // Domyślna wartość

        // Dodaj te dwie linie poniżej, aby sprawdzić, czy to rozwiązuje problem
        textNicotine.setText("Dzisiejsze spożycie nikotyny: ");
        textNicotineUnit.setText("mg");
        // Nasłuchiwacz zmian w NumberPicker
        numberPickerNicotine.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                updateComparisonText(newVal);
            }
        });
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

    // Metoda do aktualizacji tekstu porównawczego
    private void updateComparisonText(int selectedValue) {
        double cigarettesAmount = selectedValue * 10.0 / 13.0;

        String nicotineComparisonText = String.format("To w przybliżeniu %.1f papierosy", cigarettesAmount);

        textNicotineComparison.setText(nicotineComparisonText);
    }

    public void setFormDataFromDatabase(Map<String, Object> formData) {
        // Ustaw wartość NumberPicker na podstawie danych z bazy danych
        int amount = ((Long) formData.get("amount")).intValue();
        numberPickerNicotine.setValue(amount / 10);
    }

    // Metoda do pobierania danych wprowadzonych przez użytkownika
    public Map<String, Object> getFormData() {
        Map<String, Object> formData = new HashMap<>();
        formData.put("name", "Nicotine");
        formData.put("amount", numberPickerNicotine.getValue() * 10);
        return formData;
    }
}

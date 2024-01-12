package pl.pollub.android.myapplication.ui.diet;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import pl.pollub.android.myapplication.R;

public class AlcoholLayout extends LinearLayout {
    private TextView textAlcohol;
    private NumberPicker numberPickerAlcohol;
    private TextView textAlcoholUnit;
    private TextView textAlcoholComparison;

    public AlcoholLayout(Context context, AttributeSet attrs, int value) {
        super(context, attrs);
        inflate(context, R.layout.layout_alcohol, this);

        textAlcohol = findViewById(R.id.textAlcohol);
        numberPickerAlcohol = findViewById(R.id.numberPickerAlcohol);
        textAlcoholUnit = findViewById(R.id.textAlcoholUnit);
        textAlcoholComparison = findViewById(R.id.textAlcoholComparison);

        // Konfiguracja NumberPicker
        final String[] displayedValues = getDisplayedValues(); // Pobierz wartości do wyświetlenia
        numberPickerAlcohol.setMinValue(0);
        numberPickerAlcohol.setMaxValue(displayedValues.length - 1);
        numberPickerAlcohol.setWrapSelectorWheel(false);
        numberPickerAlcohol.setDisplayedValues(displayedValues);
        numberPickerAlcohol.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        if (value!=0)
        {
            int amount = value/10;
            numberPickerAlcohol.setValue(amount);
            updateComparisonText(amount);
        }
        else
        {
            numberPickerAlcohol.setValue(0); // Domyślna wartość
        }

        // Dodaj te dwie linie poniżej, aby sprawdzić, czy to rozwiązuje problem
        textAlcohol.setText("Dzisiejsze spożycie alkoholu: ");
        textAlcoholUnit.setText("mg");
        // Nasłuchiwacz zmian w NumberPicker
        numberPickerAlcohol.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
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
        double beerAmount = selectedValue * 10.0 * 20.0;
        double wineAmount = selectedValue * 10.0 * 8.3;
        double vodkaAmount = selectedValue * 10.0 * 2.5;

        String alcoholComparisonText = String.format("To w przybliżeniu %.0f ml piwa, %.0f ml wina lub %.0f ml wódki", beerAmount, wineAmount, vodkaAmount);

        textAlcoholComparison.setText(alcoholComparisonText);
    }

    public void setFormDataFromDatabase(DietEntry formData) {
        // Ustaw wartość NumberPicker na podstawie danych z bazy danych
        int amount = (formData.getAmount());
        numberPickerAlcohol.setValue(amount / 10);
    }

    // Metoda do pobierania danych wprowadzonych przez użytkownika
    public Map<String, Object> getFormData() {
        Map<String, Object> formData = new HashMap<>();
        formData.put("name", "Alcohol");
        formData.put("amount", numberPickerAlcohol.getValue() * 10);
        return formData;
    }
}

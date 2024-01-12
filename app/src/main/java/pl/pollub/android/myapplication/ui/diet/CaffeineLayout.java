package pl.pollub.android.myapplication.ui.diet;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import pl.pollub.android.myapplication.R;
public class CaffeineLayout extends LinearLayout {

    private TextView textCaffeine;
    private NumberPicker numberPickerCaffeine;
    private TextView textCaffeineUnit;
    private TextView textCaffeineComparison;

    public CaffeineLayout(Context context, AttributeSet attrs, int value) {
        super(context, attrs);
        inflate(context, R.layout.layout_caffeine, this);

        textCaffeine = findViewById(R.id.textCaffeine);
        numberPickerCaffeine = findViewById(R.id.numberPickerCaffeine);
        textCaffeineUnit = findViewById(R.id.textCaffeineUnit);
        textCaffeineComparison = findViewById(R.id.textCaffeineComparison);


        Log.d("DietDialogFragment","Value w CaffineLayout: " + value);
        // Konfiguracja NumberPicker
        final String[] displayedValues = getDisplayedValues(); // Pobierz wartości do wyświetlenia
        numberPickerCaffeine.setMinValue(0);
        numberPickerCaffeine.setMaxValue(displayedValues.length - 1);
        numberPickerCaffeine.setWrapSelectorWheel(false);
        numberPickerCaffeine.setDisplayedValues(displayedValues);
        numberPickerCaffeine.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        //numberPickerCaffeine.setValue(value/10); // Domyślna wartość
        if (value!=0)
        {
            int amount = value/10;
            numberPickerCaffeine.setValue(amount);
            updateComparisonText(amount);
        }
        else
        {
            numberPickerCaffeine.setValue(0); // Domyślna wartość
        }


        // Dodaj te dwie linie poniżej, aby sprawdzić, czy to rozwiązuje problem
        textCaffeine.setText("Dzisiejsze spożycie kofeiny: ");
        textCaffeineUnit.setText("mg");
        // Nasłuchiwacz zmian w NumberPicker
        numberPickerCaffeine.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
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
        double coffeeAmount = selectedValue * 10.0 * 2.5;
        double espressoAmount = selectedValue * 10.0 / 2.0;
        double energyDrinkAmount = selectedValue *10.0 * 3.3;

        String caffeineComparisonText = String.format("To w przybliżeniu %.0f ml kawy, %.0f ml espresso lub %.0f ml napoju energetycznego", coffeeAmount, espressoAmount, energyDrinkAmount);

        textCaffeineComparison.setText(caffeineComparisonText);
    }

    // Metoda do pobierania danych wprowadzonych przez użytkownika
    public Map<String, Object> getFormData() {
        Map<String, Object> formData = new HashMap<>();
        formData.put("name", "Caffeine");
        formData.put("amount", numberPickerCaffeine.getValue() * 10);
        return formData;
    }
}
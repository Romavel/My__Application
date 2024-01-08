package pl.pollub.android.myapplication.ui.diet;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import pl.pollub.android.myapplication.R;

public class VegetableLayout extends LinearLayout {
    private TextView textVegetable;
    private NumberPicker numberPickerVegetable;
    private TextView textVegetableUnit;
    private TextView textVegetableComparison;

    public VegetableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.layout_vegetable, this);

        textVegetable = findViewById(R.id.textVegetable);
        numberPickerVegetable = findViewById(R.id.numberPickerVegetable);
        textVegetableUnit = findViewById(R.id.textVegetableUnit);
        textVegetableComparison = findViewById(R.id.textVegetableComparison);

        // Konfiguracja NumberPicker
        final String[] displayedValues = getDisplayedValues(); // Pobierz wartości do wyświetlenia
        numberPickerVegetable.setMinValue(0);
        numberPickerVegetable.setMaxValue(displayedValues.length - 1);
        numberPickerVegetable.setWrapSelectorWheel(false);
        numberPickerVegetable.setDisplayedValues(displayedValues);
        numberPickerVegetable.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPickerVegetable.setValue(0); // Domyślna wartość

        // Dodaj te dwie linie poniżej, aby sprawdzić, czy to rozwiązuje problem
        textVegetable.setText("Dzisiejsze spożycie warzyw zielonych: ");
        textVegetableUnit.setText("μg");
        // Nasłuchiwacz zmian w NumberPicker
        numberPickerVegetable.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
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
        double spinachAmount = selectedValue * 10.0 / 5.0;
        double broccoliAmount = selectedValue * 10.0 / 2.0;
        double lettuceAmount = selectedValue * 10.0 * 1.0;

        String vegetableComparisonText = String.format("To w przybliżeniu %.0f g szpinaku, %.0f g brokułów lub %.0f g sałaty", spinachAmount, broccoliAmount, lettuceAmount);

        textVegetableComparison.setText(vegetableComparisonText);
    }

    public void setFormDataFromDatabase(DietEntry formData) {
        // Ustaw wartość NumberPicker na podstawie danych z bazy danych
        int amount = (formData.getAmount());
        numberPickerVegetable.setValue(amount / 10);
    }

    // Metoda do pobierania danych wprowadzonych przez użytkownika
    public Map<String, Object> getFormData() {
        Map<String, Object> formData = new HashMap<>();
        formData.put("name", "Green_vegetables");
        formData.put("amount", numberPickerVegetable.getValue() * 10);
        return formData;
    }
}

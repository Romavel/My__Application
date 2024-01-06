package pl.pollub.android.myapplication.ui.diet;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import pl.pollub.android.myapplication.R;

public class OtherLayout extends LinearLayout {
    private TextView textOther;
    private EditText editTextOther;

    public OtherLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.layout_other, this);

        textOther = findViewById(R.id.textOther);
        editTextOther = findViewById(R.id.editTextOther);



        // Dodaj te dwie linie poniżej, aby sprawdzić, czy to rozwiązuje problem
        textOther.setText("Dodaj inne nie przewidziane spożycie: ");
    }


    // Metoda do pobierania danych wprowadzonych przez użytkownika
    public Map<String, Object> getFormData() {
        Map<String, Object> formData = new HashMap<>();
        formData.put("name", "Other");
        //formData.put("desc", editTextOther.getText().toString());
        return formData;
    }

}

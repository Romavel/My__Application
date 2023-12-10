package pl.pollub.android.myapplication.ui.measurements;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import pl.pollub.android.myapplication.R;
import pl.pollub.android.myapplication.ui.measurements.PressureMeasurement;

import java.io.Serializable;

public class PressureMeasurementEditFragment extends DialogFragment {

    private EditText valueEditText;
    private Button saveButton;
    private Button cancelButton;

    private PressureMeasurement originalMeasurement;
    private OnEditCompleteListener onEditCompleteListener;

    // Metoda do utworzenia nowej instancji fragmentu z przekazanymi danymi pomiaru
    public static PressureMeasurementEditFragment newInstance(PressureMeasurement measurement) {
        PressureMeasurementEditFragment fragment = new PressureMeasurementEditFragment();
        Bundle args = new Bundle();
        args.putSerializable("measurement", (Serializable) measurement);
        fragment.setArguments(args);
        return fragment;
    }

    // Interfejs do obsługi zdarzenia zakończenia edycji
    public interface OnEditCompleteListener {
        void onEditComplete(PressureMeasurement updatedMeasurement);
    }

    // Ustawienie nasłuchiwacza zdarzenia zakończenia edycji
    public void setOnEditCompleteListener(OnEditCompleteListener listener) {
        this.onEditCompleteListener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            originalMeasurement = (PressureMeasurement) getArguments().getSerializable("measurement");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pressure_measurement_edit, container, false);

        valueEditText = view.findViewById(R.id.editTextValue);
        saveButton = view.findViewById(R.id.buttonSave);
        cancelButton =view.findViewById(R.id.buttonCancel);

        // Wypełnij pole tekstowe wartością aktualnego pomiaru INR
        valueEditText.setText(String.valueOf(originalMeasurement.getValue()));

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    private void saveChanges() {
        // Pobierz nową wartość pomiaru INR
        double newValue = Double.parseDouble(valueEditText.getText().toString());

        // Zaktualizuj dane pomiaru
        PressureMeasurementAdapter updatedMeasurement = new PressureMeasurement();
        updatedMeasurement.setValue(newValue);
        updatedMeasurement.setTime(originalMeasurement.getTime()); // Zachowaj oryginalną datę

        // Wywołaj nasłuchiwacza zdarzenia zakończenia edycji
        if (onEditCompleteListener != null) {
            onEditCompleteListener.onEditComplete(updatedMeasurement);
        }

        // Zamknij fragment po zapisaniu zmian
        dismiss();
    }
}

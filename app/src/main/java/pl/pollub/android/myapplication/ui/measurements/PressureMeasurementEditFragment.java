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

import java.io.Serializable;

public class PressureMeasurementEditFragment extends DialogFragment {

    private EditText systolicPressureEditText;
    private EditText diastolicPressureEditText;
    private EditText pulseEditText;
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

        systolicPressureEditText = view.findViewById(R.id.pressureSystolicEditBox);
        diastolicPressureEditText = view.findViewById(R.id.pressureDiastolicEditBox);
        pulseEditText = view.findViewById(R.id.pulseEditBox);
        saveButton = view.findViewById(R.id.buttonSaveEditPressure);
        cancelButton = view.findViewById(R.id.buttonCancelEditPressure);

        // Wypełnij pola tekstowe wartościami aktualnego pomiaru ciśnienia
        systolicPressureEditText.setText(String.valueOf(originalMeasurement.getSystolic_pressure()));
        diastolicPressureEditText.setText(String.valueOf(originalMeasurement.getDiastolic_pressure()));
        pulseEditText.setText(String.valueOf(originalMeasurement.getPulse()));

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
        try {
            // Pobierz nową wartość pomiaru ciśnienia
            int newSystolicPressure = Integer.parseInt(systolicPressureEditText.getText().toString());
            int newDiastolicPressure = Integer.parseInt(diastolicPressureEditText.getText().toString());
            int newPulse = Integer.parseInt(pulseEditText.getText().toString());

            // Zaktualizuj dane pomiaru
            PressureMeasurement updatedMeasurement = new PressureMeasurement();
            updatedMeasurement.setSystolic_pressure(newSystolicPressure);
            updatedMeasurement.setDiastolic_pressure(newDiastolicPressure);
            updatedMeasurement.setPulse(newPulse);
            updatedMeasurement.setTime(originalMeasurement.getTime()); // Zachowaj oryginalną datę

            // Wywołaj nasłuchiwacza zdarzenia zakończenia edycji
            if (onEditCompleteListener != null) {
                onEditCompleteListener.onEditComplete(updatedMeasurement);
            }

            // Zamknij fragment po zapisaniu zmian
            dismiss();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            // Obsłuż błąd parsowania, na przykład poprzez wyświetlenie komunikatu użytkownikowi
        }
    }

}
package pl.pollub.android.myapplication.ui.measurements;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import pl.pollub.android.myapplication.R;

public class PressureMeasurementDialogFragment extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pressure_measurement_dialog, container, false);

        // Inicjalizacja pól i przycisków w dialogu ciśnienia
        EditText systolicPressureBox = view.findViewById(R.id.pressureSystolicBox);
        EditText diastolicPressureBox = view.findViewById(R.id.pressureDiastolicBox);
        EditText pulseBox = view.findViewById(R.id.pulseBox);
        Button btnCancelPressure = view.findViewById(R.id.btnCancelPressure);
        Button btnAddPressure = view.findViewById(R.id.btnAddPressure);

        // Dodaj obsługę przycisków Anuluj i Dodaj
        btnCancelPressure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnAddPressure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pobierz wartości pomiaru ciśnienia
                String systolic_pressure = systolicPressureBox.getText().toString();
                String diastolic_pressure = diastolicPressureBox.getText().toString();
                String pulse = pulseBox.getText().toString();

                // Sprawdź, czy pola są puste
                if (TextUtils.isEmpty(systolic_pressure) || TextUtils.isEmpty(diastolic_pressure) || TextUtils.isEmpty(pulse)) {
                    // Wyświetl komunikat o błędzie
                    Toast.makeText(requireContext(), "Wprowadź wszystkie wartości", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Zabezpieczenie przed NumberFormatException
                try {
                    // Pobierz identyfikator zalogowanego użytkownika
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    // Utwórz nowy pomiar ciśnienia
                    PressureMeasurement pressureMeasurement = new PressureMeasurement();
                    pressureMeasurement.setSystolic_pressure(Integer.parseInt(systolic_pressure));
                    pressureMeasurement.setDiastolic_pressure(Integer.parseInt(diastolic_pressure));
                    pressureMeasurement.setPulse(Integer.parseInt(pulse));
                    pressureMeasurement.setTime(Timestamp.now());

                    // Dodaj pomiar do kolekcji BloodPressureMeasurements w dokumencie zalogowanego użytkownika
                    Task<DocumentReference> documentReferenceTask = FirebaseFirestore.getInstance().collection("Users")
                            .document(userId)
                            .collection("BloodPressureMeasurements")
                            .add(pressureMeasurement)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    pressureMeasurement.setDocument_id(documentReference.getId());

                                    Log.d("PressureMeasurementDialog", "Pomiar ciśnienia dodany do Firestore: " + documentReference.getId());
                                    dismiss(); // Zamknij okno dialogowe po dodaniu pomiaru

                                    // Zapisz zaktualizowany pomiar z identyfikatorem dokumentu
                                    documentReference.set(pressureMeasurement)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("InrMeasurementDialog", "Identyfikator dokumentu zaktualizowany w bazie Firestore");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e("InrMeasurementDialog", "Błąd podczas aktualizowania identyfikatora dokumentu w bazie Firestore", e);
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("PressureMeasurementDialog", "Błąd podczas dodawania pomiaru ciśnienia do Firestore", e);
                                }
                            });
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    // Obsłuż błąd parsowania, na przykład poprzez wyświetlenie komunikatu użytkownikowi
                    Toast.makeText(requireContext(), "Błąd parsowania wartości. Upewnij się, że wprowadzone dane są liczbami całkowitymi.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return view;
    }

}

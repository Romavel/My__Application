package pl.pollub.android.myapplication.ui.pomiary;

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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import pl.pollub.android.myapplication.R;

public class InrMeasurementDialogFragment extends DialogFragment {

    // ... inne pola

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inr_measurement_dialog, container, true);

        // Inicjalizacja pól i przycisków
        EditText inrBox = view.findViewById(R.id.INRBox);
        Button btnCancelInr = view.findViewById(R.id.btnCancelINR);
        Button btnAddInr = view.findViewById(R.id.btnAddINR);

        // ... inne inicjalizacje

        btnAddInr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pobierz wartość pomiaru INR
                String value = inrBox.getText().toString();

                // Sprawdź, czy wartość INR jest pusta
                if (TextUtils.isEmpty(value)) {
                    Toast.makeText(getContext(), "Wprowadź wartość pomiaru INR", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Pobierz identyfikator zalogowanego użytkownika
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                // Utwórz nowy pomiar INR
                InrMeasurement inrMeasurement = new InrMeasurement();
                inrMeasurement.setValue(Double.parseDouble(value));
                inrMeasurement.setTime(Timestamp.now());

                // Dodaj pomiar do kolekcji INR_Measurements w dokumencie zalogowanego użytkownika
                FirebaseFirestore.getInstance().collection("Users")
                        .document(userId)
                        .collection("INR_Measurements")
                        .add(inrMeasurement)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("InrMeasurementDialog", "Pomiar dodany do Firestore: " + documentReference.getId());
                                dismiss(); // Zamknij okno dialogowe po dodaniu pomiaru
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("InrMeasurementDialog", "Błąd podczas dodawania pomiaru do Firestore", e);
                            }
                        });
            }
        });

        btnCancelInr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(); // Zamknij okno dialogowe po anulowaniu
            }
        });

        return view;
    }
}

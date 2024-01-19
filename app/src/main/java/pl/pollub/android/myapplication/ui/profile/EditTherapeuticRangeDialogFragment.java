package pl.pollub.android.myapplication.ui.profile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import pl.pollub.android.myapplication.R;

public class EditTherapeuticRangeDialogFragment extends DialogFragment {

    private EditText upperThresholdEditText;
    private EditText lowerThresholdEditText;
    private Button btnCancelEditTherapeuticRange;
    private Button btnSaveEditTherapeuticRange;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_edit_therapeutic_range_dialog, null);

        upperThresholdEditText = view.findViewById(R.id.upperBorderBox);
        lowerThresholdEditText = view.findViewById(R.id.lowerBorderBox);
        btnCancelEditTherapeuticRange = view.findViewById(R.id.buttonCancelEditTherapeuticRange);
        btnSaveEditTherapeuticRange = view.findViewById(R.id.buttonSaveEditTherapeuticRange);

        // Sprawdź czy kolekcja "Range" istnieje w dokumencie "INR"
        checkAndLoadThresholds();

        btnCancelEditTherapeuticRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnSaveEditTherapeuticRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveThresholdsToFirestore();
            }
        });

        builder.setView(view);
        return builder.create();
    }

    private void checkAndLoadThresholds() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users")
                .document(userId)
                .collection("Range")
                .document("INR")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Dokument "Range" istnieje, wczytaj wartości
                            Double upperThreshold = documentSnapshot.getDouble("upperThreshold");
                            Double lowerThreshold = documentSnapshot.getDouble("lowerThreshold");

                            if (upperThreshold != null && lowerThreshold != null) {
                                // Ustaw wartości w EditText
                                upperThresholdEditText.setText(String.valueOf(upperThreshold));
                                lowerThresholdEditText.setText(String.valueOf(lowerThreshold));
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Błąd odczytu z bazy danych
                        Toast.makeText(requireContext(), "Błąd odczytu z bazy danych", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveThresholdsToFirestore() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Pobierz wartości z pól EditText
        String upperThresholdStr = upperThresholdEditText.getText().toString();
        String lowerThresholdStr = lowerThresholdEditText.getText().toString();

        if (upperThresholdStr.isEmpty() || lowerThresholdStr.isEmpty()) {
            // Sprawdź, czy pola nie są puste
            Toast.makeText(requireContext(), "Wprowadź wartości dla obu progów", Toast.LENGTH_SHORT).show();
            return;
        }

        // Sprawdź, czy wprowadzone wartości są liczbami
        try {
            double upperThreshold = Double.parseDouble(upperThresholdStr);
            double lowerThreshold = Double.parseDouble(lowerThresholdStr);

            // Sprawdź zabezpieczenia
            if (lowerThreshold > upperThreshold) {
                // Dolna granica nie może być większa niż górna granica
                Toast.makeText(requireContext(), "Dolna granica nie może być większa niż górna granica", Toast.LENGTH_SHORT).show();
                return;
            }

            if (lowerThreshold < 0) {
                // Dolna granica nie może być mniejsza od 0
                Toast.makeText(requireContext(), "Dolna granica nie może być mniejsza od 0", Toast.LENGTH_SHORT).show();
                return;
            }

            if (upperThreshold > 6) {
                // Górna granica nie może być większa niż 6
                Toast.makeText(requireContext(), "Górna granica nie może być większa niż 6", Toast.LENGTH_SHORT).show();
                return;
            }

            // Zapisz wartości do Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> thresholds = new HashMap<>();
            thresholds.put("upperThreshold", upperThreshold);
            thresholds.put("lowerThreshold", lowerThreshold);

            db.collection("Users")
                    .document(userId)
                    .collection("Range")
                    .document("INR")
                    .set(thresholds)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Zapisano pomyślnie
                            Toast.makeText(requireContext(), "Zapisano wartości progów", Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Błąd zapisu
                            Toast.makeText(requireContext(), "Błąd zapisu danych", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (NumberFormatException e) {
            // Wprowadzone wartości nie są liczbami
            Toast.makeText(requireContext(), "Wprowadź poprawne liczby dla progów", Toast.LENGTH_SHORT).show();
        }
    }
}

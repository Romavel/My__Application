package pl.pollub.android.myapplication.ui.profile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import pl.pollub.android.myapplication.R;
import pl.pollub.android.myapplication.User;

public class EditMainMedicationDialogFragment extends DialogFragment {

    private Spinner medicationSpinner;
    private Button btnCancelEditMainMedication;
    private Button btnSaveEditMainMedication;

    private User currentUser;

    public EditMainMedicationDialogFragment(User user) {
        this.currentUser = user;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_edit_main_medication_dialog, null);

        medicationSpinner = view.findViewById(R.id.medicationSpinner);
        btnCancelEditMainMedication = view.findViewById(R.id.btnCancelEditMainMedication);
        btnSaveEditMainMedication = view.findViewById(R.id.btnSaveEditMainMedication);

        // Inicjalizacja spinnera
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.main_medication_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        medicationSpinner.setAdapter(adapter);

        // Ustawienie wartości początkowej spinnera na podstawie danych z użytkownika
        String userMedication = currentUser.getMedication();
        if (userMedication != null) {
            int position = adapter.getPosition(userMedication);
            if (position != -1) {
                medicationSpinner.setSelection(position);
            }
        }

        // Dodanie listenera do spinnera
        medicationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Obsługa wyboru leku z spinnera
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Nie jest wymagane
            }
        });

        // Dodanie listenerów do przycisków
        btnCancelEditMainMedication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnSaveEditMainMedication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
                // Zamknij okno dialogowe
                dismiss();
            }
        });

        builder.setView(view);
        return builder.create();
    }

    private void saveChanges() {
        // Pobierz wartości z pól
        String newMainMedication = medicationSpinner.getSelectedItem().toString();

        // Zaktualizuj obiekt użytkownika
        currentUser.setMedication(newMainMedication);

        // Tutaj dodaj logikę zapisywania zmian do bazy danych Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("Users").document(userId).set(currentUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Zapisano pomyślnie
                        dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Błąd zapisu
                        // Tutaj możesz dodać odpowiednie obszary kodu obsługujące błędy
                        Toast.makeText(requireContext(), "Błąd zapisu danych", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

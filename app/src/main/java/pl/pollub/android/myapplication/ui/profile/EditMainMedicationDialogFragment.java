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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import pl.pollub.android.myapplication.R;
import pl.pollub.android.myapplication.User;
import pl.pollub.android.myapplication.ui.medications.Medication;
import pl.pollub.android.myapplication.ui.medications.MedicineTaken;

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

        // Tutaj dodaj logikę sprawdzania i aktualizacji dokumentu w kolekcji Medications
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        // Zaktualizuj pole medication w dokumencie userId
        db.collection("Users").document(userId).update("medication", newMainMedication)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Zapisano pomyślnie
                        if (isAdded() && getContext() != null) {
                            Toast.makeText(requireContext(), "Pomyślnie zapisano zmianę leku", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Błąd zapisu
                        // Tutaj możesz dodać odpowiednie obszary kodu obsługujące błędy
                        if (getContext() != null) {
                            Toast.makeText(requireContext(), "Błąd zapisu danych", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // Sprawdź, czy istnieje dokument w kolekcji Medications z polem name równym nowemu lekowi
        db.collection("Users")
                .document(userId)
                .collection("Medications")
                .whereEqualTo("name", newMainMedication)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        if (documents.size() > 0) {
                            // Dokument istnieje, sprawdź kolekcję MedicineTaken
                            DocumentSnapshot existingDocument = documents.get(0);
                            checkAndAddMedicineTaken(existingDocument.getId());
                        } else {
                            // Dokument nie istnieje, utwórz nowy
                            createNewMedicationDocument(newMainMedication);
                        }
                    } else {
                        // Obsługa błędu
                    }
                });
    }

    private void checkAndAddMedicineTaken(String medicationDocumentId) {
        // Sprawdź, czy kolekcja MedicineTaken istnieje w danym dokumencie
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("Users")
                .document(userId)
                .collection("Medications")
                .document(medicationDocumentId)
                .collection("MedicineTaken")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<DocumentSnapshot> medicineTakenDocuments = task.getResult().getDocuments();
                        if (medicineTakenDocuments.isEmpty()) {
                            // Kolekcja MedicineTaken nie istnieje, utwórz nową i dodaj dokument
                            createMedicineTakenCollectionAndDocument(medicationDocumentId);
                        } else {
                            // Kolekcja istnieje, zamknij okno dialogowe
                            dismiss();
                        }
                    } else {
                        // Obsługa błędu
                    }
                });
    }

    private void createMedicineTakenCollectionAndDocument(String medicationDocumentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Utwórz kolekcję MedicineTaken i dodaj do niej dokument
        db.collection("Users")
                .document(userId)
                .collection("Medications")
                .document(medicationDocumentId)
                .collection("MedicineTaken")
                .add(new MedicineTaken(Timestamp.now(), false))
                .addOnSuccessListener(documentReference -> {
                    // Zapisano pomyślnie, zamknij okno dialogowe
                    dismiss();
                })
                .addOnFailureListener(e -> {
                    // Obsługa błędu
                });
    }

    private void createNewMedicationDocument(String newMainMedication) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Utwórz nowy dokument w kolekcji Medications
        Medication newMedication = new Medication(null, null, newMainMedication, null, new ArrayList<>());
        db.collection("Users")
                .document(userId)
                .collection("Medications")
                .add(newMedication)
                .addOnSuccessListener(documentReference -> {
                    // Zapisano pomyślnie, utwórz kolekcję MedicineTaken i dodaj do niej dokument
                    createMedicineTakenCollectionAndDocument(documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    // Obsługa błędu
                });
    }
}

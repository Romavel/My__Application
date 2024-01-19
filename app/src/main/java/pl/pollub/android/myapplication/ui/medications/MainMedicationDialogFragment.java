package pl.pollub.android.myapplication.ui.medications;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import pl.pollub.android.myapplication.R;
import pl.pollub.android.myapplication.User;
import pl.pollub.android.myapplication.ui.medications.Medication;
import pl.pollub.android.myapplication.ui.medications.ScheduleItem;

public class MainMedicationDialogFragment extends Fragment {

    private TextView textViewMainMedicationName;
    private TextView textViewMedicationName;
    private TextView textViewSelectMedicationForm;
    private TextView textViewSelectMedicationUnit;
    private Spinner spinnerMedicationForm, spinnerUnit;
    private LinearLayout linearLayoutSchedules;
    private Button btnAddSchedule;
    private Button btnAddMainMedication;
    private Button btnCancelMainMedication;
    private List<ScheduleLayout> scheduleLayouts;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String mainMedication;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_medication_dialog, container, false);

        textViewMainMedicationName = view.findViewById(R.id.textViewMainMedicationName);
        textViewMedicationName = view.findViewById(R.id.textViewMedicationName);
        textViewSelectMedicationForm = view.findViewById(R.id.textViewSelectMedicationForm);
        textViewSelectMedicationUnit = view.findViewById(R.id.textViewSelectMedicationUnit);
        spinnerMedicationForm = view.findViewById(R.id.spinnerMedicationForm);
        spinnerUnit = view.findViewById(R.id.spinnerUnit);
        linearLayoutSchedules = view.findViewById(R.id.linearLayoutSchedules);
        btnAddSchedule = view.findViewById(R.id.buttonAddSchedule);
        btnAddMainMedication = view.findViewById(R.id.btnAddMainMedication);
        btnCancelMainMedication = view.findViewById(R.id.btnCancelMainMedication);

        scheduleLayouts = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        loadMainMedication();

        return view;
    }
    private void loadMainMedication() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Pobierz identyfikator zalogowanego użytkownika
            String userId = user.getUid();
            Log.d("MainMedicationDialog", "UserID: " + userId);

            // Pobierz dokument z kolekcji "Users" dla zalogowanego użytkownika
            FirebaseFirestore.getInstance().collection("Users").document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Jeśli dokument istnieje, pobierz dane
                                User currentUser = document.toObject(User.class);

                                if (currentUser != null) {
                                    // Przypisz wartość pola medication do zmiennej mainMedication
                                    mainMedication = currentUser.getMedication();
                                    loadMedicationDataForEdit(); // Dodano funkcję do wczytania danych leku do edycji
                                    setupUI();

                                    // W tym miejscu możesz użyć wartości mainMedication według potrzeb
                                    Log.d("MainMedicationDialog", "Main Medication: " + mainMedication);
                                } else {
                                    Log.d("MainMedicationDialog", "User object is null");
                                }
                            } else {
                                // Jeśli dokument nie istnieje, obsłuż sytuację braku danych
                                Log.d("MainMedicationDialog", "Document does not exist");
                            }
                        } else {
                            Log.d("MainMedicationDialog", "Error getting user data");
                        }
                    });
        }
    }

    // Dodano funkcję do wczytania danych leku do edycji
    private void loadMedicationDataForEdit() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Sprawdź, czy już istnieje dokument z takim mainMedication
        db.collection("Users")
                .document(userId)
                .collection("Medications")
                .whereEqualTo("name", mainMedication)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        if (documents.size() > 0) {
                            // Dokument istnieje, wczytaj dane do edycji
                            DocumentSnapshot existingDocument = documents.get(0);
                            Medication existingMedication = existingDocument.toObject(Medication.class);
                            if (existingMedication != null) {
                                // Wczytaj dane do pól interfejsu użytkownika
                                loadMedicationDataToUI(existingMedication);
                            }
                        }
                    }
                });
    }

    // Dodano funkcję do wczytania danych leku do pól interfejsu użytkownika
    private void loadMedicationDataToUI(Medication existingMedication) {
        if (existingMedication != null) {
            // Wczytaj dane do pól interfejsu użytkownika
            textViewMedicationName.setText("Nazwa leku głównego:");
            textViewMainMedicationName.setText(existingMedication.getName());
            textViewSelectMedicationForm.setText("Wybierz formę leku: ");
            textViewSelectMedicationUnit.setText("Wybierz jednostkę leku:");

            // Wczytaj dane do Spinnera formy leku
            ArrayAdapter<CharSequence> formAdapter = ArrayAdapter.createFromResource(requireContext(),
                    R.array.medication_form_array, android.R.layout.simple_spinner_item);
            formAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerMedicationForm.setAdapter(formAdapter);
            spinnerMedicationForm.setSelection(getIndex(spinnerMedicationForm, existingMedication.getForm()));

            // Wczytaj dane do Spinnera jednostki leku (przykład, dostosuj do swoich potrzeb)
            ArrayAdapter<CharSequence> unitAdapter = ArrayAdapter.createFromResource(requireContext(),
                    R.array.unit_array, android.R.layout.simple_spinner_item);
            unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerUnit.setAdapter(unitAdapter);
            spinnerUnit.setSelection(getIndex(spinnerUnit, existingMedication.getUnit()));

            // Dodaj rozkład graficzny dla każdego planu z existingMedication.getPlans()
            for (ScheduleItem scheduleItem : existingMedication.getPlans()) {
                addScheduleLayout(scheduleItem);
            }
        }
    }

    // Funkcja pomocnicza do uzyskania indeksu wybranej wartości w Spinnerze
    private int getIndex(Spinner spinner, String value) {
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinner.getAdapter();
        return adapter.getPosition(value);
    }


    private void setupUI() {
        Log.d("MainMedicationDialog", "Main Medication inside setupUI: " + mainMedication);
        textViewMedicationName.setText("Nazwa leku głównego:");
        textViewMainMedicationName.setText(mainMedication);
        textViewSelectMedicationForm.setText("Wybierz formę leku: ");
        textViewSelectMedicationUnit.setText("Wybierz jednostkę leku:");

        ArrayAdapter<CharSequence> formAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.medication_form_array, android.R.layout.simple_spinner_item);
        formAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMedicationForm.setAdapter(formAdapter);

        btnAddSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addScheduleLayout(null);
            }
        });

        btnAddMainMedication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMedicationToDatabase();
            }
        });

        btnCancelMainMedication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Zastąp MainMedicationDialogFragment przez MedicationsFragment
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, new MedicationsFragment())
                        .commit();
            }
        });
    }

    // Zaktualizowano funkcję addScheduleLayout, aby przyjmowała ScheduleItem
    private void addScheduleLayout(ScheduleItem scheduleItem) {
        ScheduleLayout scheduleLayout = new ScheduleLayout(requireContext());
        scheduleLayouts.add(scheduleLayout);
        linearLayoutSchedules.addView(scheduleLayout);

        // Jeżeli przekazano ScheduleItem, wprowadź dane do nowo utworzonego layoutu
        if (scheduleItem != null) {
            scheduleLayout.populateData(scheduleItem);
        }
    }


    private void saveMedicationToDatabase() {
        String name = textViewMainMedicationName.getText().toString().trim();
        String form = spinnerMedicationForm.getSelectedItem().toString();
        String unit = spinnerUnit.getSelectedItem().toString();

        if (name.isEmpty()) {
            Log.d("ScheduleLayout", "name is empty ");
        }

        Medication medication = new Medication(null, form, name, unit, new ArrayList<>());

        for (ScheduleLayout scheduleLayout : scheduleLayouts) {
            Log.d("ScheduleLayout", "ScheduleItem: ");
            ScheduleItem scheduleItem = scheduleLayout.getScheduleItem();
            if (scheduleItem != null) {
                Log.d("ScheduleLayout", "ScheduleItem dose: " + scheduleLayout.getScheduleItem().getDose() + " ScheduleItem daysOfWeek: " + scheduleLayout.getScheduleItem().getDaysOfWeek() + " ScheduleItem time: " + scheduleLayout.getScheduleItem().getTime());
                medication.getPlans().add(scheduleItem);
            }
        }

        String userId = auth.getCurrentUser().getUid();

        // Sprawdź, czy już istnieje dokument z takim mainMedication
        db.collection("Users")
                .document(userId)
                .collection("Medications")
                .whereEqualTo("name", mainMedication)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        if (documents.size() > 0) {
                            // Aktualizuj istniejący dokument
                            DocumentSnapshot existingDocument = documents.get(0);
                            db.collection("Users")
                                    .document(userId)
                                    .collection("Medications")
                                    .document(existingDocument.getId())
                                    .set(medication)
                                    .addOnSuccessListener(documentReference -> {
                                        // Zastąp MainMedicationDialogFragment przez MedicationsFragment
                                        replaceWithMedicationsFragment();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle failure
                                    });
                        } else {
                            // Dodaj nowy dokument
                            db.collection("Users")
                                    .document(userId)
                                    .collection("Medications")
                                    .add(medication)
                                    .addOnSuccessListener(documentReference -> {
                                        // Zastąp MainMedicationDialogFragment przez MedicationsFragment
                                        replaceWithMedicationsFragment();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle failure
                                    });
                        }
                    } else {
                        // Handle failure
                    }
                });
    }

    private void replaceWithMedicationsFragment() {
        // Zastąp MainMedicationDialogFragment przez MedicationsFragment
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, new MedicationsFragment())
                .commit();
    }


}

package pl.pollub.android.myapplication.ui.medications;

import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import pl.pollub.android.myapplication.R;
import pl.pollub.android.myapplication.User;

public class OtherMedicationDialogFragment extends Fragment {

    private EditText editTextOtherMedicationName;
    private TextView textViewMedicationName;
    private TextView textViewSelectMedicationForm;
    private TextView textViewSelectMedicationUnit;
    private Spinner spinnerMedicationForm, spinnerUnit;
    private LinearLayout linearLayoutOtherSchedules;
    private Button btnAddSchedule;
    private Button btnAddOtherMedication;
    private Button btnCancelOtherMedication;
    private List<OtherScheduleLayout> otherScheduleLayouts;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_other_medication_dialog, container, false);

        textViewMedicationName = view.findViewById(R.id.textViewMedicationName);
        editTextOtherMedicationName = view.findViewById(R.id.editTextOtherMedicationName);
        textViewSelectMedicationForm = view.findViewById(R.id.textViewSelectMedicationForm);
        textViewSelectMedicationUnit = view.findViewById(R.id.textViewSelectMedicationUnit);
        spinnerMedicationForm = view.findViewById(R.id.spinnerMedicationForm);
        spinnerUnit = view.findViewById(R.id.spinnerUnit);
        linearLayoutOtherSchedules = view.findViewById(R.id.linearLayoutOtherSchedules);
        btnAddSchedule = view.findViewById(R.id.buttonAddSchedule);
        btnAddOtherMedication = view.findViewById(R.id.btnAddOtherMedication);
        btnCancelOtherMedication = view.findViewById(R.id.btnCancelOtherMedication);

        otherScheduleLayouts = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        setupUI();

        return view;
    }


    private void setupUI() {
        textViewMedicationName.setText("Podaj nazwę leku:");
        textViewSelectMedicationForm.setText("Wybierz formę leku:");
        textViewSelectMedicationUnit.setText("Wybierz jednostkę leku:");

        ArrayAdapter<CharSequence> formAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.medication_form_array, android.R.layout.simple_spinner_item);
        formAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMedicationForm.setAdapter(formAdapter);

        btnAddSchedule.setOnClickListener(v -> addOtherScheduleLayout());

        btnAddOtherMedication.setOnClickListener(v -> saveMedicationToDatabase());

        btnCancelOtherMedication.setOnClickListener(v -> {
            // Zastąp OtherMedicationDialogFragment przez inny Fragment (jeśli wymagane)
            replaceWithMedicationsFragment();
        });
    }

    private void addOtherScheduleLayout() {
        OtherScheduleLayout otherScheduleLayout = new OtherScheduleLayout(requireContext());
        otherScheduleLayouts.add(otherScheduleLayout);
        linearLayoutOtherSchedules.addView(otherScheduleLayout);
    }


    private void saveMedicationToDatabase() {
        String name = editTextOtherMedicationName.getText().toString().trim();
        String form = spinnerMedicationForm.getSelectedItem().toString();
        String unit = spinnerUnit.getSelectedItem().toString();

        if (TextUtils.isEmpty(name)) {
            // Handle empty name
            return;
        }

        OtherMedication medication = new OtherMedication(null, form, name, unit, new ArrayList<>());

        for (OtherScheduleLayout otherScheduleLayout : otherScheduleLayouts) {
            OtherScheduleItem scheduleItem = otherScheduleLayout.getScheduleItem();
            if (scheduleItem != null) {
                medication.getPlans().add(scheduleItem);
            }
        }

        String userId = auth.getCurrentUser().getUid();

        // Sprawdź, czy lek o podanej nazwie już istnieje w bazie danych
        Query query = db.collection("Users")
                .document(userId)
                .collection("Medications")
                .whereEqualTo("name", name);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                if (!documents.isEmpty()) {
                    // Lek o podanej nazwie już istnieje, aktualizuj istniejący dokument
                    DocumentSnapshot existingDocument = documents.get(0);
                    db.collection("Users")
                            .document(userId)
                            .collection("Medications")
                            .document(existingDocument.getId())
                            .set(medication)
                            .addOnSuccessListener(documentReference -> replaceWithMedicationsFragment())
                            .addOnFailureListener(e -> {
                                // Handle failure
                            });
                } else {
                    // Lek o podanej nazwie nie istnieje, dodaj nowy dokument
                    db.collection("Users")
                            .document(userId)
                            .collection("Medications")
                            .add(medication)
                            .addOnSuccessListener(documentReference -> replaceWithMedicationsFragment())
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

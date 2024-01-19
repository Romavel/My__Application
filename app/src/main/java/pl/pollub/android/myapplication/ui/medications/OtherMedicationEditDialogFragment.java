package pl.pollub.android.myapplication.ui.medications;

import android.app.AlertDialog;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import pl.pollub.android.myapplication.R;

public class OtherMedicationEditDialogFragment extends Fragment {

    private EditText editTextOtherMedicationName;
    private TextView textViewMedicationName;
    private TextView textViewSelectMedicationForm;
    private TextView textViewSelectMedicationUnit;
    private Spinner spinnerMedicationForm, spinnerUnit;
    private LinearLayout linearLayoutOtherSchedules;
    private Button btnAddSchedule;
    private Button btnEditOtherMedication;
    private Button btnCancelEditOtherMedication;
    private List<OtherScheduleLayout> otherScheduleLayouts;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private OtherMedication originalMedication;

    private OnEditCompleteListener onEditCompleteListener;

    public OtherMedicationEditDialogFragment(OtherMedication medication) {
        this.originalMedication = medication;
    }

    public interface OnEditCompleteListener {
        void onEditComplete(OtherMedication updatedMedication);
    }

    public void setOnEditCompleteListener(OnEditCompleteListener listener) {
        this.onEditCompleteListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_other_medication_edit_dialog, container, false);

        textViewMedicationName = view.findViewById(R.id.textViewEditMedicationName);
        editTextOtherMedicationName = view.findViewById(R.id.editTextEditOtherMedicationName);
        textViewSelectMedicationForm = view.findViewById(R.id.textViewSelectMedicationForm);
        textViewSelectMedicationUnit = view.findViewById(R.id.textViewSelectMedicationUnit);
        spinnerMedicationForm = view.findViewById(R.id.spinnerMedicationForm);
        spinnerUnit = view.findViewById(R.id.spinnerUnit);
        linearLayoutOtherSchedules = view.findViewById(R.id.linearLayoutOtherSchedules);
        btnAddSchedule = view.findViewById(R.id.buttonAddSchedule);
        btnEditOtherMedication = view.findViewById(R.id.btnEditOtherMedication);
        btnCancelEditOtherMedication = view.findViewById(R.id.btnCancelEditOtherMedication);

        otherScheduleLayouts = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        setupUI();

        return view;
    }

    private void setupUI() {
        textViewMedicationName.setText("Zmień nazwę leku:");
        editTextOtherMedicationName.setText(originalMedication.getName());

        textViewSelectMedicationForm.setText("Wybierz formę leku:");
        textViewSelectMedicationUnit.setText("Wybierz jednostkę leku:");

        ArrayAdapter<CharSequence> formAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.medication_form_array, android.R.layout.simple_spinner_item);
        formAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMedicationForm.setAdapter(formAdapter);
        int formPosition = formAdapter.getPosition(originalMedication.getForm());
        spinnerMedicationForm.setSelection(formPosition);

        ArrayAdapter<CharSequence> unitAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.unit_array, android.R.layout.simple_spinner_item);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(unitAdapter);
        int unitPosition = unitAdapter.getPosition(originalMedication.getUnit());
        spinnerUnit.setSelection(unitPosition);

        for (OtherScheduleItem scheduleItem : originalMedication.getPlans()) {
            addOtherScheduleLayout(scheduleItem);
        }

        btnAddSchedule.setOnClickListener(v -> addOtherScheduleLayout());

        btnEditOtherMedication.setOnClickListener(v -> saveEditedMedication());

        btnCancelEditOtherMedication.setOnClickListener(v -> replaceWithMedicationsFragment());
    }

    private void addOtherScheduleLayout(OtherScheduleItem scheduleItem) {
        OtherScheduleLayout otherScheduleLayout = new OtherScheduleLayout(requireContext());
        otherScheduleLayout.setScheduleItem(scheduleItem);
        otherScheduleLayouts.add(otherScheduleLayout);
        linearLayoutOtherSchedules.addView(otherScheduleLayout);
    }

    private void addOtherScheduleLayout() {
        OtherScheduleLayout otherScheduleLayout = new OtherScheduleLayout(requireContext());
        otherScheduleLayouts.add(otherScheduleLayout);
        linearLayoutOtherSchedules.addView(otherScheduleLayout);
    }

    private void saveEditedMedication() {
        String name = editTextOtherMedicationName.getText().toString().trim();
        String form = spinnerMedicationForm.getSelectedItem().toString();
        String unit = spinnerUnit.getSelectedItem().toString();

        if (name.isEmpty()) {
            // Handle empty name
            Toast.makeText(requireContext(), "Nazwa leku nie może być pusta", Toast.LENGTH_SHORT).show();
            return;
        }

        OtherMedication editedMedication = new OtherMedication(originalMedication.getDocumentId(), form, name, unit, new ArrayList<>());

        for (OtherScheduleLayout otherScheduleLayout : otherScheduleLayouts) {
            OtherScheduleItem scheduleItem = otherScheduleLayout.getScheduleItem();
            if (scheduleItem != null) {
                editedMedication.getPlans().add(scheduleItem);
            }
        }

        String userId = auth.getCurrentUser().getUid();

        // Sprawdź, czy lek o podanym ID już istnieje w bazie danych
        db.collection("Users")
                .document(userId)
                .collection("Medications")
                .document(originalMedication.getDocumentId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Dokument istnieje, zaktualizuj go
                            db.collection("Users")
                                    .document(userId)
                                    .collection("Medications")
                                    .document(originalMedication.getDocumentId())
                                    .set(editedMedication)
                                    .addOnSuccessListener(documentReference -> {
                                        replaceWithMedicationsFragment();
                                        Toast.makeText(requireContext(), "Lek został zaktualizowany", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("EditMedication", "Błąd podczas aktualizacji leku", e);
                                        Toast.makeText(requireContext(), "Błąd podczas aktualizacji leku", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            // Dokument nie istnieje, obsłuż błąd
                            Toast.makeText(requireContext(), "Dokument o podanym ID nie istnieje", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Handle failure
                        Log.e("EditMedication", "Błąd podczas pobierania informacji o leku", task.getException());
                        Toast.makeText(requireContext(), "Błąd podczas pobierania informacji o leku", Toast.LENGTH_SHORT).show();
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

package pl.pollub.android.myapplication.ui.medications;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.pollub.android.myapplication.R;
import pl.pollub.android.myapplication.TimePickerFragment;


public class AddMedicationDialogFragment extends DialogFragment {

    private EditText medicationNameEditText;
    private Spinner medicationFormSpinner;
    private List<Map<String, Object>> schedules;
    private LinearLayout schedulesContainer;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_medication_dialog, null);

        medicationNameEditText = view.findViewById(R.id.editTextMedicationName);
        medicationFormSpinner = view.findViewById(R.id.spinnerMedicationForm);
        schedulesContainer = view.findViewById(R.id.linearLayoutSchedules);

        schedules = new ArrayList<>();

        // Spinner z formami leku
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.medication_form_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        medicationFormSpinner.setAdapter(adapter);

        // Dodaj harmonogram domyślnie
        addScheduleItem();

        // Przyciski Dodaj i Anuluj
        builder.setView(view)
                .setTitle("Dodaj lek")
                .setPositiveButton("Dodaj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Logika dodawania leku
                        String medicationName = medicationNameEditText.getText().toString();
                        String medicationForm = medicationFormSpinner.getSelectedItem().toString();

                        if (!TextUtils.isEmpty(medicationName)) {
                            // Dodaj lek do bazy danych lub wykonaj inne operacje
                            addMedicationToDatabase(medicationName, medicationForm, schedules);
                        }
                    }
                })
                .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });

        // Przycisk dodający harmonogram
        Button addScheduleButton = view.findViewById(R.id.buttonAddSchedule);
        addScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (schedules.size() < 7) {
                    addScheduleItem();
                }
            }
        });

        return builder.create();
    }

    private void addMedicationToDatabase(String medicationName, String medicationForm, List<Map<String, Object>> schedules) {
        // Utwórz obiekt Medication
        Medication medication = new Medication();
        medication.setName(medicationName);
        medication.setForm(medicationForm);

        // Utwórz listę ScheduleItem
        List<ScheduleItem> scheduleItemList = new ArrayList<>();
        for (Map<String, Object> scheduleData : schedules) {
            ScheduleItem scheduleItem = new ScheduleItem();

            // Sprawdzenie nullability przed uzyskaniem dostępu do pól
            scheduleItem.setDose(scheduleData.get("dose") != null ? ((Double) scheduleData.get("dose")).doubleValue() : 0.0);

            // Konwersja dni tygodnia na liczby i dodanie do listy
            ScheduleItemFragment scheduleItemFragment = new ScheduleItemFragment();
            List<Integer> daysOfWeek = scheduleItemFragment.convertDaysOfWeek((String) scheduleData.get("days_of_week"));
            scheduleItem.setDaysOfWeek(daysOfWeek);

            scheduleItem.setTime((String) scheduleData.get("time"));
            scheduleItem.setUnit((String) scheduleData.get("unit"));

            scheduleItemList.add(scheduleItem);
        }


        medication.setPlans(scheduleItemList);

        // Pobierz identyfikator zalogowanego użytkownika
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Dodaj lek do kolekcji Medications w dokumencie zalogowanego użytkownika
        FirebaseFirestore.getInstance().collection("Users")
                .document(userId)
                .collection("Medications")
                .add(medication)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Logowanie do konsoli informacji o dodanym leku
                        Log.d("AddMedicationDialog", "Lek dodany do Firestore: " + documentReference.getId());
                        dismiss(); // Zamknij okno dialogowe po dodaniu leku
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("AddMedicationDialog", "Błąd podczas dodawania leku do Firestore", e);
                    }
                });
    }

    private void addScheduleItem() {
        View scheduleView = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_schedule_item, null);
        schedulesContainer.addView(scheduleView);

        // Inicjalizacja fragmentu harmonogramu
        ScheduleItemFragment scheduleItemFragment = new ScheduleItemFragment(scheduleView, this); // Przekazanie samego this jako drugiego argumentu
        getChildFragmentManager().beginTransaction()
                .add(scheduleItemFragment, "scheduleItemFragment")
                .commit();

        // Dodaj harmonogram do listy
        schedules.add(scheduleItemFragment.getSchedule());
    }
}

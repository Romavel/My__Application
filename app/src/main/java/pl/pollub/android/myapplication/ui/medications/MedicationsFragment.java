package pl.pollub.android.myapplication.ui.medications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.type.Color;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pl.pollub.android.myapplication.R;
import pl.pollub.android.myapplication.databinding.FragmentDietBinding;
import pl.pollub.android.myapplication.databinding.FragmentMedicationsBinding;
import pl.pollub.android.myapplication.ui.diet.DietAdapter;
import pl.pollub.android.myapplication.ui.diet.DietDialogFragment;
import pl.pollub.android.myapplication.ui.diet.NewDiet;
import pl.pollub.android.myapplication.ui.measurements.InrMeasurementDialogFragment;
import pl.pollub.android.myapplication.ui.measurements.InrMeasurementListFragment;
import pl.pollub.android.myapplication.ui.measurements.PressureMeasurementDialogFragment;
import pl.pollub.android.myapplication.ui.measurements.PressureMeasurementListFragment;

public class MedicationsFragment extends Fragment {

    private FragmentMedicationsBinding binding;
    private boolean todayDocument = false;
    private boolean matchingDate = false;
    private static NewSymptom newSymptom;
    private RecyclerView recyclerViewOtherMeds;
    private RecyclerView recyclerViewSymptoms;
    SymptomAdapter symptomAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MedicationsViewModel medicationsViewModel =
                new ViewModelProvider(this).get(MedicationsViewModel.class);

        binding = FragmentMedicationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Dodaj obsługę kliknięcia w mainMedicationTextView
        TextView mainMedicationTextView = root.findViewById(R.id.mainMedicationTextView);
        CardView mainMedicationsCardView = root.findViewById(R.id.mainMedicationCardView);
        recyclerViewSymptoms = root.findViewById(R.id.recyclerViewSymptoms);
        mainMedicationsCardView.setCardBackgroundColor(0xFF365FF4);

        mainMedicationsCardView.setOnClickListener(new View.OnClickListener() {
            private boolean isWhiteBackground = true;

            @Override
            public void onClick(View v) {
                // Zmieniaj kolor tła między białym a zielonym
                if (isWhiteBackground) {
                    mainMedicationsCardView.setCardBackgroundColor(0xFF4CAF50);
                    showMedicationTakenToast();
                } else {
                    mainMedicationsCardView.setCardBackgroundColor(0xFF365FF4);
                }
                isWhiteBackground = !isWhiteBackground;
            }
        });
        checkExistingDocument();
        // Ustaw Swipe to Refresh
        SwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.medicationsSwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Odśwież dane po przeciągnięciu
            checkExistingDocument();
            swipeRefreshLayout.setRefreshing(false);
        });


        return root;
    }

    private void checkExistingDocument() {
        // Pobierz ID aktualnie zalogowanego użytkownika
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Utwórz kolekcję "Intakes" wewnątrz kolekcji "Users"
        final CollectionReference intakesCollection = db.collection("Users").document(userId).collection("Symptoms");


        // Utwórz zapytanie, aby pobrać dokumenty z datą nie wcześniejszą niż dzisiaj
        Query query = intakesCollection
                .orderBy("day", Query.Direction.DESCENDING)
                .limit(1);

        Log.d("MedicationsFragment", "Query: " + query);
        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Załóżmy, że zakładamy, że istnieje tylko jeden dokument z dzisiejszą datą,
                        // Jeśli istnieje więcej niż jeden, musisz dostosować to odpowiednio
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        newSymptom = documentSnapshot.toObject(NewSymptom.class);
                        // Pobierz dzisiejszą datę
                        Date todayDate = Calendar.getInstance().getTime();
                        // Pobierz datę z ostatniego dokumentu
                        Date documentDate = newSymptom.getDay().toDate();
                        Log.d("MedicationsFragment", "Before MatchingDate: " + matchingDate);
                        if (isSameDay(documentDate, todayDate)) {
                            Log.d("MedicationsFragment", "MatchingDate changed to True");
                            matchingDate = true;
                        }

                        if (newSymptom != null && matchingDate) {
                            // Wyświetl item_diet
                            Log.d("MedicationsFragment", "Setup recyclerView from: " + newSymptom.getSymptom_arr());
                            setupSymptomsRecyclerView(newSymptom.getSymptom_arr());
                        }
                        Log.d("MedicationsFragment", "After MatchingDate: " + matchingDate);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("MedicationsFragment", "Błąd podczas sprawdzania istnienia dokumentu", e);
                });
    }
    // Dodaj tę metodę do klasy MedicationsFragment
    private void setupSymptomsRecyclerView(List<String> symptomsList) {
        // Ustawienie layoutu w RecyclerView
        recyclerViewSymptoms.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Utworzenie adaptera i przypisanie go do RecyclerView
        symptomAdapter = new SymptomAdapter(symptomsList);
        recyclerViewSymptoms.setAdapter(symptomAdapter);
    }




    // Metoda do porównywania dat i sprawdzania, czy są z tego samego dnia
    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    // Funkcja wyświetlająca Toast z informacją o przyjęciu leku
    private void showMedicationTakenToast() {
        // Pobierz dzisiejszą datę
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        // Pobierz nazwę leku (możesz dostosować to do własnych potrzeb)
        String medicationName = "Warfin";

        // Twórz komunikat Toast
        String toastMessage = "W dniu " + currentDate + " przyjęto " + medicationName;
        Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_SHORT).show();
    }

    public void showMainMedicationDialog() {
        FragmentManager fragmentManager = getParentFragmentManager();
        MainMedicationDialogFragment mainMedicationDialogFragment = new MainMedicationDialogFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, mainMedicationDialogFragment)
                .addToBackStack(null)
                .commit();
    }
    public void showMedicationDialog() {
        FragmentManager fragmentManager = getParentFragmentManager();
        OtherMedicationDialogFragment otherMedicationDialogFragment = new OtherMedicationDialogFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, otherMedicationDialogFragment)
                .addToBackStack(null)
                .commit();
    }

    public void showSymptomDialog() {
        FragmentManager fragmentManager = getParentFragmentManager();
        AddSymptomDialogFragment addSymptomDialogFragment = new AddSymptomDialogFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, addSymptomDialogFragment)
                .addToBackStack(null)
                .commit();
    }
    public void showSymptomList() {
        // Otwórz nowy fragment z listą pomiarów INR
        FragmentManager fragmentManager = getParentFragmentManager();
        SymptomHistoryFragment symptomHistoryFragment = new SymptomHistoryFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, symptomHistoryFragment)
                .addToBackStack(null)
                .commit();
    }
    public void showPressureMeasurementList() {
        // Otwórz nowy fragment z listą pomiarów ciśnienia
        FragmentManager fragmentManager = getParentFragmentManager();
        PressureMeasurementListFragment pressureListFragment = new PressureMeasurementListFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, pressureListFragment)
                //.addToBackStack(null)
                .commit();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

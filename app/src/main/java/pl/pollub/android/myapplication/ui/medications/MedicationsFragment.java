package pl.pollub.android.myapplication.ui.medications;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import pl.pollub.android.myapplication.R;
import pl.pollub.android.myapplication.User;
import pl.pollub.android.myapplication.databinding.FragmentMedicationsBinding;
import pl.pollub.android.myapplication.ui.measurements.PressureMeasurementListFragment;

public class MedicationsFragment extends Fragment {

    private FragmentMedicationsBinding binding;
    String medicationDocumentId;
    String medicationTakenDocumentId;
    String mainMedicationName;
    Date timestampDate;
    private boolean matchingDate = false;
    private boolean isMedicationTaken = false;
    private MedicineTaken todayMedicineTaken;
    private static NewSymptom newSymptom;
    private RecyclerView recyclerViewOtherMeds;
    private RecyclerView recyclerViewSymptoms;
    private RecyclerView recyclerViewOtherMedications;
    private OtherMedicationsAdapter otherMedicationsAdapter;
    private List<OtherMedication> otherMedicationsList;
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
        recyclerViewOtherMedications = root.findViewById(R.id.otherMedicationsRecyclerView);
        recyclerViewOtherMedications.setLayoutManager(new LinearLayoutManager(requireContext()));


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new MedicationsFragment.SwipeToDeleteCallback());
        itemTouchHelper.attachToRecyclerView(recyclerViewOtherMedications);


        checkExistingMainMedicineDocument();
        checkExistingSymptomDocument();
        fetchOtherMedicationsData();
        setupOtherMedicationsRecyclerView();
        // Ustaw Swipe to Refresh
        SwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.medicationsSwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Odśwież dane po przeciągnięciu
            fetchOtherMedicationsData();
            setupOtherMedicationsRecyclerView();
            checkExistingMainMedicineDocument();
            checkExistingSymptomDocument();
            swipeRefreshLayout.setRefreshing(false);
        });


        return root;
    }


    private void setupMainMedicationUI() {
        Log.d("MedicationsFragment", "Jestem wewnątrz funkcji setupMainMedicationUI()");
        // Pobierz referencję do widoku karty leku głównego
        CardView mainMedicationsCardView = requireView().findViewById(R.id.mainMedicationCardView);
        TextView mainMedicationTextView = requireView().findViewById(R.id.mainMedicationTextView);
        mainMedicationTextView.setText(mainMedicationName);

        // Ustawienie koloru tła w zależności od isMedicationTaken
        mainMedicationsCardView.setCardBackgroundColor(isMedicationTaken ? 0xFF4CAF50 : 0xFF365FF4);


        // Dodanie obsługi zdarzenia kliknięcia w kartę leku głównego
        mainMedicationsCardView.setOnClickListener(v -> {
            // Zmiana koloru tła między białym a zielonym
            isMedicationTaken = !isMedicationTaken;
            mainMedicationsCardView.setCardBackgroundColor(isMedicationTaken ? 0xFF4CAF50 : 0xFF365FF4);

            if (isMedicationTaken) {
                mainMedicationsCardView.setCardBackgroundColor(0xFF4CAF50);
                checkExistingMainMedicineDocument();
                // Aktualizacja stanu isTaken w bazie danych
                updateMedicineTakenInDatabase(isMedicationTaken);
            } else {
                mainMedicationsCardView.setCardBackgroundColor(0xFF365FF4);
                checkExistingMainMedicineDocument();
                // Aktualizacja stanu isTaken w bazie danych
                updateMedicineTakenInDatabase(isMedicationTaken);
            }
            Log.d("MedicationsFragment", "isTaken: " + isMedicationTaken);
        });
    }



    private void checkExistingMainMedicineDocument() {
        // Pobierz ID aktualnie zalogowanego użytkownika
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Pobierz główny lek użytkownika
        FirebaseFirestore.getInstance().collection("Users").document(userId).get()
                .addOnSuccessListener(userDocument -> {
                    if (userDocument.exists()) {
                        User currentUser = userDocument.toObject(User.class);
                        if (currentUser != null) {
                            mainMedicationName = currentUser.getMedication();
                            Log.d("MedicationsFragment", "mainMedicationName: " + mainMedicationName);

                            // Szukaj dokumentu w kolekcji "Medications" dla głównego leku
                            FirebaseFirestore.getInstance()
                                    .collection("Users")
                                    .document(userId)
                                    .collection("Medications")
                                    .whereEqualTo("name", mainMedicationName)
                                    .limit(1)
                                    .get()
                                    .addOnSuccessListener(medicationsQuerySnapshot -> {
                                        if (!medicationsQuerySnapshot.isEmpty()) {
                                            DocumentSnapshot medicationsDocumentSnapshot = medicationsQuerySnapshot.getDocuments().get(0);
                                            medicationDocumentId = medicationsDocumentSnapshot.getId();
                                            Log.d("MedicationsFragment", "DokumentId: " + medicationDocumentId);
                                            FirebaseFirestore.getInstance()
                                                    .collection("Users")
                                                    .document(userId)
                                                    .collection("Medications")
                                                    .document(medicationDocumentId)
                                                    .collection("MedicineTaken")
                                                    .orderBy("date", Query.Direction.DESCENDING)
                                                    .limit(1)
                                                    .get()
                                                    .addOnSuccessListener(medicineTakenQuerySnapshot -> {
                                                        if (!medicineTakenQuerySnapshot.isEmpty()) {
                                                            DocumentSnapshot medicineTakenDocumentSnapshot = medicineTakenQuerySnapshot.getDocuments().get(0);
                                                            todayMedicineTaken = medicineTakenDocumentSnapshot.toObject(MedicineTaken.class);
                                                            Log.d("MedicationsFragment", "IsTaken inside document: " + todayMedicineTaken.getIsTaken());
                                                            timestampDate = todayMedicineTaken.getDate().toDate();
                                                            medicationTakenDocumentId = medicineTakenDocumentSnapshot.getId();
                                                            ;
                                                            Log.d("MedicationsFragment", "timestamp inside document: " + todayMedicineTaken.getDate().toDate());
                                                            if (todayMedicineTaken != null && isSameDay(timestampDate, Calendar.getInstance().getTime())) {
                                                                isMedicationTaken = todayMedicineTaken.getIsTaken();
                                                            }
                                                        }
                                                        // Setup UI based on isMedicationTaken
                                                        setupMainMedicationUI();
                                                    })
                                                    .addOnFailureListener(e -> Log.e("MedicationsFragment", "Error querying MedicineTaken", e));
                                        }
                                    })
                                    .addOnFailureListener(e -> Log.e("MedicationsFragment", "Error querying Medications", e));
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("MedicationsFragment", "Error getting user data", e));
    }


    private void updateMedicineTakenInDatabase(boolean isTaken) {
        // Sprawdź, czy documentId jest dostępne
        if (medicationTakenDocumentId != null && medicationDocumentId != null) {
            // Pobierz aktualnie zalogowanego użytkownika
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            if (currentUser != null) {
                // Pobierz identyfikator zalogowanego użytkownika
                String userId = currentUser.getUid();

                // Pobierz obiekt User z bazy danych Firestore
                FirebaseFirestore.getInstance().collection("Users").document(userId).get()
                        .addOnCompleteListener(userTask -> {
                            if (userTask.isSuccessful() && userTask.getResult() != null) {
                                DocumentSnapshot userDocument = userTask.getResult();
                                if (userDocument.exists()) {
                                    // Jeśli dokument istnieje, pobierz obiekt User
                                    User user = userDocument.toObject(User.class);

                                    // Pobierz nazwę leku głównego z pola medication w obiekcie User
                                    String mainMedicationName = user.getMedication();

                                    // Jeżeli nazwa leku nie jest pusta
                                    if (!TextUtils.isEmpty(mainMedicationName)) {
                                        // Zbuduj ścieżkę do dokumentu w kolekcji Medications
                                        DocumentReference medicationRef = FirebaseFirestore.getInstance()
                                                .collection("Users")
                                                .document(userId)
                                                .collection("Medications")
                                                .document(medicationDocumentId);

                                        // Zbuduj ścieżkę do podkolekcji MedicineTaken
                                        CollectionReference medicineTakenCollection = medicationRef.collection("MedicineTaken");


                                        timestampDate = todayMedicineTaken.getDate().toDate();
                                        // Zaktualizuj dokument lub dodaj nowy
                                        if (isSameDay(timestampDate, Calendar.getInstance().getTime())) {
                                            // Jeżeli dokument istnieje, zaktualizuj stan isTaken
                                            medicationRef.collection("MedicineTaken").document(medicationTakenDocumentId).update("isTaken", isTaken);
                                        } else {
                                            // Jeżeli dokument nie istnieje, dodaj nowy dokument
                                            MedicineTaken newMedicineTaken = new MedicineTaken(Timestamp.now(), isTaken);
                                            medicineTakenCollection.add(newMedicineTaken)
                                                    .addOnSuccessListener(documentReference -> {
                                                        // Zapisz documentId dla późniejszego użycia
                                                        medicationTakenDocumentId = documentReference.getId();
                                                    })
                                                    .addOnFailureListener(e -> Log.e("MedicationsFragment", "Error adding new MedicineTaken document", e));
                                        }
                                    }
                                }
                            }
                        });
            }
        } else {
            Log.e("MedicationsFragment", "DocumentId is null");
        }
    }


    private void checkExistingSymptomDocument() {
        // Pobierz ID aktualnie zalogowanego użytkownika
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Utwórz kolekcję "Intakes" wewnątrz kolekcji "Users"
        final CollectionReference intakesCollection = db.collection("Users").document(userId).collection("Symptoms");


        // Utwórz zapytanie, aby pobrać dokumenty z datą nie wcześniejszą niż dzisiaj
        Query query = intakesCollection
                .orderBy("day", Query.Direction.DESCENDING)
                .limit(1);

        //Log.d("MedicationsFragment", "Query: " + query);
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
                        //Log.d("MedicationsFragment", "Before MatchingDate: " + matchingDate);
                        if (isSameDay(documentDate, todayDate)) {
                            //Log.d("MedicationsFragment", "MatchingDate changed to True");
                            matchingDate = true;
                        }

                        if (newSymptom != null && matchingDate) {
                            // Wyświetl item_diet
                            //Log.d("MedicationsFragment", "Setup recyclerView from: " + newSymptom.getSymptom_arr());
                            setupSymptomsRecyclerView(newSymptom.getSymptom_arr());
                        }
                        //Log.d("MedicationsFragment", "After MatchingDate: " + matchingDate);
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

    private void setupOtherMedicationsRecyclerView() {
        // Ustaw adapter i obsłużenie kliknięcia elementu na liście
        otherMedicationsAdapter = new OtherMedicationsAdapter(otherMedicationsList);
        recyclerViewOtherMedications.setAdapter(otherMedicationsAdapter);

        /*
        // Dodaj obsługę kliknięcia na element na liście
        recyclerViewOtherMedications.addOnItemTouchListener(
                new RecyclerItemClickListener(requireContext(), recyclerViewOtherMedications,
                        new RecyclerItemClickListener.OnItemClickListener() {


                            @Override
                            public void onLongItemClick(View view, int position) {
                                // Obsługa przytrzymania elementu na liście (jeśli potrzebujesz)
                            }
                        })
        );
         */
        // Pobierz dane o innych lekach z bazy danych i zaktualizuj listę
        fetchOtherMedicationsData();
    }

    // Dodaj do klasy MedicationsFragment poniższą funkcję
    private void fetchOtherMedicationsData() {
        // Pobierz ID aktualnie zalogowanego użytkownika
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Pobierz leki z kolekcji "Medications" dla danego użytkownika (bez leków Sintrom i Warfin)
        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(userId)
                .collection("Medications")
                .whereNotIn("name", Arrays.asList("Sintrom", "Warfin"))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    otherMedicationsList = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        OtherMedication otherMedication = documentSnapshot.toObject(OtherMedication.class);
                        if (otherMedication != null) {
                            otherMedicationsList.add(otherMedication);
                        }
                    }
                    // Zaktualizuj dane w adapterze
                    otherMedicationsAdapter.setOtherMedicationsList(otherMedicationsList);
                    otherMedicationsAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("MedicationsFragment", "Error fetching other medications data", e));
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

    private class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

        public SwipeToDeleteCallback() {
            super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();

            // Obsługa przesunięcia w lewo (usunięcie)
            if (direction == ItemTouchHelper.LEFT) {
                showDeleteConfirmation(position);
            }
            // Obsługa przesunięcia w prawo (edycja)
            else if (direction == ItemTouchHelper.RIGHT) {
                // Dodaj obsługę edycji dla innych leków, jeśli potrzebujesz
                showDeleteConfirmation(position);
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorSwipeDelete))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorSwipeDelete))
                    .addSwipeRightActionIcon(R.drawable.ic_delete)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        private void showDeleteConfirmation(int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Potwierdź usunięcie")
                    .setMessage("Czy na pewno chcesz usunąć ten lek?")
                    .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Jeśli użytkownik potwierdzi, usuń lek
                            deleteOtherMedication(position);
                        }
                    })
                    .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Jeśli użytkownik anuluje, przywróć element na swoje miejsce
                            otherMedicationsAdapter.notifyItemChanged(position);
                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            // Jeśli użytkownik anuluje (poprzez naciśnięcie przycisku powrotu), przywróć element na swoje miejsce
                            otherMedicationsAdapter.notifyItemChanged(position);
                        }
                    })
                    .show();
        }

        private void deleteOtherMedication(int position) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String medicationId = otherMedicationsList.get(position).getDocumentId();

            // Usuń lek z Firestore Database
            FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(userId)
                    .collection("Medications")
                    .document(medicationId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        // Usunięto pomyślnie
                        otherMedicationsList.remove(position);
                        otherMedicationsAdapter.notifyItemRemoved(position);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("MedicationsFragment", "Błąd podczas usuwania leku", e);
                    });
        }
    }

}

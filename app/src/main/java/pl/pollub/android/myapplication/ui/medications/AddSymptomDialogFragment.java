package pl.pollub.android.myapplication.ui.medications;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import pl.pollub.android.myapplication.R;
import pl.pollub.android.myapplication.ui.diet.AlcoholLayout;
import pl.pollub.android.myapplication.ui.diet.CaffeineLayout;
import pl.pollub.android.myapplication.ui.diet.DietEntry;
import pl.pollub.android.myapplication.ui.diet.NewDiet;
import pl.pollub.android.myapplication.ui.diet.NicotineLayout;
import pl.pollub.android.myapplication.ui.diet.VegetableLayout;

public class AddSymptomDialogFragment extends DialogFragment {

    private Button btnHeadache, btnBleeding, btnBruises, btnChestPain, btnLegSwelling, btnPalpitations, btnDizzyness, btnHotFlush, btnCoughing, btnOther, btnAddSymptom, btnCancelSymptom;
    private LinearLayout container;
    private boolean isUpdate = false;
    private boolean matchingDate = false;
    private Map<String, View> layoutMap = new HashMap<>();
    private Set<String> selectedButtons = new HashSet<>();

    private static NewSymptom newSymptom;

    private String documentId;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_symptom_dialog, container, false);

        btnHeadache = view.findViewById(R.id.btnHeadache);
        btnBleeding = view.findViewById(R.id.btnBleeding);
        btnBruises = view.findViewById(R.id.btnBruises);
        btnChestPain = view.findViewById(R.id.btnChestPain);
        btnLegSwelling = view.findViewById(R.id.btnLegSwelling);
        btnPalpitations = view.findViewById(R.id.btnPalpitations);
        btnDizzyness = view.findViewById(R.id.btnDizzyness);
        btnHotFlush = view.findViewById(R.id.btnHotFlush);
        btnCoughing = view.findViewById(R.id.btnCoughing);
        //btnOther = view.findViewById(R.id.btnOther);
        btnAddSymptom = view.findViewById(R.id.btnAddSymptom);
        btnCancelSymptom = view.findViewById(R.id.btnCancelSymptom);

        container = view.findViewById(R.id.container);

        // Dodaj obsługę przycisków
        setupButton(btnHeadache, "Headache");
        setupButton(btnBleeding, "Bleeding");
        setupButton(btnBruises, "Bruises");
        setupButton(btnChestPain, "Chest_Pain");
        setupButton(btnLegSwelling, "Leg_Swelling");
        setupButton(btnPalpitations, "Palpitations");
        setupButton(btnDizzyness, "Dizzyness");
        setupButton(btnHotFlush, "Hot_Flush");
        setupButton(btnCoughing, "Coughing");
        //setupButton(btnOther, "Other");

        setupAddButton();
        setupCancelButton();
        checkExistingDocument();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        container = getView().findViewById(R.id.container);
    }

    private void setupAddButton() {
        btnAddSymptom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataToDatabase();
                dismiss(); // Zamknij dialog po dodaniu danych
            }
        });
    }

    private void setupCancelButton() {
        btnCancelSymptom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(); // Zamknij dialog bez zapisywania danych
            }
        });
    }

    private void setupButton(Button button, final String layoutTag) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedButtons.contains(layoutTag)) {
                    // Jeżeli przycisk był już zaznaczony, odznacz go
                    v.setBackgroundColor(Color.parseColor("#365FF4"));
                    selectedButtons.remove(layoutTag);
                } else {
                    // Jeżeli przycisk nie był zaznaczony, zaznacz go
                    v.setBackgroundColor(Color.parseColor("#0A900A"));
                    selectedButtons.add(layoutTag);
                }
            }
        });
    }

    private void checkExistingDocument() {
        // Pobierz ID aktualnie zalogowanego użytkownika
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Utwórz kolekcję "Symptoms" wewnątrz kolekcji "Users"
        final CollectionReference symptomsCollection = db.collection("Users").document(userId).collection("Symptoms");

        // Utwórz zapytanie, aby pobrać dokumenty z datą nie wcześniejszą niż dzisiaj
        Query query = symptomsCollection
                .orderBy("day", Query.Direction.DESCENDING)
                .limit(1);

        Log.d("AddSymptomDialogFragment", "Query: " + query);
        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                        Log.d("AddSymptomDialogFragment", "DocumentSnapshot: " + documentSnapshot);
                        NewSymptom newSymptom = documentSnapshot.toObject(NewSymptom.class);

                        // Pobierz dzisiejszą datę
                        Date todayDate = Calendar.getInstance().getTime();
                        String today = formatDateWithoutTime(todayDate);

                        Log.d("AddSymptomDialogFragment", "Today's date: " + today);

                        // Pobierz datę z ostatniego dokumentu
                        Date documentDate = newSymptom.getDay().toDate();
                        String document = formatDateWithoutTime(documentDate);
                        Log.d("AddSymptomDialogFragment", "CheckDate's date: " + document);

                        Log.d("AddSymptomDialogFragment", "Before MatchingDate" + matchingDate);
                        if (isSameDay(documentDate, todayDate)) {
                            Log.d("AddSymptomDialogFragment", "MatchingDate changed to True");
                            matchingDate = true;
                        }

                        Log.d("AddSymptomDialogFragment", "After MatchingDate" + matchingDate);
                        if (newSymptom != null && matchingDate) {
                            // Jeśli istnieją dokumenty z dzisiejszą datą, to jest to aktualizacja
                            isUpdate = true;
                            Log.d("AddSymptomDialogFragment", "NewSymptom symptomArray: " + newSymptom.getSymptom_arr() + " NewSymptom documentId: " + newSymptom.getDocumentId() + " NewSymptom day: " + newSymptom.getDay());
                            documentId = newSymptom.getDocumentId();
                            handleExistingData(newSymptom);
                        }
                    } else {
                        // Brak dokumentów dla dzisiejszej daty
                        matchingDate = false;
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("AddSymptomDialogFragment", "Błąd podczas sprawdzania istnienia dokumentu", e);
                });
    }


    private void handleExistingData(NewSymptom newSymptom) {
        Timestamp day = newSymptom.getDay();
        Log.d("AddSymptomDialogFragment", "GetSymptomArray: " + newSymptom.getSymptom_arr());

        // Iteruj przez zaznaczone przyciski
        if (newSymptom.getSymptom_arr() != null) {
            for (String symptom : newSymptom.getSymptom_arr()) {
                View layout = layoutMap.get(symptom);

                if ("Headache".equals(symptom)) {
                    // Oznacz przycisk "Headache" jako zaznaczony
                    btnHeadache.setBackgroundColor(Color.parseColor("#0A900A"));
                    selectedButtons.add("Headache");
                }
                if ("Bleeding".equals(symptom)) {
                    // Oznacz przycisk "Bleeding" jako zaznaczony
                    btnBleeding.setBackgroundColor(Color.parseColor("#0A900A"));
                    selectedButtons.add("Bleeding");
                }
                if ("Bruises".equals(symptom)) {
                    // Oznacz przycisk "Bruises" jako zaznaczony
                    btnBruises.setBackgroundColor(Color.parseColor("#0A900A"));
                    selectedButtons.add("Bruises");
                }
                if ("Chest_Pain".equals(symptom)) {
                    // Oznacz przycisk "Chest_Pain" jako zaznaczony
                    btnChestPain.setBackgroundColor(Color.parseColor("#0A900A"));
                    selectedButtons.add("Chest_Pain");
                }
                if ("Leg_Swelling".equals(symptom)) {
                    // Oznacz przycisk "Leg_Swelling" jako zaznaczony
                    btnLegSwelling.setBackgroundColor(Color.parseColor("#0A900A"));
                    selectedButtons.add("Leg_Swelling");
                }
                if ("Palpitations".equals(symptom)) {
                    // Oznacz przycisk "Palpitations" jako zaznaczony
                    btnPalpitations.setBackgroundColor(Color.parseColor("#0A900A"));
                    selectedButtons.add("Palpitations");
                }
                if ("Dizzyness".equals(symptom)) {
                    // Oznacz przycisk "Dizzyness" jako zaznaczony
                    btnDizzyness.setBackgroundColor(Color.parseColor("#0A900A"));
                    selectedButtons.add("Dizzyness");
                }
                if ("Hot_Flush".equals(symptom)) {
                    // Oznacz przycisk "Hot_Flush" jako zaznaczony
                    btnHotFlush.setBackgroundColor(Color.parseColor("#0A900A"));
                    selectedButtons.add("Hot_Flush");
                }
                if ("Coughing".equals(symptom)) {
                    // Oznacz przycisk "Coughing" jako zaznaczony
                    btnCoughing.setBackgroundColor(Color.parseColor("#0A900A"));
                    selectedButtons.add("Coughing");
                }
                /*
                if ("Other".equals(symptom)) {
                    // Oznacz przycisk "Other" jako zaznaczony
                    btnOther.setBackgroundColor(Color.parseColor("#0A900A"));
                    selectedButtons.add("Other");
                }
                */
            }
        } else {
            Log.d("AddSymptomDialogFragment", "Symptom_arr jest pusty");
        }
    }


    private void saveDataToDatabase() {
        // Pobierz instancję bazy danych Firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Pobierz ID aktualnie zalogowanego użytkownika
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("AddSymptomDialogFragment", "Timestamp: " + Timestamp.now());

        // Utwórz kolekcję "Symptoms" wewnątrz kolekcji "Users"
        NewSymptom symptomData;

        if (newSymptom != null) {
            //newSymptom.getDocumentId();
            Log.d("AddSymptomDialogFragment", "NewSymptom symptomArray: " + newSymptom.getSymptom_arr() + " NewSymptom w saveData documentId: " + newSymptom.getDocumentId() + " NewSymptom day: " + newSymptom.getDay());
            symptomData = newSymptom;
            symptomData.setDay(Timestamp.now()); // Dodaj timestamp dla dnia
        } else {
            // Jeśli newSymptom jest nullem, utwórz nowy obiekt symptomData
            symptomData = new NewSymptom();
            symptomData.setDay(Timestamp.now()); // Dodaj timestamp dla dnia
        }

        List<String> symptom_arr = new ArrayList<>();

        // Iteruj przez zaznaczone przyciski
        for (String buttonTag : selectedButtons) {
            symptom_arr.add(buttonTag);
        }

        // Dodaj listę symptomów do dokumentu
        symptomData.setSymptom_arr(symptom_arr);

        // Sprawdź, czy to jest aktualizacja czy nowy dokument
        if (isUpdate) {
            Log.d("AddSymptomDialogFragment", "To jest aktualizacja dokumentu");
            Log.d("AddSymptomDialogFragment", "To jest aktualizacja dokumentu o id: " + documentId);
            // Jeśli to jest aktualizacja, zaktualizuj istniejący dokument
            db.collection("Users").document(userId).collection("Symptoms").document(documentId)
                    .set(symptomData)

                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (getContext() != null) {
                                Toast.makeText(getContext(), "Dane zaktualizowane w bazie danych.", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e("AddSymptomDialogFragment", "Błąd: getContext() zwrócił null przy próbie utworzenia Toast.");
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Obsłuż błąd aktualizacji
                            Toast.makeText(getContext(), "Błąd podczas aktualizacji danych w bazie danych.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Log.d("AddSymptomDialogFragment", "To jest tworzenie nowego dokumentu");
            // Jeśli to nie jest aktualizacja, dodaj nowy dokument
            db.collection("Users").document(userId).collection("Symptoms").add(symptomData)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            if (getContext() != null) {
                                Toast.makeText(getContext(), "Dane zapisane do bazy danych.", Toast.LENGTH_SHORT).show();
                            } else {
                                if (getActivity() != null) {
                                    // Użyj getActivity() jako alternatywy do getContext() w przypadku braku kontekstu fragmentu
                                    Toast.makeText(getActivity(), "Dane zapisane do bazy danych.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.e("AddSymptomDialogFragment", "Błąd: Nie można uzyskać poprawnego kontekstu dla Toast.");
                                }
                            }



                            // Aktualizuj dokumentId w obiekcie NewSymptom
                            symptomData.setDocumentId(documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Obsłuż błąd dodawania nowego dokumentu
                            Toast.makeText(getContext(), "Błąd podczas zapisywania danych do bazy danych.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

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

    // Metoda do formatowania daty bez czasu
    public static String formatDateWithoutTime(java.util.Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return sdf.format(date);
    }
}
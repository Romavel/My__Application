package pl.pollub.android.myapplication.ui.diet;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import pl.pollub.android.myapplication.R;
import pl.pollub.android.myapplication.ui.measurements.InrMeasurement;

import android.widget.LinearLayout;

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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

// DietDialogFragment.java
public class DietDialogFragment extends DialogFragment {

    private Button btnCaffeine, btnNicotine, btnAlcohol, btnSugar, btnVegetables, btnOther, btnAddDiet, btnCancelDiet;
    private LinearLayout container;
    private boolean isUpdate = false;
    private Map<String, View> layoutMap = new HashMap<>();
    private Set<String> selectedButtons = new HashSet<>();

    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diet_dialog, container, false);

        btnCaffeine = view.findViewById(R.id.btnCaffeine);
        btnNicotine = view.findViewById(R.id.btnNicotine);
        btnAlcohol = view.findViewById(R.id.btnAlcohol);
        btnSugar = view.findViewById(R.id.btnSugar);
        btnVegetables = view.findViewById(R.id.btnVegetables);
        btnOther = view.findViewById(R.id.btnOther);
        btnAddDiet = view.findViewById(R.id.btnAddDiet);
        btnCancelDiet = view.findViewById(R.id.btnCancelDiet);

        container = view.findViewById(R.id.container);

        // Dodaj obsługę przycisków
        setupButton(btnCaffeine, "Caffeine");
        setupButton(btnNicotine, "Nicotine");
        setupButton(btnAlcohol, "Alcohol");
        // setupButton(btnSugar, "Sugar");
        setupButton(btnVegetables, "Vegetables");
        setupButton(btnOther, "Other");

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
        btnAddDiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataToDatabase();
                dismiss(); // Zamknij dialog po dodaniu danych
            }
        });
    }

    private void setupCancelButton() {
        btnCancelDiet.setOnClickListener(new View.OnClickListener() {
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
                    removeLayout(layoutTag);
                } else {
                    // Jeżeli przycisk nie był zaznaczony, zaznacz go
                    v.setBackgroundColor(Color.parseColor("#0A900A"));
                    selectedButtons.add(layoutTag);
                    addLayout(layoutTag);
                }
            }
        });
    }

    private void addLayout(String layoutTag) {
        View layout;
        switch (layoutTag) {
            case "Caffeine":
                layout = new CaffeineLayout(getContext(), null);
                break;
            case "Nicotine":
                layout = new NicotineLayout(getContext(), null);
                break;
            case "Alcohol":
                layout = new AlcoholLayout(getContext(), null);
                break;
                /*
            case "Sugar":
                layout = new SugarLayout(getContext(), null);
                break;
                 */
            case "Vegetables":
                layout = new VegetableLayout(getContext(), null);
                break;
            case "Other":
                layout = new OtherLayout(getContext(), null);
                break;
            // Dodaj inne przypadki dla innych używek
            default:
                return;
        }

        layoutMap.put(layoutTag, layout);
        container.addView(layout);
    }

    private void removeLayout(String layoutTag) {
        try {
            View layout = layoutMap.get(layoutTag);
            if (layout != null) {
                container.removeView(layout);
                layoutMap.remove(layoutTag);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkExistingDocument() {
        // Pobierz ID aktualnie zalogowanego użytkownika
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Utwórz kolekcję "Intakes" wewnątrz kolekcji "Users"
        final CollectionReference intakesCollection = db.getInstance().collection("Users").document(userId).collection("Intakes");

        // Utwórz zapytanie, aby pobrać dokumenty z dzisiejszą datą
        Query query = intakesCollection.whereEqualTo("day", getTodayDateString());

        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Jeśli istnieją dokumenty z dzisiejszą datą, to jest to aktualizacja
                        isUpdate = true;

                        // Załóżmy, że zakładamy, że istnieje tylko jeden dokument z dzisiejszą datą,
                        // Jeśli istnieje więcej niż jeden, musisz dostosować to odpowiednio
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                        Map<String, Object> data = documentSnapshot.getData();
                        handleExistingData(data);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Intakes", "Błąd podczas sprawdzania istnienia dokumentu", e);
                });
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

    private void handleExistingData(Map<String, Object> data) {
        // Iteruj przez zaznaczone przyciski
        for (String buttonTag : selectedButtons) {
            View layout = layoutMap.get(buttonTag);
            if (layout instanceof CaffeineLayout) {
                // Jeśli to jest CaffeineLayout, pobierz dane
                CaffeineLayout caffeineLayout = (CaffeineLayout) layout;
                caffeineLayout.setFormDataFromDatabase(data);
            }
            if (layout instanceof NicotineLayout) {
                // Jeśli to jest NicotineLayout, pobierz dane i dodaj do kolekcji "IntakeArr"
                NicotineLayout nicotineLayout = (NicotineLayout) layout;
                nicotineLayout.setFormDataFromDatabase(data);
            }
            if (layout instanceof AlcoholLayout) {
                // Jeśli to jest AlcoholLayout, pobierz dane i dodaj do kolekcji "IntakeArr"
                AlcoholLayout alcoholLayout = (AlcoholLayout) layout;
                alcoholLayout.setFormDataFromDatabase(data);
            }
            if (layout instanceof VegetableLayout) {
                // Jeśli to jest VegetableLayout, pobierz dane i dodaj do kolekcji "IntakeArr"
                VegetableLayout vegetableLayout = (VegetableLayout) layout;
                vegetableLayout.setFormDataFromDatabase(data);
            }
            /*
            if (layout instanceof SugarLayout) {
                // Jeśli to jest SugarLayout, pobierz dane i dodaj do kolekcji "IntakeArr"
                SugarLayout sugarLayout = (SugarLayout) layout;
                sugarLayout.setFormDataFromDatabase(data);
            }
            */
            if (layout instanceof OtherLayout) {
                // Jeśli to jest CaffeineLayout, pobierz dane i dodaj do kolekcji "IntakeArr"
                OtherLayout otherLayout = (OtherLayout) layout;
            }
            // Dodaj przypadki dla innych używek, jeśli istnieją
        }
    }

    private String getTodayDateString() {
        // Pobierz aktualną datę w formacie "YYYY-MM-DD"
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date today = Calendar.getInstance().getTime();
        return dateFormat.format(today);
    }

    private void saveDataToDatabase() {
        // Pobierz instancję bazy danych Firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Pobierz ID aktualnie zalogowanego użytkownika
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Utwórz kolekcję "Intakes" wewnątrz kolekcji "Users"
        Map<String, Object> intakeData = new HashMap<>();
        intakeData.put("day", FieldValue.serverTimestamp()); // Dodaj timestamp dla dnia

        List<Map<String, Object>> intakeArr = new ArrayList<>();

        // Iteruj przez zaznaczone przyciski
        for (String buttonTag : selectedButtons) {
            View layout = layoutMap.get(buttonTag);
            if (layout instanceof CaffeineLayout) {
                // Jeśli to jest CaffeineLayout, pobierz dane i dodaj do kolekcji "IntakeArr"
                CaffeineLayout caffeineLayout = (CaffeineLayout) layout;
                Map<String, Object> formData = caffeineLayout.getFormData();

                // Dodaj dane do mapy
                Map<String, Object> intakeEntry = new HashMap<>();
                intakeEntry.put("amount", formData.get("amount")); // Tutaj możesz dostosować nazwy kluczy
                intakeEntry.put("name", formData.get("name"));     // do tych, które używasz w CaffeineLayout
                intakeArr.add(intakeEntry);
            }
            if (layout instanceof NicotineLayout) {
                // Jeśli to jest NicotineLayout, pobierz dane i dodaj do kolekcji "IntakeArr"
                NicotineLayout nicotineLayout = (NicotineLayout) layout;
                Map<String, Object> formData = nicotineLayout.getFormData();

                // Dodaj dane do mapy
                Map<String, Object> intakeEntry = new HashMap<>();
                intakeEntry.put("amount", formData.get("amount")); // Tutaj możesz dostosować nazwy kluczy
                intakeEntry.put("name", formData.get("name"));     // do tych, które używasz w NicotineLayout
                intakeArr.add(intakeEntry);
            }
            if (layout instanceof AlcoholLayout) {
                // Jeśli to jest AlcoholLayout, pobierz dane i dodaj do kolekcji "IntakeArr"
                AlcoholLayout alcoholLayout = (AlcoholLayout) layout;
                Map<String, Object> formData = alcoholLayout.getFormData();

                // Dodaj dane do mapy
                Map<String, Object> intakeEntry = new HashMap<>();
                intakeEntry.put("amount", formData.get("amount")); // Tutaj możesz dostosować nazwy kluczy
                intakeEntry.put("name", formData.get("name"));     // do tych, które używasz w AlcoholLayout
                intakeArr.add(intakeEntry);
            }
            if (layout instanceof VegetableLayout) {
                // Jeśli to jest VegetableLayout, pobierz dane i dodaj do kolekcji "IntakeArr"
                VegetableLayout vegetableLayout = (VegetableLayout) layout;
                Map<String, Object> formData = vegetableLayout.getFormData();

                // Dodaj dane do mapy
                Map<String, Object> intakeEntry = new HashMap<>();
                intakeEntry.put("amount", formData.get("amount")); // Tutaj możesz dostosować nazwy kluczy
                intakeEntry.put("name", formData.get("name"));     // do tych, które używasz w VegetableLayout
                intakeArr.add(intakeEntry);
            }
            /*
            if (layout instanceof SugarLayout) {
                // Jeśli to jest SugarLayout, pobierz dane i dodaj do kolekcji "IntakeArr"
                SugarLayout sugarLayout = (SugarLayout) layout;
                Map<String, Object> formData = sugarLayout.getFormData();

                // Dodaj dane do mapy
                Map<String, Object> intakeEntry = new HashMap<>();
                intakeEntry.put("amount", formData.get("amount")); // Tutaj możesz dostosować nazwy kluczy
                intakeEntry.put("name", formData.get("name"));     // do tych, które używasz w SugarLayout
                intakeArr.add(intakeEntry);
            }
            */
            if (layout instanceof OtherLayout) {
                // Jeśli to jest CaffeineLayout, pobierz dane i dodaj do kolekcji "IntakeArr"
                OtherLayout otherLayout = (OtherLayout) layout;
                Map<String, Object> formData = otherLayout.getFormData();

                // Dodaj dane do mapy
                Map<String, Object> intakeEntry = new HashMap<>();
                //intakeEntry.put("desc", formData.get("desc")); // Tutaj możesz dostosować nazwy kluczy
                intakeEntry.put("name", formData.get("name"));     // do tych, które używasz w OtherLayout
                intakeArr.add(intakeEntry);
            }
            // Dodaj przypadki dla innych używek, jeśli istnieją
        }

        // Dodaj kolekcję "IntakeArr" do dokumentu
        intakeData.put("intake_arr", intakeArr);

        // Sprawdź, czy to jest aktualizacja czy nowy dokument
        if (isUpdate) {
            // Jeśli to jest aktualizacja, zaktualizuj istniejący dokument
            db.collection("Users").document(userId).collection("Intakes").document(getTodayDateString())
                    .update(intakeData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Obsłuż sukces aktualizacji
                            Toast.makeText(getContext(), "Dane zaktualizowane w bazie danych.", Toast.LENGTH_SHORT).show();
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
            // Jeśli to nie jest aktualizacja, dodaj nowy dokument
            db.collection("Users").document(userId).collection("Intakes").document(getTodayDateString())
                    .set(intakeData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Obsłuż sukces dodania nowego dokumentu
                            Toast.makeText(getContext(), "Dane zapisane do bazy danych.", Toast.LENGTH_SHORT).show();
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
}
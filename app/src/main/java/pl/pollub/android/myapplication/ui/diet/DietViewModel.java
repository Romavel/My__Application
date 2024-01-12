package pl.pollub.android.myapplication.ui.diet;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DietViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private MutableLiveData<NewDiet> dietLiveData;

    public LiveData<NewDiet> getDiet() {
        if (dietLiveData == null) {
            dietLiveData = new MutableLiveData<>();
            loadDietData();
        }
        return dietLiveData;
    }

    private void loadDietData() {
        // Tutaj należy dodać kod do pobierania danych diety z Firebase Firestore
        // Przykładowy kod poniżej:

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Pobierz dokumenty z kolekcji "Intakes" dla danego użytkownika
        db.collection("Users").document(userId).collection("Intakes")
                //.whereEqualTo("day", /* Tutaj dodaj aktualną datę w formie obiektu Timestamp*/)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DietEntry> dietEntries = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Przetwórz dokument i utwórz obiekt DietEntry
                            DietEntry entry = document.toObject(DietEntry.class);
                            dietEntries.add(entry);
                        }

                        // Tworzenie obiektu NewDiet i aktualizacja LiveData
                        NewDiet newDiet = new NewDiet(/* Dodaj odpowiednie parametry */);
                        newDiet.setIntake_arr(dietEntries);

                        dietLiveData.setValue(newDiet);
                    } else {
                        // Obsłuż błąd pobierania danych
                        // W przypadku błędu możesz ustawić wartość LiveData na null lub obsłużyć inaczej
                        dietLiveData.setValue(null);
                    }
                });
    }

    public DietViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Dieta fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}

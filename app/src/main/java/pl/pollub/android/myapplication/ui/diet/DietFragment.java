package pl.pollub.android.myapplication.ui.diet;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pl.pollub.android.myapplication.R;
import pl.pollub.android.myapplication.databinding.FragmentDietBinding;

public class DietFragment extends Fragment {

    private FragmentDietBinding binding;
    private boolean todayDocument = false;
    private boolean matchingDate = false;
    private static NewDiet newDiet;
    private RecyclerView recyclerView;
    private DietAdapter dietAdapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DietViewModel dietViewModel =
                new ViewModelProvider(this).get(DietViewModel.class);

        binding = FragmentDietBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        container = root.findViewById(R.id.recyclerView_diet);

        // Inicjalizuj recyclerView tutaj
        recyclerView = root.findViewById(R.id.recyclerView_diet);

        checkExistingDocument();
        // Ustaw Swipe to Refresh
        SwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.dietSwipeRefreshLayout);
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
        final CollectionReference intakesCollection = db.collection("Users").document(userId).collection("Intakes");


        // Utwórz zapytanie, aby pobrać dokumenty z datą nie wcześniejszą niż dzisiaj
        Query query = intakesCollection
                .orderBy("day", Query.Direction.DESCENDING)
                .limit(1);

        Log.d("DietFragment", "Query: " + query);
        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Załóżmy, że zakładamy, że istnieje tylko jeden dokument z dzisiejszą datą,
                        // Jeśli istnieje więcej niż jeden, musisz dostosować to odpowiednio
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        newDiet = documentSnapshot.toObject(NewDiet.class);
                        // Pobierz dzisiejszą datę
                        Date todayDate = Calendar.getInstance().getTime();
                        // Pobierz datę z ostatniego dokumentu
                        Date documentDate = newDiet.getDay().toDate();
                        Log.d("DietFragment", "Before MatchingDate: " + matchingDate);
                        if (isSameDay(documentDate, todayDate)) {
                            Log.d("DietFragment", "MatchingDate changed to True");
                            matchingDate = true;
                        }

                        if (newDiet != null && matchingDate) {
                            // Wyświetl item_diet
                            setupRecyclerView(newDiet);
                        }
                        Log.d("DietFragment", "After MatchingDate: " + matchingDate);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Intakes", "Błąd podczas sprawdzania istnienia dokumentu", e);
                });
    }

    private void setupRecyclerView(NewDiet newDiet) {
        // Skonfiguruj RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // Skonfiguruj adapter i przekaż dane
        DietAdapter dietAdapter = new DietAdapter(newDiet);
        recyclerView.setAdapter(dietAdapter);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void showDietDialog() {
        FragmentManager fragmentManager = getParentFragmentManager();
        DietDialogFragment dietDialogFragment = new DietDialogFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, dietDialogFragment)
                .addToBackStack(null)
                .commit();
    }

    public void showDietHistory() {
        FragmentManager fragmentManager = getParentFragmentManager();
        DietDialogFragment dietHistoryFragment = new DietDialogFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, dietHistoryFragment)
                .addToBackStack(null)
                .commit();
    }
}
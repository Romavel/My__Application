package pl.pollub.android.myapplication.ui.diet;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pl.pollub.android.myapplication.R;

public class DietHistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private Button loadMoreButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<NewDiet> dietList;
    private DietHistoryAdapter adapter;
    private int limit = 5; // Ilość początkowych elementów do załadowania

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diet_history, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        loadMoreButton = view.findViewById(R.id.loadMoreButton);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        dietList = new ArrayList<>();

        // Użyj nowego konstruktora adaptera, przekazując getContext()
        adapter = new DietHistoryAdapter(getContext(), dietList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this::fetchInitialData);

        loadMoreButton.setOnClickListener(v -> loadMoreData());

        fetchInitialData();

        return view;
    }
    private void fetchInitialData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(userId)
                .collection("Intakes")
                .orderBy("day", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    dietList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        NewDiet newDiet = document.toObject(NewDiet.class);
                        dietList.add(newDiet);
                    }
                    adapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                })
                .addOnFailureListener(e -> {
                    Log.e("DietHistoryFragment", "Błąd podczas pobierania danych z bazy", e);
                    Toast.makeText(getContext(), "Błąd podczas pobierania danych", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                });
    }

    private void loadMoreData() {
        // Zwiększ limit o 10 przy kliknięciu przycisku "Załaduj więcej"
        limit += 5;
        fetchInitialData();
    }
}

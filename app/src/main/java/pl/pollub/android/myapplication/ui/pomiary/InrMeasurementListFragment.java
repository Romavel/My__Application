package pl.pollub.android.myapplication.ui.pomiary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import pl.pollub.android.myapplication.R;
import pl.pollub.android.myapplication.ui.pomiary.InrMeasurement;
import pl.pollub.android.myapplication.ui.pomiary.InrMeasurementAdapter;

public class InrMeasurementListFragment extends Fragment {

    private RecyclerView recyclerView;
    private InrMeasurementAdapter adapter;
    private List<InrMeasurement> measurementList = new ArrayList<>();
    private TextView loadMoreTextView;
    private int visibleThreshold = 10;
    private boolean isLoading = false;

    private CollectionReference inrMeasurementsRef;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inrMeasurementsRef = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("INR_Measurements");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inr_measurement_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        loadMoreTextView = view.findViewById(R.id.loadMoreTextView);

        setupRecyclerView();

        loadMeasurements();

        return view;
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new InrMeasurementAdapter(measurementList);
        recyclerView.setAdapter(adapter);

        // Ustaw swipe do usuwania i edycji
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(getContext(),adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // Dodaj obsługę przewijania do wczytania kolejnych elementów
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    // Wczytaj kolejne elementy
                    loadMoreItems();
                    isLoading = true;
                }
            }
        });
    }

    private void loadMeasurements() {
        // Pobierz pierwsze 10 pomiarów lub wszystkie, jeżeli jest ich mniej
        inrMeasurementsRef.orderBy("time", Query.Direction.DESCENDING)
                .limit(visibleThreshold)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    measurementList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        InrMeasurement measurement = document.toObject(InrMeasurement.class);
                        measurementList.add(measurement);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Obsługa błędu
                });
    }

    private void loadMoreItems() {
        int lastVisibleIndex = measurementList.size() - 1;

        // Pobierz kolejne 10 pomiarów
        inrMeasurementsRef.orderBy("time", Query.Direction.DESCENDING)
                .startAfter(measurementList.get(lastVisibleIndex).getTime())
                .limit(visibleThreshold)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        int originalSize = measurementList.size();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            InrMeasurement measurement = document.toObject(InrMeasurement.class);
                            measurementList.add(measurement);
                        }
                        adapter.notifyItemRangeInserted(originalSize, measurementList.size());
                        isLoading = false;
                    } else {
                        loadMoreTextView.setText("Brak więcej pomiarów");
                    }
                })
                .addOnFailureListener(e -> {
                    // Obsługa błędu
                    isLoading = false;
                });
    }
}

package pl.pollub.android.myapplication.ui.measurements;



import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import pl.pollub.android.myapplication.R;

public class InrMeasurementListFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<InrMeasurement> inrMeasurements;
    private InrMeasurementAdapter adapter;
    private Button loadMoreButton;
    private int limit = 5;
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inr_measurement_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        inrMeasurements = new ArrayList<>();
        adapter = new InrMeasurementAdapter(inrMeasurements);

        // Ustawienie adaptera dla RecyclerView
        recyclerView.setAdapter(adapter);

        // Pobierz początkowe dane
        fetchInitialData();

        // Ustaw Swipe to Refresh
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Odśwież dane po przeciągnięciu
            fetchInitialData();
            swipeRefreshLayout.setRefreshing(false);
        });

        // Dodaj funkcję swipe do usuwania i edycji
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback());
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // Dodaj OnClickListener do przycisku loadMoreButton
        loadMoreButton = view.findViewById(R.id.loadMoreButton);
        loadMoreButton.setOnClickListener(v -> {
            // Tutaj dodaj kod do obsługi kliknięcia na przycisk "Załaduj kolejne pomiary"
            // Na przykład, możesz zwiększyć limit i ponownie pobrać dane
            limit += 5; // Przykład: Zwiększ limit o 10
            fetchInitialData(); // Ponownie pobierz dane z nowym limitem
        });

        return view;
    }

    // Metoda do pobierania początkowych danych
    private void fetchInitialData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.getInstance()
                .collection("Users")
                .document(userId)
                .collection("INR_Measurements")
                .orderBy("time", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    inrMeasurements.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        InrMeasurement measurement = document.toObject(InrMeasurement.class);
                        inrMeasurements.add(measurement);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("InrMeasurementList", "Błąd podczas pobierania pomiarów INR", e);
                });
    }

    // Klasa do obsługi przesunięcia do usunięcia i edycji
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
                editInrMeasurement(position);
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorSwipeDelete))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorSwipeEdit))
                    .addSwipeRightActionIcon(R.drawable.ic_edit)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        private void showDeleteConfirmation(int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Potwierdź usunięcie")
                    .setMessage("Czy na pewno chcesz usunąć ten pomiar INR?")
                    .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Jeśli użytkownik potwierdzi, usuń pomiar
                            deleteInrMeasurement(position);
                        }
                    })
                    .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Jeśli użytkownik anuluje, przywróć element na swoje miejsce
                            adapter.notifyItemChanged(position);
                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            // Jeśli użytkownik anuluje (poprzez naciśnięcie przycisku powrotu), przywróć element na swoje miejsce
                            adapter.notifyItemChanged(position);
                        }
                    })
                    .show();
        }
    }

    // Metoda do usuwania pomiaru
    private void deleteInrMeasurement(int position) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String measurementId = inrMeasurements.get(position).getDocument_id();

        // Wyświetlanie zmiennej measurementId w logach
        Log.d("MeasurementIdLog", "Measurement ID: " + measurementId);


        // Usuń pomiar z Firestore Database
        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(userId)
                .collection("INR_Measurements")
                .document(measurementId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Usunięto pomyślnie
                    inrMeasurements.remove(position);
                    adapter.notifyItemRemoved(position);
                    Log.d("MeasurementIdLog", "Usunięto pomiar o id: " + measurementId);
                })
                .addOnFailureListener(e -> {
                    Log.e("InrMeasurementList", "Błąd podczas usuwania pomiaru INR", e);
                });
    }


    private void editInrMeasurement(int position) {
        // Pobierz dane pomiaru, który chcesz edytować
        InrMeasurement measurementToEdit = inrMeasurements.get(position);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String measurementId = inrMeasurements.get(position).getDocument_id();

        // Otwórz nowy fragment do edycji, przekazując dane pomiaru
        FragmentManager fragmentManager = getParentFragmentManager();
        InrMeasurementEditFragment editFragment = InrMeasurementEditFragment.newInstance(measurementToEdit);
        editFragment.setOnEditCompleteListener(new InrMeasurementEditFragment.OnEditCompleteListener() {
            @Override
            public void onEditComplete(InrMeasurement updatedMeasurement) {
                FirebaseFirestore.getInstance()
                        .collection("Users")
                        .document(userId)
                        .collection("INR_Measurements")
                        .document(measurementId)
                        .update("value", updatedMeasurement.getValue())
                        .addOnSuccessListener(aVoid -> {
                            // Zaktualizowano pomyślnie
                            inrMeasurements.set(position, updatedMeasurement);
                            adapter.notifyItemChanged(position);

                            Log.d("MeasurementIdLog", "Zaktualizowano pomiar o id: " + measurementId);
                        })
                        .addOnFailureListener(e -> {
                            Log.e("InrMeasurementList", "Błąd podczas aktualizacji pomiaru INR", e);
                        });

            }
        });
        editFragment.show(fragmentManager, "EditInrMeasurement");
    }
}
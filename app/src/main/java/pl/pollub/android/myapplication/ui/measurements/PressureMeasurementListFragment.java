package pl.pollub.android.myapplication.ui.measurements;

import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class PressureMeasurementListFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<PressureMeasurement> pressureMeasurements;
    private PressureMeasurementAdapter adapter;
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pressure_measurement_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewPressure);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        pressureMeasurements = new ArrayList<>();
        adapter = new PressureMeasurementAdapter(pressureMeasurements);

        // Ustawienie adaptera dla RecyclerView
        recyclerView.setAdapter(adapter);

        // Pobierz początkowe dane
        fetchInitialData();

        // Ustaw Swipe to Refresh
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutPressure);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Odśwież dane po przeciągnięciu
            fetchInitialData();
            swipeRefreshLayout.setRefreshing(false);
        });

        // Dodaj funkcję swipe do usuwania i edycji
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback());
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return view;
    }

    // Metoda do pobierania początkowych danych
    private void fetchInitialData() {
        // Pobierz dane z Firestore Database
        // Jeżeli użytkownik ma mniej niż 10 pomiarów, pobierz wszystkie
        // Jeżeli użytkownik ma 10 lub więcej pomiarów, pobierz 10 najnowszych
        // Wywołaj tę metodę po dodaniu nowego pomiaru, edycji lub usunięciu pomiaru
        // Jeżeli chcesz dodatkowe dane, dostosuj tę metodę odpowiednio
        // Poniżej znajdziesz przykładowy kod

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.getInstance()
                .collection("Users")
                .document(userId)
                .collection("BloodPressureMeasurements")
                .orderBy("time", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    pressureMeasurements.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        PressureMeasurement measurement = document.toObject(PressureMeasurement.class);
                        pressureMeasurements.add(measurement);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("PressureMeasurementList", "Błąd podczas pobierania pomiarów ciśnienia", e);
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
                editPressureMeasurement(position);
            }
        }

        // ...

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
                    .setMessage("Czy na pewno chcesz usunąć ten pomiar ciśnienia?")
                    .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Jeśli użytkownik potwierdzi, usuń pomiar
                            deletePressureMeasurement(position);
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
    private void deletePressureMeasurement(int position) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String measurementId = pressureMeasurements.get(position).getDocument_id();

        // Usuń pomiar z Firestore Database
        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(userId)
                .collection("BloodPressureMeasurements")
                .document(measurementId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Usunięto pomyślnie
                    pressureMeasurements.remove(position);
                    adapter.notifyItemRemoved(position);
                })
                .addOnFailureListener(e -> {
                    Log.e("PressureMeasurementList", "Błąd podczas usuwania pomiaru ciśnienia", e);
                });
    }

    // Metoda do edycji pomiaru (dostosuj do swoich potrzeb)
    private void editPressureMeasurement(int position) {
        // Pobierz dane pomiaru, który chcesz edytować
        PressureMeasurement measurementToEdit = pressureMeasurements.get(position);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String measurementId = pressureMeasurements.get(position).getDocument_id();

        // Otwórz nowy fragment do edycji, przekazując dane pomiaru
        FragmentManager fragmentManager = getParentFragmentManager();
        PressureMeasurementEditFragment editFragment = PressureMeasurementEditFragment.newInstance(measurementToEdit);
        editFragment.setOnEditCompleteListener(new PressureMeasurementEditFragment.OnEditCompleteListener() {
            @Override
            public void onEditComplete(PressureMeasurement updatedMeasurement) {
                FirebaseFirestore.getInstance()
                        .collection("Users")
                        .document(userId)
                        .collection("BloodPressureMeasurements")
                        .document(measurementId)
                        .update(
                                "systolic_pressure", updatedMeasurement.getSystolic_pressure(),
                                "diastolic_pressure", updatedMeasurement.getDiastolic_pressure(),
                                "pulse", updatedMeasurement.getPulse(),
                                "time", updatedMeasurement.getTime()
                        )
                        .addOnSuccessListener(aVoid -> {
                            // Zaktualizowano pomyślnie
                            pressureMeasurements.set(position, updatedMeasurement);
                            adapter.notifyItemChanged(position);
                        })
                        .addOnFailureListener(e -> {
                            Log.e("PressureMeasurementList", "Błąd podczas aktualizacji pomiaru ciśnienia", e);
                        });

            }
        });
        editFragment.show(fragmentManager, "EditPressureMeasurement");
    }
}

package pl.pollub.android.myapplication.ui.pomiary;



import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

import pl.pollub.android.myapplication.R;

public class InrMeasurementListFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<InrMeasurement> inrMeasurements;
    private InrMeasurementAdapter adapter;
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
                .collection("INR_Measurements")
                .orderBy("time", Query.Direction.DESCENDING)
                .limit(10)
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
                //editInrMeasurement(position);
            }
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

    // Metoda do edycji pomiaru (dostosuj do swoich potrzeb)
    /*
    private void editInrMeasurement(int position) {
        // Pobierz dane pomiaru, który chcesz edytować
        InrMeasurement measurementToEdit = inrMeasurements.get(position);

        // Otwórz nowy fragment do edycji, przekazując dane pomiaru
        FragmentManager fragmentManager = getParentFragmentManager();
        InrMeasurementEditFragment editFragment = InrMeasurementEditFragment.newInstance(measurementToEdit);
        editFragment.show(fragmentManager, "EditInrMeasurement");
    }
    */
}

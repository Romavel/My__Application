package pl.pollub.android.myapplication.ui.measurements;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import pl.pollub.android.myapplication.R;

public class MeasurementsFragment extends Fragment {

    private MeasurementsViewModel measurementsViewModel;
    private LinearLayout chartLayout;
    private BarChart barChart;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        measurementsViewModel =
                new ViewModelProvider(this).get(MeasurementsViewModel.class);

        View root = inflater.inflate(R.layout.fragment_measurements, container, false);

        final TextView textView = root.findViewById(R.id.text_measurements);
        chartLayout = root.findViewById(R.id.chartLayout);
        barChart = root.findViewById(R.id.barChart);


        //final TextView textView = root.findViewById(R.id.text_pomiary);

        // Wywołaj metodę do pobierania i rysowania wykresu
        getLatestInrMeasurements();

        return root;
    }

    // Przenieś kod obsługi FAB do publicznej metody
    public void handleFabClick() {
        // Obsługa kliknięcia FAB w zależności od aktualnie wybranego fragmentu
        showMeasurementOptions();
    }

    // Dodaj metodę do obsługi kliknięcia FAB
    private void showMeasurementOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Wybierz opcję")
                .setItems(new CharSequence[]{"Dodaj pomiar INR", "Dodaj pomiar ciśnienia", "Lista pomiarów INR", "Lista pomiarów ciśnienia"},
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Obsługa wybranej opcji
                                switch (which) {
                                    case 0:
                                        // Dodaj pomiar INR
                                        showInrMeasurementDialog();
                                        break;
                                    case 1:
                                        // Dodaj pomiar ciśnienia
                                        showPressureMeasurementDialog();
                                        break;
                                    case 2:
                                        // Lista pomiarów INR
                                        showInrMeasurementList();
                                        break;
                                    case 3:
                                        // Lista pomiarów ciśnienia
                                        showPressureMeasurementList();
                                        break;
                                }
                            }
                        });
        builder.create().show();
    }
    // Dodaj metodę do obsługi wyświetlania dialogu pomiaru INR
    public void showInrMeasurementDialog() {
        FragmentManager fragmentManager = getParentFragmentManager();
        InrMeasurementDialogFragment inrDialogFragment = new InrMeasurementDialogFragment();
        inrDialogFragment.show(fragmentManager, "InrMeasurementDialog");
    }
    // Dodaj metodę do obsługi wyświetlania dialogu pomiaru ciśnienia
    public void showPressureMeasurementDialog() {
        FragmentManager fragmentManager = getParentFragmentManager();
        PressureMeasurementDialogFragment pressureDialogFragment = new PressureMeasurementDialogFragment();
        pressureDialogFragment.show(fragmentManager, "PressureMeasurementDialog");
    }
    public void showInrMeasurementList() {
        // Otwórz nowy fragment z listą pomiarów INR
        FragmentManager fragmentManager = getParentFragmentManager();
        InrMeasurementListFragment inrListFragment = new InrMeasurementListFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, inrListFragment)
                .addToBackStack(null)
                .commit();
    }
    public void showPressureMeasurementList() {
        // Otwórz nowy fragment z listą pomiarów ciśnienia
        FragmentManager fragmentManager = getParentFragmentManager();
        PressureMeasurementListFragment pressureListFragment = new PressureMeasurementListFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, pressureListFragment)
                .addToBackStack(null)
                .commit();
    }

    // Dodaj metodę do pobierania najnowszych 5 pomiarów INR
    private void getLatestInrMeasurements() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(userId)
                .collection("INR_Measurements")
                .orderBy("time", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<InrMeasurement> measurements = new ArrayList<>();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        InrMeasurement measurement = document.toObject(InrMeasurement.class);
                        measurements.add(measurement);
                    }

                    // Wywołaj metodę rysującą wykres
                    drawInrChart(measurements);
                })
                .addOnFailureListener(e -> {
                    Log.e("MeasurementsFragment", "Błąd podczas pobierania pomiarów INR", e);
                });
    }

    // Metoda do rysowania wykresu INR
    private void drawInrChart(List<InrMeasurement> measurements) {
        BarChart barChart = requireView().findViewById(R.id.barChart);

        // Inicjalizacja danych wykresu (możesz dostosować do swoich potrzeb)
        List<Float> values = new ArrayList<>();
        List<String> dates = new ArrayList<>();

        for (InrMeasurement measurement : measurements) {
            values.add((float) measurement.getValue());
            dates.add(formatDateWithoutTime(measurement.getTime().toDate()));
        }

        BarChartHelper barChartHelper = new BarChartHelper(barChart);
        barChartHelper.displayBarChart(values, dates);
    }

    // Metoda do formatowania daty bez czasu
    public static String formatDateWithoutTime(java.util.Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(date);
    }
}

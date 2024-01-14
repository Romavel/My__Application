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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


import pl.pollub.android.myapplication.R;
import pl.pollub.android.myapplication.databinding.FragmentMeasurementsBinding;
import pl.pollub.android.myapplication.databinding.FragmentProfileBinding;

public class MeasurementsFragment extends Fragment {

    private MeasurementsViewModel measurementsViewModel;
    private FragmentMeasurementsBinding binding;
    private LinearLayout chartLayout;
    private BarChart barChart;
    private LineChart lineChart;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        measurementsViewModel =
                new ViewModelProvider(this).get(MeasurementsViewModel.class);

        View root = inflater.inflate(R.layout.fragment_measurements, container, false);

        final TextView textView = root.findViewById(R.id.text_measurements);
        chartLayout = root.findViewById(R.id.chartLayout);
        barChart = root.findViewById(R.id.barChart);
        lineChart = root.findViewById(R.id.lineChart);

        // Uzyskaj bieżący dzień tygodnia i datę
        String currentDayAndDate = getCurrentDayAndDate();

        // Ustaw wartość w polu text_measurements
        textView.setText(currentDayAndDate);

        //final TextView textView = root.findViewById(R.id.text_pomiary);
        // Wywołaj metodę do pobierania i rysowania wykresu
        getLatestInrMeasurements();

        // Wywołaj metodę do pobierania i rysowania wykresu
        getLatestPressureMeasurements();

        // Ustaw Swipe to Refresh
        SwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.measurementSwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Odśwież dane po przeciągnięciu
            getLatestInrMeasurements();
            getLatestPressureMeasurements();
            swipeRefreshLayout.setRefreshing(false);
        });


        return root;
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
                //.addToBackStack(null)
                .commit();
    }
    public void showPressureMeasurementList() {
        // Otwórz nowy fragment z listą pomiarów ciśnienia
        FragmentManager fragmentManager = getParentFragmentManager();
        PressureMeasurementListFragment pressureListFragment = new PressureMeasurementListFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, pressureListFragment)
                //.addToBackStack(null)
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

    // Metoda do pobierania najnowszych 5 pomiarów ciśnienia
    private void getLatestPressureMeasurements() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(userId)
                .collection("BloodPressureMeasurements")
                .orderBy("time", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Integer> systolicValues = new ArrayList<>();
                    List<Integer> diastolicValues = new ArrayList<>();
                    List<Integer> pulseValues = new ArrayList<>();
                    List<String> dates = new ArrayList<>();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        PressureMeasurement measurement = document.toObject(PressureMeasurement.class);
                        systolicValues.add(measurement.getSystolic_pressure());
                        diastolicValues.add(measurement.getDiastolic_pressure());
                        pulseValues.add(measurement.getPulse());
                        dates.add(formatDateWithoutTime(measurement.getTime().toDate()));
                    }

                    Log.d("MeasurementsFragment", "systolicValues: " + systolicValues.toString());
                    Log.d("MeasurementsFragment", "diastolicValues: " + diastolicValues.toString());
                    Log.d("MeasurementsFragment", "pulseValues: " + pulseValues.toString());

                    // Wywołaj metodę rysującą wykres ciśnienia
                    drawPressureChart(systolicValues, diastolicValues, pulseValues, dates);
                })
                .addOnFailureListener(e -> {
                    Log.e("MeasurementsFragment", "Błąd podczas pobierania pomiarów ciśnienia", e);
                });
    }


    // Metoda do rysowania wykresu ciśnienia
    private void drawPressureChart(List<Integer> systolicValues, List<Integer> diastolicValues, List<Integer> pulseValues, List<String> dates) {
        Log.d("LineChartHelper", "systolicValues: " + systolicValues.toString());
        Log.d("LineChartHelper", "diastolicValues: " + diastolicValues.toString());
        Log.d("LineChartHelper", "pulseValues: " + pulseValues.toString());
        LineChartHelper lineChartHelper = new LineChartHelper(lineChart);
        lineChartHelper.displayLineChart(systolicValues, diastolicValues, pulseValues, dates, requireContext());
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

    private String getCurrentDayAndDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        String dayOfWeek = dayFormat.format(calendar.getTime());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM", Locale.getDefault());
        String formattedDate = dateFormat.format(calendar.getTime());

        return dayOfWeek + ", " + formattedDate;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

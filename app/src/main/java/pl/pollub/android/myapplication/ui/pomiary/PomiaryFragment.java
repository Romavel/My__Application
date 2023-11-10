package pl.pollub.android.myapplication.ui.pomiary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.github.mikephil.charting.charts.BarChart;

import pl.pollub.android.myapplication.R;

public class PomiaryFragment extends Fragment {

    // Dodaj odpowiednie importy dla wykorzystywanych bibliotek

    private BarChart barChart;
    private Button buttonAddInrMeasurement;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_pomiary, container, false);

        barChart = root.findViewById(R.id.barChart);
        buttonAddInrMeasurement = root.findViewById(R.id.buttonAddInrMeasurement);

        // Dodaj obsługę przycisku
        buttonAddInrMeasurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Po kliknięciu, otwórz nowy widok dodawania pomiaru INR
                openAddInrMeasurementView();
            }
        });

        // Tutaj możesz dodać kod do wczytywania danych do wykresu z bazy danych

        return root;
    }

    private void openAddInrMeasurementView() {
        // W tym miejscu otwórz nowy widok dodawania pomiaru INR
        // (np. za pomocą Intents w Android)
    }
}

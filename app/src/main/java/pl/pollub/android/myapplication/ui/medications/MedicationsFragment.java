package pl.pollub.android.myapplication.ui.medications;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;


import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pl.pollub.android.myapplication.R;
import pl.pollub.android.myapplication.databinding.FragmentMedicationsBinding;
import pl.pollub.android.myapplication.ui.measurements.InrMeasurementDialogFragment;
import pl.pollub.android.myapplication.ui.measurements.InrMeasurementListFragment;
import pl.pollub.android.myapplication.ui.measurements.PressureMeasurementDialogFragment;
import pl.pollub.android.myapplication.ui.measurements.PressureMeasurementListFragment;

public class MedicationsFragment extends Fragment {

    private FragmentMedicationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MedicationsViewModel medicationsViewModel =
                new ViewModelProvider(this).get(MedicationsViewModel.class);

        binding = FragmentMedicationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Dodaj obsługę kliknięcia w mainMedicationTextView
        TextView mainMedicationTextView = root.findViewById(R.id.mainMedicationTextView);
        CardView mainMedicationsCardView = root.findViewById(R.id.mainMedicationCardView);

        mainMedicationsCardView.setOnClickListener(new View.OnClickListener() {
            private boolean isWhiteBackground = true;

            @Override
            public void onClick(View v) {
                // Zmieniaj kolor tła między białym a zielonym
                if (isWhiteBackground) {
                    mainMedicationsCardView.setCardBackgroundColor(0xFF4CAF50);
                    showMedicationTakenToast();
                } else {
                    mainMedicationsCardView.setCardBackgroundColor(Color.WHITE);
                }
                isWhiteBackground = !isWhiteBackground;
            }
        });

        return root;
    }

    // Funkcja wyświetlająca Toast z informacją o przyjęciu leku
    private void showMedicationTakenToast() {
        // Pobierz dzisiejszą datę
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        // Pobierz nazwę leku (możesz dostosować to do własnych potrzeb)
        String medicationName = "Warfin";

        // Twórz komunikat Toast
        String toastMessage = "W dniu " + currentDate + " przyjęto " + medicationName;
        Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_SHORT).show();
    }
    // Dodaj metodę do obsługi wyświetlania dialogu pomiaru INR
    public void showMedicationDialog() {
        FragmentManager fragmentManager = getParentFragmentManager();
        AddMedicationDialogFragment addMedicationDialogFragment = new AddMedicationDialogFragment();
        addMedicationDialogFragment.show(fragmentManager, "AddMedicationDialogFragment");
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

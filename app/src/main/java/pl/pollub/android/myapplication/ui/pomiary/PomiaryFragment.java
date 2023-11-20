package pl.pollub.android.myapplication.ui.pomiary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import pl.pollub.android.myapplication.R;
import pl.pollub.android.myapplication.databinding.FragmentPomiaryBinding;

public class PomiaryFragment extends Fragment {

    private FragmentPomiaryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PomiaryViewModel pomiaryViewModel =
                new ViewModelProvider(this).get(PomiaryViewModel.class);

        binding = FragmentPomiaryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textPomiary;
        pomiaryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // Dodaj Floating Action Button
        FloatingActionButton fab = root.findViewById(R.id.fabAddMeasurement);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obsługa kliknięcia FAB - otwórz listę opcji
                showMeasurementOptions();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



    // Dodaj metodę do obsługi kliknięcia FAB
    private void showMeasurementOptions() {
        // Tutaj dodaj kod do otwarcia listy opcji (Dodaj pomiar INR, dodaj pomiar ciśnienia, zobacz historię pomiarów INR, zobacz historię pomiarów ciśnienia)
        // Możesz użyć AlertDialog lub utworzyć nową aktywność/fragment z listą opcji
        // Przykładowy kod z użyciem AlertDialog:
        /*
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Wybierz opcję")
                .setItems(new CharSequence[]{"Dodaj pomiar INR", "Dodaj pomiar ciśnienia", "Zobacz historię pomiarów INR", "Zobacz historię pomiarów ciśnienia"},
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Obsługa wybranej opcji
                                switch (which) {
                                    case 0:
                                        // Dodaj pomiar INR
                                        break;
                                    case 1:
                                        // Dodaj pomiar ciśnienia
                                        break;
                                    case 2:
                                        // Zobacz historię pomiarów INR
                                        break;
                                    case 3:
                                        // Zobacz historię pomiarów ciśnienia
                                        break;
                                }
                            }
                        });
        builder.create().show();
        */
    }
}

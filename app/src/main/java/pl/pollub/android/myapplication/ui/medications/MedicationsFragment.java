package pl.pollub.android.myapplication.ui.medications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Arrays;
import java.util.List;

import pl.pollub.android.myapplication.R;
import pl.pollub.android.myapplication.databinding.FragmentMedicationsBinding;

public class MedicationsFragment extends Fragment {

    private FragmentMedicationsBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MedicationsViewModel medicationsViewModel =
                new ViewModelProvider(this).get(MedicationsViewModel.class);

        binding = FragmentMedicationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textLeki;
        medicationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Dodaj lek");

        // Inicjalizuj widok dialogu z layoutu
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_medication, null);
        builder.setView(dialogView);

        // Inicjalizuj elementy interfejsu użytkownika
        EditText nazwaLekuEditText = dialogView.findViewById(R.id.editTextNazwaLeku);
        Spinner formaLekuSpinner = dialogView.findViewById(R.id.spinnerFormaLeku);
        Button jednostkaButton = dialogView.findViewById(R.id.buttonJednostka);
        EditText dawkaEditText = dialogView.findViewById(R.id.editTextDawka);
        Spinner razyDziennieSpinner = dialogView.findViewById(R.id.spinnerRazyDziennie);
        MultiChoiceSpinner dniTygodniaSpinner = dialogView.findViewById(R.id.spinnerDniTygodnia);

        // Inicjalizuj spinner z formami leku
        ArrayAdapter<CharSequence> formaLekuAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.formy_leku_array, android.R.layout.simple_spinner_item);
        formaLekuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        formaLekuSpinner.setAdapter(formaLekuAdapter);

        // Ustawienie początkowej jednostki
        String[] jednostki = {"mg", "ml"};
        int[] currentIndex = {0};
        jednostkaButton.setText(jednostki[currentIndex[0]]);
        jednostkaButton.setOnClickListener(view -> {
            currentIndex[0] = (currentIndex[0] + 1) % jednostki.length;
            jednostkaButton.setText(jednostki[currentIndex[0]]);
        });

        // Inicjalizuj spinner z ilością razy dziennie
        ArrayAdapter<CharSequence> razyDziennieAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.razy_dziennie_array, android.R.layout.simple_spinner_item);
        razyDziennieAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        razyDziennieSpinner.setAdapter(razyDziennieAdapter);

        MultiChoiceSpinner multiChoiceSpinner = findViewById(R.id.spinnerDniTygodnia);

    // Ustaw elementy Multiple Choice Spinner
        List<String> items = Arrays.asList("Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek", "Sobota", "Niedziela");
        multiChoiceSpinner.setItems(items);


        // Dodaj przycisk "Dodaj" do dialogu
        builder.setPositiveButton("Dodaj", (dialog, which) -> {
            // Tutaj dodaj kod do obsługi dodania leku na podstawie wprowadzonych danych
            // np. pobierz wartości z EditText, Spinner, itp.
            // i przekształć je na obiekt Lek do zapisania w bazie danych lub innego odpowiedniego miejsca
            String nazwaLeku = nazwaLekuEditText.getText().toString();
            String formaLeku = formaLekuSpinner.getSelectedItem().toString();
            String jednostka = jednostki[currentIndex[0]];
            String dawka = dawkaEditText.getText().toString();
            int razyDziennie = Integer.parseInt(razyDziennieSpinner.getSelectedItem().toString());
            List<String> dniTygodnia = dniTygodniaSpinner.getSelectedItems();

            // Tutaj możesz użyć zebranych danych do utworzenia obiektu Lek i przekazania go dalej
            // np. poprzez wywołanie metody w MedicationsViewModel lub innym miejscu, w zależności od architektury
        });

        // Dodaj przycisk "Anuluj" do dialogu
        builder.setNegativeButton("Anuluj", (dialog, which) -> {
            // Obsługa anulowania dodawania leku
            dialog.dismiss();
        });

        // Wyświetl dialog
        builder.create().show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

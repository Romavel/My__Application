package pl.pollub.android.myapplication.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import pl.pollub.android.myapplication.R;
import pl.pollub.android.myapplication.User;

public class EditPersonalDataDialogFragment extends DialogFragment {

    private EditText editNameBox;
    private EditText editSurnameBox;
    private EditText editPhoneNumberBox;
    private EditText editMainIllnessBox;
    private Spinner genderSpinner;
    private Spinner countriesSpinner;
    private Button buttonSaveEditPersonalData;
    private Button buttonCancelEditPersonalData;

    private User currentUser; // Przechowuje dane aktualnie zalogowanego użytkownika

    public EditPersonalDataDialogFragment(User currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_personal_data_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicjalizacja pól
        editNameBox = view.findViewById(R.id.editNameBox);
        editSurnameBox = view.findViewById(R.id.editSurnameBox);
        editPhoneNumberBox = view.findViewById(R.id.editPhoneNumberBox);
        editMainIllnessBox = view.findViewById(R.id.editMainIllnessBox);
        genderSpinner = view.findViewById(R.id.genderSpinner);
        countriesSpinner = view.findViewById(R.id.countriesSpinner);
        buttonSaveEditPersonalData = view.findViewById(R.id.buttonSaveEditPersonalData);
        buttonCancelEditPersonalData = view.findViewById(R.id.buttonCancelEditPersonalData);

        // Ustawienie dostępnych opcji dla spinnerów
        setupGenderSpinner();
        setupCountriesSpinner();

        // Obsługa kliknięcia przycisku Anuluj
        buttonCancelEditPersonalData.setOnClickListener(v -> dismiss());

        // Obsługa kliknięcia przycisku Zmień
        buttonSaveEditPersonalData.setOnClickListener(v -> saveChanges());

        // Wypełnienie pól danymi użytkownika
        fillUserData();
    }


    private void setupGenderSpinner() {
        List<String> genderList = new ArrayList<>();
        for (Genders gender : Genders.values()) {
            genderList.add(gender.getDescription());
        }

        // Dla genderSpinner
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                genderList
        );
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

    }

    private void setupCountriesSpinner() {
        List<String> countriesList = new ArrayList<>();
        for (Countries country : Countries.values()) {
            countriesList.add(country.getDescription());
        }

        // Dla countriesSpinner
        ArrayAdapter<String> countriesAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                countriesList
        );
        countriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countriesSpinner.setAdapter(countriesAdapter);
    }


    private void fillUserData() {
        if (currentUser != null) {
            editNameBox.setText(currentUser.getFirst_name());
            editSurnameBox.setText(currentUser.getLast_name());
            editPhoneNumberBox.setText(currentUser.getPhone_number());
            editMainIllnessBox.setText(currentUser.getIllness());

            setSpinnerSelection(genderSpinner, currentUser.getGender());
            Log.d("ProfileFragment", "Gender spinner: " + currentUser.getGender());
            setSpinnerSelection(countriesSpinner, currentUser.getCountry());
            Log.d("ProfileFragment", "Gender spinner: " + currentUser.getCountry());
        }
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        if (value != null) {
            int position = ((ArrayAdapter<String>) spinner.getAdapter()).getPosition(value);
            spinner.setSelection(position);
        }
    }

    private void saveChanges() {
        // Pobierz wartości z pól
        String newName = editNameBox.getText().toString();
        String newSurname = editSurnameBox.getText().toString();
        String newPhoneNumber = editPhoneNumberBox.getText().toString();
        String newMainIllness = editMainIllnessBox.getText().toString();
        String newGender = Genders.values()[genderSpinner.getSelectedItemPosition()].getValue();
        String newCountry = Countries.values()[countriesSpinner.getSelectedItemPosition()].getValue();


        // Zaktualizuj obiekt użytkownika
        currentUser.setFirst_name(newName);
        currentUser.setLast_name(newSurname);
        currentUser.setPhone_number(newPhoneNumber);
        currentUser.setIllness(newMainIllness);
        currentUser.setGender(newGender);
        currentUser.setCountry(newCountry);

        // Tutaj dodaj logikę zapisywania zmian do bazy danych Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("Users").document(userId).set(currentUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Zapisano pomyślnie
                        dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Błąd zapisu
                        // Tutaj możesz dodać odpowiednie obszary kodu obsługujące błędy
                        Toast.makeText(requireContext(), "Błąd zapisu danych", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}

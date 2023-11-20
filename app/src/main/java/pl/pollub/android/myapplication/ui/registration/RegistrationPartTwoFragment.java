package pl.pollub.android.myapplication.ui.registration;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import pl.pollub.android.myapplication.LoginActivity;
import pl.pollub.android.myapplication.R;
import pl.pollub.android.myapplication.User;
import pl.pollub.android.myapplication.ui.registration.RegistrationActivity;

public class RegistrationPartTwoFragment extends Fragment {

    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextPhoneNumber;
    private EditText editTextBirthDate;
    private Spinner spinnerCountry;
    private RadioGroup radioGroupGender;
    private Button buttonPrevious;
    private Button buttonRegister;

    public RegistrationPartTwoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration_part_two, container, false);

        // Inicjalizuj pola
        editTextFirstName = view.findViewById(R.id.editTextFirstName);
        editTextLastName = view.findViewById(R.id.editTextLastName);
        editTextPhoneNumber = view.findViewById(R.id.editTextPhoneNumber);
        editTextBirthDate = view.findViewById(R.id.editTextBirthDate);
        spinnerCountry = view.findViewById(R.id.spinnerCountry);
        radioGroupGender = view.findViewById(R.id.radioGroupGender);
        buttonPrevious = view.findViewById(R.id.buttonPrevious);
        buttonRegister = view.findViewById(R.id.buttonRegister);

        // Dodaj nasłuchiwanie na przycisk wstecz
        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Powrót do poprzedniego fragmentu
                ((RegistrationActivity) requireActivity()).navigateToPreviousFragment();
            }
        });

        // Dodaj nasłuchiwanie na przycisk zarejestruj
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Próbuj zarejestrować użytkownika
                registerUser();
            }
        });

        return view;
    }

    private void registerUser() {
        // Pobierz dane z pól
        String firstName = editTextFirstName.getText().toString();
        String lastName = editTextLastName.getText().toString();
        String phoneNumber = editTextPhoneNumber.getText().toString();
        String birthDate = editTextBirthDate.getText().toString();
        String country = spinnerCountry.getSelectedItem().toString();

        // Pobierz zaznaczoną płcię
        RadioButton selectedRadioButton = getView().findViewById(radioGroupGender.getCheckedRadioButtonId());
        String gender = selectedRadioButton.getText().toString();

        // Utwórz obiekt User
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNumber(phoneNumber);
        user.setBirthDate(birthDate);
        user.setCountry(country);
        user.setGender(gender);

        // Dodaj kod do rejestracji użytkownika w Firestore
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                        getArguments().getString("email"), getArguments().getString("password"))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Pobierz UID nowo utworzonego użytkownika
                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        // Ustaw UID i datę rejestracji w obiekcie User
                        user.setUserId(userId);
                        user.setRegistrationDate(getCurrentDateTime());

                        // Zapisz pozostałe dane użytkownika do Firestore Database
                        FirebaseFirestore.getInstance().collection("Users").document(userId).set(user)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(requireContext(), "Rejestracja udana", Toast.LENGTH_SHORT).show();
                                    // Przejdź do ekranu logowania po udanej rejestracji
                                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                                    startActivity(intent);
                                    requireActivity().finish();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(requireContext(), "Błąd rejestracji", Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(requireContext(), "Błąd rejestracji", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}

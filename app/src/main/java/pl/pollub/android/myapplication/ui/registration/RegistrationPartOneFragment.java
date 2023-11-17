package pl.pollub.android.myapplication.ui.registration;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import pl.pollub.android.myapplication.LoginActivity;
import pl.pollub.android.myapplication.R;

public class RegistrationPartOneFragment extends Fragment {

    private EditText editTextUsername;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private Button buttonNext;
    private Button buttonCancel;
    private TextView textViewPasswordConditions;

    public RegistrationPartOneFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration_part_one, container, false);

        // Inicjalizuj pola
        editTextUsername = view.findViewById(R.id.editTextUsername);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        editTextConfirmPassword = view.findViewById(R.id.editTextConfirmPassword);
        buttonNext = view.findViewById(R.id.buttonNext);
        buttonCancel = view.findViewById(R.id.buttonCancel);
        textViewPasswordConditions = view.findViewById(R.id.textViewPasswordConditions);

        // Dodaj nasłuchiwanie zmian w polu hasła
        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Sprawdź warunki hasła przy każdej zmianie
                validatePassword(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // Dodaj nasłuchiwanie na przycisk dalej
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Przejdź do kolejnej części rejestracji po spełnieniu warunków
                if (validateFields()) {
                    // Dodaj kod do przejścia do drugiej części rejestracji
                    ((RegistrationActivity) requireActivity()).showRegistrationPartTwoFragment();
                }
            }
        });


        // Dodaj nasłuchiwanie na przycisk anuluj
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Przejdź do LoginActivity po naciśnięciu przycisku Anuluj
                loadLoginActivity();
            }
        });

        return view;
    }

    // Dodaj nową metodę do przejścia do LoginActivity
    private void loadLoginActivity() {
        // Przykładowe przejście do LoginActivity
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish(); // Opcjonalnie zakończ bieżącą aktywność, jeśli nie chcesz wracać do niej
    }

    // Dodaj nową metodę do przejścia do drugiej części rejestracji
    private void loadNextFragment() {
        // Przykładowe przejście do kolejnego fragmentu (RegistrationPartTwoFragment)
        RegistrationPartTwoFragment fragment = new RegistrationPartTwoFragment();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.registration_fragment_container, fragment);
        transaction.addToBackStack(null); // Jeśli chcesz dodać do stosu cofania
        transaction.commit();
    }

    // Metoda do sprawdzania warunków hasła
    private void validatePassword(String password) {
        boolean isUpperCase = !password.equals(password.toLowerCase());
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecialChar = !password.matches("[A-Za-z0-9 ]*");

        String errorText = "";

        if (password.length() < 8) {
            errorText += "Co najmniej 8 znaków.\n";
        }

        if (!isUpperCase) {
            errorText += "Przynajmniej 1 wielka litera.\n";
        }

        if (!hasDigit) {
            errorText += "Przynajmniej 1 cyfra.\n";
        }

        if (!hasSpecialChar) {
            errorText += "Przynajmniej 1 znak specjalny.\n";
        }

        if (errorText.isEmpty()) {
            // Wszystkie warunki spełnione, zmień kolor tekstu na zielony
            textViewPasswordConditions.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            textViewPasswordConditions.setText("Warunki hasła spełnione");
        } else {
            // Warunki nie spełnione, zmień kolor tekstu na czerwony
            textViewPasswordConditions.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            textViewPasswordConditions.setText(errorText.trim());
        }
    }

    // Metoda do walidacji pól (nazwa użytkownika, email, hasło, potwierdzenie hasła)
    private boolean validateFields() {
        // Pobierz dane z pól
        String username = editTextUsername.getText().toString();
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();

        // Sprawdź, czy nazwa użytkownika jest wystarczająco długa
        if (username.isEmpty() || username.length() < 5) {
            Toast.makeText(requireContext(), "Nazwa użytkownika musi mieć co najmniej 5 znaków", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Sprawdź, czy adres email jest poprawny
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(requireContext(), "Podano nieprawidłowy adres email", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Sprawdź, czy hasło i jego potwierdzenie są identyczne
        if (password.isEmpty() || !password.equals(confirmPassword)) {
            Toast.makeText(requireContext(), "Hasła nie są identyczne", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}

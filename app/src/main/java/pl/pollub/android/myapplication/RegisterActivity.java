package pl.pollub.android.myapplication;


import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

// RegistrationActivity.java
public class RegisterActivity extends AppCompatActivity {

    private EditText firstNameEditText, lastNameEditText, usernameEditText, emailEditText,
            passwordEditText, confirmPasswordEditText, phoneNumberEditText;
    private Spinner countrySpinner;
    private RadioGroup genderRadioGroup;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicjalizacja widoków
        firstNameEditText = findViewById(R.id.editTextFirstName);
        lastNameEditText = findViewById(R.id.editTextLastName);
        usernameEditText = findViewById(R.id.editTextUsername);
        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        confirmPasswordEditText = findViewById(R.id.editTextConfirmPassword);
        countrySpinner = findViewById(R.id.spinnerCountry);
        phoneNumberEditText = findViewById(R.id.editTextPhoneNumber);
        genderRadioGroup = findViewById(R.id.radioGroupGender);
        registerButton = findViewById(R.id.buttonRegister);

        // Ustawienie adaptera dla Spinnera z listą krajów
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.countries_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(adapter);

        // Obsługa przycisku rejestracji
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        // Pobierz dane z formularza
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String username = usernameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
        String country = countrySpinner.getSelectedItem().toString();
        String phoneNumber = phoneNumberEditText.getText().toString();

        int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedGenderRadioButton = findViewById(selectedGenderId);
        String gender = selectedGenderRadioButton.getText().toString();

        // Sprawdź czy hasła się zgadzają
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Hasła nie są identyczne", Toast.LENGTH_SHORT).show();
            return;
        }

        // Zapisz dane do bazy danych
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setCountry(country);
        user.setPhoneNumber(phoneNumber);
        user.setGender(gender);

        // Użyj Firebase Firestore do zapisu danych do bazy
        // Poniżej znajduje się ogólny szkic, dostosuj go do swoich potrzeb
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(RegisterActivity.this, "Rejestracja udana", Toast.LENGTH_SHORT).show();
                        // Tutaj możesz przenieść użytkownika do kolejnego ekranu, np. ekranu logowania
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "Błąd podczas rejestracji", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

package pl.pollub.android.myapplication;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextUsername;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private TextView editTextBirthDate;
    private Spinner spinnerCountry;
    private EditText editTextPhoneNumber;
    private RadioGroup radioGroupGender;
    private Button buttonRegister;

    private TextView textViewPasswordConditions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicjalizuj pola
        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextBirthDate = findViewById(R.id.editTextBirthDate);
        spinnerCountry = findViewById(R.id.spinnerCountry);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewPasswordConditions = findViewById(R.id.textViewPasswordConditions);

        // Ustaw adapter dla spinnera (lista krajów)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.countries_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountry.setAdapter(adapter);

        // Ustaw nasłuchiwanie na przycisk rejestracji
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        // Pobierz dane z pól
        String firstName = editTextFirstName.getText().toString();
        String lastName = editTextLastName.getText().toString();
        String username = editTextUsername.getText().toString();
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();
        String phoneNumber = editTextPhoneNumber.getText().toString();
        String birthDate = editTextBirthDate.getText().toString();
        String country = spinnerCountry.getSelectedItem().toString();

        // Sprawdź, czy hasła są zgodne
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Hasła nie są zgodne", Toast.LENGTH_SHORT).show();
            return;
        }

        // Dodatkowa walidacja hasła
        validatePassword(password);

        // Pobierz zaznaczoną płcię
        RadioButton selectedRadioButton = findViewById(radioGroupGender.getCheckedRadioButtonId());
        String gender = selectedRadioButton.getText().toString();

        // Utwórz obiekt User
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setBirthDate(birthDate);
        user.setCountry(country);
        user.setGender(gender);

        // Dodaj kod do rejestracji użytkownika w Firebase Authentication
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Pobierz UID nowo utworzonego użytkownika
                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            // Ustaw UID i datę rejestracji w obiekcie User
                            user.setUserId(userId);
                            user.setRegistrationDate(getCurrentDateTime());

                            // Zapisz pozostałe dane użytkownika do Firestore Database
                            FirebaseFirestore.getInstance().collection("Users").document(userId).set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("RegisterActivity", "Pomyślnie zarejestrowano użytkownika");
                                            Toast.makeText(RegisterActivity.this, "Rejestracja udana", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("RegisterActivity", "Nie udało się zarejestrować użytkownika");
                                            Toast.makeText(RegisterActivity.this, "Błąd rejestracji", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Log.d("RegisterActivity", "Nie udało się zarejestrować użytkownika");
                            Toast.makeText(RegisterActivity.this, "Błąd rejestracji", Toast.LENGTH_SHORT).show();
                        }
                    }
                    private String getCurrentDateTime() {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        return sdf.format(new Date());
                    }
                });
    }
    // Metoda wywoływana po wyborze daty z kalendarza
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        // Aktualizacja pola z datą urodzenia
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        editTextBirthDate.setText(dateFormat.format(calendar.getTime()));
    }

    // Metoda do pokazania DatePickerDialog po kliknięciu w pole daty urodzenia
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

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

}

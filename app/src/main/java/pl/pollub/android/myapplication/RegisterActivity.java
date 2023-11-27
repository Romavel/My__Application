package pl.pollub.android.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    private EditText editTextUsername;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private Button buttonRegister;
    private Button buttonCancel;


    private TextView textViewPasswordConditions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register);

        // Inicjalizuj pola
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewPasswordConditions = findViewById(R.id.textViewPasswordConditions);

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
        String username = editTextUsername.getText().toString();
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();

        // Sprawdź, czy hasła są zgodne
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Hasła nie są zgodne", Toast.LENGTH_SHORT).show();
            return;
        }

        // Dodatkowa walidacja hasła
        validatePassword(password);

        // Sprawdź dostępność username i email
        checkUsernameAndEmailAvailability(username, email);
    }

    private void checkUsernameAndEmailAvailability(final String username, final String email) {
        // Sprawdź dostępność username
        FirebaseFirestore.getInstance().collection("Users")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                // Username jest już zajęty
                                Toast.makeText(RegisterActivity.this, "Username jest już zajęty", Toast.LENGTH_SHORT).show();
                            } else {
                                // Username jest dostępny, sprawdź dostępność email
                                checkEmailAvailability(email);
                            }
                        } else {
                            Log.d("RegisterActivity", "Błąd podczas sprawdzania dostępności username: " + task.getException());
                            Toast.makeText(RegisterActivity.this, "Błąd podczas sprawdzania dostępności username", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkEmailAvailability(final String email) {
        // Sprawdź dostępność email
        FirebaseFirestore.getInstance().collection("Users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                // Email jest już zajęty
                                Toast.makeText(RegisterActivity.this, "Email jest już zajęty", Toast.LENGTH_SHORT).show();
                            } else {
                                // Email jest dostępny, kontynuuj proces rejestracji
                                proceedWithRegistration();
                            }
                        } else {
                            Log.d("RegisterActivity", "Błąd podczas sprawdzania dostępności email: " + task.getException());
                            Toast.makeText(RegisterActivity.this, "Błąd podczas sprawdzania dostępności email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void proceedWithRegistration() {
        // Kontynuuj proces rejestracji
        String username = editTextUsername.getText().toString();
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();

        // Utwórz obiekt User
        User user = new User();
        user.setFirst_name("");
        user.setLast_name("");
        user.setUsername(username);
        user.setEmail(email);
        user.setPhone_number("");
        user.setBirth_date("");
        user.setCountry("");
        user.setGender("");

        // Dodaj kod do rejestracji użytkownika w Firebase Authentication
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Pobierz UID nowo utworzonego użytkownika
                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            // Ustaw UID i datę rejestracji w obiekcie User
                            //user.setUserId(userId);
                            user.setRegistration_date(Timestamp.now());

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
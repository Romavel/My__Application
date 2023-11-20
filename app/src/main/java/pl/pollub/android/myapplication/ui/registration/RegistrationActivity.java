package pl.pollub.android.myapplication.ui.registration;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import pl.pollub.android.myapplication.R;
import pl.pollub.android.myapplication.User;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register);

        // Wyświetl pierwszy fragment (RegistrationPartOneFragment) przy starcie aktywności
        showFragment(new RegistrationPartOneFragment(), "RegistrationPartOneFragment");
    }

    // Metoda nawigacyjna do wyświetlania drugiego fragmentu
    public void showRegistrationPartTwoFragment(String username, String email) {
        // Przekazanie danych do drugiego fragmentu
        Bundle bundle = new Bundle();
        bundle.putString("USERNAME", username);
        bundle.putString("EMAIL", email);

        RegistrationPartTwoFragment fragment = new RegistrationPartTwoFragment();
        fragment.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)  // Możesz dodać nazwę do nawigacji wstecz
                .commit();
    }

    // Metoda do wyświetlania fragmentów
    private void showFragment(Fragment fragment, String fragmentTag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment, fragmentTag)
                .addToBackStack(fragmentTag)
                .commit();
    }

    // Metoda do nawigacji do kolejnego fragmentu
    public void navigateToNextFragment() {
        showFragment(new RegistrationPartTwoFragment(),"RegistrationPartTwoFragment");
    }

    // Metoda do nawigacji do poprzedniego fragmentu
    public void navigateToPreviousFragment() {
        getSupportFragmentManager().popBackStack("RegistrationPartOneFragment", 0);
    }

    // Metoda do rejestracji użytkownika
    public void registerUser(String username, String email, String password) {
        // Dodaj kod do rejestracji użytkownika w Firebase Authentication
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Rejestracja udana, możesz wykonać dodatkowe czynności
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            // Pobierz UID zarejestrowanego użytkownika
                            String userId = user.getUid();

                            // Przykładowa metoda do zapisu dodatkowych danych do Firestore Database
                            User userData = new User();
                            userData.setUserId(userId);
                            userData.setUsername(username);
                            userData.setEmail(email);

                            FirebaseFirestore.getInstance().collection("Users").document(userId).set(userData)
                                    .addOnSuccessListener(aVoid -> {
                                        // Rejestracja i zapis danych udane
                                        Toast.makeText(RegistrationActivity.this, "Rejestracja udana", Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Rejestracja udana, ale zapis danych nieudany
                                        Toast.makeText(RegistrationActivity.this, "Błąd rejestracji", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        // Rejestracja nieudana, wyświetl komunikat użytkownikowi
                        Toast.makeText(this, "Błąd rejestracji: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

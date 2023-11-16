package pl.pollub.android.myapplication;

import static androidx.fragment.app.FragmentManager.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;


public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private static final String TAG = "LoginActivity";

    private Button buttonLogin;
    private TextView textViewRegister;
    private TextView textViewOr;
    private ImageView imageViewPerson;
    private ImageView imageViewLock;
    private CardView cardViewGoogleSignIn;

    private FirebaseAuth firebaseAuth;
    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicjalizacja obiektów widoku
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewRegister = findViewById(R.id.textViewRegister);
        textViewOr = findViewById(R.id.textViewOr);
        imageViewPerson = findViewById(R.id.imageViewPerson);
        imageViewLock = findViewById(R.id.imageViewLock);
        cardViewGoogleSignIn = findViewById(R.id.cardViewGoogleSignIn);

        // Inicjalizacja obiektu FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();


        // Inside onCreate() or another appropriate method
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);


        // Nasłuchiwanie przycisku logowania
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        // Przejście do ekranu rejestracji po kliknięciu na "Nie masz jeszcze konta? Zarejestruj się"
        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        // Nasłuchiwanie przycisku logowania za pomocą konta Google
        cardViewGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });
    }

    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Walidacja pól
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Wprowadź poprawny adres email i hasło", Toast.LENGTH_SHORT).show();
            return;
        }

        // Logowanie za pomocą Firebase Authentication
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Zalogowano pomyślnie
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Zalogowano jako " + user.getEmail(), Toast.LENGTH_SHORT).show();
                            // Tutaj możesz przekierować użytkownika na inny ekran po zalogowaniu
                        } else {
                            // Logowanie nieudane
                            Toast.makeText(LoginActivity.this, "Logowanie nieudane", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    private void signInWithGoogle() {

        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignInResult(task);
        }
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Zalogowano za pomocą konta Google, teraz należy zalogować się do Firebase
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            // Logowanie za pomocą konta Google nie powiodło się
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this, "Logowanie za pomocą konta Google nie powiodło się", Toast.LENGTH_SHORT).show();
        }
    }

    public void onGoogleSignInClick(View view) {
        signInWithGoogle();
    }


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Zalogowano pomyślnie za pomocą konta Google
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Zalogowano za pomocą konta Google jako " + user.getEmail(), Toast.LENGTH_SHORT).show();
                            // Tutaj możesz przekierować użytkownika na inny ekran po zalogowaniu
                        } else {
                            // Logowanie za pomocą konta Google nie powiodło się
                            Toast.makeText(LoginActivity.this, "Logowanie za pomocą konta Google nie powiodło się", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}

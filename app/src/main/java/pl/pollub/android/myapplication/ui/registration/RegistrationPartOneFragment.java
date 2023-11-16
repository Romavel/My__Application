package pl.pollub.android.myapplication.ui.registration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

import pl.pollub.android.myapplication.R;

public class RegistrationPartOneFragment extends Fragment {

    private EditText editTextUsername;
    private EditText editTextEmail;
    // Dodaj pozostałe pola, takie jak hasło, potwierdzenie hasła, etc.

    private Button buttonNext;

    public RegistrationPartOneFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registration_part_one, container, false);

        // Inicjalizuj pola
        editTextUsername = view.findViewById(R.id.editTextUsername);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        // Inicjalizuj pozostałe pola

        buttonNext = view.findViewById(R.id.buttonNext);

        // Ustaw nasłuchiwanie na przycisk "Next"
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Wywołaj funkcję sprawdzającą dostępność e-maila i nazwy użytkownika
                checkEmailAndUsernameAvailability();
            }
        });

        return view;
    }

    private void checkEmailAndUsernameAvailability() {
        String email = editTextEmail.getText().toString();
        String username = editTextUsername.getText().toString();
        // Dodaj pozostałe pola, takie jak hasło, potwierdzenie hasła, etc.

        // Sprawdź dostępność e-maila i nazwy użytkownika
        checkAvailability(email, new OnCheckAvailabilityListener() {
            @Override
            public void onAvailabilityChecked(boolean isAvailableEmail, boolean isAvailableUsername) {
                if (isAvailableEmail && isAvailableUsername) {
                    // Obie warunki spełnione, przejdź do kolejnego fragmentu
                    // Wywołaj metodę przechodzenia do następnego fragmentu
                    moveToNextFragment();
                } else {
                    if (!isAvailableEmail) {
                        Toast.makeText(getContext(), "Ten adres e-mail jest już używany", Toast.LENGTH_SHORT).show();
                    }
                    if (!isAvailableUsername) {
                        Toast.makeText(getContext(), "Ta nazwa użytkownika jest już zajęta", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void checkAvailability(String value, OnCheckAvailabilityListener listener) {
        FirebaseFirestore.getInstance().collection("Users")
                .whereEqualTo("email", value)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    boolean isAvailableEmail = queryDocumentSnapshots.isEmpty();
                    // Sprawdź dostępność nazwy użytkownika
                    checkAvailabilityUsername(listener, isAvailableEmail);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Błąd podczas sprawdzania dostępności: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void checkAvailabilityUsername(OnCheckAvailabilityListener listener, boolean isAvailableEmail) {
        String username = editTextUsername.getText().toString();
        FirebaseFirestore.getInstance().collection("Users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    boolean isAvailableUsername = queryDocumentSnapshots.isEmpty();
                    listener.onAvailabilityChecked(isAvailableEmail, isAvailableUsername);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Błąd podczas sprawdzania dostępności: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private interface OnCheckAvailabilityListener {
        void onAvailabilityChecked(boolean isAvailableEmail, boolean isAvailableUsername);
    }

    private void moveToNextFragment() {
        // Tutaj dodaj kod przechodzenia do kolejnego fragmentu
        // Na przykład:
        // ((YourActivity) getActivity()).loadNextFragment();
    }
}
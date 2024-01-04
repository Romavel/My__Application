package pl.pollub.android.myapplication.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Locale;

import pl.pollub.android.myapplication.LoginActivity;
import pl.pollub.android.myapplication.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfilViewModel profilViewModel =
                new ViewModelProvider(this).get(ProfilViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button buttonLogout = binding.buttonLogout;
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Wywołaj metodę obsługującą wylogowanie
                logoutUser();
            }
        });

        // Pobierz dane użytkownika i ustaw widoki
        loadUserData();

        return root;
    }

    private void logoutUser() {
        // Wylogowanie użytkownika za pomocą Firebase Authentication
        FirebaseAuth.getInstance().signOut();

        // Przenieś użytkownika do LoginActivity
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish(); // Jeśli chcesz, aby użytkownik nie mógł wrócić do ProfileFragment przyciskiem "wstecz"
    }
    private void loadUserData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Pobierz identyfikator zalogowanego użytkownika
            String userId = user.getUid();
            Log.d("ProfilFragment","UserID: " + userId.toString());
            // Pobierz dokument z kolekcji "Users" dla zalogowanego użytkownika
            FirebaseFirestore.getInstance().collection("Users").document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Jeśli dokument istnieje, pobierz dane
                                displayUserData(document);
                                Log.d("ProfilFragment","Pobrano dane użytkownika");
                            } else {
                                // Jeśli dokument nie istnieje, obsłuż sytuację braku danych
                                Log.d("ProfilFragment","Nie udało się pobrać danych użytkownika");
                            }
                        } else {
                            Log.d("ProfilFragment","Błąd przy pobieraniu danych użytkownika");
                        }
                    });
        }
    }

    private void displayUserData(DocumentSnapshot document) {
        TextView textWelcome = binding.textWelcome;
        TextView textRegistrationDate = binding.textRegistrationDate;
        TextView textFullName = binding.textFullName;
        TextView textCountry = binding.textCountry;
        TextView textCondition = binding.textCondition;
        TextView textMainMedication = binding.textMainMedication;

        // Ustaw dane w widokach
        textWelcome.setText(String.format("Witaj %s", document.getString("username")));

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        if (document.contains("registration_date")) {
            textRegistrationDate.setText(String.format("Data rejestracji: %s", dateFormat.format(document.getDate("registration_date"))));
        } else {
            textRegistrationDate.setVisibility(View.GONE);
        }

        if (document.contains("first_name") && document.contains("last_name")) {
            String firstName = document.getString("first_name");
            String lastName = document.getString("last_name");

            if (!TextUtils.isEmpty(firstName) || !TextUtils.isEmpty(lastName)) {
                String fullName = String.format("%s %s", firstName, lastName).trim();
                textFullName.setText(String.format("Imię i nazwisko: %s", fullName));
            } else {
                textFullName.setVisibility(View.GONE);
            }
        } else {
            textFullName.setVisibility(View.GONE);
        }




        if (document.contains("country")) {
            textCountry.setText(String.format("Kraj: %s", document.getString("country")));
        } else {
            textCountry.setVisibility(View.GONE);
        }

        if (document.contains("condition")) {
            textCondition.setText(String.format("Główne schorzenie: %s", document.getString("condition")));
        } else {
            textCondition.setVisibility(View.GONE);
        }

        if (document.contains("main_medication")) {
            textMainMedication.setText(String.format("Główny lek: %s", document.getString("main_medication")));
        } else {
            textMainMedication.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

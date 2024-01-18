package pl.pollub.android.myapplication.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Locale;

import pl.pollub.android.myapplication.LoginActivity;
import pl.pollub.android.myapplication.R;
import pl.pollub.android.myapplication.User;
import pl.pollub.android.myapplication.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    // Klucze SharedPreferences dla dolnej i górnej granicy
    private static final String PREF_LOWER_THRESHOLD = "lower_threshold";
    private static final String PREF_UPPER_THRESHOLD = "upper_threshold";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfilViewModel profilViewModel =
                new ViewModelProvider(this).get(ProfilViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        // Inicjalizacja widoków
        TextView accessibilitySettings = binding.accessibilitySettings;
        TextView notificationSettings = binding.notificationSettings;
        TextView therapeuticRangeSettings = binding.therapeuticRangeSettings;
        TextView editPersonalData = binding.editPersonalData;
        TextView editMainMedication = binding.editMainMedication;
        Button buttonLogout = binding.buttonLogout;

        //Wypisanie tekstu
        accessibilitySettings.setText("Ustawienia dostępności");
        notificationSettings.setText("Ustawienia powiadomień");
        therapeuticRangeSettings.setText("Ustaw przedział terapeutyczny");
        editPersonalData.setText("Edytuj dane");
        editMainMedication.setText("Zmień lek główny");


        // Dodanie click listenerów do TextViews
        accessibilitySettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obsługa kliknięcia w ustawienia dostępności
                // Dodaj tutaj kod, który ma zostać wykonany po kliknięciu
            }
        });

        notificationSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obsługa kliknięcia w ustawienia powiadomień
                // Dodaj tutaj kod, który ma zostać wykonany po kliknięciu
            }
        });

        therapeuticRangeSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obsługa kliknięcia w ustaw przediał terapeutyczny
                // Dodaj tutaj kod, który ma zostać wykonany po kliknięciu
                showEditTherapeuticRangeDialog();
            }
        });

        editPersonalData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obsługa kliknięcia w edytuj dane
                // Dodaj tutaj kod, który ma zostać wykonany po kliknięciu
                showEditPersonalDataDialog();
            }
        });

        editMainMedication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obsługa kliknięcia w zmień lek główny
                // Dodaj tutaj kod, który ma zostać wykonany po kliknięciu
                showEditMainMedicationDialog();
            }
        });

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Wywołaj metodę obsługującą wylogowanie
                logoutUser();
            }
        });

        // Pobierz dane użytkownika i ustaw widoki
        loadUserData();

        SwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.profileSwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Odśwież dane po przeciągnięciu
            loadUserData();
            swipeRefreshLayout.setRefreshing(false);
        });

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
            Log.d("ProfilFragment", "UserID: " + userId.toString());
            // Pobierz dokument z kolekcji "Users" dla zalogowanego użytkownika
            FirebaseFirestore.getInstance().collection("Users").document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Jeśli dokument istnieje, pobierz dane
                                displayUserData(document);
                                Log.d("ProfilFragment", "Pobrano dane użytkownika");
                            } else {
                                // Jeśli dokument nie istnieje, obsłuż sytuację braku danych
                                Log.d("ProfilFragment", "Nie udało się pobrać danych użytkownika");
                            }
                        } else {
                            Log.d("ProfilFragment", "Błąd przy pobieraniu danych użytkownika");
                        }
                    });
        }
    }

    private void displayUserData(DocumentSnapshot document) {
        TextView textWelcome = binding.textWelcome;
        TextView textRegistrationDate = binding.textRegistrationDate;
        TextView textFullName = binding.textFullName;
        TextView textCountry = binding.textCountry;
        TextView textGender = binding.textGender;
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


        if (document.contains("gender")) {
            String genderValue = document.getString("gender");
            Genders gender = Genders.valueOf(genderValue.toUpperCase());
            textGender.setText(String.format("Płeć: %s", gender.getDescription()));
        } else {
            textGender.setVisibility(View.GONE);
        }

        if (document.contains("country")) {
            String countryValue = document.getString("country");
            Countries country = Countries.valueOf(countryValue.toUpperCase());
            textCountry.setText(String.format("Kraj: %s", country.getDescription()));
        } else {
            textCountry.setVisibility(View.GONE);
        }

        if (document.contains("illness")) {
            textCondition.setText(String.format("Główne schorzenie: %s", document.getString("illness")));
        } else {
            textCondition.setVisibility(View.GONE);
        }

        if (document.contains("medication")) {
            textMainMedication.setText(String.format("Główny lek: %s", document.getString("medication")));
        } else {
            textMainMedication.setVisibility(View.GONE);
        }
    }

    public void showEditPersonalDataDialog() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Pobierz identyfikator zalogowanego użytkownika
            String userId = currentUser.getUid();

            // Pobierz dokument z kolekcji "Users" dla zalogowanego użytkownika
            FirebaseFirestore.getInstance().collection("Users").document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Jeśli dokument istnieje, pobierz dane i przekaż do dialogu
                                User userData = document.toObject(User.class);
                                if (userData != null) {
                                    showEditPersonalDataDialog(userData);
                                }
                            } else {
                                Log.d("ProfilFragment", "Nie udało się pobrać danych użytkownika");
                            }
                        } else {
                            Log.d("ProfilFragment", "Błąd przy pobieraniu danych użytkownika");
                        }
                    });
        }
    }

    public void showEditPersonalDataDialog(User user) {
        FragmentManager fragmentManager = getParentFragmentManager();
        EditPersonalDataDialogFragment editPersonalDataDialogFragment = new EditPersonalDataDialogFragment(user);
        editPersonalDataDialogFragment.show(fragmentManager, "EditPersonalDataDialogFragment");
    }

    public void showEditMainMedicationDialog() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Pobierz identyfikator zalogowanego użytkownika
            String userId = currentUser.getUid();

            // Pobierz dokument z kolekcji "Users" dla zalogowanego użytkownika
            FirebaseFirestore.getInstance().collection("Users").document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Jeśli dokument istnieje, pobierz dane i przekaż do dialogu
                                User userData = document.toObject(User.class);
                                if (userData != null) {
                                    showEditMainMedicationDialog(userData);
                                }
                            } else {
                                Log.d("ProfilFragment", "Nie udało się pobrać danych użytkownika");
                            }
                        } else {
                            Log.d("ProfilFragment", "Błąd przy pobieraniu danych użytkownika");
                        }
                    });
        }
    }

    private void showEditMainMedicationDialog(User user) {
        // Wywołaj dialog przekazując obiekt User
        if (getActivity() != null) {
            EditMainMedicationDialogFragment editMainMedicationDialogFragment = new EditMainMedicationDialogFragment(user);
            editMainMedicationDialogFragment.show(getActivity().getSupportFragmentManager(), "EditMainMedicationDialogFragment");
        }
    }

    // Dodaj metodę do obsługi wyświetlania dialogu granic INR
    public void showEditTherapeuticRangeDialog() {
        Log.d("ProfileFragment", "Show Edit Therapeutic Range Dialog");

        FragmentManager fragmentManager = getParentFragmentManager();
        EditTherapeuticRangeDialogFragment editTherapeuticRangeDialogFragment = new EditTherapeuticRangeDialogFragment();

        // Przekazanie aktualnych wartości granic do dialogu
        Bundle args = new Bundle();
        args.putFloat("lowerThreshold", getThresholdFromPreferences(PREF_LOWER_THRESHOLD));
        args.putFloat("upperThreshold", getThresholdFromPreferences(PREF_UPPER_THRESHOLD));
        editTherapeuticRangeDialogFragment.setArguments(args);

        editTherapeuticRangeDialogFragment.show(fragmentManager, "EditTherapeuticRangeDialog");
    }


    // Metoda do pobrania wartości granicy z SharedPreferences
    private float getThresholdFromPreferences(String key) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("your_preferences_name", Context.MODE_PRIVATE);
        // Zwróć wartość dla danego klucza, jeśli nie ma - domyślnie 0.0f
        return sharedPreferences.getFloat(key, 0.0f);
    }

    // Dodaj metodę do zapisywania wartości granicy do SharedPreferences
    private void saveThresholdToPreferences(String key, float value) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("your_preferences_name", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

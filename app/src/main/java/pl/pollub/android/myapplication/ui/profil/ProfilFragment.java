package pl.pollub.android.myapplication.ui.profil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;

import pl.pollub.android.myapplication.databinding.FragmentProfilBinding;

public class ProfilFragment extends Fragment {

    private FragmentProfilBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfilViewModel profilViewModel =
                new ViewModelProvider(this).get(ProfilViewModel.class);

        binding = FragmentProfilBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textProfil;
        profilViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        Button buttonLogout = binding.buttonLogout;
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Wywołaj metodę obsługującą wylogowanie
                logoutUser();
            }
        });

        return root;
    }

    private void logoutUser() {
        // Wylogowanie użytkownika za pomocą Firebase Authentication
        FirebaseAuth.getInstance().signOut();

        // Jeśli korzystasz z nawigacji, możesz przekierować użytkownika na ekran logowania
        // NavController navController = Navigation.findNavController(requireView());
        // navController.navigate(R.id.action_profilFragment_to_loginFragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

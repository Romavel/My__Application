package pl.pollub.android.myapplication.ui.profile;

import android.content.Intent;
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

        // Przenieś użytkownika do LoginActivity
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish(); // Jeśli chcesz, aby użytkownik nie mógł wrócić do ProfileFragment przyciskiem "wstecz"
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

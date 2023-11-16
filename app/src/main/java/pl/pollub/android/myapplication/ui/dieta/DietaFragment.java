package pl.pollub.android.myapplication.ui.dieta;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import pl.pollub.android.myapplication.LoginActivity;
import pl.pollub.android.myapplication.databinding.FragmentDietaBinding;
import pl.pollub.android.myapplication.RegisterActivity;

public class DietaFragment extends Fragment {

    private FragmentDietaBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DietaViewModel dietaViewModel =
                new ViewModelProvider(this).get(DietaViewModel.class);

        binding = FragmentDietaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final Button registerButton = binding.buttonRegister;
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Przenieś użytkownika do RegisterActivity po kliknięciu przycisku
                Intent intent = new Intent(getActivity(), RegisterActivity.class);
                startActivity(intent);
            }
        });




        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

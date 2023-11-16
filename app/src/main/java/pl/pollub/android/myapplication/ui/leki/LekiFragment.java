package pl.pollub.android.myapplication.ui.leki;

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

import pl.pollub.android.myapplication.LoginActivity;
import pl.pollub.android.myapplication.databinding.FragmentLekiBinding;

public class LekiFragment extends Fragment {

    private FragmentLekiBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LekiViewModel lekiViewModel =
                new ViewModelProvider(this).get(LekiViewModel.class);

        binding = FragmentLekiBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final Button LoginButton = binding.buttonLogin;
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Przenieś użytkownika do RegisterActivity po kliknięciu przycisku
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        final TextView textView = binding.textLeki;
        lekiViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

package pl.pollub.android.myapplication.ui.pomiary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import pl.pollub.android.myapplication.databinding.FragmentPomiaryBinding;

public class PomiaryFragment extends Fragment {

    private FragmentPomiaryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PomiaryViewModel pomiaryViewModel =
                new ViewModelProvider(this).get(PomiaryViewModel.class);

        binding = FragmentPomiaryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textPomiary;
        pomiaryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

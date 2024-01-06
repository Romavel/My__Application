package pl.pollub.android.myapplication.ui.diet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import pl.pollub.android.myapplication.R;
import pl.pollub.android.myapplication.databinding.FragmentDietBinding;
import pl.pollub.android.myapplication.ui.measurements.InrMeasurementDialogFragment;
import pl.pollub.android.myapplication.ui.measurements.PressureMeasurementListFragment;

public class DietFragment extends Fragment {

    private FragmentDietBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DietViewModel dietViewModel =
                new ViewModelProvider(this).get(DietViewModel.class);

        binding = FragmentDietBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    public void showDietDialog() {
        FragmentManager fragmentManager = getParentFragmentManager();
        DietDialogFragment dietDialogFragment = new DietDialogFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, dietDialogFragment)
                .addToBackStack(null)
                .commit();
    }
}

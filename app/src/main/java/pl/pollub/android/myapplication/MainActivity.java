package pl.pollub.android.myapplication;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import pl.pollub.android.myapplication.databinding.ActivityMainBinding;
import pl.pollub.android.myapplication.ui.measurements.MeasurementsFragment;
import pl.pollub.android.myapplication.ui.diet.DietFragment;
import pl.pollub.android.myapplication.ui.medications.MedicationsFragment;
import pl.pollub.android.myapplication.ui.profile.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new MeasurementsFragment());
        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_pomiary) {
                replaceFragment(new MeasurementsFragment());
                binding.fab.show();
            } else if (itemId == R.id.navigation_leki) {
                replaceFragment(new MedicationsFragment());
                binding.fab.show();
            } else if (itemId == R.id.navigation_dieta) {
                replaceFragment(new DietFragment());
                binding.fab.show();
            } else if (itemId == R.id.navigation_profil) {
                replaceFragment(new ProfileFragment());
                binding.fab.hide();
            }
            return true;
        });

        // Inicjalizuj Floating Action Button dla dodawania różnych elementów
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleFabClick();
            }
        });
    }


    // Metoda do obsługi FAB w zależności od aktualnie wybranego fragmentu
    private void handleFabClick() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
        if (currentFragment instanceof MeasurementsFragment) {
            // Obsługa dodawania pomiarów INR i ciśnienia
            ((MeasurementsFragment) currentFragment).handleFabClick();
        } else if (currentFragment instanceof MedicationsFragment) {
            // Obsługa dodawania leków i objawów
            //((MedicationsFragment) currentFragment).showAddDialog();
        } else if (currentFragment instanceof DietFragment) {
            // Obsługa dodawania składników żywieniowych i używek
            // Dodaj obsługę dla DietFragment
        } else if (currentFragment instanceof ProfileFragment) {
            // Obsługa dodawania informacji do profilu
            // Dodaj obsługę dla ProfileFragment

        }
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

}
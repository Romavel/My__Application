package pl.pollub.android.myapplication;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import pl.pollub.android.myapplication.databinding.ActivityMainBinding;
import pl.pollub.android.myapplication.ui.dieta.DietaFragment;
import pl.pollub.android.myapplication.ui.leki.LekiFragment;
import pl.pollub.android.myapplication.ui.pomiary.PomiaryFragment;
import pl.pollub.android.myapplication.ui.profil.ProfilFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new PomiaryFragment());
        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_pomiary) {
                replaceFragment(new PomiaryFragment());
                binding.fab.show();
            } else if (itemId == R.id.navigation_leki) {
                replaceFragment(new LekiFragment());
                binding.fab.show();
            } else if (itemId == R.id.navigation_dieta) {
                replaceFragment(new DietaFragment());
                binding.fab.show();
            } else if (itemId == R.id.navigation_profil) {
                replaceFragment(new ProfilFragment());
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
        if (currentFragment instanceof PomiaryFragment) {
            // Obsługa dodawania pomiarów INR i ciśnienia
            ((PomiaryFragment) currentFragment).handleFabClick();
        } else if (currentFragment instanceof LekiFragment) {
            // Obsługa dodawania leków i objawów
            //((LekiFragment) currentFragment).showAddDialog();
        } else if (currentFragment instanceof DietaFragment) {
            // Obsługa dodawania składników żywieniowych i używek
            // Dodaj obsługę dla DietaFragment
        } else if (currentFragment instanceof ProfilFragment) {
            // Obsługa dodawania informacji do profilu
            // Dodaj obsługę dla ProfilFragment

        }
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

}
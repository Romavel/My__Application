package pl.pollub.android.myapplication;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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
            } else if (itemId == R.id.navigation_leki) {
                replaceFragment(new LekiFragment());
            } else if (itemId == R.id.navigation_dieta) {
                replaceFragment(new DietaFragment());
            } else if (itemId == R.id.navigation_profil) {
                replaceFragment(new ProfilFragment());
            }
            return true;
        });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

}
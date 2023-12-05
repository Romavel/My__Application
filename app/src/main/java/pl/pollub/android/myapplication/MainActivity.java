package pl.pollub.android.myapplication;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import pl.pollub.android.myapplication.databinding.ActivityMainBinding;
import pl.pollub.android.myapplication.ui.measurements.InrMeasurementDialogFragment;
import pl.pollub.android.myapplication.ui.measurements.InrMeasurementListFragment;
import pl.pollub.android.myapplication.ui.measurements.MeasurementsFragment;
import pl.pollub.android.myapplication.ui.diet.DietFragment;
import pl.pollub.android.myapplication.ui.measurements.PressureMeasurementDialogFragment;
import pl.pollub.android.myapplication.ui.measurements.PressureMeasurementListFragment;
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
            showMeasurementsBottomDialog();
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

    private void showMeasurementsBottomDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet_measurements_layout);

        LinearLayout inrLayout = dialog.findViewById(R.id.layout_INR);
        LinearLayout bloodPressureLayout = dialog.findViewById(R.id.layout_Blood_Pressure);
        LinearLayout inrList = dialog.findViewById(R.id.layout_INR_List);
        LinearLayout bloodPressureList = dialog.findViewById(R.id.layout_Blood_Pressure_List);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);
        inrLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Wywołaj metodę showInrMeasurementDialog() w MeasurementsFragment
                handleInrMeasurementDialog();
                dialog.dismiss();
            }
        });

        bloodPressureLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Wywołaj metodę showPressureMeasurementDialog() w MeasurementsFragment
                handlePressureMeasurementDialog();
                dialog.dismiss();
            }
        });

        inrList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Wywołaj metodę showInrMeasurementList() w MeasurementsFragment
                handleInrMeasurementList();
                dialog.dismiss();
            }
        });

        bloodPressureList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Wywołaj metodę showPressureMeasurementList() w MeasurementsFragment
                handlePressureMeasurementList();
                dialog.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    // Metody do obsługi akcji w MeasurementsFragment
    private void handleInrMeasurementDialog() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
        if (currentFragment instanceof MeasurementsFragment) {
            ((MeasurementsFragment) currentFragment).showInrMeasurementDialog();
        }
    }

    private void handlePressureMeasurementDialog() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
        if (currentFragment instanceof MeasurementsFragment) {
            ((MeasurementsFragment) currentFragment).showPressureMeasurementDialog();
        }
    }

    private void handleInrMeasurementList() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
        if (currentFragment instanceof MeasurementsFragment) {
            ((MeasurementsFragment) currentFragment).showInrMeasurementList();
        }
    }

    private void handlePressureMeasurementList() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
        if (currentFragment instanceof MeasurementsFragment) {
            ((MeasurementsFragment) currentFragment).showPressureMeasurementList();
        }
    }
}


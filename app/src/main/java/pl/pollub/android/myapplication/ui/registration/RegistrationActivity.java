package pl.pollub.android.myapplication.ui.registration;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import pl.pollub.android.myapplication.R;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_registration);

        // Wyświetl pierwszy fragment (RegistrationPartOneFragment) przy starcie aktywności
        showFragment(new RegistrationPartOneFragment(), "RegistrationPartOneFragment");
    }


    // Metoda nawigacyjna do wyświetlania drugiego fragmentu
    public void showRegistrationPartTwoFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new RegistrationPartTwoFragment())
                .addToBackStack(null)  // Możesz dodać nazwę do nawigacji wstecz
                .commit();
    }

    
    // Metoda do wyświetlania fragmentów
    private void showFragment(Fragment fragment, String fragmentTag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment, fragmentTag)
                .addToBackStack(fragmentTag)
                .commit();
    }

    // Wywołanie metody showFragment z dodaną nazwą



    // Metoda do nawigacji do kolejnego fragmentu
    public void navigateToNextFragment() {
        showFragment(new RegistrationPartTwoFragment(),"RegistrationPartTwoFragment");
    }

    // Metoda do nawigacji do poprzedniego fragmentu
    public void navigateToPreviousFragment() {
        getSupportFragmentManager().popBackStack("RegistrationPartOneFragment", 0);

    }
}

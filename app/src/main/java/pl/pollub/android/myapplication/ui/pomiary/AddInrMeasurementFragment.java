package pl.pollub.android.myapplication.ui.pomiary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import pl.pollub.android.myapplication.R;

public class AddInrMeasurementFragment extends Fragment {

    // Dodaj odpowiednie importy dla wykorzystywanych bibliotek

    private EditText editTextInrValue;
    private Button buttonAddMeasurement;
    private Button buttonCancel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_inr_measurement, container, false);

        editTextInrValue = root.findViewById(R.id.editTextInrValue);
        buttonAddMeasurement = root.findViewById(R.id.buttonAddMeasurement);
        buttonCancel = root.findViewById(R.id.buttonCancel);

        buttonAddMeasurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dodaj kod do zapisu wartości do bazy danych (Firestore)
                // Możesz skorzystać z Firebase Firestore SDK
                // np. Firestore.getInstance().collection("measurements").add(new Measurement(value));
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Zamknij bieżący fragment (możesz użyć metody getActivity().onBackPressed())
            }
        });

        return root;
    }
}

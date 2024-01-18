package pl.pollub.android.myapplication.ui.profile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import pl.pollub.android.myapplication.R;

public class EditTherapeuticRangeDialogFragment extends DialogFragment {

    private EditText upperThresholdEditText;
    private EditText lowerThresholdEditText;
    private Button saveButton;
    private Button cancelButton;

    private static final String FILE_NAME = "therapeutic_range_data.txt";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_edit_therapeutic_range_dialog, null);

        upperThresholdEditText = view.findViewById(R.id.upperBorderBox);
        lowerThresholdEditText = view.findViewById(R.id.lowerBorderBox);
        saveButton = view.findViewById(R.id.buttonSaveEditTherapeuticRange);
        cancelButton = view.findViewById(R.id.buttonCancelEditTherapeuticRange);

        // Pobierz wcześniej zapisane wartości
        loadThresholdValues();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveThresholdValues();
                dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        builder.setView(view);
        return builder.create();
    }

    private void saveThresholdValues() {
        try (FileOutputStream fos = requireActivity().openFileOutput(FILE_NAME, Context.MODE_PRIVATE)) {
            // Konwertuj wartości na tekst
            String upperThresholdText = upperThresholdEditText.getText().toString();
            String lowerThresholdText = lowerThresholdEditText.getText().toString();

            // Zapisz wartości do pliku
            fos.write(upperThresholdText.getBytes());
            fos.write("\n".getBytes()); // Dodaj nową linię między wartościami
            fos.write(lowerThresholdText.getBytes());

            // Poinformuj użytkownika o udanym zapisie
            Toast.makeText(requireContext(), "Zapisano wartości", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            // Poinformuj użytkownika o błędzie
            Toast.makeText(requireContext(), "Błąd zapisu wartości", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadThresholdValues() {
        try {
            // Sprawdź, czy plik istnieje
            if (requireActivity().getFileStreamPath(FILE_NAME).exists()) {
                // Odczytaj wartości z pliku
                byte[] buffer = new byte[1024];
                StringBuilder upperThresholdText = new StringBuilder();
                StringBuilder lowerThresholdText = new StringBuilder();

                FileInputStream fis = requireActivity().openFileInput(FILE_NAME);
                int n;

                while ((n = fis.read(buffer)) != -1) {
                    String chunk = new String(buffer, 0, n);
                    String[] values = chunk.split("\n");

                    if (values.length >= 2) {
                        upperThresholdText.append(values[0]);
                        lowerThresholdText.append(values[1]);
                    }
                }

                // Ustaw wczytane wartości w EditText
                upperThresholdEditText.setText(upperThresholdText.toString());
                lowerThresholdEditText.setText(lowerThresholdText.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

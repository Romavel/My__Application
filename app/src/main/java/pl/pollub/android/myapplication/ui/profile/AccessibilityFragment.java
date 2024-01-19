package pl.pollub.android.myapplication.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import pl.pollub.android.myapplication.R;

public class AccessibilityFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accessibility, container, false);

        TextView titleTextView = view.findViewById(R.id.titleTextView);
        TextView contentTextView = view.findViewById(R.id.contentTextView);

        titleTextView.setText("Dostępność");
        titleTextView.setTextSize(24); // Font size for title
        titleTextView.setTextColor(getResources().getColor(R.color.cyan)); // Assuming you have defined cyan color in resources

        contentTextView.setText("Aplikacja wspiera dynamiczną wielkość czcionki. " +
                "Możesz ją zmienić w ustawieniach Androida: " +
                "Ustawienia -> Dostępność -> Rozmiar czcionki.");

        return view;
    }
}

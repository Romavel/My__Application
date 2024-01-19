package pl.pollub.android.myapplication.ui.measurements;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import pl.pollub.android.myapplication.R;

public class InrMeasurementAdapter extends RecyclerView.Adapter<InrMeasurementAdapter.ViewHolder> {

    private List<InrMeasurement> inrMeasurements;

    public InrMeasurementAdapter(List<InrMeasurement> inrMeasurements) {
        this.inrMeasurements = inrMeasurements;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inr_measurement, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InrMeasurement measurement = inrMeasurements.get(position);

        // Ustaw dane pomiaru w widoku ViewHolder
        holder.dateTextView.setText(formatDateWithoutTime(measurement.getTime().toDate()));
        holder.valueTextView.setText(String.valueOf(measurement.getValue()));
    }

    @Override
    public int getItemCount() {
        return inrMeasurements.size();
    }

    // Metoda do formatowania daty bez czasu
    private String formatDateWithoutTime(java.util.Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    // ViewHolder przechowujący widoki elementów listy
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView dateTextView;
        public TextView valueTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            valueTextView = itemView.findViewById(R.id.valueTextView);
        }
    }
}
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

public class PressureMeasurementAdapter extends RecyclerView.Adapter<PressureMeasurementAdapter.ViewHolder> {

    private List<PressureMeasurement> pressureMeasurements;

    public PressureMeasurementAdapter(List<PressureMeasurement> pressureMeasurements) {
        this.pressureMeasurements = pressureMeasurements;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pressure_measurement, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PressureMeasurement measurement = pressureMeasurements.get(position);

        // Ustaw dane pomiaru w widoku ViewHolder
        holder.dateTextView.setText(formatDateWithoutTime(measurement.getTime().toDate()));
        holder.systolicPressureTextView.setText(String.valueOf(measurement.getSystolic_pressure()));
        holder.diastolicPressureTextView.setText(String.valueOf(measurement.getDiastolic_pressure()));
        holder.pulseTextView.setText(String.valueOf(measurement.getPulse()));
    }

    @Override
    public int getItemCount() {
        return pressureMeasurements.size();
    }

    // Metoda do formatowania daty bez czasu
    private String formatDateWithoutTime(java.util.Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    // ViewHolder przechowujący widoki elementów listy
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView dateTextView;
        public TextView systolicPressureTextView;
        public TextView diastolicPressureTextView;
        public TextView pulseTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            systolicPressureTextView = itemView.findViewById(R.id.systolicTextView);
            diastolicPressureTextView = itemView.findViewById(R.id.diastolicTextView);
            pulseTextView = itemView.findViewById(R.id.pulseTextView);
        }
    }
}

package pl.pollub.android.myapplication.ui.pomiary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pl.pollub.android.myapplication.R;

public class InrMeasurementAdapter extends RecyclerView.Adapter<InrMeasurementAdapter.ViewHolder> {

    private List<InrMeasurement> inrMeasurements;
    private Context context;

    public InrMeasurementAdapter(List<InrMeasurement> inrMeasurements) {
        this.inrMeasurements = inrMeasurements;
        this.context = context;
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
        holder.valueTextView.setText("Wartość: " + String.valueOf(measurement.getValue()));
        holder.dateTextView.setText("Data: " + formatDateWithoutTime(measurement.getTime().toDate()));
    }

    @Override
    public int getItemCount() {
        return inrMeasurements.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView valueTextView;
        TextView dateTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            valueTextView = itemView.findViewById(R.id.valueTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }

    private String formatDateWithoutTime(java.util.Date date) {
        String formattedDate = PomiaryFragment.formatDateWithoutTime(date);

        return formattedDate;
    }
}


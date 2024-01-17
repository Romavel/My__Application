package pl.pollub.android.myapplication.ui.medications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

import pl.pollub.android.myapplication.R;

public class OtherMedicationsAdapter extends RecyclerView.Adapter<OtherMedicationsAdapter.OtherMedicationViewHolder> {

    private List<OtherMedication> otherMedications;
    private Context mContext;

    public OtherMedicationsAdapter(List<OtherMedication> otherMedications) {
        this.otherMedications = otherMedications;
    }

    @NonNull
    @Override
    public OtherMedicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_other_medication, parent, false);
        return new OtherMedicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OtherMedicationViewHolder holder, int position) {
        OtherMedication otherMedication = otherMedications.get(position);
        holder.bind(otherMedication);
    }

    @Override
    public int getItemCount() {
        return otherMedications != null ? otherMedications.size() : 0;
    }


    public void setOtherMedicationsList(List<OtherMedication> otherMedications) {
        this.otherMedications = otherMedications;
    }

    // Metoda do ustawiania kontekstu z zewnątrz
    public void setContext(Context context) {
        mContext = context;
    }

    public static class OtherMedicationViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private ShapeableImageView listImage;
        private TextView otherMedicationTextView;

        public OtherMedicationViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            listImage = itemView.findViewById(R.id.listImage);
            otherMedicationTextView = itemView.findViewById(R.id.otherMedicationTextView);
        }

        public void bind(OtherMedication otherMedication) {
            // Ustawienie obrazka, można dostosować do różnych leków
            // listImage.setImageResource(R.drawable.ic_medications);

            // Ustawienie nazwy leku
            otherMedicationTextView.setText(otherMedication.getName());
        }
    }
}

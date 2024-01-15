package pl.pollub.android.myapplication.ui.medications;

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

// Modyfikacje w klasie SymptomAdapter

public class SymptomAdapter extends RecyclerView.Adapter<SymptomAdapter.SymptomViewHolder> {

    private List<String> symptoms;

    public SymptomAdapter(List<String> symptoms) {
        this.symptoms = symptoms;
    }

    @NonNull
    @Override
    public SymptomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_symptom, parent, false);
        return new SymptomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SymptomViewHolder holder, int position) {
        String symptom = symptoms.get(position);
        holder.bind(symptom);
    }

    @Override
    public int getItemCount() {
        return symptoms.size();
    }

    public static class SymptomViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private ShapeableImageView listImage;
        private TextView textSymptom;

        public SymptomViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            listImage = itemView.findViewById(R.id.listImage);
            textSymptom = itemView.findViewById(R.id.textSymptom);
        }

        public void bind(String symptom) {
            // Ustawienia obrazka, można dostosować do różnych symptomów
            // listImage.setImageResource(R.drawable.ic_symptoms);

            // Tłumaczenia symptomów
            String translatedSymptom = translateSymptom(symptom);
            textSymptom.setText(translatedSymptom);
        }

        private String translateSymptom(String symptom) {
            switch (symptom) {
                case "Headache":
                    return "Ból głowy";
                case "Bleeding":
                    return "Krwawienie z nieznanej przyczyny";
                case "Bruises":
                    return "Siniaki z nieznanej przyczyny";
                case "Chest_Pain":
                    return "Ból w klatce piersiowej";
                case "Leg_Swelling":
                    return "Opuchlizna nóg";
                case "Palpitations":
                    return "Kołatanie serca";
                case "Dizzyness":
                    return "Zawroty głowy";
                case "Hot_Flush":
                    return "Uderzenie gorąca";
                case "Coughing":
                    return "Kaszel";
                default:
                    return symptom; // Jeśli nie ma tłumaczenia, zwróć sam symptom
            }
        }
    }
}


package pl.pollub.android.myapplication.ui.diet;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pl.pollub.android.myapplication.R;

public class DietAdapter extends RecyclerView.Adapter<DietAdapter.DietViewHolder> {

    private NewDiet newDiet;

    public DietAdapter(NewDiet newDiet) {
        this.newDiet = newDiet;
    }

    @NonNull
    @Override
    public DietViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_diet, parent, false);
        return new DietViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DietViewHolder holder, int position) {
        // Tutaj wypełnij pola widoku na podstawie modelu danych
        holder.bind(newDiet, position);
    }

    @Override
    public int getItemCount() {
        // Zwracaj ilość DietEntry wewnątrz NewDiet
        return newDiet != null && newDiet.getIntake_arr() != null
                ? newDiet.getIntake_arr().size()
                : 0;
    }

    public static class DietViewHolder extends RecyclerView.ViewHolder {

        // Deklaracje pól z item_diet.xml
        private CardView cardView;
        private TextView textName;
        private TextView textAmount;
        private TextView textUnit;
        private TextView textComparison;

        public DietViewHolder(@NonNull View itemView) {
            super(itemView);

            // Inicjalizacja pól z item_diet.xml
            cardView = itemView.findViewById(R.id.cardView);
            textName = itemView.findViewById(R.id.textName);
            textAmount = itemView.findViewById(R.id.textAmount);
            textUnit = itemView.findViewById(R.id.textUnit);
            textComparison = itemView.findViewById(R.id.textComparison);
        }

        public void bind(NewDiet newDiet, int position) {
            // Pobierz listę DietEntry z NewDiet
            List<DietEntry> dietEntries = newDiet.getIntake_arr();

            // Pobierz DietEntry dla bieżącej pozycji
            DietEntry entry = dietEntries.get(position);

            // Ustaw wartości w polach widoku
            textName.setText(entry.getName() + ": ");
            textAmount.setText(String.valueOf(entry.getAmount()));

            // Ustaw widoczność pola comparison w zależności od warunków
            if ("Caffeine".equals(entry.getName())) {
                Log.d("DietDialogFragment","Wewnątrz if Caffeine");
                textUnit.setText("mg");
                textComparison.setText("To w przybliżeniu 435 ml kawy, 100ml espresso lub 400ml napoju energatycznego");
            } else if ("Nicotine".equals(entry.getName())) {
                Log.d("DietDialogFragment","Wewnątrz if Nicotine");
                textUnit.setText("mg");
                textComparison.setText("Inna informacja dla Nicotine");
            } else if ("Alcohol".equals(entry.getName())) {
                Log.d("DietDialogFragment","Wewnątrz if Alcohol");
                textUnit.setText("mg");
                textComparison.setText("Inna informacja dla Alcohol");
            } else if ("Green_vegetables".equals(entry.getName())) {
                Log.d("DietDialogFragment","Wewnątrz if Vegetable");
                textUnit.setText("μg");
                textComparison.setText("Inna informacja dla Green_vegetables");
            }
        }
    }
}
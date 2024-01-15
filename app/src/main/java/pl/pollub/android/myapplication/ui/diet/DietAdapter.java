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
                textComparison.setText(updateCaffeineComparisonText(entry.getAmount()));
            } else if ("Nicotine".equals(entry.getName())) {
                Log.d("DietDialogFragment","Wewnątrz if Nicotine");
                textUnit.setText("mg");
                textComparison.setText(updateNicotineComparisonText(entry.getAmount()));
            } else if ("Alcohol".equals(entry.getName())) {
                Log.d("DietDialogFragment","Wewnątrz if Alcohol");
                textUnit.setText("mg");
                textComparison.setText(updateAlcoholComparisonText(entry.getAmount()));
            } else if ("Green_vegetables".equals(entry.getName())) {
                Log.d("DietDialogFragment","Wewnątrz if Vegetable");
                textUnit.setText("μg");
                textComparison.setText(updateVegetableComparisonText(entry.getAmount()));
            }
        }
        private String updateCaffeineComparisonText(int selectedValue) {
            double coffeeAmount = selectedValue * 2.5;
            double espressoAmount = selectedValue / 2.0;
            double energyDrinkAmount = selectedValue * 3.3;

            String caffeineComparisonText = String.format("To w przybliżeniu %.0f ml kawy, %.0f ml espresso lub %.0f ml napoju energetycznego", coffeeAmount, espressoAmount, energyDrinkAmount);

            return caffeineComparisonText;
        }

        private String updateAlcoholComparisonText(int selectedValue) {
            double beerAmount = selectedValue * 20.0;
            double wineAmount = selectedValue * 8.3;
            double vodkaAmount = selectedValue * 2.5;

            String alcoholComparisonText = String.format("To w przybliżeniu %.0f ml piwa, %.0f ml wina lub %.0f ml wódki", beerAmount, wineAmount, vodkaAmount);

            return alcoholComparisonText;
        }

        private String updateNicotineComparisonText(int selectedValue) {
            double cigarettesAmount = selectedValue / 13.0;

            String nicotineComparisonText = String.format("To w przybliżeniu %.1f papierosy", cigarettesAmount);

            return nicotineComparisonText;
        }

        private String updateVegetableComparisonText(int selectedValue) {
            double spinachAmount = selectedValue / 5.0;
            double broccoliAmount = selectedValue / 2.0;
            double lettuceAmount = selectedValue * 1.0;

            String vegetableComparisonText = String.format("To w przybliżeniu %.0f g szpinaku, %.0f g brokułów lub %.0f g sałaty", spinachAmount, broccoliAmount, lettuceAmount);

            return vegetableComparisonText;
        }
    }
}
package pl.pollub.android.myapplication.ui.diet;

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
        holder.bind(newDiet);
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
        private TextView textCaffeineShow;
        private TextView textCaffeineValueShow;
        private TextView textCaffeineComparisonShow;
        private TextView textCaffeineUnitShow;
        private CardView cardViewCaffeineShow;

        private TextView textNicotineShow;
        private TextView textNicotineValueShow;
        private TextView textNicotineComparisonShow;
        private TextView textNicotineUnitShow;
        private CardView cardViewNicotineShow;
        private TextView textAlcoholShow;
        private TextView textAlcoholValueShow;
        private TextView textAlcoholComparisonShow;
        private TextView textAlcoholUnitShow;
        private CardView cardViewAlcoholShow;
        private TextView textVegetableShow;
        private TextView textVegetableValueShow;
        private TextView textVegetableComparisonShow;
        private TextView textVegetableUnitShow;
        private CardView cardViewVegetableShow;

        public DietViewHolder(@NonNull View itemView) {
            super(itemView);

            // Inicjalizacja pól z item_diet.xml
            textCaffeineShow = itemView.findViewById(R.id.textCaffeineShow);
            textCaffeineValueShow = itemView.findViewById(R.id.textCaffeineValueShow);
            textCaffeineUnitShow = itemView.findViewById(R.id.textCaffeineUnitShow);
            textCaffeineComparisonShow = itemView.findViewById(R.id.textCaffeineComparisonShow);
            cardViewCaffeineShow = itemView.findViewById(R.id.cardViewCaffeineShow);

            textNicotineShow = itemView.findViewById(R.id.textNicotineShow);
            textNicotineValueShow = itemView.findViewById(R.id.textNicotineValueShow);
            textNicotineUnitShow = itemView.findViewById(R.id.textNicotineUnitShow);
            textNicotineComparisonShow = itemView.findViewById(R.id.textNicotineComparisonShow);
            cardViewNicotineShow = itemView.findViewById(R.id.cardViewNicotineShow);

            textAlcoholShow = itemView.findViewById(R.id.textAlcoholShow);
            textAlcoholValueShow = itemView.findViewById(R.id.textAlcoholValueShow);
            textAlcoholUnitShow = itemView.findViewById(R.id.textAlcoholUnitShow);
            textAlcoholComparisonShow = itemView.findViewById(R.id.textAlcoholComparisonShow);
            cardViewAlcoholShow = itemView.findViewById(R.id.cardViewAlcoholShow);

            textVegetableShow = itemView.findViewById(R.id.textVegetableShow);
            textVegetableValueShow = itemView.findViewById(R.id.textVegetableValueShow);
            textVegetableUnitShow = itemView.findViewById(R.id.textVegetableUnitShow);
            textVegetableComparisonShow = itemView.findViewById(R.id.textVegetableComparisonShow);
            cardViewVegetableShow = itemView.findViewById(R.id.cardViewVegetableShow);

            textCaffeineShow.setText("Kofeina: ");
            textCaffeineUnitShow.setText("mg");

            textNicotineShow.setText("Nikotyna: ");
            textNicotineUnitShow.setText("mg");

            textAlcoholShow.setText("Alkohol: ");
            textAlcoholUnitShow.setText("mg");

            textVegetableShow.setText("Warzywa zielone: ");
            textVegetableUnitShow.setText("μg");
        }

        public void bind(NewDiet newDiet) {
            // Pobierz listę DietEntry z NewDiet
            //List<DietEntry> dietEntries = newDiet.getIntake_arr();
            // Pobierz DietEntry dla bieżącej pozycji
            //DietEntry entry = dietEntries.get(getAdapterPosition());
            if(newDiet.getIntake_arr()!= null) {
                for (DietEntry intakeEntry : newDiet.getIntake_arr()) {
                    String name = (String) intakeEntry.getName();
                    if ("Caffeine".equals(name)) {
                        textCaffeineValueShow.setText(String.valueOf(intakeEntry.getAmount()));
                        textCaffeineComparisonShow.setText("To w przybliżeniu 435 ml kawy, 100ml espresso lub 400ml napoju energatycznego");
                        cardViewCaffeineShow.setVisibility(View.VISIBLE);
                    } else {
                        // Ukryj widok, jeśli nie jest to Caffeine
                        cardViewCaffeineShow.setVisibility(View.GONE);
                    }
                    if ("Nicotine".equals(name)) {
                        textNicotineValueShow.setText(String.valueOf(intakeEntry.getAmount()));
                        textNicotineComparisonShow.setText("To w przybliżeniu 435 ml kawy, 100ml espresso lub 400ml napoju energatycznego");
                        cardViewNicotineShow.setVisibility(View.VISIBLE);
                    } else {
                        // Ukryj widok, jeśli nie jest to Caffeine
                        cardViewNicotineShow.setVisibility(View.GONE);
                    }
                    if ("Alcohol".equals(name)) {
                        textAlcoholValueShow.setText(String.valueOf(intakeEntry.getAmount()));
                        textAlcoholComparisonShow.setText("To w przybliżeniu 435 ml kawy, 100ml espresso lub 400ml napoju energatycznego");
                        cardViewAlcoholShow.setVisibility(View.VISIBLE);
                    } else {
                        // Ukryj widok, jeśli nie jest to Caffeine
                        cardViewAlcoholShow.setVisibility(View.GONE);
                    }
                    if ("Green_vegetables".equals(name)) {
                        textVegetableValueShow.setText(String.valueOf(intakeEntry.getAmount()));
                        textVegetableComparisonShow.setText("To w przybliżeniu 435 ml kawy, 100ml espresso lub 400ml napoju energatycznego");
                        cardViewVegetableShow.setVisibility(View.VISIBLE);
                    } else {
                        // Ukryj widok, jeśli nie jest to Caffeine
                        cardViewVegetableShow.setVisibility(View.GONE);
                    }
                }
            }
        }
    }
}
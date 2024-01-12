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

public class DietAdapter extends RecyclerView.Adapter<DietAdapter.ViewHolder> {

    private List<DietEntry> dietEntryList;

    public DietAdapter(List<DietEntry> dietEntryList) {
        this.dietEntryList = dietEntryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_diet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DietEntry dietEntry = dietEntryList.get(position);

        // Tutaj ustaw wartości w zależności od rodzaju wpisu dietetycznego
        String name = dietEntry.getName();
        int amount = dietEntry.getAmount();
        switch (name) {
            case "Caffeine":
                // Wypełnij pola dla kofeiny
                holder.textCaffeineValueShow.setText(String.valueOf(amount));
                holder.textCaffeineComparisonShow.setText("To w przybliżeniu ..."); // Dodaj odpowiedni tekst
                holder.cardViewCaffeineShow.setVisibility(View.VISIBLE);
                break;
            // Dodaj przypadki dla innych rodzajów wpisów dietetycznych, jeśli są potrzebne
        }
    }

    @Override
    public int getItemCount() {
        return dietEntryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Dodaj pola dla elementów interfejsu użytkownika z item_diet.xml
        TextView textCaffeineValueShow;
        TextView textCaffeineComparisonShow;
        CardView cardViewCaffeineShow;

        // Dodaj inne pola według potrzeb

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Znajdź elementy interfejsu użytkownika z item_diet.xml
            textCaffeineValueShow = itemView.findViewById(R.id.textCaffeineValueShow);
            textCaffeineComparisonShow = itemView.findViewById(R.id.textCaffeineComparisonShow);
            cardViewCaffeineShow = itemView.findViewById(R.id.cardViewCaffeineShow);

            // Znajdź inne elementy interfejsu użytkownika i przypisz do pól
        }
    }
}
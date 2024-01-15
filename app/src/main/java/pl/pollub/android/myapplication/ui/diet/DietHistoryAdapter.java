package pl.pollub.android.myapplication.ui.diet;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import pl.pollub.android.myapplication.R;

public class DietHistoryAdapter extends RecyclerView.Adapter<DietHistoryAdapter.ViewHolder> {

    private List<NewDiet> dietList;
    private Context context;
    private boolean Caffeine = false, Nicotine = false, Alcohol = false, Vegetables = false;

    public DietHistoryAdapter(Context context, List<NewDiet> dietList) {
        this.context = context;
        this.dietList = dietList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_diet_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NewDiet newDiet = dietList.get(position);
        holder.bind(newDiet);
    }

    @Override
    public int getItemCount() {
        return dietList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        private TextView textViewHistoryDate;
        private LinearLayout linearLayoutCaffeine, linearLayoutNicotine, linearLayoutAlcohol, linearLayoutVegetables;
        private TextView textCaffeineHistory, textNicotineHistory, textAlcoholHistory, textVegetableHistory;
        private TextView textCaffeineValueHistory, textNicotineValueHistory, textAlcoholValueHistory, textVegetableValueHistory;
        private TextView textCaffeineUnitHistory, textNicotineUnitHistory, textAlcoholUnitHistory, textVegetableUnitHistory;
        private View divider1, divider2, divider3, divider4;
        private  Button entryDeleteButton;




        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewHistory);
            textViewHistoryDate = itemView.findViewById(R.id.textViewHistoryDate);
            linearLayoutCaffeine = itemView.findViewById(R.id.linearLayoutCaffeine);
            linearLayoutNicotine = itemView.findViewById(R.id.linearLayoutNicotine);
            linearLayoutAlcohol = itemView.findViewById(R.id.linearLayoutAlcohol);
            linearLayoutVegetables = itemView.findViewById(R.id.linearLayoutVegetables);

            textCaffeineHistory = itemView.findViewById(R.id.textCaffeineHistory);
            textNicotineHistory = itemView.findViewById(R.id.textNicotineHistory);
            textAlcoholHistory = itemView.findViewById(R.id.textAlcoholHistory);
            textVegetableHistory = itemView.findViewById(R.id.textVegetableHistory);

            textCaffeineValueHistory = itemView.findViewById(R.id.textCaffeineValueHistory);
            textNicotineValueHistory = itemView.findViewById(R.id.textNicotineValueHistory);
            textAlcoholValueHistory = itemView.findViewById(R.id.textAlcoholValueHistory);
            textVegetableValueHistory = itemView.findViewById(R.id.textVegetableValueHistory);

            textCaffeineUnitHistory = itemView.findViewById(R.id.textCaffeineUnitHistory);
            textNicotineUnitHistory = itemView.findViewById(R.id.textNicotineUnitHistory);
            textAlcoholUnitHistory = itemView.findViewById(R.id.textAlcoholUnitHistory);
            textVegetableUnitHistory = itemView.findViewById(R.id.textVegetableUnitHistory);

            divider1 = itemView.findViewById(R.id.divider1);
            divider2 = itemView.findViewById(R.id.divider2);
            divider3 = itemView.findViewById(R.id.divider3);
            divider4 = itemView.findViewById(R.id.divider4);

            entryDeleteButton = itemView.findViewById(R.id.btnDeleteHistory);
        }

        public void bind(NewDiet newDiet) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            String formattedDate = sdf.format(newDiet.getDay().toDate());
            textViewHistoryDate.setText(formattedDate);

            // Poniżej kod, aby sprawdzić, czy CardView jest widoczny
            if (cardView.getVisibility() == View.GONE) {
                cardView.setVisibility(View.VISIBLE);
            }

            // Wyczyść widoki, które mogą być widoczne z poprzednich wiązań
            linearLayoutCaffeine.setVisibility(View.GONE);
            linearLayoutNicotine.setVisibility(View.GONE);
            linearLayoutAlcohol.setVisibility(View.GONE);
            linearLayoutVegetables.setVisibility(View.GONE);

            List<DietEntry> dietEntries = newDiet.getIntake_arr();

            // Iteruj przez wszystkie elementy listy dietEntries
            for (DietEntry entry : dietEntries) {
                // Ustaw jednostki i wartości w zależności od nazwy
                setVisibilityBasedOnName(entry.getName());

                // Ustaw widoczność pól w zależności od nazwy
                if ("Caffeine".equals(entry.getName())) {
                    if (Caffeine){
                        linearLayoutCaffeine.setVisibility(View.VISIBLE);
                        textCaffeineHistory.setText("Kofeina: ");
                        textCaffeineValueHistory.setText(String.valueOf(entry.getAmount()));
                        textCaffeineUnitHistory.setText("mg");
                    }
                } else if ("Nicotine".equals(entry.getName())) {
                    if (Nicotine){
                        linearLayoutNicotine.setVisibility(View.VISIBLE);
                        textNicotineHistory.setText("Nikotyna: ");
                        textNicotineValueHistory.setText(String.valueOf(entry.getAmount()));
                        textNicotineUnitHistory.setText("mg");
                    }
                } else if ("Alcohol".equals(entry.getName())) {
                    if (Alcohol){
                        linearLayoutAlcohol.setVisibility(View.VISIBLE);
                        textAlcoholHistory.setText("Alkohol: ");
                        textAlcoholValueHistory.setText(String.valueOf(entry.getAmount()));
                        textAlcoholUnitHistory.setText("mg");
                    }

                } else if ("Green_vegetables".equals(entry.getName())) {
                    if (Vegetables){
                        linearLayoutVegetables.setVisibility(View.VISIBLE);
                        textVegetableHistory.setText("Warzywa zielone: ");
                        textVegetableValueHistory.setText(String.valueOf(entry.getAmount()));
                        textVegetableUnitHistory.setText("mg");
                    }
                }
            }

            // Ustaw listener do przycisku usuwania
            entryDeleteButton.setOnClickListener(v -> {
                // Obsługa usunięcia wpisu z historii

                // Wyświetlenie alertu z pytaniem czy na pewno chcesz usunąć historię spożycia
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Usuń historię spożycia")
                        .setMessage("Czy na pewno chcesz usunąć historię spożycia z dnia " + formattedDate + "?")
                        .setPositiveButton("Tak", (dialog, which) -> {
                            // Usuwanie dokumentu z bazy danych
                            deleteDietEntry(newDiet.getDocumentId());
                            // Usunięcie pozycji z listy i poinformowanie adaptera o zmianach
                            dietList.remove(newDiet);
                            notifyDataSetChanged();
                        })
                        .setNegativeButton("Anuluj", (dialog, which) -> {
                            // Anulowanie operacji usuwania
                            dialog.dismiss();
                        })
                        .show();
            });
        }


        private void setVisibilityBasedOnName(String name) {
            // Ustaw widoczność pól w zależności od nazwy
            if ("Caffeine".equals(name)) {
                Caffeine = true;
            }else if ("Nicotine".equals(name)) {
                Nicotine = true;
            } else if ("Alcohol".equals(name)) {
                Alcohol = true;
            } else if ("Green_vegetables".equals(name)) {
                Vegetables = true;
            }
        }


        private void deleteDietEntry(String documentId) {
            // Usunięcie dokumentu z bazy danych
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(userId)
                    .collection("Intakes")  // Zmiana na kolekcję Intakes
                    .document(documentId)
                    .delete()
                    .addOnSuccessListener(aVoid -> Log.d("DietHistoryAdapter", "Usunięto dokument z historii spożycia"))
                    .addOnFailureListener(e -> Log.e("DietHistoryAdapter", "Błąd podczas usuwania dokumentu z historii spożycia", e));
        }
    }
}

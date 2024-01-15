package pl.pollub.android.myapplication.ui.medications;

import android.app.AlertDialog;
import android.content.Context;
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

public class SymptomHistoryAdapter extends RecyclerView.Adapter<SymptomHistoryAdapter.ViewHolder> {

    private List<NewSymptom> symptomList;
    private Context context;

    public SymptomHistoryAdapter(Context context, List<NewSymptom> symptomList) {
        this.context = context;
        this.symptomList = symptomList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_symptom_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NewSymptom newSymptom = symptomList.get(position);
        holder.bind(newSymptom);
    }

    @Override
    public int getItemCount() {
        return symptomList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView textViewHistoryDate;
        LinearLayout linearLayoutHeadache, linearLayoutBleeding, linearLayoutBruises,
                linearLayoutChestPain, linearLayoutLegSwelling, linearLayoutPalpitations,
                linearLayoutDizzyness, linearLayoutHotFlush, linearLayoutCoughing;
        TextView textHeadacheHistory, textBleedingHistory, textBruisesHistory,
                textChestPainHistory, textLegSwellingHistory, textPalpitationsHistory,
                textDizzynessHistory, textHotFlushHistory, textCoughingHistory;
        View divider0, divider1, divider2, divider3, divider4, divider5, divider6, divider7, divider8;
        Button btnDeleteHistory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardViewHistory);
            textViewHistoryDate = itemView.findViewById(R.id.textViewHistoryDate);
            linearLayoutHeadache = itemView.findViewById(R.id.linearLayoutHeadache);
            linearLayoutBleeding = itemView.findViewById(R.id.linearLayoutBleeding);
            linearLayoutBruises = itemView.findViewById(R.id.linearLayoutBruises);
            linearLayoutChestPain = itemView.findViewById(R.id.linearLayoutChestPain);
            linearLayoutLegSwelling = itemView.findViewById(R.id.linearLayoutLegSwelling);
            linearLayoutPalpitations = itemView.findViewById(R.id.linearLayoutPalpitations);
            linearLayoutDizzyness = itemView.findViewById(R.id.linearLayoutDizzyness);
            linearLayoutHotFlush = itemView.findViewById(R.id.linearLayoutHotFlush);
            linearLayoutCoughing = itemView.findViewById(R.id.linearLayoutCoughing);

            textHeadacheHistory = itemView.findViewById(R.id.textHeadacheHistory);
            textBleedingHistory = itemView.findViewById(R.id.textBleedingHistory);
            textBruisesHistory = itemView.findViewById(R.id.textBruisesHistory);
            textChestPainHistory = itemView.findViewById(R.id.textChestPainHistory);
            textLegSwellingHistory = itemView.findViewById(R.id.textLegSwellingHistory);
            textPalpitationsHistory = itemView.findViewById(R.id.textPalpitationsHistory);
            textDizzynessHistory = itemView.findViewById(R.id.textDizzynessHistory);
            textHotFlushHistory = itemView.findViewById(R.id.textHotFlushHistory);
            textCoughingHistory = itemView.findViewById(R.id.textCoughingHistory);

            divider0 = itemView.findViewById(R.id.divider0);
            divider1 = itemView.findViewById(R.id.divider1);
            divider2 = itemView.findViewById(R.id.divider2);
            divider3 = itemView.findViewById(R.id.divider3);
            divider4 = itemView.findViewById(R.id.divider4);
            divider5 = itemView.findViewById(R.id.divider5);
            divider6 = itemView.findViewById(R.id.divider6);
            divider7 = itemView.findViewById(R.id.divider7);
            divider8 = itemView.findViewById(R.id.divider8);

            btnDeleteHistory = itemView.findViewById(R.id.btnDeleteHistory);
        }

        public void bind(NewSymptom newSymptom) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            String formattedDate = sdf.format(newSymptom.getDay().toDate());
            textViewHistoryDate.setText(formattedDate);

            // Poniżej kod, aby sprawdzić, czy CardView jest widoczny
            if (cardView.getVisibility() == View.GONE) {
                cardView.setVisibility(View.VISIBLE);
            }

            // Wyczyść widoki, które mogą być widoczne z poprzednich wiązań
            linearLayoutHeadache.setVisibility(View.GONE);
            linearLayoutBleeding.setVisibility(View.GONE);
            linearLayoutBruises.setVisibility(View.GONE);
            linearLayoutChestPain.setVisibility(View.GONE);
            linearLayoutLegSwelling.setVisibility(View.GONE);
            linearLayoutPalpitations.setVisibility(View.GONE);
            linearLayoutDizzyness.setVisibility(View.GONE);
            linearLayoutHotFlush.setVisibility(View.GONE);
            linearLayoutCoughing.setVisibility(View.GONE);

            // Iteruj przez wszystkie elementy listy symptom_arr
            for (String symptom : newSymptom.getSymptom_arr()) {
                // Ustaw widoczność pól w zależności od objawu
                setVisibilityBasedOnSymptom(symptom);
            }

            // Ustaw listener do przycisku usuwania
            btnDeleteHistory.setOnClickListener(v -> {
                // Obsługa usunięcia wpisu z historii

                // Wyświetlenie alertu z pytaniem czy na pewno chcesz usunąć historię objawów
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Usuń historię objawów")
                        .setMessage("Czy na pewno chcesz usunąć historię objawów z dnia " + formattedDate + "?")
                        .setPositiveButton("Tak", (dialog, which) -> {
                            // Usuwanie dokumentu z bazy danych
                            deleteSymptomEntry(newSymptom.getDocumentId());
                            // Usunięcie pozycji z listy i poinformowanie adaptera o zmianach
                            symptomList.remove(newSymptom);
                            notifyDataSetChanged();
                        })
                        .setNegativeButton("Anuluj", (dialog, which) -> {
                            // Anulowanie operacji usuwania
                            dialog.dismiss();
                        })
                        .show();
            });
        }

        private void setVisibilityBasedOnSymptom(String symptom) {
            // Ustaw widoczność pól w zależności od objawu
            switch (symptom) {
                case "Headache":
                    linearLayoutHeadache.setVisibility(View.VISIBLE);
                    textHeadacheHistory.setText("Ból głowy");
                    break;
                case "Bleeding":
                    linearLayoutBleeding.setVisibility(View.VISIBLE);
                    textBleedingHistory.setText("Krwawienie z nieznanej przyczyny");
                    break;
                case "Bruises":
                    linearLayoutBruises.setVisibility(View.VISIBLE);
                    textBruisesHistory.setText("Siniaki z nieznanej przyczyny");
                    break;
                case "Chest_Pain":
                    linearLayoutChestPain.setVisibility(View.VISIBLE);
                    textChestPainHistory.setText("Ból w klatce piersiowej");
                    break;
                case "Leg_Swelling":
                    linearLayoutLegSwelling.setVisibility(View.VISIBLE);
                    textLegSwellingHistory.setText("Opuchlizna nóg");
                    break;
                case "Palpitations":
                    linearLayoutPalpitations.setVisibility(View.VISIBLE);
                    textPalpitationsHistory.setText("Kołatanie serca");
                    break;
                case "Dizzyness":
                    linearLayoutDizzyness.setVisibility(View.VISIBLE);
                    textDizzynessHistory.setText("Zawroty głowy");
                    break;
                case "Hot_Flush":
                    linearLayoutHotFlush.setVisibility(View.VISIBLE);
                    textHotFlushHistory.setText("Uderzenie gorąca");
                    break;
                case "Coughing":
                    linearLayoutCoughing.setVisibility(View.VISIBLE);
                    textCoughingHistory.setText("Kaszel");
                    break;
            }
        }

        private void deleteSymptomEntry(String documentId) {
            // Usuń dokument z bazy danych
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(userId)
                    .collection("Symptoms")
                    .document(documentId)
                    .delete()
                    .addOnSuccessListener(aVoid -> Log.d("SymptomHistoryAdapter", "Dokument usunięty z bazy danych"))
                    .addOnFailureListener(e -> Log.e("SymptomHistoryAdapter", "Błąd podczas usuwania dokumentu z bazy danych", e));
        }
    }
}

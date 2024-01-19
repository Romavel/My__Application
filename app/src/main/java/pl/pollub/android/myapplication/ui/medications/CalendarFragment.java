package pl.pollub.android.myapplication.ui.medications;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import pl.pollub.android.myapplication.R;
import pl.pollub.android.myapplication.User;

public class CalendarFragment extends Fragment implements CalendarAdapter.OnItemListener {

    private TextView monthYearText;

    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;
    private String userId; // Identyfikator użytkownika
    CardView calendarCardView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        initWidgets(view);
        selectedDate = LocalDate.now();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Pobierz identyfikator użytkownika
        setMonthView();
        getMedicationData();
        return view;
    }

    private void initWidgets(View view) {
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        monthYearText = view.findViewById(R.id.monthYearTV);
    }

    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    private ArrayList<String> daysInMonthArray(LocalDate date) {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for (int i = 1; i <= 42; i++) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add("");
            } else {
                daysInMonthArray.add(String.valueOf(i - dayOfWeek));
            }
        }
        return daysInMonthArray;
    }

    private String monthYearFromDate(LocalDate date) {
        return date.getMonth().toString() + " " + date.getYear();
    }

    @Override
    public void onItemClick(int position, String dayText) {
        if (!dayText.equals("")) {
            String message = "Selected Date " + dayText + " " + monthYearFromDate(selectedDate);
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    private void getMedicationData() {
        getMedicationField(userId, new OnMedicationFieldCallback() {
            @Override
            public void onMedicationFieldReceived(String medicationName) {
                getMedicationDocumentId(medicationName, new OnDocumentIdCallback() {
                    @Override
                    public void onDocumentIdReceived(String medicationDocumentId) {
                        getMedicineTakenList(userId, medicationDocumentId);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getMedicineTakenList(String userId, String medicationDocumentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users")
                .document(userId)
                .collection("Medications")
                .document(medicationDocumentId)
                .collection("MedicineTaken")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<DocumentSnapshot> medicineTakenDocuments = task.getResult().getDocuments();
                        processMedicineTakenList(medicineTakenDocuments);
                    } else {
                        Toast.makeText(requireContext(), "Error fetching medication data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void processMedicineTakenList(List<DocumentSnapshot> medicineTakenDocuments) {
        for (DocumentSnapshot document : medicineTakenDocuments) {
            MedicineTaken medicineTaken = document.toObject(MedicineTaken.class);
            if (medicineTaken != null) {
                LocalDate date = medicineTaken.getDate().toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                int dayOfMonth = date.getDayOfMonth();
                boolean isTaken = medicineTaken.getIsTaken();
                updateCalendarCell(dayOfMonth, isTaken);
            }
        }
    }

    private void updateCalendarCell(int dayOfMonth, boolean isTaken) {
        View calendarCell = findCalendarCell(dayOfMonth);
        if (calendarCell != null) {
            CardView calendarCardView = calendarCell.findViewById(R.id.calendarCardView);
            int color = isTaken ? Color.GREEN : Color.RED;
            calendarCardView.setCardBackgroundColor(color);
        }
    }


    private View findCalendarCell(int dayOfMonth) {
        RecyclerView.ViewHolder viewHolder = calendarRecyclerView.findViewHolderForAdapterPosition(dayOfMonth - 1);
        if (viewHolder != null) {
            return viewHolder.itemView;
        }
        return null;
    }

    // Pozostała część kodu funkcji CalendarFragment

    private void getMedicationField(String userId, final OnMedicationFieldCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Pobierz dokument użytkownika
        db.collection("Users")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        User user = task.getResult().toObject(User.class);
                        if (user != null) {
                            // Pobierz wartość z pola medication
                            String medication = user.getMedication();
                            callback.onMedicationFieldReceived(medication);
                        } else {
                            callback.onFailure("Nie można uzyskać danych użytkownika");
                        }
                    } else {
                        callback.onFailure("Błąd podczas pobierania danych użytkownika");
                    }
                });
    }

    // Interfejs do obsługi wyniku
    public interface OnMedicationFieldCallback {
        void onMedicationFieldReceived(String medication);
        void onFailure(String errorMessage);
    }

    private void getMedicationDocumentId(String name, final OnDocumentIdCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Log.d("CalendarFragment", "Medication name: "+ name+"G");
        // Sprawdź, czy istnieje lek o danej nazwie w kolekcji Medication
        db.collection("Medication")
                .whereEqualTo("name", name)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        if (!task.getResult().getDocuments().isEmpty()) {
                            // Pobierz documentId pierwszego dokumentu pasującego do zapytania
                            String documentId = task.getResult().getDocuments().get(0).getId();
                            callback.onDocumentIdReceived(documentId);
                        } else {
                            callback.onFailure("Nie znaleziono leku o nazwie: " + name);
                        }
                    } else {
                        callback.onFailure("Błąd podczas pobierania danych o leku");
                    }
                });
    }

    // Interfejs do obsługi wyniku
    public interface OnDocumentIdCallback {
        void onDocumentIdReceived(String documentId);
        void onFailure(String errorMessage);
    }
}


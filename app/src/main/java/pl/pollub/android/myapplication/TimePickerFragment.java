package pl.pollub.android.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Locale;


public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private OnTimeSetListener listener;

    public interface OnTimeSetListener {
        void onTimeSet(String formattedTime);
    }

    // Metoda do ustawienia nasłuchiwacza
    public void setOnTimeSetListener(OnTimeSetListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Ustawienia czasu na aktualny czas
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Utwórz nowy obiekt TimePickerDialog i zwróć go
        return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Formatowanie czasu do stringa w formacie "hh:mm"
        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);

        // Przekazanie sformatowanego czasu do nasłuchiwacza
        if (listener != null) {
            listener.onTimeSet(formattedTime);
        }
    }
}
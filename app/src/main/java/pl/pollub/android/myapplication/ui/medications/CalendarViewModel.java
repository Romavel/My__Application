package pl.pollub.android.myapplication.ui.medications;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class CalendarViewModel extends ViewModel {

    private final MutableLiveData<Boolean> isDataLoaded = new MutableLiveData<>();

    public LiveData<Boolean> getIsDataLoaded() {
        return isDataLoaded;
    }

    public void setDataLoaded(boolean loaded) {
        isDataLoaded.setValue(loaded);
    }
}

package pl.pollub.android.myapplication.ui.measurements;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MeasurementsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MeasurementsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Pomiary fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}

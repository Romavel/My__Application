package pl.pollub.android.myapplication.ui.medications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MedicationsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MedicationsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Leki fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}

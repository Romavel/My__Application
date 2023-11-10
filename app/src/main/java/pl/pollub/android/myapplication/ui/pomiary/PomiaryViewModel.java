package pl.pollub.android.myapplication.ui.pomiary;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PomiaryViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public PomiaryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Pomiary fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}

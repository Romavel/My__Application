package pl.pollub.android.myapplication.ui.leki;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LekiViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public LekiViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Leki fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}

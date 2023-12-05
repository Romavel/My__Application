package pl.pollub.android.myapplication.ui.diet;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DietViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public DietViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Dieta fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}

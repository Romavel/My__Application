package pl.pollub.android.myapplication.ui.dieta;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DietaViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public DietaViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Dieta fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}

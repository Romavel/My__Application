package pl.pollub.android.myapplication.ui.profil;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfilViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ProfilViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Profil fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}

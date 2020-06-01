package org.saki.doridori.ui.slideshow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.saki.doridori.database.Card;
import org.saki.doridori.database.CardDaoAsync;

public class SlideshowViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private LiveData<Card> a_card_info;

    public SlideshowViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
        //a_card_info = CardDaoAsync.FindCardbyId()
    }

    public LiveData<String> getText() {
        return mText;
    }
    //public LiveData<Card> getA_card_info(int theID){    }
}
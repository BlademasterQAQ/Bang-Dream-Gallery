package org.saki.doridori.ui.gallery;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.saki.doridori.database.Card;
import org.saki.doridori.database.CardDao;
import org.saki.doridori.database.MainDatabase;

import java.util.List;

public class GalleryViewModel extends AndroidViewModel {

    private MutableLiveData<String> mText;
    private LiveData<List<Card>> cardsLiveData;
    private static MutableLiveData<List<Card>> searchData = new MutableLiveData<>();
    private static MutableLiveData<Integer> searchDataCount = new MutableLiveData<>(0);
    private static CardDao cardDao;


    public GalleryViewModel(@NonNull Application application) {
        super(application);
        //mText = new MutableLiveData<>();
        //mText.setValue("This is gallery fragment");
        cardDao = MainDatabase.getDatabase(application).cardDao();
        cardsLiveData = cardDao.getAllCards();
        searchData.setValue(cardsLiveData.getValue()); // 用於第一次啓動
    }

    MutableLiveData<List<Card>> getSearchData(){return searchData;}

    MutableLiveData<Integer> getSearchDataCount(){return searchDataCount;}


    LiveData<List<Card>> getAllCards(){return cardsLiveData;}

    void applyFilter(FilterCondition condition){
        new SearchCardTask().execute(condition);
    }

    private static class SearchCardTask extends AsyncTask<FilterCondition, Void, List<Card>> {

        @Override
        protected List<Card> doInBackground(FilterCondition... condition) {
            return cardDao.getFiltedCards(condition[0].star, condition[0].characterID, condition[0].attribute);
        }

        @Override
        protected void onPostExecute(List<Card> cardResult) {
            super.onPostExecute(cardResult);
            searchData.setValue(cardResult); //change LiveData value
            searchDataCount.setValue(cardResult.size());
        }
    }
}
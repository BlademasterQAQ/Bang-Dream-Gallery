package org.saki.doridori.database;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.concurrent.ExecutionException;

public class CardDaoAsync {
    public static void Insert(final Card entity, final CardDao theDao) {
        //Log.d("async","create task");
        new InsertAsyncTask(theDao).execute(entity);
    }

    private static class InsertAsyncTask extends AsyncTask<Card, Void, Void> {

        private CardDao cardDao;

        InsertAsyncTask(CardDao cardDao) {
            this.cardDao = cardDao;
            Log.d("async","insert task created");
        }

        @Override
        protected Void doInBackground(Card... cards) {

            cardDao.insert(cards[0]);
            Log.d("async","insert run ok");
            return null;
        }
    }

    public static void DelAll(final CardDao theDao) {
        //Log.d("async","create task");
        new DelAllAsyncTask(theDao).execute();
    }

    private static class DelAllAsyncTask extends AsyncTask<Card, Void, Void> {

        private CardDao cardDao;

        DelAllAsyncTask(CardDao cardDao) {
            this.cardDao = cardDao;
            Log.d("async","delall task created");
        }

        @Override
        protected Void doInBackground(Card... cards) {

            cardDao.delAll();
            Log.d("async","delall run ok");
            return null;
        }
    }

/*    public static void FindCardbyId(final int cardId, final CardDao theDao) throws ExecutionException, InterruptedException {
        Log.d("async","findcardbyid task created, target is "+cardId);
        LiveData<Card> result =  new FindCardbyIdAsyncTask(theDao).execute(cardId).get();
        //Log.d("async","findcardbyid task ok "+result.getValue().resourceSetName);
        //return result;
    }
    private static class FindCardbyIdAsyncTask extends AsyncTask<Integer, Void, LiveData<Card>> {
        private CardDao cardDao;

        FindCardbyIdAsyncTask(CardDao cardDao) {
            this.cardDao = cardDao;
        }

        @Override
        protected LiveData<Card> doInBackground(Integer... integers) {
            return cardDao.findCardById(integers[0]);
        }

        protected void onPostExecute(LiveData<Card> result){
            Log.d("async","findcardbyid task ok "+result.getValue().resourceSetName);
            //HomeFragment.mainCallBack.CallbackQuery("CARD_DATABASE_UPDATED", null);
        }

    }*/
    /*public static void FindCardbyId(final int cardId, final CardDao theDao) throws ExecutionException, InterruptedException {
    Log.d("async","findcardbyid task created, target is "+cardId);
    new FindCardbyIdAsyncTask(theDao).execute(cardId);
    //Log.d("async","findcardbyid task ok "+result.getValue().resourceSetName);
    //return result;
}
    private static class FindCardbyIdAsyncTask extends AsyncTask<Integer, Void, Card> {
        private CardDao cardDao;

        FindCardbyIdAsyncTask(CardDao cardDao) {
            this.cardDao = cardDao;
        }

        @Override
        protected Card doInBackground(Integer... integers) {
            return cardDao.testfindCardById(integers[0]);
        }

        protected void onPostExecute(Card result){
            Log.d("async","findcardbyid task ok "+result.resourceSetName);
            //HomeFragment.mainCallBack.CallbackQuery("CARD_DATABASE_UPDATED", null);
        }

    }*/
}

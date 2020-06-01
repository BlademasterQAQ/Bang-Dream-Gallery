package org.saki.doridori.database.operator;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.saki.doridori.database.Card;
import org.saki.doridori.database.CardDao;
import org.saki.doridori.ui.home.HomeFragment;

import java.util.Iterator;

public class rebuildCardDatabase {
    private static CardDao cardDao;
    private static String jsonToParse;
    public static void start(CardDao the_dao, String the_json){
        cardDao=the_dao;
        jsonToParse=the_json;
        new RebuildAsyncTask().execute();
    }
    private static class RebuildAsyncTask extends AsyncTask<Void, Void, Boolean> {

       // private CardDao cardDao;

        @Override
        protected Boolean doInBackground(Void... voids) {
            Log.d("async","rebuild started");
            cardDao.delAll();
            try {
                parseAndWrite();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Boolean result){
            Log.d("async","card database rebuilt");
            HomeFragment.mainCallBack.CallbackQuery("CARD_DATABASE_UPDATED", null);
        }
    }
    private static void parseAndWrite() throws JSONException {
        JSONObject wholeJson = new JSONObject(jsonToParse);
        Iterator<String> iterator = wholeJson.keys();
        while (iterator.hasNext()){
            // 獲得 key
            String cardId = iterator.next(); // 寫入資料庫時候需要 toInt
            String cardValue = wholeJson.getString(cardId);
            JSONObject oneCard = new JSONObject(cardValue);

            int characterId = oneCard.getInt("characterId");
            int rarity = oneCard.getInt("rarity");
            String attribute = oneCard.getString("attribute");
            String resourceSetName = oneCard.getString("resourceSetName");


            cardDao.insert(new Card(Integer.parseInt(cardId),characterId,rarity,attribute,resourceSetName));
        }
    }
}

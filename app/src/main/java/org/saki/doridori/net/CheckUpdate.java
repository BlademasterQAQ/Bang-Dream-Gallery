package org.saki.doridori.net;

import android.os.AsyncTask;
import android.util.Log;

import org.saki.doridori.ui.home.HomeViewModel;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class CheckUpdate {
    public static void AsyncCheckUpdate(String the_url){
        Log.d("CHECK_UPDATE_START","creating task");
        //theContext = context;
        //String the_url = theContext.getString(R.string.card_url);
        // final String url_string = the_context.getString(R.string.card_url); //read string in XML

        new CheckUpdateTask().execute(the_url);
    }
    private static class CheckUpdateTask extends AsyncTask<String, Void, Long> {
        @Override
        protected Long doInBackground(String... strings) {
            HttpURLConnection conn;
            try {
                Log.d("HTTP", "http start");

                URL url = new URL(strings[0]);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("HEAD");
                conn.setUseCaches(false);
                conn.setConnectTimeout(1000 * 5);
                conn.connect();
                int status = conn.getResponseCode();
                Log.d("HTTP", String.valueOf(status));
                if(status == HttpURLConnection.HTTP_OK){
                    //String header = conn.getHeaderField("last-modified");
                    //Log.d("HTTP", "last modified = " + header);
                    //Date modified = new Date(conn.getLastModified());
                    //String dd = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(modified);
                    return conn.getLastModified();
                }
            } catch (IOException e) {
                e.printStackTrace();
                //HomeFragment.mainCallBack.CallbackQuery("CHECK_UPDATE_FAILED", null);
                return null;
            }
            return null;
        }

        protected void onPostExecute(Long result) {
            //String[] results = new String[1];
            //results[0] = result;
            //HomeFragment.mainCallBack.CallbackQuery("CHECK_UPDATE_RESULT", results);
            if(result == null) HomeViewModel.oncheckingUpdateFailed();
            else {
                HomeViewModel.setCloudVersion(result);
            }

        }
    }
}

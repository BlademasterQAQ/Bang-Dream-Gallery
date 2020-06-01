package org.saki.doridori.net;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


import org.saki.doridori.R;
import org.saki.doridori.ui.home.HomeFragment;
import org.saki.doridori.ui.home.HomeViewModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class DownloadJson {
// 該 class 用於建立

    //private static Context theContext;

    public static void AsyncCatchData(String the_url){
        Log.d("JSON_DOWNLOAD_START","creating task");
        //theContext = context;
        //String the_url = theContext.getString(R.string.card_url);
        // final String url_string = the_context.getString(R.string.card_url); //read string in XML

        new DownloadJsonTask().execute(the_url);
    }
    private static class DownloadJsonTask extends AsyncTask<String, Void, String[]>{
        @Override
        protected String[] doInBackground(String... strings) {
            try{
                Log.d("async","download starting");
                URL the_url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) the_url.openConnection();
                // create connection object
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                // set network failed time
                InputStream the_stream = connection.getInputStream();
                BufferedReader the_reader = new BufferedReader(new InputStreamReader(the_stream));
                String one_line = the_reader.readLine();
                StringBuilder json = new StringBuilder();
                while (one_line != null) {
                    json.append(one_line);
                    one_line = the_reader.readLine();
                }
                Log.d("JSON_OK","dl ok");

                String[] result = new String[2];
                result[0] = json.toString();
                if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    result[1] = String.valueOf(connection.getLastModified());
                }else result[1] = null;
                return result;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(String[] result) {
            if (result != null) {
                Log.d("ASYNC", "" + result[0]);
                String[] the_json = new String[2];
                the_json[0] = result[0];
                if(result[1] != null)the_json[1] = result[1];
                HomeFragment.mainCallBack.CallbackQuery("JSON_DOWNLOAD_RESULT", the_json);
            }
            else {
                Log.d("ASYNC", "Download json failed" );
                HomeFragment.mainCallBack.CallbackQuery("JSON_DOWNLOAD_RESULT", null);
            }
        }
    }
}

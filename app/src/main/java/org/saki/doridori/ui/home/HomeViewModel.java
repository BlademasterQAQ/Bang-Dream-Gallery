package org.saki.doridori.ui.home;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import org.saki.doridori.R;
import org.saki.doridori.database.Card;
import org.saki.doridori.database.CardDao;
import org.saki.doridori.database.MainDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeViewModel extends AndroidViewModel {

    private static MutableLiveData<Integer[]> mDatabaseAmount;
    private LiveData<Integer> DatabaseAmount;
    private static Application application;
    private static boolean firstStart = true;
    private static Date cloudversion;
    private static MutableLiveData<String> cloudVersion = null;
    private static Date localversion;
    private static MutableLiveData<String> localVersion = null;
    private static SharedPreferences sharepre;
    private static SharedPreferences.Editor editor;
    private static boolean iffail = false;

    enum status{
        OK,
        CHECKING,
        NEEDUPDATE,
        ERROR
    }
    private static MutableLiveData<status> mstatus = null;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        //mText = new MutableLiveData<>();
        //mText.setValue("This is home fragment");
        CardDao cardDao = MainDatabase.getDatabase(application).cardDao();

        Integer[] mAmount = new Integer[5];
        mAmount[0] = cardDao.getCardCount();
        mAmount[1] = cardDao.getstar1Count();
        mAmount[2] = cardDao.getstar2Count();
        mAmount[3] = cardDao.getstar3Count();
        mAmount[4] = cardDao.getstar4Count();
        mDatabaseAmount = new MutableLiveData<>();
        mDatabaseAmount.setValue(mAmount);

        if(cloudVersion == null){
            cloudVersion = new MutableLiveData<>();
            cloudVersion.setValue("");
        }

        sharepre = application.getSharedPreferences("DEFAULT", Context.MODE_PRIVATE);
        Long intdate = sharepre.getLong("LOCAL_VERSION", 0);
        localversion = new Date(intdate);

        if(localVersion == null){
            localVersion = new MutableLiveData<>();
            if(intdate == 0)localVersion.setValue("大腦處於降級狀態");
            else localVersion.setValue(new SimpleDateFormat("yyyy/MM/dd HH:mm").format(localversion));
        }

        if (mstatus == null){
            mstatus = new MutableLiveData<>();
            mstatus.setValue(HomeViewModel.status.OK);
        }

    }

    public static void oncheckingUpdate(){
        cloudVersion.setValue(application.getString(R.string.Checking_Update));
        mstatus.setValue(status.CHECKING);
        iffail = false;
    }

    public static void oncheckingUpdateFailed(){
        cloudVersion.setValue(application.getString(R.string.Checking_Update_failed));
        mstatus.setValue(status.ERROR);
        iffail = true;
    }

    public static void setCloudVersion(long version) {
        Date mdate = new Date(version);
        String dd = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(mdate);
        cloudversion = mdate;
        cloudVersion.setValue(dd);
    }

    public static void setLocalversion(long version) {
        Date mdate = new Date(version);
        String dd = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(mdate);
        localversion = mdate;
        localVersion.postValue(dd);
        editor = sharepre.edit();
        editor.putLong("LOCAL_VERSION", version);
        editor.apply();
    }

    public MutableLiveData<String> getCloudVersion(){return cloudVersion;}

    public MutableLiveData<String> getLocalVersion(){return localVersion;}

    public MutableLiveData<status> getStatus(){return mstatus;}

    public static void updateStatus(){
        if(localversion == null || iffail )mstatus.setValue(status.ERROR);
        if(localversion == null || cloudversion == null )return;
        if(localversion.before(cloudversion))mstatus.setValue(status.NEEDUPDATE);
        if (localversion.equals(cloudversion))mstatus.setValue(status.OK);
    }


    public MutableLiveData<Integer[]> getAmount() {return mDatabaseAmount; }

    public static boolean getIfFirst(){
        if(firstStart){firstStart = false; return true;}
        return false;
    }

    public static void norify_updated() {
        new recalc_cards().execute();
    }

    private static class recalc_cards extends AsyncTask<Void, Void, Integer[]> {

        recalc_cards() {
            Log.d("async","notify task created");
        }

        @Override
        protected Integer[] doInBackground(Void... voids) {
            CardDao cardDao = MainDatabase.getDatabase(application).cardDao();

            Integer[] mAmount = new Integer[5];
            mAmount[0] = cardDao.getCardCount();
            mAmount[1] = cardDao.getstar1Count();
            mAmount[2] = cardDao.getstar2Count();
            mAmount[3] = cardDao.getstar3Count();
            mAmount[4] = cardDao.getstar4Count();
            Log.d("async","notify run ok");

            return mAmount;

        }

        protected void onPostExecute(Integer[] m) {
            Log.d("async","notified");
            mDatabaseAmount.setValue(m);
        }
    }
}
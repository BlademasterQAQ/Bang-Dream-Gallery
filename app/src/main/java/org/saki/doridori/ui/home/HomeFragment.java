package org.saki.doridori.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;

import org.saki.doridori.BuildConfig;
import org.saki.doridori.MainActivity;
import org.saki.doridori.R;
import org.saki.doridori.callbacks.MainCallBack;
import org.saki.doridori.database.Card;
import org.saki.doridori.database.CardDao;
import org.saki.doridori.database.CardDaoAsync;
import org.saki.doridori.database.MainDatabase;
import org.saki.doridori.database.operator.rebuildCardDatabase;
import org.saki.doridori.net.CheckUpdate;
import org.saki.doridori.net.DownloadJson;

import java.util.Objects;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    public static MainCallBack mainCallBack;
    private int count = 0;
    private Button UpdateCardJsonButton, CheckAppUpdateBtn;
    private SwipeRefreshLayout swipeRefreshLayout;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_home, container, false);

        swipeRefreshLayout = root.findViewById(R.id.swipe_down_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        UpdateCardJsonButton = root.findViewById(R.id.update_card_json_button);
        CheckAppUpdateBtn = root.findViewById(R.id.button_open_update_channel);

        if(HomeViewModel.getIfFirst()){
            Log.d("START", "it is first start");
            swipeRefreshLayout.setRefreshing(true);
            HomeViewModel.oncheckingUpdate();
            CheckUpdate.AsyncCheckUpdate(Objects.requireNonNull(getContext()).getString(R.string.card_url));
        }else Log.d("START", "it is NOT first start");


        //final Button TestDatabaseButton = root.findViewById(R.id.test_database_button);
        // 更換標題欄 title
        //MainActivity.changeTitleCallBack.changeTitle(Objects.requireNonNull(getActivity()).getString(R.string.app_name));
        //Toolbar mtoolbar = root.findViewById(R.id.toolbar);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle(Objects.requireNonNull(getActivity()).getString(R.string.app_name));
        //mtoolbar.setTitle("testtesttest");
        // 顯示當前 app 版本
        ((TextView) root.findViewById(R.id.textView_version)).setText(BuildConfig.VERSION_NAME);

        CheckAppUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(Intent.ACTION_VIEW);
                mIntent.setData(Uri.parse("tg://resolve?domain=BangDreamGallery"));
                if(mIntent.resolveActivity(Objects.requireNonNull(getContext()).getPackageManager()) != null){
                startActivity(mIntent);
                }
                else {
                    mIntent.setData(Uri.parse("https://t.me/s/BangDreamGallery"));
                    try{
                        startActivity(mIntent);
                    }catch (Exception e){
                        Log.d("error", e.toString());
                    }
                }
            }
        });

        UpdateCardJsonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doUpdateButton();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                HomeViewModel.oncheckingUpdate();
                CheckUpdate.AsyncCheckUpdate(Objects.requireNonNull(getContext()).getString(R.string.card_url));
            }
        });

        final String[] this_checkversion = {null};
        mainCallBack = new MainCallBack() {
            @Override
            public void CallbackQuery(String type, String[] querys) { // 更新資料完成後把按鍵改回來
                switch (type){
                    case "JSON_DOWNLOAD_RESULT":{
                        Log.d("callback","JSON_DOWNLOAD_RESULT");
                        if(querys==null) {
                            resetUpdateButton();
                            Snackbar.make(root, R.string.snackbar_database_update_failed, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            HomeViewModel.oncheckingUpdateFailed();
                            break;
                        } // json 下载没有成功
                        // json 下載成功，更新資料庫
                        CardDao cardDao = MainDatabase.getDatabase(getContext()).cardDao();
                        rebuildCardDatabase.start(cardDao,querys[0]);
                        this_checkversion[0] = querys[1];
                        break;
                    }
                    case "CARD_DATABASE_UPDATED":{
                        //資料庫更新成功
                        Log.d("callback","CARD_DATABASE_UPDATED");
                        try {
                            Snackbar.make(root, R.string.snackbar_database_updated, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            resetUpdateButton();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        HomeViewModel.setLocalversion(Long.parseLong(this_checkversion[0]));
                        HomeViewModel.norify_updated();
                        HomeViewModel.oncheckingUpdate();
                        CheckUpdate.AsyncCheckUpdate(Objects.requireNonNull(getContext()).getString(R.string.card_url));
                        break;
                    }
                    case "CHECK_UPDATE_RESULT":{

                        break;
                    }
                    case "CHECK_UPDATE_FAILED":{

                        break;
                    }
                }
            }
        };

        final TextView database_amount = root.findViewById(R.id.database_amount); // 資料庫狀態顯示框
        homeViewModel.getAmount().observe(getViewLifecycleOwner(), new Observer<Integer[]>() {
            @Override
            public void onChanged(Integer[] integers) {
                String mstring = Objects.requireNonNull(getContext()).getString(R.string.home_database_amount)
                        +"\t"
                        + String.valueOf(integers[0])
                        + "\n★　　　　\t" + String.valueOf(integers[1])
                        + "\n★★　　　\t" + String.valueOf(integers[2])
                        + "\n★★★　　\t" + String.valueOf(integers[3])
                        + "\n★★★★　\t" + String.valueOf(integers[4]);
                database_amount.setText(mstring);
            }
        });
        final TextView cloudversion = root.findViewById(R.id.remote_version);
        homeViewModel.getCloudVersion().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                cloudversion.setText(s);
                HomeViewModel.updateStatus();
            }
        });
        final TextView localversion = root.findViewById(R.id.local_version);
        homeViewModel.getLocalVersion().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                localversion.setText(s);
                HomeViewModel.updateStatus();
            }
        });
        final ImageView statusicon = root.findViewById(R.id.status_icon);
        homeViewModel.getStatus().observe(getViewLifecycleOwner(), new Observer<HomeViewModel.status>() {
            @Override
            public void onChanged(HomeViewModel.status status) {
                switch (status){
                    case OK:statusicon.setImageDrawable(getContext().getDrawable(R.drawable.ic_check_circle_black_24dp));break;
                    case CHECKING:statusicon.setImageDrawable(getContext().getDrawable(R.drawable.ic_sync_black_24dp));break;
                    case ERROR:statusicon.setImageDrawable(getContext().getDrawable(R.drawable.ic_error_black_24dp));break;
                    case NEEDUPDATE:statusicon.setImageDrawable(getContext().getDrawable(R.drawable.ic_arrow_upward_black_24dp));break;
                }
                if(status != HomeViewModel.status.CHECKING){
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        return root;
    }
    private void resetUpdateButton(){
        UpdateCardJsonButton.setClickable(true);
        UpdateCardJsonButton.setText(R.string.update_card_json_button);
        UpdateCardJsonButton.setEnabled(true);
    }

    private void doUpdateButton(){
        UpdateCardJsonButton.setClickable(false);
        UpdateCardJsonButton.setText(R.string.update_card_json_button_loading);
        UpdateCardJsonButton.setEnabled(false);
        HomeViewModel.oncheckingUpdate();
        Log.d("button","get json clicked");
        // 先削除所有資料再載入新資料
        DownloadJson.AsyncCatchData(getString(R.string.card_url));// catch full cards data json
        // 成功與否將會 callback
    }
}

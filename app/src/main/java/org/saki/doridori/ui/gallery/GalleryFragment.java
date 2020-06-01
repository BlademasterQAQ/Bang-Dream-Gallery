package org.saki.doridori.ui.gallery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.Preference;
import android.provider.Settings;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.l4digital.fastscroll.FastScroller;


import org.saki.doridori.BuildConfig;
import org.saki.doridori.R;
import org.saki.doridori.callbacks.MainCallBack;
import org.saki.doridori.database.Card;
import org.saki.doridori.ui.home.HomeViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private RecyclerView recycler_view;
    private MyAdapter adapter;
    private ArrayList<String> mData = new ArrayList<>();
    private Context context;
    private Bundle mBundleRecyclerViewState;
    private Parcelable mListState;
    private boolean should_scroll = false;
    public static MainCallBack renderok, saveok;
    private FrameLayout search_overlay;
    private TextView noDataCryFace;

    private CheckBox[] sel_star = new CheckBox[4];
    private CheckBox[] sel_band = new CheckBox[6];
    private CheckBox[] sel_type = new CheckBox[4];
    private Button sel_reset;
    private TextView sel_count;
    private FilterCondition sel_condition = new FilterCondition();



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        adapter = new MyAdapter(context);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("fragment", "on pause");

        mBundleRecyclerViewState = new Bundle();

        mListState = Objects.requireNonNull(recycler_view.getLayoutManager()).onSaveInstanceState();

        mBundleRecyclerViewState.putParcelable("KEY_RECYCLER_STATE", mListState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("fragment", "on resume");

        if (mBundleRecyclerViewState != null) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    mListState = mBundleRecyclerViewState.getParcelable("KEY_RECYCLER_STATE");
                    Objects.requireNonNull(recycler_view.getLayoutManager()).onRestoreInstanceState(mListState);
                }
            }, 50);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        mListState = Objects.requireNonNull(recycler_view.getLayoutManager()).onSaveInstanceState();
        savedInstanceState.putParcelable("KEY_RECYCLER_STATE", mListState);
        super.onSaveInstanceState(savedInstanceState);
        Log.d("fragment", "liststate saved");

    }

    private SharedPreferences sharepre;
    private SharedPreferences.Editor editor;

    @Override // 在 fragment 創建時準備需要的資料
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 在 Fragment 創建時
        should_scroll = true;
        sharepre = getActivity().getPreferences(Context.MODE_PRIVATE);

        if (savedInstanceState != null) {
            Log.d("fragment", "on create saved state exist");

        }else  Log.d("fragment", "on create saved state NOT exist");
        initData(); // 準備 view model 和 recycler view 的資料
    }

    @Override
    public void onStop() {

        super.onStop();
        Log.d("fragment", "on stop");

    }

    private ActionBar mToolbar;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, final Bundle savedInstanceState) {
        galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);

        final View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        // 工具欄
        //mToolbar = root.findViewById(R.id.toolbar1);
        mToolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        setHasOptionsMenu(true);


        // 連結元件
        recycler_view = (RecyclerView) root.findViewById(R.id.recycler_cards);
        final FastScroller fscro = root.findViewById(R.id.fast_scroller);
        search_overlay = root.findViewById(R.id.search_overlay);
        noDataCryFace = root.findViewById(R.id.textView_nodata);
        // 讀取上一次滾動的位置
        final int scrolled = sharepre.getInt("scrolly", 0);
        Log.d("fragment", "scrolled" + String.valueOf(scrolled));
        // 設置RecyclerView為列表型態
        recycler_view.setLayoutManager(new LinearLayoutManager(root.getContext()));

        // 設置格線
        //recycler_view.addItemDecoration(new DividerItemDecoration(root.getContext(), DividerItemDecoration.VERTICAL));


        // 設置adapter給recycler_view
        recycler_view.setAdapter(adapter);
        // 給這個 RecyclerView 綁定一個快速拖動條
        fscro.attachRecyclerView(recycler_view);
        // 使得快速拖動條拖動時候的氣泡顯示當前卡面的編號
        fscro.setSectionIndexer(new FastScroller.SectionIndexer() {
            @Override
            public CharSequence getSectionText(int position) {
                /*switch (adapter.cardList.get(position).attribute){
                    case "powerful": fscro.setBubbleColor(0xFFFF8D8D);break;
                    case "pure": fscro.setBubbleColor(0xFFADFF8D);break;
                    case "cool": fscro.setBubbleColor(0xFF8DA2FF);break;
                    case "happy": fscro.setBubbleColor(0xFFF8B98B);break;
                }*/
                return String.valueOf(adapter.cardList.get(position).cardId);
            }
        });
        // 在快速拖動條被拖動之後儲存列表滾動的位置
        fscro.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Log.d("fragment", "into " + String.valueOf(recycler_view.computeVerticalScrollOffset()));
                editor.putInt("scrolly", recycler_view.computeVerticalScrollOffset());
                editor.apply();
                return false;
            }
        });

        editor = sharepre.edit();
        // 在列表滾動之後儲存滾動的位置
        recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //if(newState == RecyclerView.SCROLL_STATE_SETTLING){
                    //Log.d("fragment", "into " + String.valueOf(recycler_view.computeVerticalScrollOffset()));
                    editor.putInt("scrolly", recycler_view.computeVerticalScrollOffset());
                    editor.apply();
                //}
            }
        });

        // 篩選介面，連結大量元件
        connectSelCheckBox(root);
        View.OnClickListener sel_check_change = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countCheckBoxAndRefresh();
            }
        };
        // 設定每一個選擇框監聽
        setBoxListener(sel_check_change);
        // 設定 reset 按鍵
        sel_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sel_condition = new FilterCondition();
                galleryViewModel.applyFilter(sel_condition);
                checkAllSelBox();
            }
        });
        // 點擊空白隱藏篩選介面
        search_overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //search_overlay.animate().alpha(0.0f);
                search_overlay.setVisibility(View.GONE);
            }
        });
        galleryViewModel.getSearchDataCount().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                sel_count.setText("(" + integer + ")");
                if(integer == 0){
                    noDataCryFace.setVisibility(View.VISIBLE);
                }else{
                    noDataCryFace.setVisibility(View.GONE);
                }
            }
        });

        // 這個回調會在第一張卡片完成渲染後調用，使得 RecyclerView 回到上一次的位置
        renderok = new MainCallBack() {
            @Override
            public void CallbackQuery(String type, String[] querys) {
                if (type.equals("CARD_RENDER_OK")){
                    //Log.d("callback","CARD_RENDER_OK");
                    // 只有 RecyclerView 第一次加載和列表在頂部的時候才恢復上一次位置
                    if(should_scroll && recycler_view.computeVerticalScrollOffset() == 0){
                        should_scroll = false;
                        new Handler().post(new Runnable() {

                            @Override
                            public void run() {
                                should_scroll = false;
                                recycler_view.scrollBy(0, scrolled);
                                //Log.d("fragment", "resume scroll " + String.valueOf(scrolled));
                                //recycler_view.smoothScrollToPosition(30);
                            }
                        });
                    }
                }
            }
        };
        saveok = new MainCallBack(){
            @Override
            public void CallbackQuery(String type, String[] querys) {
                if (type.equals("CARD_SAVE_OK")){
                    // 顯示儲存成功提示
                    Snackbar.make(root, querys[0], Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
                if (type.equals("NO_WRITE_PERMISSION")){
                    Snackbar.make(root, R.string.snackbar_no_write_permission, Snackbar.LENGTH_LONG)
                            .setAction("設定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                    myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                                    myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(myAppSettings);
                                }
                            }).show();
                    return;
                }
            }
        };
        return root;
    }


    // 將 adapter 綁定到卡面列表的 Livedata
    private void initData(){
        galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);
        galleryViewModel.getSearchData().observe(this, new Observer<List<Card>>() {
            @Override
            public void onChanged(List<Card> cards) {
                adapter.setCardList(cards);

            }
        });
        // 第一次檢索
        galleryViewModel.applyFilter(sel_condition);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                if(search_overlay.getVisibility() == View.VISIBLE){
                    //search_overlay.animate().alpha(0.0f);
                    search_overlay.setVisibility(View.GONE);
                }
                else {
                    //search_overlay.animate().alpha(1.0f);
                    search_overlay.setVisibility(View.VISIBLE);
                }
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }

    }

    private void connectSelCheckBox(View root){
        sel_star[0] = root.findViewById(R.id.sel_star_1);
        sel_star[1] = root.findViewById(R.id.sel_star_2);
        sel_star[2] = root.findViewById(R.id.sel_star_3);
        sel_star[3] = root.findViewById(R.id.sel_star_4);

        sel_band[0] = root.findViewById(R.id.sel_band_ppp);
        sel_band[1] = root.findViewById(R.id.sel_band_a);
        sel_band[2] = root.findViewById(R.id.sel_band_h);
        sel_band[3] = root.findViewById(R.id.sel_band_pp);
        sel_band[4] = root.findViewById(R.id.sel_band_r);
        sel_band[5] = root.findViewById(R.id.sel_band_m);

        sel_type[0] = root.findViewById(R.id.sel_attr_powerful);
        sel_type[1] = root.findViewById(R.id.sel_attr_cool);
        sel_type[2] = root.findViewById(R.id.sel_attr_happy);
        sel_type[3] = root.findViewById(R.id.sel_attr_pure);

        sel_reset = root.findViewById(R.id.sel_reset_btn);

        sel_count = root.findViewById(R.id.sel_count);
    }
    // 開始篩選
    private void countCheckBoxAndRefresh(){
        // 清點搜索框，製作 list 和 condition
        List<Integer> theStar = new ArrayList<>();
        for(int i = 0; i < 4; i++){
            if(sel_star[i].isChecked()) theStar.add(i + 1);
        }
        List<Integer> theBand = new ArrayList<>();
        for(int i = 0; i < 6; i++){
            if(sel_band[i].isChecked()) theBand.add(i + 1);
        }
        List<String> theType = new ArrayList<>();
        if(sel_type[0].isChecked()) theType.add("powerful");
        if(sel_type[1].isChecked()) theType.add("cool");
        if(sel_type[2].isChecked()) theType.add("happy");
        if(sel_type[3].isChecked()) theType.add("pure");

        sel_condition.setStar(theStar);
        sel_condition.setBand(theBand);
        sel_condition.setAttribute(theType);

        galleryViewModel.applyFilter(sel_condition);
    }
    // 巨大多選擇框設定監聽器
    private void setBoxListener(View.OnClickListener listener){
        for (CheckBox oneBox:sel_star) {
            oneBox.setOnClickListener(listener);
        }
        for (CheckBox oneBox:sel_band) {
            oneBox.setOnClickListener(listener);
        }
        for (CheckBox oneBox:sel_type) {
            oneBox.setOnClickListener(listener);
        }
    }
    // 按下重設，勾上所有框
    private void checkAllSelBox(){
        for (CheckBox oneBox:sel_star) {
            oneBox.setChecked(true);
        }
        for (CheckBox oneBox:sel_band) {
            oneBox.setChecked(true);
        }
        for (CheckBox oneBox:sel_type) {
            oneBox.setChecked(true);
        }
    }
}

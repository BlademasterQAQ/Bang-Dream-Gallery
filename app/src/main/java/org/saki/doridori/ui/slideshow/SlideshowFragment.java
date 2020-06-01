package org.saki.doridori.ui.slideshow;

import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import org.saki.doridori.R;
import org.saki.doridori.database.Card;
import org.saki.doridori.database.CardDao;
import org.saki.doridori.database.MainDatabase;

public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;
    private CardView the_cardview;
    private TextView charid, star, type, url1, url2;
    private EditText inputid;
    private Button search;
    private LiveData<Card> the_card;
    private ImageView theImage;
    private Switch isTrained;


    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData(); // 準備 view model 和 recycler view 的資料
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        //final TextView textView = root.findViewById(R.id.text_slideshow);
/*        slideshowViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        the_cardview = root.findViewById(R.id.cg_cardview);
        charid = root.findViewById(R.id.textView4);
        star = root.findViewById(R.id.textView5);
        type = root.findViewById(R.id.textView6);
        url1 = root.findViewById(R.id.textView7);
        url2 = root.findViewById(R.id.textView8);
        inputid = root.findViewById(R.id.edit_testid_box);
        search = root.findViewById(R.id.button_test);
        theImage = root.findViewById(R.id.imageView3);
        isTrained = root.findViewById(R.id.isTrained);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int the_id = Integer.parseInt(inputid.getText().toString());
                CardDao cardDao = MainDatabase.getDatabase(getContext()).cardDao();
                Log.d("find", "button clicked");
                getFromDB(the_id, cardDao);

            }
            private void getFromDB(final int id, CardDao theDao){
                theDao.findCardById(id).observe(getViewLifecycleOwner(), new Observer<Card>() {
                    @Override
                    public void onChanged(final Card card) {
                        if(card == null){
                            Log.d("observe", "no card of id "+ id);
                            return;
                        }
                        //Log.d("observe", "observe ok the id is "+ id);
                        //Log.d("card", "observe ok the char is "+ card.characterId);
                        //Log.d("card", "observe ok the star is "+ card.rarity);
                        TextView title = root.findViewById(R.id.textViewCardtitle);
                        title.setText(card.resourceSetName);

                        charid.setText(String.valueOf(card.characterId));
                        star.setText(String.valueOf(card.rarity));
                        type.setText(card.attribute);
                        url1.setText(card.picUriOrigin);
                        url2.setText(card.picUriTrained);

                        if(card.rarity < 3){
                            root.findViewById(R.id.isTrained).setVisibility(View.INVISIBLE);
                            drawCG(card.picUriOrigin);
                        }else{
                            root.findViewById(R.id.isTrained).setVisibility(View.VISIBLE);
                            if(isTrained.isChecked()){
                                drawCG(card.picUriTrained);
                            }else{
                                drawCG(card.picUriOrigin);
                            }
                        }
                        switch (card.attribute){
                            case "powerful": the_cardview.setCardBackgroundColor(0xFFFF8D8D);break;
                            case "pure": the_cardview.setCardBackgroundColor(0xFFADFF8D);break;
                            case "cool": the_cardview.setCardBackgroundColor(0xFF8DA2FF);break;
                            case "happy": the_cardview.setCardBackgroundColor(0xFFF8B98B);break;
                        }

                        isTrained.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if(isTrained.isChecked()){
                                    drawCG(card.picUriTrained);
                                }else{
                                    drawCG(card.picUriOrigin);
                                }
                            }
                        });
                    }
                });
            }
        });


        return root;
    }

    private void initData(){

    }

    private void drawCG(String url){
        final CircularProgressDrawable drawable = new CircularProgressDrawable(getContext());
        drawable.setStrokeCap(Paint.Cap.ROUND);
        drawable.setStyle(CircularProgressDrawable.DEFAULT);
        drawable.setCenterRadius(80f);
        drawable.start();
        Glide.with(getContext()).load(url)
                //.override(40,30)
                .fitCenter()
                .placeholder(drawable)
                .transition(new DrawableTransitionOptions().crossFade())
                .into(theImage);
    }
}

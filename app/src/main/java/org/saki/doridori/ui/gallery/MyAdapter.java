package org.saki.doridori.ui.gallery;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;
import org.saki.doridori.BuildConfig;
import org.saki.doridori.R;
import org.saki.doridori.database.Card;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private Context context;
    List<Card> cardList;

    MyAdapter(Context the_context) {
        context = the_context;
    }

    void setCardList(List<Card> cardList){
        this.cardList = cardList;
        notifyDataSetChanged();
    }

    // 建立ViewHolder
    class ViewHolder extends RecyclerView.ViewHolder{
        // 宣告元件
        private CardView cgCardItem;
        private TextView titleInCard;
        private ImageView cardImage;
        private Switch trainSwitch;
        private ImageButton saveKey;

        ViewHolder(View itemView) {
            super(itemView);
            //
            cgCardItem = (CardView) itemView.findViewById(R.id.cg_cardview);
            titleInCard = itemView.findViewById(R.id.textViewCardtitle);
            cardImage = itemView.findViewById(R.id.imageView3);
            trainSwitch = itemView.findViewById(R.id.isTrained);
            saveKey = itemView.findViewById(R.id.saveCGkey);
        }

        void drawCG(String url){
            // 準備一個轉圈圈的動畫
            final CircularProgressDrawable drawable = new CircularProgressDrawable(context);
            drawable.setStrokeCap(Paint.Cap.ROUND);
            drawable.setStyle(CircularProgressDrawable.DEFAULT);
            drawable.setCenterRadius(80f);
            drawable.start();
            // 開始渲染圖片
            Glide.with(context).load(url)
                    //.override(40,30)
                    .fitCenter()
                    .placeholder(drawable)
                    .error(R.drawable.ic_error_outline_black_24dp)
                    .transition(new DrawableTransitionOptions().crossFade())
                    .into(cardImage);
        }
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 連結項目布局檔list_item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cg_cardview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull final ViewHolder holder, int position) {
        if(cardList == null){
            return;
        }

        final Card thisCard = cardList.get(position);
        if(thisCard != null){
            GalleryFragment.renderok.CallbackQuery("CARD_RENDER_OK", null);
            // 設定標題
            holder.titleInCard.setText(String.valueOf(thisCard.cardId) );
            if(thisCard.rarity < 3){
                holder.trainSwitch.setVisibility(View.INVISIBLE);
                holder.drawCG(thisCard.picUriOrigin);
            }else{
                holder.trainSwitch.setVisibility(View.VISIBLE);
                if(holder.trainSwitch.isChecked()){
                    holder.drawCG(thisCard.picUriTrained);
                }else{
                    holder.drawCG(thisCard.picUriOrigin);
                }
            }
            switch (thisCard.attribute){
                case "powerful": holder.cgCardItem.setCardBackgroundColor(0xFFFF8D8D);break;
                case "pure": holder.cgCardItem.setCardBackgroundColor(0xFFADFF8D);break;
                case "cool": holder.cgCardItem.setCardBackgroundColor(0xFF8DA2FF);break;
                case "happy": holder.cgCardItem.setCardBackgroundColor(0xFFF8B98B);break;
            }
            holder.trainSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(holder.trainSwitch.isChecked()){
                        holder.drawCG(thisCard.picUriTrained);
                    }else{
                        holder.drawCG(thisCard.picUriOrigin);
                    }
                }
            });
            holder.cardImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(holder.trainSwitch.isChecked()){
                        openCG(thisCard.picUriTrained);
                    }else{
                        openCG(thisCard.picUriOrigin);
                    }
                }
            });
            holder.saveKey.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(holder.trainSwitch.isChecked()){
                        saveCG(thisCard, thisCard.picUriTrained);
                    }else{
                        saveCG(thisCard, thisCard.picUriOrigin);
                    }
                }
            });
        }
    }
    // 調用系統相簿顯示 CG
    private void openCG(final String imageUrl){
        final Long tsLong = System.currentTimeMillis();
        Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap b, @Nullable Transition<? super Bitmap> transition) {
                        Long tsLong2 = System.currentTimeMillis();
                        if(tsLong2 - tsLong > 1300) return;
                        File file = context.getCacheDir();
                        try {
                            URI uri = new URI(imageUrl);
                            String path = uri.getPath();
                            String nameOfFile = file + "/" + path.substring(path.lastIndexOf('/') + 1);
                            OutputStream os = new FileOutputStream(nameOfFile);
                            b.compress(Bitmap.CompressFormat.PNG, 100, os);
                            os.flush();
                            os.close();

                            Intent share = new Intent(Intent.ACTION_VIEW);
                            //share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + nameOfFile));
                            //share.setData(Uri.parse("file://" + nameOfFile)); android 6.0-
                            share.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            share.setData(FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider" , new File(nameOfFile)));
                            //share.setType("image/*");
                            context.startActivity(share);
                        } catch (IOException | URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }


                });
    }
    // 儲存 CG 到本地
    private void saveCG(final Card theCard, final String imageUrl){
        final Long tsLong = System.currentTimeMillis();
        Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap b, @Nullable Transition<? super Bitmap> transition) {
                        Long tsLong2 = System.currentTimeMillis();
                        if(tsLong2 - tsLong > 1300) return;

                        OutputStream fos = null;
                        // make filename
                        URI uri;
                        try {
                            uri = new URI(imageUrl);
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                            return;
                        }
                        String path = uri.getPath();
                        String trainedORnot = path.substring(path.lastIndexOf('/') + 1);
                        String filename = String.valueOf(theCard.cardId) + "_" + trainedORnot;
                        String imagesDir = null;
                        // end make filename
                        // make permission on 9 or below
                        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P){
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED) {

                                String[] result = new String[1];
                                result[0] = "Bang_Dream_CG";
                                GalleryFragment.saveok.CallbackQuery("NO_WRITE_PERMISSION", result);
                                    ActivityCompat.requestPermissions((Activity) context,
                                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            1234);
                                    return;
                                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                                    // app-defined int constant. The callback method gets the
                                    // result of the request.

                            } else {
                                // Permission has already been granted
                            }

                        }



                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            ContentResolver resolver = context.getContentResolver();
                            ContentValues contentValues = new ContentValues();

                            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
                            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
                            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator +"Bang_Dream_CG");
                            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                            try {
                                fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                                return;
                            }
                        } else {
                            imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
                            //Log.d("file", imagesDir);
                            imagesDir = imagesDir +  File.separator + "Bang_Dream_CG";
                            File appDir = new File(imagesDir);
                            if (!appDir.exists()) {
                                if(!appDir.mkdir())return;
                            }
                            File image = new File(imagesDir, filename);
                            try {
                                fos = new FileOutputStream(image);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }

                        b.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        try {
                            Objects.requireNonNull(fos).close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // notify mediastore for changing
                        if(imagesDir != null){
                            //Log.d("notify", imagesDir + File.separator + filename);
                            Uri contentUri = Uri.fromFile(new File(imagesDir + File.separator + filename));
                            Intent notifyintent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, contentUri);
                            context.sendBroadcast(notifyintent);
                        }

                        String[] result = new String[1];
                        result[0] = context.getString(R.string.snackbar_cg_save_ok) +  Environment.DIRECTORY_PICTURES + File.separator +"Bang_Dream_CG";
                        GalleryFragment.saveok.CallbackQuery("CARD_SAVE_OK", result);
                    }


                });
    }

    @Override
    public int getItemCount() {
        if(cardList == null){
            return 0;
        }else{
            return cardList.size();
        }
    }


}
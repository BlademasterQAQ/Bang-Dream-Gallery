package org.saki.doridori.database;

import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "cards",//定义表名
        indices = @Index(value = {"cardId", "characterId"}, unique = false))//定义索引)
public class Card {
    @PrimaryKey
    public int cardId;
    @ColumnInfo(name = "characterId")
    public int characterId;
    @ColumnInfo(name = "rarity")
    public int rarity;
    @ColumnInfo(name = "attribute")
    public String attribute;
    @ColumnInfo(name = "resourceSetName")
    public String resourceSetName;
    @ColumnInfo(name = "picUriOrigin")
    public String picUriOrigin;
    @ColumnInfo(name = "picUriTrained")
    public String picUriTrained;

    public Card(int cardId, int characterId, int rarity, String attribute, String resourceSetName){ // 這是個構造函式，用來快速產生 CardList 表中的一條記錄（一張卡）
        //Log.d("database","card created");
        this.cardId = cardId;
        this.characterId = characterId;
        this.rarity = rarity;
        this.attribute = attribute;
        this.resourceSetName = resourceSetName;
        this.picUriOrigin = "https://bestdori.com/assets/jp/characters/resourceset/" + resourceSetName
                + "_rip/card_normal.png";
        String picUriTrained = null;
        if(rarity > 2)  {this.picUriTrained = "https://bestdori.com/assets/jp/characters/resourceset/"
                + resourceSetName + "_rip/card_after_training.png";}else this.picUriTrained = null;
    }
}

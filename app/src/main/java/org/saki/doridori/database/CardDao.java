package org.saki.doridori.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CardDao { // 從網上 CV 的一些資料庫操作，據說可以查詢插入之類的

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Card... entities);

    @Query("DELETE FROM cards")
    void delAll();

    @Update
    void update(Card entity);

    @Query("SELECT * FROM cards WHERE cardId = :id")
    LiveData<Card> findCardById(int id);

    @Query("SELECT * FROM cards ORDER BY cardId ASC")
    LiveData<List<Card>> getAllCards();

    @Query("SELECT * FROM cards WHERE rarity IN (:star) AND characterId IN (:charid) AND attribute IN (:attribute)")
    List<Card> getFiltedCards(List<Integer> star, List<Integer> charid, List<String> attribute);

    @Query("SELECT COUNT(cardId) FROM cards WHERE rarity IN (:star) AND characterId IN (:charid) AND attribute IN (:attribute)")
    int getFiltedCardsCount(List<Integer> star, List<Integer> charid, List<String> attribute);

    @Query("SELECT COUNT(cardId) FROM cards")
    int getCardCount();

    @Query("SELECT COUNT(cardId) FROM cards WHERE rarity = 1")
    int getstar1Count();

    @Query("SELECT COUNT(cardId) FROM cards WHERE rarity = 2")
    int getstar2Count();

    @Query("SELECT COUNT(cardId) FROM cards WHERE rarity = 3")
    int getstar3Count();

    @Query("SELECT COUNT(cardId) FROM cards WHERE rarity = 4")
    int getstar4Count();
}

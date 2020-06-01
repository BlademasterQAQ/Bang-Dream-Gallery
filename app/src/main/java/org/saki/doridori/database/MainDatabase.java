package org.saki.doridori.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = Card.class,version = 1)
public abstract class MainDatabase extends RoomDatabase {
    // 根據網上 CV，這裏可以聲明一些資料庫實例的操作，比如說初始化資料庫之類的。
    private static MainDatabase INSTANCE; // 不懂能幹嘛，應該是實例化的資料庫對象 object
    private static final String DB_NAME = "dori.db"; // 應該是資料庫的檔案名
    public abstract CardDao cardDao(); // 不知道能幹啥。

    public static MainDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (Card.class) {
                if (INSTANCE == null) {
                    // 如果資料庫實例還不存在，就加載一個，但是不知道從哪裏加載
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MainDatabase.class, DB_NAME).allowMainThreadQueries().build();
                }
            }
        }
        return INSTANCE;
    }

}

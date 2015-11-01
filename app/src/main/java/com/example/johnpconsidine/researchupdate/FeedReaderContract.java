package com.example.johnpconsidine.researchupdate;

import android.provider.BaseColumns;

/**
 * Created by johnpconsidine on 10/31/15.
 */
public class FeedReaderContract {

    public static abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String COL_1 = "accelerometer";
        public static final String COL_2 = "gyroscope";
        public static final String COL_3 = "gravity";
    }
}


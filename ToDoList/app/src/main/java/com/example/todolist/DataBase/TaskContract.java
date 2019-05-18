package com.example.todolist.DataBase;

import android.provider.BaseColumns;

public class TaskContract {
    static final String DB_NAME = "com.example.todolist.db";
    static final int DB_VERSION = 1;

    public class TaskEntry implements BaseColumns {
        public static final String TABLE = "tasks";
        public static final String COL_TASK_TITLE = "title";
    }
}

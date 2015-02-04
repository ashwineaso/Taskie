package in.altersense.taskapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import in.altersense.taskapp.common.Config;
import in.altersense.taskapp.models.TaskGroup;

/**
 * Created by mahesmohan on 2/4/15.
 */
public class TaskGroupDbHelper extends SQLiteOpenHelper {
    private static String CREATION_STATEMENT = "CREATE TABLE " + TaskGroup.TABLE_NAME + " ( " +
            TaskGroup.KEYS.UUID.getName() + " " + TaskGroup.KEYS.UUID.getType() + ", " +
            TaskGroup.KEYS.TITLE.getName() + " " + TaskGroup.KEYS.TITLE.getType() + ", " +
            TaskGroup.KEYS.HAS_UPDATE.getName() + " " + TaskGroup.KEYS.HAS_UPDATE.getType() + ");";

    public TaskGroupDbHelper(Context context) {
        super(context, TaskGroup.TABLE_NAME, null, Config.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                CREATION_STATEMENT
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: Move to a better database upgradation logic.
        db.execSQL("DROP TABLE IF EXISTS " + TaskGroup.TABLE_NAME);
        db.execSQL(CREATION_STATEMENT);
    }
}

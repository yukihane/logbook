package yukihane.logbook.db;

import static yukihane.logbook.LogbookApplication.TAG;

import java.sql.SQLException;

import yukihane.logbook.entity.Comment;
import yukihane.logbook.entity.StatusMessage;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    // name of the database file for your application -- change to something appropriate for your app
    private static final String DATABASE_NAME = "logbook.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 1;

    // the DAO object we use to access the Item table
    private Dao<StatusMessage, String> statusMessageDao = null;
    private Dao<Comment, String> commentDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(TAG, "onCreate");
            TableUtils.createTable(connectionSource, StatusMessage.class);
            TableUtils.createTable(connectionSource, Comment.class);
        } catch (SQLException e) {
            Log.e(TAG, "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(TAG, "onUpgrade");
            TableUtils.dropTable(connectionSource, StatusMessage.class, true);
            TableUtils.dropTable(connectionSource, Comment.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(TAG, "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the Database Access Object (DAO) for our Item class. It will create it or just give the cached
     * value.
     */
    public Dao<StatusMessage, String> getStatusMessageDao() throws SQLException {
        if (statusMessageDao == null) {
            statusMessageDao = getDao(StatusMessage.class);
        }
        return statusMessageDao;
    }

    public Dao<Comment, String> getCommentDao() throws SQLException {
        if (commentDao == null) {
            commentDao = getDao(Comment.class);
        }
        return commentDao;
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        statusMessageDao = null;
        commentDao = null;
    }
}

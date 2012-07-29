package com.thoughtworks.hp.datastore;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.*;
import android.util.Log;
import com.thoughtworks.hp.models.ShoppingList;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListTable implements Table<ShoppingList> {

    public static final String TABLE_NAME = "SHOPPING_LISTS";
    private SQLiteOpenHelper database;
    private static String TAG = "com.thoughtworks.hp.datastore.ShoppingListTable";

    public ShoppingListTable(SQLiteOpenHelper database) {
        this.database = database;
    }

    private static class ShoppingListCursor extends SQLiteCursor {

        private static final String FIELD_LIST = " id, name ";
        private static final String ALL_QUERY = "SELECT "+ FIELD_LIST +" FROM "+ TABLE_NAME;
        private static final String ID_QUERY = "SELECT "+ FIELD_LIST +" FROM "+ TABLE_NAME +" WHERE id = ?";
        private static final String NAME_QUERY = "SELECT "+ FIELD_LIST +" FROM "+ TABLE_NAME +" WHERE name = ? ";

        public ShoppingListCursor(SQLiteDatabase db, SQLiteCursorDriver driver, String editTable, SQLiteQuery query) {
            super(db, driver, editTable, query);
        }

        private static class Factory implements SQLiteDatabase.CursorFactory {
            public Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver driver, String editTable, SQLiteQuery query) {
                return new ShoppingListCursor(db, driver, editTable, query);
            }
        }

        private long getShoppingListId() {
            return getLong(getColumnIndexOrThrow("id"));
        }

        private String getShoppingListName() {
            return getString(getColumnIndexOrThrow("name"));
        }

        public ShoppingList getShoppingList() {
            return new ShoppingList(getShoppingListId(), getShoppingListName());
        }
    }

    protected List<ShoppingList> findShoppingList(String query, String[] params) {
        Cursor shoppingListCursor = null;
        List<ShoppingList> shoppingLists = new ArrayList<ShoppingList>();
        try {
            shoppingListCursor = database.getReadableDatabase().rawQueryWithFactory(new ShoppingListCursor.Factory(), query, params, null);
            if(shoppingListCursor != null && shoppingListCursor.moveToFirst()) {
                do {
                    shoppingLists.add(((ShoppingListCursor) shoppingListCursor).getShoppingList());
                } while(shoppingListCursor.moveToNext());
            }
        } catch(SQLException sqle) {
            Log.e(TAG, "Could not look up the shopping list with params " + params + ". The error is: " + sqle.getMessage());
        }
        finally {
            if(shoppingListCursor != null && !shoppingListCursor.isClosed()) {
                shoppingListCursor.close();
            }
        }
        return shoppingLists;
    }

    public List<ShoppingList> findAll() {
        return findShoppingList(ShoppingListCursor.ALL_QUERY, null);
    }

    public ShoppingList findById(long id) {
        String id_string = Long.toString(id);
        List<ShoppingList> shoppingLists = findShoppingList(ShoppingListCursor.ID_QUERY, new String[] {id_string});
        return (shoppingLists == null || shoppingLists.isEmpty()) ? null : shoppingLists.get(0);
    }

    public ShoppingList create(ShoppingList newShoppingList) {
        if (newShoppingList != null) {
            database.getWritableDatabase().beginTransaction();
            try {
                ContentValues dbValues = new ContentValues();
                dbValues.put("name", newShoppingList.getName());
                long id = database.getWritableDatabase().insertOrThrow(TABLE_NAME, "name", dbValues);
                newShoppingList.setId(id);
                database.getWritableDatabase().setTransactionSuccessful();
            } catch (SQLException sqle) {
                Log.e(TAG, "Could not create new shopping list. Exception is :" + sqle.getMessage());
            } finally {
                database.getWritableDatabase().endTransaction();
            }
        }
        return newShoppingList;
    }

}

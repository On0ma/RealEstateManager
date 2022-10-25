package com.openclassrooms.realestatemanager.ui;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.openclassrooms.realestatemanager.database.RealEstateManagerDatabase;
import com.openclassrooms.realestatemanager.database.dao.PropertyDao;

public class RealEstateManagerContentProvider extends ContentProvider {
    public RealEstateManagerContentProvider() {
    }

    static final String AUTHORITY = "com.openclassrooms.realestatemanager.provider";

    static final String URL = "content://" + AUTHORITY + "/property";

    static final Uri URI_PROPERTY = Uri.parse(URL);

    // Match code for some items in the Property table
    static final int CODE_PROPERTY_DIR = 1;
    static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);;

    static {
        // Access the whole table
        MATCHER.addURI(AUTHORITY, "property", CODE_PROPERTY_DIR);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        switch (MATCHER.match(uri)) {
            case CODE_PROPERTY_DIR:
                return "vnd.android.cursor.dir/" + AUTHORITY + ".property";
            default:
                throw new IllegalArgumentException("Unknown URI: "+ uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        final int code = MATCHER.match(uri);
        if (code == CODE_PROPERTY_DIR) {
            final Context context = getContext();
            if (context == null) {
                return null;
            }
            PropertyDao propertyDao = RealEstateManagerDatabase.getInstance(context).propertyDao();
            final Cursor cursor;
            cursor = propertyDao.selectAllProperties();
            cursor.setNotificationUri(context.getContentResolver(), uri);
            return cursor;
        } else {
            throw new IllegalArgumentException("Unknown URI: "+ uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
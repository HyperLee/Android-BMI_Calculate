package com.demo.android.bmi;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;
import java.sql.SQLException;

/**
 * Created by peter.chiu on 2016/1/20.
 */
public class HistoryActivity extends ListActivity{

    private DB mDbHelper;
    private Cursor mCursor;

    static final String[] records = new String[] {
	    /*"20", "21", "22", "24", "23", "22", "20"*/};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        //Tell the list view which view to display when the list is empty
        getListView().setEmptyView(findViewById(R.id.empty));
        setAdapter();
    }
    private  void setAdapter(){
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, records);
        //R.array.records >> array in strings.xml//
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.records, android.R.layout.simple_list_item_1);
//        setListAdapter(adapter);
        mDbHelper = new DB(this);
        mDbHelper = new DB(this);
        try {
            mDbHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
         fillData();
//        mCursor = mDbHelper.getAll();
//        startManagingCursor(mCursor);
//
//        String[] from_column = new String[]{DB.KEY_ITEM, DB.KEY_CREATED};
//        int[] to_layout = new int[]{android.R.id.text1, android.R.id.text2};
//
//        // Now create a simple cursor adapter
//        SimpleCursorAdapter adapter =
//                new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2,
//                        mCursor, from_column, to_layout);
//        setListAdapter(adapter);
    }
//
private void fillData() {
    mCursor = mDbHelper.getAll();
    startManagingCursor(mCursor);

    String[] from_column = new String[]{DB.KEY_ITEM, DB.KEY_CREATED};
    int[] to_layout = new int[]{android.R.id.text1, android.R.id.text2};

    // Now create a simple cursor adapter
    SimpleCursorAdapter adapter =
            new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2,
                    mCursor, from_column, to_layout);
    setListAdapter(adapter);
}

}

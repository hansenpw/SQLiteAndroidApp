package com.example.hansen.sqlitetest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public final static String KEY_EXTRA_ID = "KEY_EXTRA_ID";

    private ListView listView;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button1);
        if(button != null){
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, CreateOrEditActivity.class);
                    intent.putExtra(KEY_EXTRA_ID, 0);
                    startActivity(intent);
                }
            });
        }

        dbHelper = new DBHelper(this);

        if (savedInstanceState == null) dbHelper.insertCurrencyDefault();

        final Cursor cursor = dbHelper.getAllExpense();
        String [] columns = new String[]{
                DBHelper.EXPENSE_COLUMN_TITLE,
                DBHelper.EXPENSE_COLUMN_VALUE
        };
        int [] widgets = new int[]{
                R.id.dataTitle,
                R.id.dataValue
        };

        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.expense_info,
                cursor, columns, widgets, 0);
        listView = (ListView) findViewById(R.id.listView1);
        listView.setAdapter(cursorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor itemCursor = (Cursor) MainActivity.this.listView.getItemAtPosition(position);
                int expenseID = itemCursor.getInt(itemCursor.getColumnIndex(DBHelper.EXPENSE_COLUMN_ID));
                Intent intent = new Intent(getApplicationContext(), CreateOrEditActivity.class);
                intent.putExtra(KEY_EXTRA_ID, expenseID);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("cur", 1);
        super.onSaveInstanceState(outState);
    }

    private int countTotalExpense(){
        int totalExpense = 0;
        int columnIndex;
        final Cursor cursor = dbHelper.getTotalExpense();
        cursor.moveToFirst();
        columnIndex = cursor.getColumnIndex(DBHelper.EXPENSE_COLUMN_VALUE);
        while(!cursor.isAfterLast()){
            totalExpense += cursor.getInt(columnIndex);
            cursor.moveToNext();
        }
        return totalExpense;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.deleteAllMenu:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Confirm all expense(s) deletion")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbHelper.deleteAllExpense();
                                Toast.makeText(getApplicationContext(), "Delete All Success", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("no", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                AlertDialog d = builder.create();
                d.setTitle("Delete All?");
                d.show();
                return true;

            case R.id.countMenu:
                Toast.makeText(getApplicationContext(), "Total = " + Integer.toString(countTotalExpense()),
                        Toast.LENGTH_LONG).show();
                return true;

            case R.id.currencyMenu:
                Intent intent = new Intent(getApplicationContext(), CurrencyEditActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

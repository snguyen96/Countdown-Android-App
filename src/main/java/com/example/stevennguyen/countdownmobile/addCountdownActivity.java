package com.example.stevennguyen.countdownmobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class addCountdownActivity extends AppCompatActivity {

    // initialize key for description of countdown
    public static final String KEY = "cd";
    // initialize today's date
    public DatePicker today;
    public int tDay, tMonth, tYear;
    public int[] daysInMonths = {31,28,31,30,31,30,31,31,30,31,30,31};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_countdown);

        // set today's date values
        today = (DatePicker) findViewById(R.id.datePicker);
        tDay = today.getDayOfMonth();
        tMonth = today.getMonth() + 1;
        tYear = today.getYear();

        if(tYear % 4 == 0){
            daysInMonths[1] = 29;
        }
    }

    // When user taps done button --> get the description text, put in result
    public void done(View view) {
        // initialize and set picked date values
        DatePicker datePicked = (DatePicker) findViewById(R.id.datePicker);
        int day = datePicked.getDayOfMonth();
        int month = datePicked.getMonth() + 1;
        int year = datePicked.getYear();

        // initialize days_until variable to be calculated
        int days_until = day - tDay;

        // if chosen day is current month & year
        if(tMonth == month && tDay <= day && tYear == year) {
            days_until += 0;
        }else if(tMonth < month && tYear <= year) {  // chosen day is next months, this year or after
            days_until = daysInMonths[tMonth-1] - tDay;
            days_until += calculateDays(month, day, year);
        }else if(tMonth >= month && tYear < year) {   // chosen day is months of next years
            days_until = daysInMonths[tMonth-1] - tDay;
            days_until += calculateDays(month, day, year);
        }else{
            days_until = 0;
        }

        // Create intent and send values from days_until and textbox
        Intent intent = new Intent();
        EditText editText = (EditText) findViewById(R.id.description);
        String input_desc = editText.getText().toString();
        setResult(RESULT_OK, intent);

        addData(input_desc, days_until, tMonth+"/"+tDay+"/"+tYear);
        finish();
    }

    // calculate countdown
    private int calculateDays(int m, int d, int y) {
        int total = 0; // initialize total variable

        if(y == tYear) {     // calculate for current year
            for (int i = tMonth; i < m - 1; i++) {   // all months between + rest of goal month
                total += daysInMonths[i];
            }
            return total + d;
        }else if(y == tYear + 1) {    // calculate for next year
            if(tYear + 1 % 4 == 0){   // if next year is a leap year
                daysInMonths[1] = 29;
            }
            for (int i = tMonth; i < 12; i++) {    // rest of year + months between + rest of goal month
                total += daysInMonths[i];
            }
            for (int j = 0; j < m - 1; j++) {
                total += daysInMonths[j];
            }
            return total + d;
        }else {     // greater than two years, don't count
            return tDay-daysInMonths[tMonth-1];
        }
    }

    // add data
    public void addData(String d, int c, String date) {
        DataBaseHelper myDb = new DataBaseHelper(this);
        boolean isInserted = myDb.insertData(d, c, date);
        if(isInserted) {
            Toast.makeText(addCountdownActivity.this, "Data Inserted", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(addCountdownActivity.this, "Data not Inserted", Toast.LENGTH_LONG).show();
        }
    }
}

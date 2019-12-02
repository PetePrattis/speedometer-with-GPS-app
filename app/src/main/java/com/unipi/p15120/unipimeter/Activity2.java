package com.unipi.p15120.unipimeter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.style.StyleSpan;
import android.view.View;
import android.text.SpannableStringBuilder;
import android.widget.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import android.app.AlertDialog;

public class Activity2 extends AppCompatActivity {


    TextView textView19,textView20,textView21,textView22,textView23,textView8;
    Button button;
    String poi;
    SQLiteDatabase db;
    public int[] times;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        textView8 = findViewById(R.id.textView8);
        textView19 = findViewById(R.id.textView19);
        textView20 = findViewById(R.id.textView20);
        textView21 = findViewById(R.id.textView21);
        textView22 = findViewById(R.id.textView22);
        textView23 = findViewById(R.id.textView23);
        button = findViewById(R.id.button5);
        textView8.setBackgroundColor(getResources().getColor(R.color.ic_launcher_background));
        textView19.setBackgroundColor(getResources().getColor(R.color.ic_launcher_background));
        textView20.setBackgroundColor(getResources().getColor(R.color.ic_launcher_background));
        textView21.setBackgroundColor(getResources().getColor(R.color.ic_launcher_background));
        textView22.setBackgroundColor(getResources().getColor(R.color.ic_launcher_background));
        textView23.setBackgroundColor(getResources().getColor(R.color.ic_launcher_background));

        button.setText("-");
        poi = "";


        db = openOrCreateDatabase("UnipiMeter", Context.MODE_PRIVATE, null);//I open the database

        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM SPEED", null);//I get the count of the table's SPEED recordings
        if (cursor != null){//if there aren't any
            cursor.moveToFirst();
            if(cursor.getInt(0) == 0){
                SpannableStringBuilder str = new SpannableStringBuilder("-Sum of violations of Speed Limit : " + " entry not found.");
                str.setSpan(new StyleSpan(Typeface.BOLD), 0, "-Sum of violations of Speed Limit : ".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                textView19.setText(str);
            }
            else {//if there are
                long count = DatabaseUtils.queryNumEntries(db, "SPEED");
                SpannableStringBuilder str = new SpannableStringBuilder("-Sum of violations of Speed Limit  : " + " is " + String.valueOf(count));
                str.setSpan(new StyleSpan(Typeface.BOLD), 0, "-Sum of violations of Speed Limit : ".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView19.setText(str);
            }
        }

        Cursor cursor1 = db.rawQuery("SELECT COUNT(*) FROM SPEED", null);//I get the count of the table's SPEED recordings to calculate the average speed
        if (cursor1 != null){//if there aren't any
            cursor1.moveToFirst();
            if(cursor1.getInt(0) == 0){
                SpannableStringBuilder str = new SpannableStringBuilder("-Average Max Speed : " + " entry not found.");
                str.setSpan(new StyleSpan(Typeface.BOLD), 0, "-Average Max Speed : ".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                textView20.setText(str);
            }
            else{//else I add all the speed and divide by count to find the average
                Cursor c1 = db.rawQuery("SELECT * FROM SPEED",null);
                double sumspeed = 0;
                double speed = 0;
                double averagespeed=0;
                while (c1.moveToNext()){
                    speed =c1.getDouble(1);
                    sumspeed = sumspeed + speed;
                }
                long count = DatabaseUtils.queryNumEntries(db, "SPEED");
                averagespeed = sumspeed / count;
                SpannableStringBuilder str = new SpannableStringBuilder("-Average Max Speed : " + "is " + String.valueOf(averagespeed) + " km/h");
                str.setSpan(new StyleSpan(Typeface.BOLD), 0, "-Average Max Speed : ".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                textView20.setText(str);
            }
        }

        Cursor cursor2 = db.rawQuery("SELECT COUNT(*) FROM SPEED", null);//I will find the max speed
        if (cursor2 != null){
            cursor2.moveToFirst();
            if(cursor2.getInt(0) == 0){
                SpannableStringBuilder str = new SpannableStringBuilder("-Max Speed : " + " entry not found.");
                str.setSpan(new StyleSpan(Typeface.BOLD), 0, "-Max Speed : ".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                textView21.setText(str);
            }
            else{//this isn't the max speed but the highest speed achieved before we violated the limit
                Cursor c2 = db.rawQuery("SELECT * FROM SPEED",null);
                int i=0;
                int m=0;
                double maxspeed = 0;
                String date ="";
                double lat =0;
                double lon =0;
                double speed =0;
                Timestamp timestamp =null;
                while (c2.moveToNext()){
                    speed =c2.getDouble(1);
                    if (speed >= maxspeed){
                        m = i;
                        maxspeed = speed;
                        date =c2.getString(0);
                        lat =c2.getDouble(2);
                        lon =c2.getDouble(3);
                        try {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
                            Date parsedDate = dateFormat.parse(date);
                            timestamp = new java.sql.Timestamp(parsedDate.getTime());
                        } catch(Exception e) {}

                    }
                    i+=1;
                }
                SpannableStringBuilder str = new SpannableStringBuilder("-Max Speed : " + String.valueOf(maxspeed)+" km/h was achieved at "+timestamp+" at the location with coordinates " +String.valueOf(lat)+" "+String.valueOf(lon));
                str.setSpan(new StyleSpan(Typeface.BOLD), 0, "-Max Speed : ".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                textView21.setText(str);
            }
        }


        Cursor cursor3 = db.rawQuery("SELECT COUNT(*) FROM EVENTS", null);//we will find how many times we were within radius of a poi
        if (cursor3 != null){
            cursor3.moveToFirst();
            if(cursor3.getInt(0) == 0){
                SpannableStringBuilder str = new SpannableStringBuilder("-Sum of visited Points of Interest : " + " entry not found.");
                str.setSpan(new StyleSpan(Typeface.BOLD), 0, "-Sum of visited Points of Interest : ".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                textView22.setText(str);
            }
            else{
                long count = DatabaseUtils.queryNumEntries(db, "EVENTS");
                SpannableStringBuilder str = new SpannableStringBuilder("-Sum of visited Points of Interest : " + " is " + String.valueOf(count));
                str.setSpan(new StyleSpan(Typeface.BOLD), 0, "-Sum of visited Points of Interest : ".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView22.setText(str);

            }
        }

        times = new int[2];
        Arrays.fill(times, 0);

        Cursor cursor4 = db.rawQuery("SELECT COUNT(*) FROM EVENTS", null);//we find the most visited poi
        if (cursor4 != null){
            cursor4.moveToFirst();
            if(cursor4.getInt(0) == 0){
                SpannableStringBuilder str = new SpannableStringBuilder("-Most visited Point of Interest : " + " entry not found.");
                str.setSpan(new StyleSpan(Typeface.BOLD), 0, "-Most visited Point of Interest : ".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                textView23.setText(str);
            }
            else{
                Cursor c4 = db.rawQuery("SELECT * FROM EVENTS",null);
                String title ="";
                String date = "";
                String description = "";
                double lat =0;
                double lon =0;
                while (c4.moveToNext()){
                    title =c4.getString(1);
                    if (title.equals("University of Piraeus"))
                        times[0] +=1;
                    else if (title.equals("Parthenon"))
                        times[1] +=1;
                }
                int l = 0;
                for (int i =1; i < times.length; i++){
                    if(times[i] > times[l])
                        l=i;
                }
                if (l==0){
                    SpannableStringBuilder str = new SpannableStringBuilder("-Most visited Point of Interest : " +" is the University of Piraeus which was visited "+String.valueOf(times[l])+" times");
                    str.setSpan(new StyleSpan(Typeface.BOLD), 0, "-Most visited Point of Interest : ".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    textView23.setText(str);
                    button.setText("Show Information");
                    poi = "University of Piraeus";
                }
                else if (l==1){
                    SpannableStringBuilder str = new SpannableStringBuilder("-Most visited Point of Interest : " +" is the Parthenon which was visited "+String.valueOf(times[l])+" times");
                    str.setSpan(new StyleSpan(Typeface.BOLD), 0, "-Most visited Point of Interest : ".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    textView23.setText(str);
                    button.setText("Show Information");
                    poi = "Parthenon";
                }


            }
        }




    }

    public void info(View view){//if there is a most visited poi we click to see its information in an alertbox
        if (button.getText()=="Show Information"){
            Cursor cursor = db.rawQuery("SELECT * FROM POIS", null);
            while (cursor.moveToNext()){
                String t = cursor.getString(0);
                if (t.equals(poi)) {
                    String d = cursor.getString(1);
                    String c = cursor.getString(2);
                    double lat = cursor.getDouble(3);
                    double lon = cursor.getDouble(4);

                    StringBuilder sb = new StringBuilder();
                    sb.append("Title : " + t +"\n").
                            append("Category : " + c + "\n").
                            append("Location : " + lat + " " + lon + "\n").
                            append("Description : " + d);

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                    alertDialog.setTitle("Information");
                    alertDialog.setMessage(sb);

                    alertDialog.show();

                }

            }
        }

    }


    public void back(View view){//we go back to main menu-activity1
        startActivity(new Intent(this,Activity1.class));
    }
}

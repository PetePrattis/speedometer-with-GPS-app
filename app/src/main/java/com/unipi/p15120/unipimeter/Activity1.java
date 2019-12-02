package com.unipi.p15120.unipimeter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.location.Location;
import android.location.LocationListener;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.*;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.sql.Timestamp;
import java.util.Arrays;



public class Activity1 extends AppCompatActivity implements LocationListener{//implementing the Interface LocationListener for receiving notifications when the location has changed

    LocationManager locationManager;//initialize variable, type LocationManager which is a class that provides access to system location services
    SQLiteDatabase db;//initialize variable of sqlite database type
    TextView textView,textView2,textView3,textView4,textView5,textView6,textView7;//initialize my Views
    SeekBar seekbar1, seekbar2;
    Button button1,button2;
    long start,finish,time;//variables that will help me count time in milliseconds
    double lat1,lon1,lat2,lon2,alt1,alt2,time1,distance, speed;//variables to save the coordinates, time, distance and speed
    public String[] radiuspois;//an array in which I will save if I am within radius of each poi
    int counts,radius,maxspeed;
    boolean above;



    private class skListener1 implements SeekBar.OnSeekBarChangeListener {//a class that implements the Interface for receiving any change of a seekbar View
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {//a method of the Interface which is called when seekbar value has changed
            if (progress==0)//I change the radius according to the seekbar value, if value is 0 the radius will become 500 meters not 0
                textView3.setText(String.valueOf(500)+" meters");
            else//the radius scale is increasing by 1000 meters
                textView3.setText(String.valueOf(progress*1000)+" meters");
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}

    }

    private class skListener2 implements SeekBar.OnSeekBarChangeListener {//this is the class for the second seekbar
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (progress==0)
                textView5.setText(String.valueOf(5)+" km/h");
            else
                textView5.setText(String.valueOf(progress*10)+" km/h");
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}

    }

    @Override//methods of the Interface LocationListener that I implemented at my activity
    public void onLocationChanged(Location location) {//when the location has changed
        if(counts==0) {//If the counter is 0 which means it is the first time that the location has changed
            start = System.nanoTime();//a timer starts
            lat1 = location.getLatitude();//the coordinates are saved
            lon1 = location.getLongitude();
            distance = 0;//I initialize the distance
            //alt1 = location.getAltitude();//this would also save the altitude
            counts +=1;
        }
        else if (counts == 5){//if the location has changed 5 times (I choose 5 times nad not 0 in order for the calculations to be less)
            finish = System.nanoTime();//I finish the timer
            lat2 = location.getLatitude();//the coordinates are saved
            lon2 = location.getLongitude();
            //alt2 = location.getAltitude();
            time = finish - start;//I save the time in nanoseconds
            time1 = (double)time / 1_000_000_000.0;//I convert time in seconds
            distance = distance + measureDistance(lat1,lat2,lon1,lon2);//I calculate the distance between two points through my custom method measureDistance
            speed = distance / time1;//calculate the speed in meters/second
            speed = speed * 3.6;//convert speed from m/s to km/h
            speed = (int) speed;//i get rid of the decimal numbers
            textView.setText(Double.toString(speed));//the speed is shown
            start = 0;//restart time and counter
            finish = 0;
            counts=0;

            checkSpeed(lat2,lon2,speed);//method that checks if current speed is above the limit
            checkPOIs(lat2,lon2);//method that checks if we are within radius of a poi
        }
        else{
            lat2 = location.getLatitude();
            lon2 = location.getLongitude();
            distance = distance + measureDistance(lat1,lat2,lon1,lon2);//i calculate the distance travelled
            lat1 = lat2;
            lon1 = lon2;
            counts +=1;
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        seekbar1 =findViewById(R.id.seekBar);
        seekbar1.setOnSeekBarChangeListener(new skListener1());
        textView4 = findViewById(R.id.textView4);
        textView5 = findViewById(R.id.textView5);
        seekbar2 =findViewById(R.id.seekBar2);
        seekbar2.setOnSeekBarChangeListener(new skListener2());
        textView2.setVisibility(View.INVISIBLE);
        textView3.setVisibility(View.INVISIBLE);
        seekbar1.setVisibility(View.INVISIBLE);
        textView4.setVisibility(View.INVISIBLE);
        textView5.setVisibility(View.INVISIBLE);
        seekbar2.setVisibility(View.INVISIBLE);
        button1 = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        textView6 = findViewById(R.id.textView6);
        textView7 = findViewById(R.id.textView7);
        textView.setBackgroundColor(getResources().getColor(R.color.ic_launcher_background));
        textView2.setBackgroundColor(getResources().getColor(R.color.ic_launcher_background));
        textView3.setBackgroundColor(getResources().getColor(R.color.ic_launcher_background));
        textView4.setBackgroundColor(getResources().getColor(R.color.ic_launcher_background));
        textView5.setBackgroundColor(getResources().getColor(R.color.ic_launcher_background));
        seekbar1.setBackgroundColor(getResources().getColor(R.color.ic_launcher_background));
        seekbar2.setBackgroundColor(getResources().getColor(R.color.ic_launcher_background));
        textView6.setBackgroundColor(getResources().getColor(R.color.ic_launcher_background));
        textView7.setBackgroundColor(getResources().getColor(R.color.ic_launcher_background));
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        distance =0;
        start = 0;
        counts = 0;
        above = false;
        radiuspois = new String[2];//this array is initialized by 'out' which means we are not within radius of any poi
        Arrays.fill(radiuspois, "out");

        db = openOrCreateDatabase("UnipiMeter", Context.MODE_PRIVATE, null);//creation of the database and the tables
        db.execSQL("CREATE TABLE IF NOT EXISTS SPEED(time VARCHAR,speed INTEGER, lat DOUBLE, lon DOUBLE);");//this table has the recordings of the speeds that exceeded the limit
        db.execSQL("CREATE TABLE IF NOT EXISTS EVENTS(time VARCHAR, poi TEXT, lat DOUBLE, lon DOUBLE);");//this table has the  recordings of the times we were within radius of a poi
        db.execSQL("CREATE TABLE IF NOT EXISTS POIS(title TEXT, description TEXT, category TEXT, lat DOUBLE, lon DOUBLE);");//this table has the recordings of the pois and their information
        db.execSQL("CREATE TABLE IF NOT EXISTS MAX(radius INTEGER, speed INTEGER);");//this table has the recordings of the radius and the max speed

        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM POIS", null);//I initialize the pois
        if (cursor != null){
            cursor.moveToFirst();
            if(cursor.getInt(0) == 0){
                db.execSQL("INSERT INTO POIS VALUES ('University of Piraeus', 'The University of Piraeus is a Greek public university located in Piraeus, Greece with a total of nine academic departments focused mainly on Statistics, Economics, Finance, Business Management and Information Technology.','education',37.941770, 23.652772);");
                db.execSQL("INSERT INTO POIS VALUES ('Parthenon', 'The Parthenon is a former temple on the Athenian Acropolis, Greece, dedicated to the goddess Athena whom the people of Athens considered their patron.','monuments',37.971514, 23.727337);");

            }
        }

        Cursor cursor1 = db.rawQuery("SELECT COUNT(*) FROM MAX", null);//I insert the radius and the speed limit
        if (cursor1 != null){
            cursor1.moveToFirst();
            if(cursor1.getInt(0) == 0){
                db.execSQL("INSERT INTO MAX VALUES (10000,60);");
            }
        }

        Cursor cursor2 = db.rawQuery("SELECT * FROM MAX", null);//I get the radius and the speed limit
        while (cursor2.moveToNext()){
            radius=cursor2.getInt(0);
            maxspeed=cursor2.getInt(1);
        }

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)//if the permission for location access is given
            button1.setText("Start");
        else//if not
            button1.setText("Activation");

    }

    public void start(View view){//on start button click
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && button1.getText()=="Start"){//if the permission for location access is given
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 0,this);//we request location updates
            button1.setText("Exit");
        }
        else if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && button1.getText()=="Exit"){//if we want to exit the app
            System.exit(0);
        }
        else if (button1.getText() == "Activation"){//if we haven't given permission yet
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},6);//we request the permission
            button1.setText("Start");
        }
    }

    public double measureDistance(Double lat1,Double lat2,Double lon1,Double lon2){//custom method that calculates distance between two points
        //formula found on the web
        final int R = 6371; // Radius of the earth
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters
        return distance;
    }

    public void checkPOIs(Double latitude1,Double longitude1){//custom method that checks if we are within radius of a poi
        Cursor cursor = db.rawQuery("SELECT * FROM POIS",null);//we get for every poi its information
        int i=0;
        while (cursor.moveToNext()){
            String title = cursor.getString(0);
            String description = cursor.getString(1);
            String category = cursor.getString(2);
            double latitude2=cursor.getDouble(3);
            double longitude2=cursor.getDouble(4);

            double distance = measureDistance(latitude1,latitude2,longitude1,longitude2);
            if(radiuspois[i]=="out" && distance > radius){//if we weren't within radius before and now we are
                radiuspois[i]="in";
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                Toast.makeText(this,"We are within radius of point of interest: "+title+" with distance "+distance+" meters.",Toast.LENGTH_SHORT).show();
                db.execSQL("INSERT INTO EVENTS VALUES ('"+timeStamp+"', '"+title+"', '"+latitude2+"', '"+longitude2+"');");
            }
            else if (radiuspois[i] == "in" && distance <= radius){//if we are no longer within radius
                radiuspois[i] = "out";
                Toast.makeText(this,"We are no longer within radius of point of interest: "+title,Toast.LENGTH_SHORT).show();
            }
            i +=1;

        }
    }

    public void checkSpeed(Double latitude,Double longitude, Double speed){//custom method that checks if we exceeded speed limit
        if (speed >= maxspeed && above == false) {
            above = true;
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            db.execSQL("INSERT INTO SPEED VALUES ('"+timeStamp+"', '"+speed+"', '"+latitude+"', '"+longitude+"');");//we insert the recording into SPEED table
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
                Date parsedDate = dateFormat.parse(timeStamp);
                Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
                Toast.makeText(this,"You exceeded max speed at "+ timestamp,Toast.LENGTH_SHORT).show();
            } catch(Exception e) {}

        }
        else if( speed < maxspeed && above == true)
            above =false;

    }
    public void configure(View view){//the configure button is to change radius and max speed
        if (button2.getText().equals("Configure")) {
            button2.setText("Save Changes");
            textView2.setVisibility(View.VISIBLE);
            textView3.setVisibility(View.VISIBLE);
            seekbar1.setVisibility(View.VISIBLE);
            textView3.setText(String.valueOf(radius)+ " meters");
            textView4.setVisibility(View.VISIBLE);
            textView5.setVisibility(View.VISIBLE);
            seekbar2.setVisibility(View.VISIBLE);
            textView5.setText(String.valueOf(maxspeed)+ " km/h");

            if (radius == 500)//every time we show the seekbars we initialize their values according to what the variables radius and maxspeed are
                seekbar1.setProgress(0);
            else
                seekbar1.setProgress(radius / 1000);

            if(maxspeed == 5)
                seekbar2.setProgress(0);
            else
                seekbar2.setProgress(maxspeed / 10);

        }
        else if (button2.getText().equals("Save Changes")){//we save the changes
            button2.setText("Configure");

            if (seekbar1.getProgress()==0)
                radius = 500;
            else
                radius = seekbar1.getProgress() * 1000;

            if (seekbar2.getProgress()==0)
                maxspeed = 5;
            else
                maxspeed = seekbar2.getProgress() * 10;

            db.execSQL("DELETE FROM MAX");//we overwrite the previous recording
            db.execSQL("INSERT INTO MAX VALUES ('"+radius+"', '"+maxspeed+"');");
            textView2.setVisibility(View.INVISIBLE);
            textView3.setVisibility(View.INVISIBLE);
            seekbar1.setVisibility(View.INVISIBLE);
            textView4.setVisibility(View.INVISIBLE);
            textView5.setVisibility(View.INVISIBLE);
            seekbar2.setVisibility(View.INVISIBLE);


        }

    }

    public void statistics(View view){//we go to the statistics menu at the activity2
        startActivity(new Intent(this,Activity2.class));

    }

}

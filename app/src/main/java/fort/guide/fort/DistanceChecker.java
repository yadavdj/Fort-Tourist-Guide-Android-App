package fort.guide.fort;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by dj on 22/3/19.
 */

public class DistanceChecker implements GPSManager.MLocationListener,Runnable {

    Location location;
FortSource fortSource;
Retrofit retrofit;
JSONPlaceHolder jsonPlaceHolder;
public static final String TAG ="Distance Checker";
Context context;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public DistanceChecker(Context context){

        fortSource = new FortSource();
    }


    @Override
    public void run() {

        while (true){
            try {

                Log.d("Distance Checker"," Thread Started ");
                Thread.sleep(3000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }


    }


    public void getAlert(){

        try{

            Log.d("Distance Checker"," Get Alert ");




        }catch (Exception e ){
            e.printStackTrace();
        }

    }





        @Override
    public void onLocation(Location location) {

        setLocation(location);

        Log.d(TAG,"location : "+location.getLatitude()+" longitude : "+location.getLongitude());

    }

}

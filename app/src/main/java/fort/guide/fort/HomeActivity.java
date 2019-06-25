package fort.guide.fort;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends FragmentActivity implements OnMapReadyCallback,GPSManager.MLocationListener ,Runnable{

    private GoogleMap mMap;
    ImageView searchButton;
    AutoCompleteTextView searchBar;
    double latitude, longitude;
    String[] fortName = {"Torna fort", "Rajgad fort", "Sihngad fort", "Lohgad fort", "Raigad fort", "koyna dam", "Panshet dam", "Khadakwasala"};
    Retrofit retrofit;
    JSONPlaceHolder jsonPlaceHolder;
    static String source;
    ImageView danger,gpsButton;
    static FortSource fortSource;
    private static final String TAG = "HomeActivity";
    GPSManager gpsManager;
    Location location;
    Marker obstacle;
    DistanceChecker distanceChecker;
    BottomSheetBehavior bottomSheetBehavior;
    ImageView imageOfObject;
    TextView nameOfObject;
    TextView infoOfObject;
    Location obstacleLocationChecking;
    Location userLocationForChecking;
    int i=0;
    Thread searchObstacle;
    EditText latEdit,longEdit;
    Button submit;
    static double latDouble;
    static double longDouble;



    MediaPlayer mediaPlayer;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public HomeActivity() {

        fortSource = new FortSource();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.43.30:8587/packet/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        gpsManager = new GPSManager(this);
        obstacleLocationChecking = new Location("obstacle");
        userLocationForChecking = new Location("user");
        mediaPlayer = new MediaPlayer();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        LinearLayout linearLayout = findViewById(R.id.bottomLayout);
        searchButton = findViewById(R.id.search_button);
        searchBar = findViewById(R.id.search_bar);
        danger = findViewById(R.id.danger);
        gpsButton = findViewById(R.id.gps);
        imageOfObject = findViewById(R.id.imageOfObject);
        nameOfObject = findViewById(R.id.name);
        infoOfObject = findViewById(R.id.infoOfObject);
        searchObstacle = new Thread(new HomeActivity());
        latEdit= findViewById(R.id.latEdit);
        longEdit=findViewById(R.id.longEdit);
        submit = findViewById(R.id.submit);

        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);
        bottomSheetBehavior.setHideable(false);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(HomeActivity.this, android.R.layout.simple_list_item_1, fortName);
        searchBar.setAdapter(adapter);

        gpsManager.getLocation(HomeActivity.this);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (latEdit.getText().toString() != null && longEdit.getText().toString() != null){

                    latDouble = Double.parseDouble(latEdit.getText().toString());
                    longDouble = Double.parseDouble(longEdit.getText().toString());

                    if (!searchObstacle.isAlive()){
                        searchObstacle.start();
                    }

                }



            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(HomeActivity.this,MainActivity.class);
                startActivity(intent);

                GeoCoding();


            }
        });

        gpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(HomeActivity.this,"GPS is started :",Toast.LENGTH_LONG).show();
                Log.d(TAG,"GPS is started ");

                Log.d(TAG,"Thread is started");

                try{

                    LatLng latLng = new LatLng(mMap.getMyLocation().getLatitude(),mMap.getMyLocation().getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                    Log.d(TAG,"LatLng : "+getLocation().getLatitude()+" "+getLocation().getLongitude());


                }catch (Exception e){
                    e.printStackTrace();

                }

            }
        });


        danger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Toast.makeText(HomeActivity.this, "Loading Data", Toast.LENGTH_LONG).show();
                    if (source!=null) {
                        getPackets(source);
                        }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void GeoCoding() {

        Geocoder geocoder = new Geocoder(HomeActivity.this);

        List<Address> list = new ArrayList<>();

        try {

            source = searchBar.getText().toString();
            list = geocoder.getFromLocationName(source, 1);

            if (list.size() > 0) {
                Address address = list.get(0);

                latitude = address.getLatitude();
                longitude = address.getLongitude();
                LatLng location = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(location).title(address.getLocality()));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 16));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable((Context) context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public void getPackets(String source) {

        jsonPlaceHolder = retrofit.create(JSONPlaceHolder.class);
        Call<List<Packet>> call = jsonPlaceHolder.getPackets(source);

        call.enqueue(new Callback<List<Packet>>() {
            @Override
            public void onResponse(Call<List<Packet>> call, Response<List<Packet>> response) {

                fortSource.addAll(response.body());

                for (Packet packet : fortSource) {
                    Log.d(TAG, "latitude : " + packet.getLatitude() + " longitude : " + packet.getLongitude());
                    LatLng location = new LatLng(packet.getLatitude(), packet.getLongitude());
                    obstacle=mMap.addMarker(new MarkerOptions().position(location).icon(bitmapDescriptorFromVector(HomeActivity.this, R.drawable.ic_assignment_late_black_24dp)).snippet(packet.getObstacleName()));
                    obstacle.setVisible(true);
                }

                Log.d(TAG, "Response Code " + fortSource.size());

                Log.d(TAG, "List size " + response.body().size());

            }

            @Override
            public void onFailure(Call<List<Packet>> call, Throwable t) {


                Log.d(TAG, "Failure " + t.getMessage());

            }
        });


    }


    private void markerfinder(String markerString) {

        for (Packet packet : fortSource) {
            if (markerString.equals(packet.getObstacleName())) {
                Log.d(TAG, "This marker has Obstacle Match : " + packet.getObstacleName());
                Log.d(TAG, "This marker has Get Audio : " + packet.getAudio());
                Log.d(TAG, "This marker has Obstacle Image : " + packet.getImageUrl());
                String url=packet.getImageUrl();
                Picasso.with(HomeActivity.this).load("https://firebasestorage.googleapis.com/v0/b/images-c1253.appspot.com/o/Sinhgad%20Fort%2FBatata%20point.JPG?alt=media&token=f7ae183a-8397-48ad-8a7e-75ba4ced1234").into(imageOfObject);
                playMusic("https://firebasestorage.googleapis.com/v0/b/images-c1253.appspot.com/o/Sinhgad%20Fort%2FAudio%2FBatata%20Point.mp3?alt=media&token=c7638cc0-99fd-4039-a48b-7641496864be");
                nameOfObject.setText(packet.getObstacleName());
                infoOfObject.setText(packet.getImageUrl().toString());
            }else {
                Log.d(TAG,"It does not have obstacle match");
            }
        }

    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(2);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.d(TAG,"marker.getSnippet() : "+marker.getSnippet());
                markerfinder(marker.getSnippet());

                return false;
            }
        });
    }

    @Override
    public void onLocation(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng).icon(bitmapDescriptorFromVector(HomeActivity.this,R.drawable.danger)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
    }

    @Override
    public void run() {
        while (true){
                try{
                getPackets();
                Thread.sleep(6000);
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void playMusic(String audio){
        try {
            mediaPlayer.reset();
            Uri uri = Uri.parse(audio);
            mediaPlayer.setDataSource(this,uri);
            mediaPlayer.prepare();
            Log.d(TAG,"Audio is ready");
            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getPackets(){
        Log.d(TAG,"fortsource size : "+fortSource.size());
        if (fortSource.size()>i) {
            if (latDouble!=0&&longDouble!=0) {
                obstacleLocationChecking.setLatitude(latDouble);
                obstacleLocationChecking.setLongitude(longDouble);
                userLocationForChecking.setLatitude(fortSource.get(i).getLatitude());
                userLocationForChecking.setLongitude(fortSource.get(i).getLongitude());

                float locationDistance = obstacleLocationChecking.distanceTo(userLocationForChecking);

                if (30.0 >= locationDistance) {
                    Log.d(TAG, "Distance is not correct : " + locationDistance);
                    //Toast.makeText(HomeActivity.this,"Danger zone is near to you",Toast.LENGTH_LONG).show();

                } else {
                    playMusic("https://firebasestorage.googleapis.com/v0/b/images-c1253.appspot.com/o/Sinhgad%20Fort%2FAudio%2FBatata%20Point.mp3?alt=media&token=c7638cc0-99fd-4039-a48b-7641496864be");

                    mediaPlayer.reset();
                    Uri uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/images-c1253.appspot.com/o/Sinhgad%20Fort%2FAudio%2FBatata%20Point.mp3?alt=media&token=c7638cc0-99fd-4039-a48b-7641496864be");
                    try {
                        //mediaPlayer.setDataSource(getApplication(),uri);
                        mediaPlayer.setDataSource(uri.toString());
                        mediaPlayer.prepare();
                        Log.d(TAG, "Audio is ready");
                        mediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "Distance is above 30 KM " + locationDistance);
                    //Toast.makeText(this,"zone is not near to you",Toast.LENGTH_LONG).show();
                }

                i++;
            }
        }
    }

/*

    private double meterDistanceBetweenPoints(double lat_a, double lng_a, double lat_b, double lng_b) {
        float pk = (float) (180.f/Math.PI);

        float a1 = (float) (lat_a / pk);
        float a2 = (float) (lng_a / pk);
        float b1 = (float) (lat_b / pk);
        float b2 = (float) (lng_b / pk);

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000 * tt;
    }
*/

}

package awesome.team.bluetooth230_2;

import android.bluetooth.BluetoothGattService;
import android.content.ActivityNotFoundException;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//For animation:
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


public class MainActivity extends Activity implements OnTouchListener{

    private Timer timer = new Timer();
    Handler handler = new Handler();

    //Debugging
    private static final String TAG = "BluetoothActivity";

    //Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 0;
    private static final int REQUEST_ENABLE_BT = 1;

    //Bluetooth Items
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothService mBluetoothService;


    //UI elements
    private TextView Text_Day_Date;
    private View Image1;
    private View Image2;
    private View Image3;
    private View Image4;

    private ImageView Image5;

    private View Image_Left;
    private View Image_Right;

    private boolean connected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Text_Day_Date = (TextView) findViewById(R.id.Text_Day_Date);
        Image1 = (View) findViewById(R.id.Image1);
        Image2 = (View) findViewById(R.id.Image2);
        Image3 = (View) findViewById(R.id.Image3);
        Image4 = (View) findViewById(R.id.Image4);

        Image5 = (ImageView) findViewById(R.id.Image5);

        Image_Left = (View) findViewById(R.id.Image_Left);
        Image_Right = (View) findViewById(R.id.Image_Right);

        Image1.setOnTouchListener(this);
        Image2.setOnTouchListener(this);
        Image3.setOnTouchListener(this);
        Image4.setOnTouchListener(this);

        Image5.setOnTouchListener(this);

        Image_Left.setOnTouchListener(this);
        Image_Right.setOnTouchListener(this);


        //Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //If the adapter is ull, then Bluetooth is not supported
        if(mBluetoothAdapter == null){
            Toast.makeText(this,"Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        mBluetoothService = new BluetoothService(this);


        //Search for bluetooth Device (PC)
        Intent intent = new Intent(this,DeviceListActivity.class);
        try {
            startActivityForResult(intent, REQUEST_CONNECT_DEVICE);
        }catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
        //Get 1s time started, 1-shot:
        doTimerTask();

    }

    private void doTimerTask(){

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        displayTimeDate();
                    }
                });
            }
        },0,1000);

    }

    private void displayTimeDate(){
        Date CurDate = new Date();

        //SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MM d '/' hh:mm:ss a");
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMM d, yyyy");

        String formattedDateString = formatter.format(CurDate);
        Text_Day_Date.setText(formattedDateString);
    }



    protected void onStart(){
        super.onStart();
        //If BT is not on, request that it be enabled.
        //SetupChat() will then be called during onActivityResult
        if(!mBluetoothAdapter.isEnabled()){
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
    }


    protected void onDestroy(){
        super.onDestroy();
        if(mBluetoothService != null){
            mBluetoothService.stop();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.button_scan:
                Intent intent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(intent, REQUEST_CONNECT_DEVICE);
        }

        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode){
            case REQUEST_CONNECT_DEVICE:
                if(resultCode == Activity.RESULT_OK){
                    //Get Device MAC Address
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    //Get BluetoothDevice Object
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    //Attempt to Connect to Device
                    mBluetoothService.connect(device);
                    connected = true;
                }
                break;
            case REQUEST_ENABLE_BT:
                if(resultCode != Activity.RESULT_OK){
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_LONG).show();
                    finish();
                }
        }
    }



    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //Load animations
        Animation linear = AnimationUtils.loadAnimation(this, R.anim.in_out);

        if(connected){
            switch(event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    switch(v.getId()){
                        case R.id.Image1:
                            mBluetoothService.write('1');
                            Image1.startAnimation(linear);

                            Image5.setImageResource(R.drawable.coffee_cup_1_1920x1080);

                            break;
                        case R.id.Image2:
                            mBluetoothService.write('2');
                            Image2.startAnimation(linear);

                            Image5.setImageResource(R.drawable.summer_white_beach_1920x1080);

                            break;
                        case R.id.Image3:
                            //Insert our codes here
                            break;
                        case R.id.Image4:
                            //Insert our codes here
                            break;

                        case R.id.Image_Left:
                            mBluetoothService.write('B');
                            Image_Left.startAnimation(linear);
                            break;
                        case R.id.Image_Right:
                            mBluetoothService.write('C');
                            Image_Right.startAnimation(linear);
                            break;
                    }
                    break;
            }
        }

        return true;
    }
}

package awesome.team.bluetooth230_2;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

/**
 * Created by Vader on 6/24/2015.
 */
public class BluetoothService {

    //Debugging
    private static final String TAG = "BluetoothService";

    //Unique UUID for this application
    //private static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0900200c9a66");
    //Well known SPP UUID (will *probably* map to RFCOMM channel 1 (default) if not in use)
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//"00001101-0000-1000-8000-00805F9B34FB"
    private BluetoothAdapter mBluetoothAdapter;
    private ConnectThread mConnectThread;

    private boolean connected = false;

    /*
    BluetoothDevice/Socket member field?
    boolean statel
    public run/cancel out of connect Thread class
     */


    public BluetoothService(Context context){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public synchronized void connect(BluetoothDevice device){
        //cancel any thread currently running a connection
        if(mConnectThread != null){
            mConnectThread.cancel();
            mConnectThread = null;
        }

        //Start new thread with given device
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        connected = true;
    }

    public void write(char b){
        if(connected){
           byte buffer = (byte)b;
            //create temporary object
            ConnectThread r;
            //Synchronize a copy of the ConnectedThread
            synchronized(this){
                r = mConnectThread;
            }
            //perform write unsynchronized
            r.write(buffer);
        }
    }

    public synchronized void stop(){
        if(mConnectThread != null){
            mConnectThread.cancel();
            mConnectThread = null;
            connected = false;
        }
    }


    private class ConnectThread extends Thread{
        private final BluetoothSocket mSocket;
        private final BluetoothDevice mDevice;
        private OutputStream mOutputStream;

        public ConnectThread(BluetoothDevice device){
            mDevice = device;
            BluetoothSocket temp = null;

            //Get a BluetoothSocket for a connection with the given BluetoothDevice
            try{
                temp = mDevice.createRfcommSocketToServiceRecord(MY_UUID);
            }catch (IOException e){
                Log.e(TAG,"create socket failed",e);
            }
            mSocket = temp;
        }

        public void run(){
            Log.i(TAG, "ConnectThread run()");

            //Always cancel discovery because it will slow down a connection
            mBluetoothAdapter.cancelDiscovery();

            //Make Connection to the BluetoothSocket
            try{
                //this is a blocking call and will only return on a successful connection or an exception
                mSocket.connect();
            }catch(IOException e){
                Log.e(TAG, "mSocket.connect90 failed", e);
                try{
                    mSocket.close();
                }catch(IOException e2){
                    Log.e(TAG, "unable to close() socket during connection Failure", e2);
                }
            }

            OutputStream tempOut = null;
            //Get the Bluetooth output stream
            try{
                tempOut = mSocket.getOutputStream();
            }catch(IOException e){
                Log.e(TAG, "temp outputStream not created", e);
            }
            mOutputStream = tempOut;
        }

        public void write(byte buffer){
            if(mOutputStream != null){
                try{
                    mOutputStream.write(buffer);
                }catch(IOException e){
                    Log.e(TAG,"Exception during write",e);
                }
            }
        }

        public void cancel(){
            try{
                mSocket.close();
            }catch(IOException e){
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }


    }


}

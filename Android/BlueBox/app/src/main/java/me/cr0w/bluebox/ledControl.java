package me.cr0w.bluebox;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import java.io.IOException;
import java.util.UUID;


public class ledControl extends AppCompatActivity {


    Button  btnDis;
    ImageButton lights,send;
    EditText inmsg;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    ConnectBT btobj=new ConnectBT();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led_control);

        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS);
        lights=(ImageButton)findViewById(R.id.lightbtn);
        btnDis=(Button)findViewById(R.id.disc);
        inmsg=(EditText)findViewById(R.id.inputmsg);
        send=(ImageButton)findViewById(R.id.sndMsg);

        btobj.execute();

        lights.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                led();
            }
        });
        btnDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = inmsg.getText().toString();
                inmsg.getText().clear();
                disp(str);
            }
        });


    }


    private class ConnectBT extends AsyncTask<Void, Void, Void> // UI thread
    {
        private boolean ConnectSuccess = true;
        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(ledControl.this, "Connecting...", "Please wait!!!");
        }
        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            }
            catch (IOException e) {
                ConnectSuccess = false;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!ConnectSuccess) {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else { msg("Connected to device");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }

    private void disconnect() {
        if (btSocket!=null) //If the btSocket is busy
        {
            try {
                btSocket.close(); //close connection
            }
            catch (IOException e) {
                msg("Error");
            }
        }
        finish(); //return to the first layout}
    }

    private void led() {
        if (btSocket!=null) {
            try {
                btSocket.getOutputStream().write("lights".toString().getBytes());
            }
            catch (IOException e) {
                msg("Unable to perform operation");
            }
        }
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    private void disp(String s){
        if (btSocket!=null) {
            try {
                btSocket.getOutputStream().write(s.getBytes());
            }
            catch (IOException e) {
                msg("Unable to perform operation");
            }
        }
    }


}

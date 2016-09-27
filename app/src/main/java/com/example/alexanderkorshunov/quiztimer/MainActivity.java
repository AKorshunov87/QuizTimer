package com.example.alexanderkorshunov.quiztimer;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements OnClickListener {

    private static final int ADMIN_INTENT = 15;
    private static final String description = "Some Description About Your Admin";
    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mComponentName;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
      /* do what you need to do */
            ButtonEnabled(true);
            mDevicePolicyManager.lockNow();
            KeyguardManager keyguardManager = (KeyguardManager)getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
            if( keyguardManager.inKeyguardRestrictedInputMode()) {
                //it is locked
            } else {
                //it is not locked
                /* and here comes the "trick" */
                ButtonEnabled(false);
                handler.postDelayed(this, 100);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDevicePolicyManager = (DevicePolicyManager)getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        mComponentName = new ComponentName(this, MyAdminReceiver.class);
        Button btnEnableAdmin = (Button) findViewById(R.id.btnEnableAdmin);
        Button btnDisableAdmin = (Button) findViewById(R.id.btnDisableAdmin);
        Button btnLock = (Button) findViewById(R.id.btnLock);
        btnEnableAdmin.setOnClickListener(this);
        btnDisableAdmin.setOnClickListener(this);
        btnLock.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnEnableAdmin:
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,description);
                startActivityForResult(intent, ADMIN_INTENT);
                break;

            case R.id.btnDisableAdmin:
                mDevicePolicyManager.removeActiveAdmin(mComponentName);
                Toast.makeText(getApplicationContext(), "Admin registration removed", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnLock:
                boolean isAdmin = mDevicePolicyManager.isAdminActive(mComponentName);
                if (isAdmin) {
                    int ms = Integer.parseInt(((EditText)findViewById(R.id.txtBxMs)).getText().toString());
/*                    Timer timer = new Timer("LockTimer");
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            ButtonEnabled(true);
                            mDevicePolicyManager.lockNow();
                        }
                    }, ms);*/

                    handler.postDelayed(runnable, ms);
                    ButtonEnabled(false);
                }else {
                    Toast.makeText(getApplicationContext(), "Not Registered as admin", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADMIN_INTENT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Registered As Admin", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(), "Failed to register as Admin", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void ButtonEnabled(boolean isEnabled) {
        (findViewById(R.id.btnDisableAdmin)).setEnabled(isEnabled);
        (findViewById(R.id.btnEnableAdmin)).setEnabled(isEnabled);
        (findViewById(R.id.txtBxMs)).setEnabled(isEnabled);
        (findViewById(R.id.btnLock)).setEnabled(isEnabled);
    }

}
package com.example.finalrfid;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalrfid.firebase.CanchaFirebase;
import com.example.finalrfid.ui.dashboard.DashboardFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.IOException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "nfcinventory_simple";

    // NFC-related variables
    NfcAdapter mNfcAdapter;
    PendingIntent mNfcPendingIntent;
    IntentFilter[] mReadWriteTagFilters;
    public boolean mWriteMode = false;
    public boolean mAuthenticationMode = false;
    public boolean ReadUIDMode = true;
    String[][]mTechList;

    AlertDialog mTagDialog;
    RadioGroup mRadioGroup;

    private TextView mTextMessage;
    //Sector num
    int sectorNum = 01;
    String blockNum="04";
    String inverseBlockNum = "FB";
    String keysNum = "07";

    public String hexKeyA = "FFFFFFFFFFFF";
    public String hexKeyB = "FFFFFFFFFFFF";
    public String newHexKeyA = "";
    public String newHexKeyB = "";
    public int depositMoney = 0;
    public int currentMoney=0;
    public boolean read = false;

    public DashboardFragment listaFragment;
    public boolean isInCurrentDeposit = false;


    public String selectedKey = "";

    boolean keys = false;

    EditText mHexKeyA;
    EditText mHexKeyB;
    EditText mDatatoWrite;

    public int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        CanchaFirebase x = new CanchaFirebase();
        x.cargaDato();

    }

}

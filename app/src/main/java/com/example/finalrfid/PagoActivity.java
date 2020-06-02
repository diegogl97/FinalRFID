package com.example.finalrfid;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalrfid.firebase.CanchaFirebase;

import java.io.IOException;
import java.util.Locale;

public class PagoActivity extends AppCompatActivity {
    private static final String TAG = "nfcinventory_simple";

    // NFC-related variables
    NfcAdapter mNfcAdapter;
    PendingIntent mNfcPendingIntent;
    IntentFilter[] mReadWriteTagFilters;
    private boolean mWriteMode = false;
    private boolean mAuthenticationMode = false;
    private boolean ReadUIDMode = false;
    String[][]mTechList;

    private boolean tagRead = false;
    // UI elements
    EditText mTagUID;
    EditText mCardType;
    EditText mHexKeyA;
    EditText mHexKeyB;
    EditText mSector;
    EditText mDataBloque;
    EditText mDatatoWrite;
    AlertDialog mTagDialog;
    RadioGroup mRadioGroup;
    int saldo = 0;
    //SE ALMACENA EN SECTOR 29


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pago);

        mHexKeyA = ((EditText) findViewById(R.id.editTextKeyA));
        mHexKeyB = ((EditText) findViewById(R.id.editTextKeyB));
        mDataBloque = ((EditText) findViewById(R.id.editTextBloqueLeido));
        mDatatoWrite = ((EditText) findViewById(R.id.editTextBloqueAEscribir));
        mRadioGroup = ((RadioGroup) findViewById(R.id.rBtnGrp));

        TextView n = (TextView) findViewById(R.id.pagoNom);
        TextView m = (TextView) findViewById(R.id.pagoMat);
        TextView hi = (TextView) findViewById(R.id.pagoHora);
        TextView c = (TextView) findViewById(R.id.pagoCancha);
        TextView hp = (TextView) findViewById(R.id.pagoHorasP);
        TextView t = (TextView) findViewById(R.id.pagoTotal);
        n.setText(MainActivity.nombreP);
        m.setText(MainActivity.matriculaP);
        hi.setText(MainActivity.horaIP);
        c.setText(MainActivity.canchaP);
        hp.setText(Integer.toString(MainActivity.horasP));
        t.setText("$" + Integer.toString(MainActivity.total));


        findViewById(R.id.pagar_button).setOnClickListener(mTagRead);
        // get an instance of the context's cached NfcAdapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        // if null is returned this demo cannot run. Use this check if the
        // "required" parameter of <uses-feature> in the manifest is not set
        if (mNfcAdapter == null)
        {
            Toast.makeText(this,
                    "Su dispositivo no soporta NFC. No se puede correr la aplicación.",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // check if NFC is enabled
        checkNfcEnabled();

        // Handle foreground NFC scanning in this activity by creating a
        // PendingIntent with FLAG_ACTIVITY_SINGLE_TOP flag so each new scan
        // is not added to the Back Stack
        mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        // Create intent filter to handle MIFARE NFC tags detected from inside our
        // application
        IntentFilter mifareDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);

        try {
            mifareDetected.addDataType("application/com.e.mifarecontrol");
        } catch (IntentFilter.MalformedMimeTypeException e)
        {
            throw new RuntimeException("No se pudo añadir un tipo MIME.", e);
        }

        // Create intent filter to detect any MIFARE NFC tag
        mReadWriteTagFilters = new IntentFilter[] { mifareDetected };


        // Setup a tech list for all NFC tags
        mTechList = new String[][] { new String[] { MifareClassic.class.getName() } };

        resolveReadIntent(getIntent());

        Button back = findViewById(R.id.volver_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    void resolveReadIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            MifareClassic mfc = MifareClassic.get(tagFromIntent);


                try {
                    mfc.connect();
                    boolean auth = true;
                    String hexkey = "";

                    int sector = 7;
                    byte[] datakey;

                    hexkey = "ffffffffffff";
                    datakey = hexStringToByteArray(hexkey);
                    auth = mfc.authenticateSectorWithKeyB(sector, datakey);

                    if(auth){
                        int bloque = 29;
                        byte[] dataread = mfc.readBlock(bloque+1);
                        Log.i("Bloques", getHexString(dataread, dataread.length));

                        dataread = mfc.readBlock(bloque);
                        String blockread = getHexString(dataread, dataread.length);
                        Log.i(TAG, "Bloque Leido: " + blockread);
                        blockread = HexToDecimal(blockread);
                        saldo = Integer.parseInt(blockread);


                    }else{ // Authentication failed - Handle it
                        Toast.makeText(this,
                                "Lectura de bloque FALLIDA dado autentificación fallida.",
                                Toast.LENGTH_LONG).show();
                    }

                    mfc.close();
                    mTagDialog.cancel();

                }catch (IOException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            if (saldo < MainActivity.total){
                Toast.makeText(getApplicationContext(), "Saldo insuficiente, realiza una recarga", Toast.LENGTH_LONG).show();
            }
            else{
                resolveWriteIntent(intent);
                resolveWriteIntent(getIntent());

                CanchaFirebase x = new CanchaFirebase();
                x.cargaDato(MainActivity.nombreP,MainActivity.matriculaP,MainActivity.canchaP, MainActivity.horaIP,MainActivity.horasP);
                Toast.makeText(getApplicationContext(), "Registro completo", Toast.LENGTH_LONG).show();
                finish();
            }


        }
    }


    void resolveWriteIntent(Intent intent) {


        String action = intent.getAction();
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            MifareClassic mfc = MifareClassic.get(tagFromIntent);

            try {
                mfc.connect();
                boolean auth = false;
                String hexkey = "";
                int bloque = 29;
                int sector = 7;
                byte[] datakey;


                hexkey = "ffffffffffff";
                datakey = hexStringToByteArray(hexkey);
                auth = mfc.authenticateSectorWithKeyB(sector, datakey);

                if(auth){
                    String strdata = Integer.toString(MainActivity.total);
                    // HACER DECIMAL A HEXADECIMAL
                    int hex = Integer.parseInt(strdata);
                    saldo = saldo - hex;
                    strdata = Integer.toHexString(saldo);
                    String x = HexFill(strdata);
                    strdata = x;

                    byte[] datatowrite = hexStringToByteArray(strdata);
                    mfc.writeBlock(bloque, datatowrite);


                }else{ // Authentication failed - Handle it
                    Toast.makeText(this,
                            "Escritura a bloque FALLIDA dado autentificación fallida.",
                            Toast.LENGTH_LONG).show();
                }

                mfc.close();
                mTagDialog.cancel();

            }catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }

        }
    }


    void resolveAuthIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            MifareClassic mfc = MifareClassic.get(tagFromIntent);

            try {
                mfc.connect();
                boolean auth = false;
                String hexkey = "";
                int id = mRadioGroup.getCheckedRadioButtonId();
                int sector = Integer.valueOf(mSector.getText().toString());
                byte[] datakey;

                if (id == R.id.radioButtonkeyA){
                    hexkey = mHexKeyA.getText().toString();
                    datakey = hexStringToByteArray(hexkey);
                    auth = mfc.authenticateSectorWithKeyA(sector, datakey);
                }
                else if (id == R.id.radioButtonkeyB){
                    hexkey = mHexKeyB.getText().toString();
                    datakey = hexStringToByteArray(hexkey);
                    auth = mfc.authenticateSectorWithKeyB(sector, datakey);
                }
                else {
                    //no item selected poner toast
                    Toast.makeText(this,
                            "°Seleccionar llave A o B!",
                            Toast.LENGTH_LONG).show();
                    mfc.close();
                    return;
                }

                if(auth){
                    Toast.makeText(this,
                            "Autentificación de sector EXITOSA.",
                            Toast.LENGTH_LONG).show();
                }else{ // Authentication failed - Handle it
                    Toast.makeText(this,
                            "Autentificación de sector FALLIDA.",
                            Toast.LENGTH_LONG).show();
                }
                mfc.close();
            }catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        }
    }


    /* Called when the activity will start interacting with the user. */
    @Override
    public void onResume()
    {
        super.onResume();

        // Double check if NFC is enabled
        checkNfcEnabled();

        Log.d(TAG, "onResume: " + getIntent());

        mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mReadWriteTagFilters, mTechList);
    }


    /*
     * This is called for activities that set launchMode to "singleTop" or
     * "singleTask" in their manifest package, or if a client used the
     * FLAG_ACTIVITY_SINGLE_TOP flag when calling startActivity(Intent).
     */
    @Override
    public void onNewIntent(Intent intent)
    {
        Log.d(TAG, "onNewIntent: " + intent);
        Log.i("Foreground dispatch", "Discovered tag with intent: " + intent);

        if (mAuthenticationMode)
        {
            // Currently in tag AUTHENTICATION mode
            resolveAuthIntent(intent);
            mTagDialog.cancel();
        }
        else if (!mWriteMode)
        {
            // Currently in tag READING mode
            resolveReadIntent(intent);
        } else
        {
            // Currently in tag WRITING mode
            resolveWriteIntent(intent);
        }
    }


    /* Called when the system is about to start resuming a previous activity. */
    @Override
    public void onPause()
    {
        super.onPause();
        Log.d(TAG, "onPause: " + getIntent());
        mNfcAdapter.disableForegroundDispatch(this);

    }


    private void enableTagWriteMode()
    {
        mWriteMode = true;
        mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent,
                mReadWriteTagFilters, mTechList);
    }

    private void enableTagReadMode()
    {
        mWriteMode = false;
        /*mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent,
                mReadWriteTagFilters, mTechList);*/
    }

    private void enableTagAuthMode()
    {
        mAuthenticationMode = true;
        mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent,
                mReadWriteTagFilters, mTechList);
    }


    /*
     * **** TAG AUTHENTICATE METHODS ****
     */
    private View.OnClickListener mTagAuthenticate = new View.OnClickListener()
    {
        @Override
        public void onClick(View arg0)
        {

            enableTagAuthMode();

            AlertDialog.Builder builder = new AlertDialog.Builder(
                    PagoActivity.this)
                    .setTitle(getString(R.string.ready_to_authenticate))
                    .setMessage(getString(R.string.ready_to_authenticate_instructions))
                    .setCancelable(true)
                    .setNegativeButton("Cancelar",
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog,
                                                    int id)
                                {
                                    dialog.cancel();
                                }
                            })
                    .setOnCancelListener(new DialogInterface.OnCancelListener()
                    {
                        @Override
                        public void onCancel(DialogInterface dialog)
                        {
                            mAuthenticationMode = false;
                        }
                    });
            mTagDialog = builder.create();
            mTagDialog.show();
        }
    };


    /*
     * **** TAG READ METHODS ****
     */

    private View.OnClickListener mTagRead = new View.OnClickListener()
    {
        @Override
        public void onClick(View arg0)
        {


            ReadUIDMode = false;

            AlertDialog.Builder builder = new AlertDialog.Builder(
                    PagoActivity.this)
                    .setTitle(getString(R.string.ready_to_read))
                    .setMessage(getString(R.string.ready_to_read_instructions))
                    .setCancelable(true)
                    .setNegativeButton("Cancelar",
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog,
                                                    int id)
                                {
                                    dialog.cancel();
                                }
                            })
                    .setOnCancelListener(new DialogInterface.OnCancelListener()
                    {
                        @Override
                        public void onCancel(DialogInterface dialog)
                        {
                            enableTagReadMode();
                            ReadUIDMode = true;
                        }
                    });
            mTagDialog = builder.create();
            mTagDialog.show();
        }
    };


    /*
     * **** TAG WRITE METHODS ****
     */

    private View.OnClickListener mTagWrite = new View.OnClickListener()
    {
        @Override
        public void onClick(View arg0)
        {

            enableTagWriteMode();

            AlertDialog.Builder builder = new AlertDialog.Builder(
                    PagoActivity.this)
                    .setTitle(getString(R.string.ready_to_write))
                    .setMessage(getString(R.string.ready_to_write_instructions))
                    .setCancelable(true)
                    .setNegativeButton("Cancelar",
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog,
                                                    int id)
                                {
                                    dialog.cancel();
                                }
                            })
                    .setOnCancelListener(new DialogInterface.OnCancelListener()
                    {
                        @Override
                        public void onCancel(DialogInterface dialog)
                        {
                            enableTagReadMode();
                        }
                    });
            mTagDialog = builder.create();
            mTagDialog.show();
        }
    };



    /*
     * **** HELPER METHODS ****
     */

    private void checkNfcEnabled()
    {
        Boolean nfcEnabled = mNfcAdapter.isEnabled();
        if (!nfcEnabled)
        {
            new AlertDialog.Builder(PagoActivity.this)
                    .setTitle(getString(R.string.warning_nfc_is_off))
                    .setMessage(getString(R.string.turn_on_nfc))
                    .setCancelable(false)
                    .setPositiveButton("Actualizar Settings",
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog,
                                                    int id)
                                {
                                    startActivity(new Intent(
                                            android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                                }
                            }).create().show();
        }
    }

    public static String getHexString(byte[] b, int length)
    {
        String result = "";
        Locale loc = Locale.getDefault();

        for (int i = 0; i < length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
            result += ""; //Poner espacio si se quiere separar de dos en dos caracteres hex
        }
        return result.toUpperCase(loc);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    public static String HexToDecimal(String hex){
        int decimal=Integer.parseInt(hex,16);
        String dec = Integer.toString(decimal);
        return dec;
    }

    public static String HexFill(String strdata){
        char[] data = {'0', '0', '0', '0','0', '0','0', '0','0', '0','0', '0','0', '0','0', '0','0', '0', '0', '0','0', '0','0', '0','0', '0','0', '0','0', '0','0', '0'};
        String resultado = "";
        char[] numero = strdata.toCharArray();
        int j = 0;
        for(int i = 0; i <= 31; i++){
            data[i] = '0';
            if(i > 31-strdata.length()){
                data[i] = numero[j];
                j++;
            }
        }
        resultado = String.valueOf(data);
        return resultado;
    }
}

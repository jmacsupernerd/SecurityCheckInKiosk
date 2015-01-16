package com.mcwilliams.securitykiosk;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;


public class HomeScreen extends ActionBarActivity {
    private TextView customerName;
    private TextView visitingFirstName;
    private TextView visitingLastName;
    private ImageView customerImage;
    private ImageView placeHolderCamera;
    private EditText hebPartnerFirstName;
    private EditText hebPartnerLastName;
    private TextView timeStamp;
    static final int REQUEST_IMAGE_CAPTURE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        customerName = (TextView) findViewById(R.id.tvBadgeName);
        customerImage = (ImageView) findViewById(R.id.ivCustomerPicture);
        placeHolderCamera = (ImageView) findViewById(R.id.ivPlaceHolder);
        visitingFirstName = (TextView) findViewById(R.id.tvVisitingFirstName);
        visitingLastName = (TextView) findViewById(R.id.tvVisitingLastName);
        hebPartnerFirstName = (EditText) findViewById(R.id.etFirstName);
        hebPartnerLastName = (EditText) findViewById(R.id.etLastName);
        timeStamp = (TextView) findViewById(R.id.tvTimeStamp);

        timeStamp.setText(getCurrentTimeStamp());

        hebPartnerFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                visitingFirstName.setText(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        hebPartnerLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                visitingLastName.setText(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.done) {
            sendEmail();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBadgePictureClicked(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void onScanLicenseClicked(View view) {
        Intent scanLicense = new Intent(this, MainActivity.class);
        startActivityForResult(scanLicense, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0) {
            String name = data.getStringExtra("name");
            customerName.setText(name);
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            customerImage.setImageBitmap(imageBitmap);
            placeHolderCamera.setVisibility(View.GONE);
        }

    }

    public void sendEmail() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String host = "smtp.gmail.com";
                    String address = "jtmacnb@gmail.com";
                    String from = "jkkm30@gmail.com";
                    String pass = "Jkkm#2014";
                    String to = "jtmacnb@gmail.com";
                    Multipart multiPart;
                    String finalString = "";

                    Properties props = System.getProperties();
                    props.put("mail.smtp.starttls.enable", "true");
                    props.put("mail.smtp.host", host);
                    props.put("mail.smtp.user", address);
                    props.put("mail.smtp.password", pass);
                    props.put("mail.smtp.port", "587");
                    props.put("mail.smtp.auth", "true");
                    Log.i("Check", "done pops");
                    Session session = Session.getDefaultInstance(props, null);

                    DataHandler handler = new DataHandler(new ByteArrayDataSource(finalString.getBytes(), "text/plain"));
                    MimeMessage message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(from));
                    message.setDataHandler(handler);
                    Log.i("Check", "done sessions");
                    multiPart = new MimeMultipart();

                    InternetAddress toAddress;
                    toAddress = new InternetAddress(to);
                    message.addRecipient(Message.RecipientType.TO, toAddress);
                    Log.i("Check", "added recipient");
                    message.setSubject("Security Kiosk Appt");
                    message.setContent(multiPart);
                    message.setText(customerName.getText().toString() + " is here to see you.");

                    Log.i("check", "transport");
                    Transport transport = session.getTransport("smtp");
                    Log.i("check", "connecting");
                    transport.connect(host, address, pass);
                    Log.i("check", "wana send");
                    transport.sendMessage(message, message.getAllRecipients());
                    transport.close();
                    Log.i("check", "sent");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    public static String getCurrentTimeStamp(){
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm");
            String currentTimeStamp = dateFormat.format(new Date()); // Find todays date

            return currentTimeStamp;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

}

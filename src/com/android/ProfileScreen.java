/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import static com.android.HomeScreen.EXTRA_MESSAGE;
import static com.android.HomeScreen.EXTRA_MESSAGE2;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 *
 * @author Andy
 */
public class ProfileScreen extends Activity {

    public final static String EXTRA_MESSAGE = "Recommend a Show";
    public final static String EXTRA_MESSAGE2 = "Recommend aShow";
    private ProfileScreen.ClientSender clientSender;
    private ServerInformation networkInfo = new ServerInformation();
    private String SERVER_IP = networkInfo.getSERVER_IP();
    private int PORT = networkInfo.getPORT();
    private Context context;
    EditText userText;
    String username, answer, gender;
    String[] details;
    EditText passwordField, ageField;
    RadioButton male, female;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the message from the intent
        Intent intent = getIntent();
        username = intent.getStringExtra(HomeScreen.EXTRA_MESSAGE);
        answer = intent.getStringExtra(HomeScreen.EXTRA_MESSAGE2);
        details = answer.split(">|\n");

        // layout

        LinearLayout myLayout = new LinearLayout(this);
        myLayout.setPadding(500, 200, 500, 200);
        myLayout.setOrientation(1);

        // text fields - username, password and age
        EditText usernameField = new EditText(this);
        usernameField.setText(username);
        usernameField.setHint("Username");
        usernameField.setKeyListener(null);
        passwordField = new EditText(this);
        passwordField.setTransformationMethod(new PasswordTransformationMethod());
        passwordField.setHint("Password");
        passwordField.setText(details[5]);
        ageField = new EditText(this);
        ageField.setHint("Age");
        ageField.setText(details[1]);

        // buttons - update and view ratings

        Button updateButton = new Button(this);
        updateButton.setText("Update Details");
        updateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    update();
                } catch (Exception e) {
                }
            }
        });
        Button ratingsButton = new Button(this);
        ratingsButton.setBackgroundColor(Color.RED);
        ratingsButton.setText("View Your Show Ratings");
        ratingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                viewRatings();
            }
        });

        // radio group for gender

        RadioGroup genderGroup = new RadioGroup(this);
        genderGroup.setOrientation(0);

        RadioButton male = new RadioButton(this);
        male.setText("Male");
        RadioButton female = new RadioButton(this);
        female.setText("Female");
        TextView tv = new TextView(this);

        genderGroup.addView(male);
        genderGroup.addView(female);
        genderGroup.addView(tv);

        if ("m".equals(details[3])) {
            male.setChecked(true);
            gender = "m";
        } else {
            female.setChecked(true);
            gender = "f";
        }

        // add all to the layout

        myLayout.addView(usernameField);
        myLayout.addView(passwordField);
        myLayout.addView(ageField);
        myLayout.addView(genderGroup);
        myLayout.addView(updateButton);
        myLayout.addView(ratingsButton);

        setContentView(myLayout);
    }

    private void update() throws Exception {
        String requestString = "update";

        String password, age;
        password = passwordField.getText().toString();
        // hash password
        SHA1 sha = new SHA1();
        password = sha.SHA1(password);
        age = ageField.getText().toString();

        context = this.getApplicationContext();
        clientSender = new ProfileScreen.ClientSender(context, this);
        ClientRequest request = new ClientRequest(username, requestString, password, age, gender);
        String message = request.createUpdateProfileRequest();
        clientSender.execute(message);
    }

    private void viewRatings() {
        String requestString = "ratings";

        context = this.getApplicationContext();
        clientSender = new ProfileScreen.ClientSender(context, this);
        ClientRequest request = new ClientRequest(username, requestString);
        String message = request.createRecommendationRequest();
        clientSender.execute(message);
    }

    /**
     * ***************** INNER CLASS - a thread for connecting to server ******
     */
    private class ClientSender extends AsyncTask<String, Void, String> {

        private Socket socket;
        private String answer;
        private Context context;
        private BufferedWriter out;
        private BufferedReader in;
        private Activity activity;

        public ClientSender(Context context, Activity act) {
            this.context = context;
            socket = null;
            out = null;
            in = null;
            activity = act;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                if (socket == null) {
                    socket = new Socket(SERVER_IP, PORT);

                    out = new BufferedWriter(
                            new OutputStreamWriter(socket.getOutputStream()));
                    in = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
                }

                out.write(params[0] + "\n");
                out.flush();

                answer = in.readLine() + System.getProperty("line.separator");

                ServerResponse response = new ServerResponse(answer);
                answer = response.getProfileResponse();

                return answer;
            } catch (IOException e) {
            }

            return "Error";
        }

        @Override
        protected void onPostExecute(String answer) {
            if (socket != null) {
                if (answer.contains("///")) {
                    Intent intent = new Intent(activity, RatingScreen.class);
                    intent.putExtra(EXTRA_MESSAGE, username);
                    intent.putExtra(EXTRA_MESSAGE2, answer);
                    startActivity(intent);
                } else {
                    Toast.makeText(context, answer, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Can't connect to server!",
                        Toast.LENGTH_LONG).show();
            }

        }
    }
}
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
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
public class SignUp extends Activity {

    private SignUp.ClientSender clientSender;
    private ServerInformation networkInfo = new ServerInformation();
    private String SERVER_IP = networkInfo.getSERVER_IP();
    private int PORT = networkInfo.getPORT();
    private Context context;
    EditText userText, passText, ageText;
    private String username, password, age, gender;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        userText = (EditText) findViewById(R.id.username_message);
        passText = (EditText) findViewById(R.id.password_message);
        ageText = (EditText) findViewById(R.id.age_message);
    }

    public void signUpRequest(View view) throws Exception {

        username = userText.getText().toString();
        password = passText.getText().toString();
        // hash password
        SHA1 sha = new SHA1();
        password = sha.SHA1(password);
        
        age = ageText.getText().toString();
        String requestString = "signup";

        context = this.getApplicationContext();
        clientSender = new SignUp.ClientSender(context);
        ClientRequest request = new ClientRequest(username, requestString, password, age, gender);
        String message = request.createSignUpRequest();
        clientSender.execute(message);

        Intent intent = new Intent(this, StartScreen.class);
        startActivity(intent);
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_male:
                if (checked) {
                    gender = "m";
                    break;
                }
            case R.id.radio_female:
                if (checked) {
                    gender = "f";
                    break;
                }
        }
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

        public ClientSender(Context context) {
            this.context = context;
            socket = null;
            out = null;
            in = null;
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
                answer = response.getSignUpValue();

                return answer;
            } catch (IOException e) {
            }

            return "Error";
        }

        @Override
        protected void onPostExecute(String answer) {
            if (socket != null) {
                Toast.makeText(context, answer, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Can't connect to server!",
                        Toast.LENGTH_LONG).show();
            }

        }
    }
}

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
public class LogIn extends Activity {

    public final static String EXTRA_MESSAGE = "Recommend a Show";
    private LogIn.ClientSender clientSender;
    private ServerInformation networkInfo = new ServerInformation();
    private String SERVER_IP = networkInfo.getSERVER_IP();
    private int PORT = networkInfo.getPORT();
    private Context context;
    EditText userText, passText;
    String username, password;
    boolean loggedin = false;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log);
        userText = (EditText) findViewById(R.id.edit_message);
        passText = (EditText) findViewById(R.id.edit_message2);
    }

    public void logInRequest(View view) throws Exception {

        username = userText.getText().toString();
        password = passText.getText().toString();
        // hash password
        SHA1 sha = new SHA1();
        password = sha.SHA1(password);
        String requestString = "login";

        context = this.getApplicationContext();
        clientSender = new LogIn.ClientSender(context, this);
        ClientRequest request = new ClientRequest(username, requestString, password);
        String message = request.createLogInRequest();
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
        private Activity logAct;

        public ClientSender(Context context, Activity act) {
            this.context = context;
            socket = null;
            out = null;
            in = null;
            logAct = act;
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
                answer = response.getLogInValue();

                return answer;
            } catch (IOException e) {
            }

            return "Error";
        }

        @Override
        protected void onPostExecute(String answer) {
            if (socket != null) {

                if (answer.contains("successfully")) {
                    Intent intent = new Intent(logAct, HomeScreen.class);
                    intent.putExtra(EXTRA_MESSAGE, username);
                    startActivity(intent);

                    Toast.makeText(context, answer, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, answer, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(context, "Can't connect to server!",
                        Toast.LENGTH_LONG).show();
            }

        }
    }
}

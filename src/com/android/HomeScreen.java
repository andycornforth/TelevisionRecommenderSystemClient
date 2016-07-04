/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
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
public class HomeScreen extends Activity {

    private HomeScreen.ClientSender clientSender;
    public final static String EXTRA_MESSAGE = "Recommend a Show";
    public final static String EXTRA_MESSAGE2 = "Recommend aShow";
    private ServerInformation networkInfo = new ServerInformation();
    private String SERVER_IP = networkInfo.getSERVER_IP();
    private int PORT = networkInfo.getPORT();
    private Context context;
    private String username;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);
        context = this.getApplicationContext();
        clientSender = new HomeScreen.ClientSender(context, this);
        // Get the message from the intent
        Intent intent = getIntent();
        username = intent.getStringExtra(LogIn.EXTRA_MESSAGE);

    }

    public void viewProfile(View view) {
        String requestString = "profile";

        clientSender = new HomeScreen.ClientSender(context, this);
        ClientRequest request = new ClientRequest(username, requestString);
        String message = request.createRecommendationRequest();
        clientSender.execute(message);
    }

    public void getRecommendation(View view) {
        String requestString = "recommendation";

        clientSender = new HomeScreen.ClientSender(context, this);
        ClientRequest request = new ClientRequest(username, requestString);
        String message = request.createRecommendationRequest();
        clientSender.execute(message);
        Toast.makeText(context, "Loading.  ", Toast.LENGTH_SHORT).show();
        Toast.makeText(context, "Loading.. ", Toast.LENGTH_SHORT).show();
        Toast.makeText(context, "Loading...", Toast.LENGTH_LONG).show();
    }

    public void getListing(View view) {
        Intent intent = new Intent(this, TVGuideScreen.class);
        intent.putExtra(EXTRA_MESSAGE, username);
        startActivity(intent);
    }

    public void getShow(View view) {
        Intent intent = new Intent(this, SearchScreen.class);
        intent.putExtra(EXTRA_MESSAGE, username);
        startActivity(intent);
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
                answer = response.getResponseBody();

                return answer;
            } catch (IOException e) {
            }

            return "Error";
        }

        @Override
        protected void onPostExecute(String answer) {
            if (socket != null) {
                if (answer.contains("age>")) {
                    Intent intent = new Intent(activity, ProfileScreen.class);
                    intent.putExtra(EXTRA_MESSAGE, username);
                    intent.putExtra(EXTRA_MESSAGE2, answer);
                    startActivity(intent);
                } else {
                    for (int i = 0; i < 3; i++) {
                        Toast.makeText(context, answer, Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                Toast.makeText(context, "Can't connect to server!",
                        Toast.LENGTH_LONG).show();
            }

        }
    }
}

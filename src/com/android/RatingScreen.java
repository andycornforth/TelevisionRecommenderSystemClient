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
public class RatingScreen extends Activity {

    public final static String EXTRA_MESSAGE = "Recommend a Show";
    private RatingScreen.ClientSender clientSender;
    private ServerInformation networkInfo = new ServerInformation();
    private String SERVER_IP = networkInfo.getSERVER_IP();
    private int PORT = networkInfo.getPORT();
    private Context context;
    EditText userText;
    String username, answer;
    String[] ratings, items = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    boolean initialised = false;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the message from the intent
        Intent intent = getIntent();
        username = intent.getStringExtra(ProfileScreen.EXTRA_MESSAGE);
        answer = intent.getStringExtra(ProfileScreen.EXTRA_MESSAGE2);
        ratings = answer.split("///");

        // layout

        LinearLayout myLayout = new LinearLayout(this);
        myLayout.setPadding(500, 200, 500, 200);
        myLayout.setOrientation(1);

        TextView title = new TextView(this);
        title.setText("Your Ratings List\n\n\n");

        myLayout.addView(title);

        for (int i = 0; i < ratings.length; i = i + 2) {
            LinearLayout innerLayout = new LinearLayout(this);
            final EditText show = new EditText(this);
            show.setText(ratings[i]);
            show.setWidth(750);
            show.setKeyListener(null);

            context = this.getApplicationContext();
            Spinner spin = new Spinner(this);
            ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, items);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spin.setAdapter(aa);
            int position = Integer.parseInt(ratings[i + 1]);
            position--;
            spin.setSelection(position);
            spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    if (initialised) {
                        String rating = parent.getItemAtPosition(pos).toString();
                        String showname = show.getText().toString();
                        rateShow(showname, rating);
                    }
                }

                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            spin.setBackgroundColor(Color.RED);

            innerLayout.addView(show);
            innerLayout.addView(spin);
            myLayout.addView(innerLayout);
        }

        setContentView(myLayout);
        initialised = true;
    }

    private void rateShow(String show, String rating) {

        String requestString = "rate";

        context = this.getApplicationContext();
        clientSender = new RatingScreen.ClientSender(context, this);
        ClientRequest request = new ClientRequest(username, requestString, show, rating);
        String message = request.createRateRequest();
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
                answer = response.getRateResponse();

                return answer;
            } catch (IOException e) {
            }

            return "Error";
        }

        @Override
        protected void onPostExecute(String answer) {
            if (socket != null) {
//                Toast.makeText(context, answer, Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(context, "Can't connect to server!",
                        Toast.LENGTH_LONG).show();
            }

        }
    }
}

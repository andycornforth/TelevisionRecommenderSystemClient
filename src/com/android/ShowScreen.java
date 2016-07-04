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
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
public class ShowScreen extends Activity {

    public final static String EXTRA_MESSAGE = "Recommend a Show";
    private ShowScreen.ClientSender clientSender;
    private ServerInformation networkInfo = new ServerInformation();
    private String SERVER_IP = networkInfo.getSERVER_IP();
    private int PORT = networkInfo.getPORT();
    private Context context;
    EditText userText, passText;
    String username, answer, show, rating;
    boolean loggedin = false;
    String[] items = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    Spinner spin;
    
    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the message from the intent
        Intent intent = getIntent();
        username = intent.getStringExtra(SearchScreen.EXTRA_MESSAGE);
        answer = intent.getStringExtra(SearchScreen.EXTRA_MESSAGE2);
        show = intent.getStringExtra(SearchScreen.SHOW_MESSAGE);
        answer = answer.replace("Show>", "\n\t\t\t\t\t\t\t\t\t\t\tShow:\t\t\t\t");
        answer = answer.replace("/ResponseType>", "\n");
        answer = answer.replace("Description>", "\n\n\nDescription:");


        LinearLayout myLayout = new LinearLayout(this);
        myLayout.setPadding(200, 200, 200, 200);
        myLayout.setOrientation(0);

        TextView showInfo = new TextView(this);
        showInfo.setTextSize(20);
        showInfo.setText(answer);
        showInfo.setId(5);

        Button rateButton = new Button(this);
        rateButton.setText("Rate Show");
        rateButton.setTextSize(20);
        rateButton.setVisibility(View.VISIBLE);
        rateButton.setBackgroundColor(Color.RED);

        rateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rateShow();
            }
        });

        TextView text = new TextView(this);
        text.setTextSize(20);
        text.setText("Select you rating: ");

        context = this.getApplicationContext();
        spin = new Spinner(this);
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, items);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                rating = parent.getItemAtPosition(pos).toString();
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        myLayout.addView(text);
        myLayout.addView(spin);
        myLayout.addView(rateButton);
        myLayout.addView(showInfo);

        setContentView(myLayout);
    }

    private void rateShow() {

        String requestString = "rate";

        context = this.getApplicationContext();
        clientSender = new ShowScreen.ClientSender(context, this);
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
                Toast.makeText(context, answer, Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(context, "Can't connect to server!",
                        Toast.LENGTH_LONG).show();
            }

        }
    }
}

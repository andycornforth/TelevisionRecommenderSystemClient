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
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import static com.android.SearchScreen.EXTRA_MESSAGE;
import static com.android.SearchScreen.EXTRA_MESSAGE2;
import static com.android.SearchScreen.SHOW_MESSAGE;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Andy
 */
public class TVGuideScreen extends Activity {

    public final static String EXTRA_MESSAGE = "Recommend a Show1";
    public final static String EXTRA_MESSAGE2 = "RecommendShow";
    public final static String SHOW_MESSAGE = "Recommend aShow";
    private TVGuideScreen.ClientSender clientSender;
    private ServerInformation networkInfo = new ServerInformation();
    private String SERVER_IP = networkInfo.getSERVER_IP();
    private int PORT = networkInfo.getPORT();
    private Context context;
    EditText userText;
    String username, answer, show, channel, day, rating, showname;
    boolean initialised = false;
    String[] items = {"bbc1", "bbc2", "ch4", "five", "sky_one", "scifi",
        "sky_sports1", "sky_movies_premiere"}, itemsRate = {"1", "2", "3",
        "4", "5", "6", "7", "8", "9", "10"}, schedule, date;
    TextView episodeInformation, episodeTitle;
    Button rateButton;
    Spinner spinRate;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the message from the intent
        Intent intent = getIntent();
        if (intent.getStringExtra(HomeScreen.EXTRA_MESSAGE) != null) {
            username = intent.getStringExtra(HomeScreen.EXTRA_MESSAGE);
        } else {
            username = intent.getStringExtra(TVGuideScreen.EXTRA_MESSAGE);
        }
        answer = intent.getStringExtra(TVGuideScreen.EXTRA_MESSAGE2);

        date = getDateArray();
        // layout

        LinearLayout myLayout = new LinearLayout(this);
        myLayout.setPadding(200, 100, 200, 200);
        myLayout.setOrientation(1);

        LinearLayout spinLayout = new LinearLayout(this);
        // need to get channels, hardcoded for time being

        // channel spinner;
        Spinner spin = new Spinner(this);
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, items);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                channel = parent.getItemAtPosition(pos).toString();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinLayout.addView(spin);
        //date spinner
        Spinner spin2 = new Spinner(this);
        ArrayAdapter a2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, date);
        a2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin2.setAdapter(a2);
        spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                day = parent.getItemAtPosition(pos).toString();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinLayout.addView(spin2);
        Button goButton = new Button(this);
        goButton.setText("GO");
        goButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getChannelSchedule();
            }
        });
        spinLayout.addView(goButton);

        myLayout.addView(spinLayout);
        // show channel listings
        if (answer != null) {
            schedule = answer.split("/");

            for (int i = 0; i < schedule.length; i = i + 3) {

                LinearLayout innerLayout = new LinearLayout(this);

                final EditText startTime, endTime, showTitle;
                showTitle = new EditText(this);
                showTitle.setText(schedule[i]);
                startTime = new EditText(this);
                startTime.setText(schedule[i + 1]);
                endTime = new EditText(this);
                endTime.setText(schedule[i + 2]);
                showTitle.setWidth(750);
                showTitle.setKeyListener(null);
                startTime.setWidth(200);
                startTime.setKeyListener(null);
                endTime.setWidth(200);
                endTime.setKeyListener(null);

                showTitle.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        getEpisode(showTitle.getText().toString(), channel,
                                startTime.getText().toString());
                        rateButton.setVisibility(View.VISIBLE);
                        spinRate.setVisibility(View.VISIBLE);
                    }
                });

                innerLayout.addView(startTime);
                innerLayout.addView(endTime);
                innerLayout.addView(showTitle);
                myLayout.addView(innerLayout);
            }
        }
        LinearLayout episodeLayout = new LinearLayout(this);
        episodeLayout.setPadding(50, 200, 0, 0);
        episodeLayout.setOrientation(1);
        LinearLayout upperLayout = new LinearLayout(this);
        episodeTitle = new TextView(this);
        episodeTitle.setTextSize(20);
        upperLayout.addView(episodeTitle);
        // add spinner to rate
        spinRate = new Spinner(this);
        ArrayAdapter aRate = new ArrayAdapter(this, android.R.layout.simple_spinner_item, itemsRate);
        aRate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinRate.setAdapter(aRate);
        spinRate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                rating = parent.getItemAtPosition(pos).toString();
                showname = episodeTitle.getText().toString();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinRate.setVisibility(View.GONE);
        //add spinner to upper layout
        upperLayout.addView(spinRate);
        // add button
        rateButton = new Button(this);
        rateButton.setText("Rate Show");
        rateButton.setTextSize(20);
        rateButton.setVisibility(View.GONE);
        rateButton.setBackgroundColor(Color.RED);

        rateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rateShow();
            }
        });
        //add button to upper layout
        upperLayout.addView(rateButton);

        episodeInformation = new TextView(this);
        episodeLayout.addView(upperLayout);
        episodeLayout.addView(episodeInformation);

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.addView(myLayout);
        mainLayout.addView(episodeLayout);

        // add all to a scroll bar and set as content view
        ScrollView sc = new ScrollView(this);
        sc.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        sc.setFillViewport(true);
        sc.addView(mainLayout);
        setContentView(sc);
        initialised = true;
    }

    private void getChannelSchedule() {

        String requestString = "listing";
        day = day.replace("/", "");

        context = this.getApplicationContext();
        clientSender = new TVGuideScreen.ClientSender(context, this);
        ClientRequest request = new ClientRequest(username, requestString, channel, day);
        String message = request.createListingsRequest();
        clientSender.execute(message);

    }

    private void getEpisode(String showName, String channelName, String startTime) {
        episodeTitle.setText(showName);

        String requestString = "episode";
        day = day.replace("/", "");

        context = this.getApplicationContext();
        clientSender = new TVGuideScreen.ClientSender(context, this);
        ClientRequest request = new ClientRequest(username, requestString, showName, channel, day, startTime);
        String message = request.createEpisodeRequest();
        clientSender.execute(message);
    }

    private String[] getDateArray() {
        String[] days = new String[7];
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        Calendar cal = Calendar.getInstance();
        days[0] = df.format(cal.getTime());
        for (int i = 1; i < 7; i++) {
            cal.add(Calendar.DATE, 1);
            Date dateAdd = cal.getTime();
            String d = df.format(dateAdd);
            days[i] = d;
        }

        return days;
    }

    private void rateShow() {

        String requestString = "rate";

        context = this.getApplicationContext();
        clientSender = new TVGuideScreen.ClientSender(context, this);
        ClientRequest request = new ClientRequest(username, requestString, showname, rating);
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
                answer = response.getResponseBody();

                return answer;
            } catch (IOException e) {
            }

            return "Error";
        }

        @Override
        protected void onPostExecute(String answer) {
            if (socket != null) {
                if (answer.contains("Description>")) {
                    episodeInformation.setText(answer.replace("Description>", ""));
                } else if (answer.contains("the show")) {
                    Toast.makeText(context, answer, Toast.LENGTH_SHORT).show();
                } else {
                    // create an intent
                    Intent intent;
                    if (answer.contains("Show>")) {
                        intent = new Intent(activity, ShowScreen.class);
                    } else {
                        intent = new Intent(activity, TVGuideScreen.class);
                    }
                    intent.putExtra(EXTRA_MESSAGE, username);
                    intent.putExtra(EXTRA_MESSAGE2, answer);
                    intent.putExtra(SHOW_MESSAGE, show);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(context, "Can't connect to server!",
                        Toast.LENGTH_LONG).show();
            }

        }
    }
}

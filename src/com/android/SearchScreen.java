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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import static com.android.LogIn.EXTRA_MESSAGE;
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
public class SearchScreen extends Activity {

    public final static String EXTRA_MESSAGE = "Recommend a Show1";
    public final static String EXTRA_MESSAGE2 = "Recommend a Show";
    public final static String SHOW_MESSAGE = "Recommend aShow";
    private SearchScreen.ClientSender clientSender;
    private ServerInformation networkInfo = new ServerInformation();
    private String SERVER_IP = networkInfo.getSERVER_IP();
    private int PORT = networkInfo.getPORT();
    private Context context;
    EditText userText, passText;
    String username, show;
    boolean loggedin = false;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log);
        userText = (EditText) findViewById(R.id.edit_message);
        userText.setHint("Enter Show Title");
        passText = (EditText) findViewById(R.id.edit_message2);
        passText.setVisibility(View.GONE);
        Button button = (Button) findViewById(R.id.recommend_button);
        button.setText("Search");
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                searchRequest();
            }
        });
        // Get the message from the intent
        Intent intent = getIntent();
        username = intent.getStringExtra(LogIn.EXTRA_MESSAGE);
    }

    public void searchRequest() {

        show = userText.getText().toString();
        String requestString = "show";

        context = this.getApplicationContext();
        clientSender = new SearchScreen.ClientSender(context, this);
        ClientRequest request = new ClientRequest(username, requestString, show);
        String message = request.createShowRequest();
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
                // create an intent
                Intent intent = new Intent(activity, ShowScreen.class);
                intent.putExtra(EXTRA_MESSAGE, username);
                intent.putExtra(EXTRA_MESSAGE2, answer);
                intent.putExtra(SHOW_MESSAGE, show);
                startActivity(intent);
//                Toast.makeText(context, answer, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Can't connect to server!",
                        Toast.LENGTH_LONG).show();
            }

        }
    }
}

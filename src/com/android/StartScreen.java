/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
/**
 *
 * @author Andy
 */
public class StartScreen extends Activity {

    Button login, signup;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        login = (Button) findViewById(R.id.login_button);
        signup = (Button) findViewById(R.id.signup_button);
        login.setText("LOG IN");
        signup.setText("SIGN UP");
    }
    
    public void signUpScreen(View view){
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }
    
    public void logInScreen(View view){
        Intent intent = new Intent(this, LogIn.class);
        startActivity(intent);
    }
}

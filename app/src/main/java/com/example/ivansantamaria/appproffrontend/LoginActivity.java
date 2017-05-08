
package com.example.ivansantamaria.appproffrontend;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;


import org.json.JSONObject;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // Instancia la api una vez en la clase
    API api;
    private Facade facade;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private InfoSesion info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        info = new InfoSesion(this);

        if (info.getTipo() != -1)
        {
            Intent i = (info.getTipo() == 0) ? new Intent(this, Busqueda_Profesores.class) : new Intent(this, Perfil_Profesor.class);
            startActivity(i);
        }

        /*
         * Para cerrar sesión, hacer logout a /api/logout y posteriormente
         * borrar las preferencias => sharedpref.edit().remove("tipo").remove("token").apply();
         *
         */

        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.login);

        mPasswordView = (EditText) findViewById(R.id.password);

        Button mEmailSignInButtonProf = (Button) findViewById(R.id.log_in_button_prof);
        mEmailSignInButtonProf.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin_Prof();
            }
        });

        Button mEmailSignInButtonAlu = (Button) findViewById(R.id.log_in_button_alu);
        mEmailSignInButtonAlu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin_Alu();
            }
        });

        Button mRegisterButton = (Button) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignUp();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        api = new API("http://10.0.2.2:8080", this);
    }

    private void attemptSignUp() {
        Intent i = new Intent(this, Registro1.class);
        startActivity(i);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin_Prof() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid length password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            /* Envia la petición */
            try {
                facade = new Facade(api);
                JSONObject resultado = facade.login(new PersonaVO(email,password),1);
            } catch (APIexception e) {
                // Si el código de error era diferente a OK, habrá excepcion
                // La ex tendrá el código de error y el json de la respuesta del servidor
                Log.d("API", "Error solicitando login -> " + e.code + " | " + e.json);
                showProgress(false);

                mEmailView.setError("Usuario o contraseña incorrectos");
                mEmailView.requestFocus();
                return;
            }

            Intent i = new Intent(this, Perfil_Profesor.class);
            showProgress(false);
            info.set(email,1);
            startActivity(i);

        }
    }

    private void attemptLogin_Alu() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid length password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            /* Envia la petición */
            try {
                facade = new Facade(api);
                JSONObject resultado = facade.login(new PersonaVO(email,password),0);
            } catch (APIexception e) {
                // Si el código de error era diferente a OK, habrá excepcion
                // La ex tendrá el código de error y el json de la respuesta del servidor
                Log.d("API", "Error solicitando login -> " + e.code + " | " + e.json);
                showProgress(false);

                mEmailView.setError("Usuario o contraseña incorrectos");
                mEmailView.requestFocus();
                return;
            }

            Intent i = new Intent(this, Busqueda_Profesores.class);
            showProgress(false);
            info.set(email,0);
            startActivity(i);

        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}


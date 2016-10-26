package ch.hsr.mge.gadgeothek;

import android.app.Fragment;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.Stack;

import ch.hsr.mge.gadgeothek.service.Callback;
import ch.hsr.mge.gadgeothek.service.LibraryService;

import static android.R.id.content;

public class LoginActivity extends AppCompatActivity implements LoginFragment.IHandleLoginFragment, RegisterFragment.IHandleRegisterFragment {
    LoginFragment loginFragment;
    RegisterFragment registerFragment;

    public static String ARG_SESSION_TOKEN = "";

    private Stack<Fragment> history = new Stack<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginFragment = new LoginFragment();
        registerFragment = new RegisterFragment();

        setTitle(getString(R.string.title_activity_login));

        // add the starting fragment
        getFragmentManager().beginTransaction().replace(R.id.login_fragment_container, loginFragment).commit();
    }

    @Override
    public void onAttemptLogin(String email, String password) {
        LibraryService.setServerAddress(loginFragment.mServerView.getText().toString());
        LibraryService.login(email, password,
                new Callback<Boolean>() {

                    @Override
                    public void onCompletion(Boolean input) {
                        loginFragment.showProgress(false);
                        if (input) {
                            Log.d("Gadgeothek", "Login erfolgreich!");
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            // TODO: Session Token auslesen und abspeichern, bzw der Main Activity mitgeben
                            intent.putExtra(LoginActivity.ARG_SESSION_TOKEN, input);
                            getApplicationContext().startActivity(intent);
                        } else {
                            loginFragment.mPasswordView.setError("Invalid Password");
                            Log.w("Gadgeothek", "Login fehlgeschlagen.");
                        }
                    }

                    @Override
                    public void onError(String message) {
                        //TODO: display message in snackbar
                        Log.e("Gadgeothek", "Login-Fehler:" + message);
                    }
                }

        );
    }

    @Override
    public void onStartRegistration() {
        snackIt("GoTo Register Screen...");
        Log.e("Gadgeothek", "go to Registration Screen");
        if(loginFragment.mServerView.getText().toString().contains("http://")){
            LibraryService.setServerAddress(loginFragment.mServerView.getText().toString());
            history.push(registerFragment);

            setTitle(R.string.title_activity_register);
            getFragmentManager().beginTransaction().replace(R.id.login_fragment_container, registerFragment).commit();
        } else {
            loginFragment.mServerView.setError("This field is Required");
        }
    }

    @Override
    public void onAttemptRegistration(String email, String password, String name, String studentnumber) {
        LibraryService.register(email, password,name,studentnumber,

                new Callback<Boolean>() {

                    @Override
                    public void onCompletion(Boolean input) {
                        registerFragment.showProgress(false);
                        if (input) {
                            Log.d("Gadgeothek", "Registration erfolgreich!");
                            snackIt("Registration successful");
                            onCancelRegistration();
                        } else {
                            registerFragment.mEmailView.setError(getString(R.string.error_invalid_email));
                            snackIt("Registation failed");
                            Log.w("Gadgeothek", "Registration fehlgeschlagen.");
                        }
                    }

                    @Override
                    public void onError(String message) {
                        //TODO: display message in snackbar
                        snackIt("Server-Fehler:" + message);
                        Log.e("Gadgeothek", "Server-Fehler:" + message);
                    }
                }

        );
    }

    @Override
    public void onCancelRegistration() {
        Log.e("Gadgeothek", "go back to Login Screen");
        history.push(loginFragment);

        setTitle(R.string.title_activity_login);
        getFragmentManager().beginTransaction().replace(R.id.login_fragment_container, loginFragment).commit();
    }



    private void snackIt(String message) {
        Snackbar snackbar = Snackbar.make (findViewById(R.id.activity_login), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}

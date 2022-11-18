package com.example.se306project1.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.splashscreen.SplashScreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.se306project1.R;
import com.example.se306project1.database.FireStoreCallback;

import com.example.se306project1.database.UserDatabase;
import com.example.se306project1.models.User;
import com.example.se306project1.utilities.ActivityState;
import com.example.se306project1.utilities.ContextState;
import com.example.se306project1.utilities.PasswordEncripter;
import com.example.se306project1.utilities.UserState;

/**
 * @Description: This is MainActivity class which used to manage mainActivity.xml
 * @author: XiaoXiao Zhuang
 * @date: 11/08/2022
 */
public class MainActivity extends AppCompatActivity {
    private final UserDatabase userDatabase = UserDatabase.getInstance();

    public static void start() {
        AppCompatActivity activity = ActivityState.getInstance().getCurrentActivity();
        Intent intent = new Intent(activity.getBaseContext(), MainActivity.class);
        activity.startActivity(intent);
    }

    private static class ViewHolder {
        ConstraintLayout signUp, login;
        EditText registerUsernameEditText, registerPasswordEditText, registerConfirmPasswordEditText, loginUsernameEditText, loginPasswordEditText;
        Button registerSignUpButton, loginLoginButton;
        TextView loginSignUpTextView, registerLoginButton;
    }

    private final ViewHolder vh = new ViewHolder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);
        setContentView(R.layout.activity_main);
        ActivityState.getInstance().setCurrentActivity(this);
        ContextState.getInstance().setCurrentContext(getApplicationContext());

        createView();
        // set the click listener for change between login page and sign up page
        vh.registerLoginButton.setOnClickListener(view -> getLoginPage());
        vh.loginSignUpTextView.setOnClickListener(view -> getSignUpPage());
        vh.registerUsernameEditText.setOnFocusChangeListener((view, focus) -> {
            if (!focus) {
                onUserNotValid();
            }
        });
        vh.registerSignUpButton.setOnClickListener(view -> onUserSignUp());
        vh.loginLoginButton.setOnClickListener(view -> onUserLogin());
    }

    @Override
    public void onBackPressed() { }

    private void createView() {
        vh.registerUsernameEditText = findViewById(R.id.register_username_edit_text);
        vh.registerPasswordEditText = findViewById(R.id.register_password_edit_text);
        vh.registerConfirmPasswordEditText = findViewById(R.id.confirm_password);
        vh.registerLoginButton = findViewById(R.id.register_login_button);
        vh.registerSignUpButton = findViewById(R.id.register_sign_up_button);
        vh.loginUsernameEditText = findViewById(R.id.login_username_edit_text);
        vh.loginPasswordEditText = findViewById(R.id.login_password_edit_text);
        vh.loginLoginButton = findViewById(R.id.login_login_button);
        vh.loginSignUpTextView = findViewById(R.id.login_sign_up_button);
        vh.signUp = findViewById(R.id.sign_up_page);
        vh.login = findViewById(R.id.login_page);
    }


    private void switchToCategoryActivity() {
        CategoryActivity.start();
    }

    //set up the initial login page
    private void getLoginPage() {
        vh.signUp.setVisibility(View.GONE);
        vh.login.setVisibility(View.VISIBLE);
        vh.registerUsernameEditText.setText("");
        vh.registerPasswordEditText.setText("");
        vh.registerConfirmPasswordEditText.setText("");
    }

    //set up the initial sign up page
    private void getSignUpPage() {
        vh.signUp.setVisibility(View.VISIBLE);
        vh.login.setVisibility(View.GONE);
        vh.loginUsernameEditText.setText("");
        vh.loginPasswordEditText.setText("");
    }

    //Check if the user is valid
    private void onUserNotValid() {
        if (getRegisterUsername().isEmpty()) {
            return;
        }
        userDatabase.isUserExist(new FireStoreCallback() {
            @Override
            public <T> void Callback(T value) {
                boolean isExist = (Boolean) value;
                if (isExist) {
                    usernameIsUsed();
                }
            }
        }, getRegisterUsername());
    }

    //login functionality, this will check if the user is valid
    private void onUserLogin() {
        if (checkEmptyLogin()) {
            userDatabase.isUserExist(new FireStoreCallback() {
                @Override
                public <T> void Callback(T value) {
                    boolean isExist = (Boolean) value;
                    if (!isExist) {
                        userNotFound();
                    }
                }
            }, getLoginUsername());
            userDatabase.isLoginValid(new FireStoreCallback() {
                @Override
                public <T> void Callback(T value) {
                    boolean isValid = (Boolean) value;
                    userLogin(isValid, getLoginUsername());
                }
            }, getLoginUsername(), getLoginPassword());
        }
    }

    //sign up functionality, used for sign up new account, it will check whether the user has already
    // exist ( check if there is the same user name existing in database)
    private void onUserSignUp() {
        if (checkRegisterEmptyInput()) {
            userDatabase.isUserExist(new FireStoreCallback() {
                @Override
                public <T> void Callback(T value) {
                    boolean isExist = (Boolean) value;
                    if (isExist) {
                        usernameIsUsed();
                    } else if (checkConfirmPassword()) {
                        userSignUp(getRegisterUsername(), getRegisterPassword());
                    }
                }
            }, getRegisterUsername());
        }
    }

    //The user is valid and login successfully , record this user and will redirect to main activity
    private void userLogin(boolean isValid, String username) {
        if (isValid) {
            createCurrentUser(username);
            Toast.makeText(this, "Successfully login", Toast.LENGTH_SHORT).show();
            switchToCategoryActivity();
            vh.loginUsernameEditText.setText("");
            vh.loginPasswordEditText.setText("");
        } else {
            Toast.makeText(this, "The combination of password and username is incorrect, please try agian", Toast.LENGTH_SHORT).show();
        }
    }

    //user sign up successfully, add a new account into database, record this username and redirect to main activity
    private void userSignUp(String username, String password) {
        Toast.makeText(this, "CONGRATULATION! YOU ARE A MEMBER NOW!", Toast.LENGTH_SHORT).show();
        String encryptedPassword = getEncryptedPassword(password);
        createCurrentUser(username);
        userDatabase.addUserToFireStore(username, encryptedPassword);
        vh.registerUsernameEditText.setText("");
        vh.registerPasswordEditText.setText("");
        vh.registerConfirmPasswordEditText.setText("");
        switchToCategoryActivity();
    }

    //check if no fill username or password
    private boolean checkEmptyLogin() {
        if (vh.loginUsernameEditText.getText().length() == 0) {
            Toast.makeText(this, "Please enter your username", Toast.LENGTH_SHORT).show();
            return false;
        } else if (vh.loginPasswordEditText.getText().length() == 0) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // record currentUser
    private void createCurrentUser(String username) {
        UserState userState = UserState.getInstance();
        User user = new User(username);
        userState.setCurrentUser(user);
    }

    //the message for user not found
    private void userNotFound() {
        Toast.makeText(this, "User not found, please try again", Toast.LENGTH_SHORT).show();
    }

    private void usernameIsUsed() {
        Toast.makeText(this, "This username has been used", Toast.LENGTH_SHORT).show();
    }

    private boolean checkRegisterEmptyInput() {
        if (vh.registerUsernameEditText.getText().length() == 0) {
            Toast.makeText(this, "Please enter your username", Toast.LENGTH_SHORT).show();
            return false;
        } else if (getRegisterPassword().isEmpty() || getConfirmPassword().isEmpty()) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean checkConfirmPassword() {
        if (!getRegisterPassword().equals(getConfirmPassword())) {
            Toast.makeText(this, "Password and confirm password do not match, please try again", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private String getRegisterUsername() {
        return vh.registerUsernameEditText.getText().toString();
    }

    private String getRegisterPassword() {
        return vh.registerPasswordEditText.getText().toString();
    }

    private String getConfirmPassword() {
        return vh.registerConfirmPasswordEditText.getText().toString();
    }

    private String getLoginUsername() {
        return vh.loginUsernameEditText.getText().toString();
    }

    private String getLoginPassword() {
        return vh.loginPasswordEditText.getText().toString();
    }

    //get encryptedPassword
    private String getEncryptedPassword(String password) {
        return PasswordEncripter.hashPassword(password);
    }

}
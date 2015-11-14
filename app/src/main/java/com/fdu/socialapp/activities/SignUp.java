package com.fdu.socialapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SignUpCallback;
import com.fdu.socialapp.R;
import com.fdu.socialapp.model.MsnaUser;

public class SignUp extends BaseActivity {
    private EditText username;
    private EditText pwd;
    private EditText pwd2;
    private int username_checked = 0;
    private int pwd_checked = 0;
    private int pwd2_checked = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntent();
        setContentView(R.layout.activity_sign_up);
        username = (EditText) findViewById(R.id.RegUsername);
        pwd = (EditText) findViewById(R.id.RegPwd);
        pwd2 = (EditText) findViewById(R.id.RegPwdAgain);
        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String str = username.getText().toString();
                    if (str.length() <= 0) {
                        Toast.makeText(SignUp.this, "账户名不得为空", Toast.LENGTH_SHORT).show();
                        username.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_white_24dp, 0);
                        username_checked = 0;
                    } else {
                        username.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_black_24dp, 0);
                        username_checked = 1;
                    }
                }
            }
        });
        pwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    //You must get username before inputting pwd
                    if (username_checked == 0) {
                        username.requestFocus();
                        pwd.clearFocus();
                    }
                } else {

                    if (username_checked == 1) {
                        String str = pwd.getText().toString();

                        //Check limits
                        if (str.length() < 6) {
                            Toast.makeText(SignUp.this, "密码不得少于6位", Toast.LENGTH_SHORT).show();
                            pwd.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_white_24dp, 0);
                            pwd_checked = 0;
                        }

                        //Checked
                        else {
                            pwd.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_black_24dp, 0);
                            pwd_checked = 1;
                        }
                    }
                }
            }
        });
        pwd2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //You must get pwd before inputting pwd2
                    if (pwd_checked == 0) {
                        pwd.requestFocus();
                        pwd2.clearFocus();
                    }
                }
            }
        });

        //Check the password again when press the Enter
        pwd2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_DOWN) {
                    String str = pwd2.getText().toString();

                    //Check limits
                    if (!str.equals(pwd.getText().toString())) {
                        Toast.makeText(SignUp.this, "两次密码输入不同", Toast.LENGTH_SHORT).show();
                        pwd2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_white_24dp, 0);
                        pwd2_checked = 0;
                    }

                    //Checked
                    else {
                        pwd2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_black_24dp, 0);
                        pwd2_checked = 1;
                    }
                    return true;
                }
                return false;

            }
        });

        pwd2.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                if (pwd2.getText().toString().equals(pwd.getText().toString())) {
                    pwd2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_black_24dp, 0);
                    pwd2_checked = 1;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (pwd2.getText().toString().equals(pwd.getText().toString())) {
                    pwd2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_black_24dp, 0);
                    pwd2_checked = 1;
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Called when the user clicks the RegCommit button
     */
    public void regCommit(View view) {
        if (username_checked == 1 && pwd_checked == 1 && pwd2_checked == 1) {
            MsnaUser.signUpByNameAndPwd(username.getText().toString(), pwd.getText().toString(), new SignUpCallback() {
                public void done(AVException e) {
                    if (filterException(e)) {
                        // successfully
                        Toast.makeText(SignUp.this, "注册成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUp.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        } else {
            toast("注册信息不完整");
        }
    }
}

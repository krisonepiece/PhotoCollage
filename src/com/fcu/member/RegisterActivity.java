/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 * */
package com.fcu.member;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fcu.R;

public class RegisterActivity extends Fragment {
	private static final String TAG = RegisterActivity.class.getSimpleName();
	private Button btnRegister;
	private Button btnLinkToLogin;
	private EditText inputFullName;
	private EditText inputEmail;
	private EditText inputPassword;
	private ProgressDialog pDialog;
	private SessionManager session;
	private SQLiteHandler db;
	private View thisView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		thisView = inflater.inflate(R.layout.activity_register, container, false);
		inputFullName = (EditText) thisView.findViewById(R.id.name);
		inputEmail = (EditText) thisView.findViewById(R.id.email);
		inputPassword = (EditText) thisView.findViewById(R.id.password);
		btnRegister = (Button) thisView.findViewById(R.id.btnRegister);
		btnLinkToLogin = (Button) thisView.findViewById(R.id.btnLinkToLoginScreen);

		// Progress dialog
		pDialog = new ProgressDialog(getActivity());
		pDialog.setCancelable(false);

		// Session manager
		session = new SessionManager(getActivity().getApplicationContext());

		// SQLite database handler
		db = new SQLiteHandler(getActivity().getApplicationContext());

		// Check if user is already logged in or not
		if (session.isLoggedIn()) {
			// User is already logged in. Take him to main activity
//			Intent intent = new Intent(RegisterActivity.this,
//					MemberActivity.class);
//			startActivity(intent);
//			finish();
			Fragment fragment = new MemberActivity();
			FragmentManager fragmentManager = getActivity().getFragmentManager();
	        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
		}

		// Register Button Click event
		btnRegister.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				String name = inputFullName.getText().toString();
				String email = inputEmail.getText().toString();
				String password = inputPassword.getText().toString();

				if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
					registerUser(name, email, password);
				} else {
					Toast.makeText(getActivity().getApplicationContext(),
							"Please enter your details!", Toast.LENGTH_LONG)
							.show();
				}
			}
		});

		// Link to Login Screen
		btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
//				Intent i = new Intent(getApplicationContext(),
//						LoginActivity.class);
//				startActivity(i);
//				finish();
				Fragment fragment = new LoginActivity();
				FragmentManager fragmentManager = getActivity().getFragmentManager();
		        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
			}
		});

		
		return thisView;
	}

	/**
	 * Function to store user in MySQL database will post params(tag, name,
	 * email, password) to register url
	 * */
	private void registerUser(final String name, final String email,
			final String password) {
		// Tag used to cancel the request
		String tag_string_req = "req_register";

		pDialog.setMessage("Registering ...");
		showDialog();

		StringRequest strReq = new StringRequest(Method.POST,
				AppConfig.URL_REGISTER, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d(TAG, "Register Response: " + response.toString());
						hideDialog();

						try {
							JSONObject jObj = new JSONObject(response);
							boolean error = jObj.getBoolean("error");
							if (!error) {
								// User successfully stored in MySQL
								// Now store the user in sqlite
								String uid = jObj.getString("uid");

								JSONObject user = jObj.getJSONObject("user");
								String name = user.getString("name");
								String email = user.getString("email");
								String created_at = user
										.getString("created_at");

								// Inserting row in users table
								db.addUser(name, email, uid, created_at);

								// Launch login activity
//								Intent intent = new Intent(
//										RegisterActivity.this,
//										LoginActivity.class);
//								startActivity(intent);
//								finish();
								Fragment fragment = new LoginActivity();
								FragmentManager fragmentManager = getActivity().getFragmentManager();
						        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
							} else {

								// Error occurred in registration. Get the error
								// message
								String errorMsg = jObj.getString("error_msg");
								Toast.makeText(getActivity().getApplicationContext(),
										errorMsg, Toast.LENGTH_LONG).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e(TAG, "Registration Error: " + error.getMessage());
						Toast.makeText(getActivity().getApplicationContext(),
								error.getMessage(), Toast.LENGTH_LONG).show();
						hideDialog();
					}
				}) {

			@Override
			protected Map<String, String> getParams() {
				// Posting params to register url
				Map<String, String> params = new HashMap<String, String>();
				params.put("tag", "register");
				params.put("name", name);
				params.put("email", email);
				params.put("password", password);

				return params;
			}

		};

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
	}

	private void showDialog() {
		if (!pDialog.isShowing())
			pDialog.show();
	}

	private void hideDialog() {
		if (pDialog.isShowing())
			pDialog.dismiss();
	}
}

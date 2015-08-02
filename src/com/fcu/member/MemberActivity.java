package com.fcu.member;

import java.util.HashMap;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fcu.R;

public class MemberActivity extends Fragment {

	private TextView txtName;
	private TextView txtEmail;
	private Button btnLogout;

	private SQLiteHandler db;
	private SessionManager session;
	private View thisView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		thisView = inflater.inflate(R.layout.activity_member, container, false);
		txtName = (TextView) thisView.findViewById(R.id.name);
		txtEmail = (TextView) thisView.findViewById(R.id.email);
		btnLogout = (Button) thisView.findViewById(R.id.btnLogout);

		// SqLite database handler
		db = new SQLiteHandler(getActivity().getApplicationContext());

		// session manager
		session = new SessionManager(getActivity().getApplicationContext());

		if (!session.isLoggedIn()) {
			logoutUser();
		}

		// Fetching user details from sqlite
		HashMap<String, String> user = db.getUserDetails();

		String name = user.get("name");
		String email = user.get("email");

		// Displaying the user details on the screen
		txtName.setText(name);
		txtEmail.setText(email);

		// Logout button click event
		btnLogout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				logoutUser();
			}
		});
		
		return thisView;
	}

	/**
	 * Logging out the user. Will set isLoggedIn flag to false in shared
	 * preferences Clears the user data from sqlite users table
	 * */
	private void logoutUser() {
		session.setLogin(false);

		db.deleteUsers();

		// Launching the login activity
//		Intent intent = new Intent(MemberActivity.this, LoginActivity.class);
//		startActivity(intent);
//		finish();
		Fragment fragment = new LoginActivity();
		FragmentManager fragmentManager = getActivity().getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
	}
}

package com.example.spendsprout_opsc.utils

import android.content.Context
import android.widget.TextView
import com.example.spendsprout_opsc.R
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

object UserDisplayUtils {

    fun resolveDisplayName(context: Context): String {
        val authUser = FirebaseAuth.getInstance().currentUser
        val displayName = authUser?.displayName?.takeIf { it.isNotBlank() }
        val email = authUser?.email
        if (!displayName.isNullOrBlank()) return displayName
        if (!email.isNullOrBlank()) return email

        val prefs = context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        return prefs.getString("username", "User") ?: "User"
    }

    fun bindNavHeader(navView: NavigationView, context: Context) {
        val headerView = navView.getHeaderView(0) ?: return
        val usernameTextView = headerView.findViewById<TextView>(R.id.txt_Username)
        usernameTextView?.text = resolveDisplayName(context)
    }
}


package com.example.newsapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.newsapp.extensions.NotificationHelper
import com.example.newsapp.extensions.openUrlInCustomTabs

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onNewIntent(intent)
        if (savedInstanceState == null) {
            NewsBroadcastReceiver.setupAlarmManager(this)
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentHolder, HolderFragment.newInstance())
                .commit()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.getStringExtra(NotificationHelper.SHOW_NEWS)?.let {
            openUrlInCustomTabs(this, it)
        }
    }

    override fun onBackPressed() {
        finish()
    }
}

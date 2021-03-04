package com.example.newsapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.newsapp.extensions.openUrlInCustomTabs
import com.example.newsapp.notifications.NotificationHelper
import com.example.newsapp.notifications.PushWorkManager
import org.koin.core.component.KoinApiExtension
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var workManager: WorkManager

    @KoinApiExtension
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onNewIntent(intent)
        showAlertPowerSettings()
        if (savedInstanceState == null) {
            setupWorkManager()
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

    @KoinApiExtension
    private fun setupWorkManager() {
        if (!this::workManager.isInitialized) {
            workManager = WorkManager.getInstance(this)
            workManager.enqueueUniquePeriodicWork(
                PushWorkManager.TAG,
                ExistingPeriodicWorkPolicy.REPLACE,
                PeriodicWorkRequestBuilder<PushWorkManager>(1, TimeUnit.HOURS, 2, TimeUnit.HOURS)
                    .setInitialDelay(15, TimeUnit.MINUTES)
                    .build()
            )
        }
    }

    private fun showAlertPowerSettings() {
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        if (!pm.isIgnoringBatteryOptimizations(packageName) && !isDoNotShowAgain()) {
            AlertDialog.Builder(this)
                .setMessage(R.string.message_battery_optimization)
                .setPositiveButton(R.string.remove) { dialog, _ ->
                    startActivity(
                        Intent().apply {
                            action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
                        }
                    )

                    dialog.dismiss()
                }
                .setNeutralButton(R.string.do_not_show_again) { dialog, _ ->
                    setDoNotShowAgain()
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun isDoNotShowAgain(): Boolean {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        return sharedPref.getBoolean(getString(R.string.shpr_do_not_show_again), false)
    }

    private fun setDoNotShowAgain() {
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putBoolean(getString(R.string.shpr_do_not_show_again), true)
            apply()
        }
    }

    override fun onBackPressed() {
        finish()
    }
}

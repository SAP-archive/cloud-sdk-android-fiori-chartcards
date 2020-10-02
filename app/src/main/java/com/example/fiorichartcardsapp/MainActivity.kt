package com.example.fiorichartcardsapp

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import androidx.preference.PreferenceManager
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration : AppBarConfiguration

    private lateinit var sharedPrefs: SharedPreferences

    private var useLiveData by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart_cards)
        PreferenceManager.setDefaultValues(this, R.xml.settings_preferences, false)
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
    }

    override fun onResume() {
        super.onResume()
        Toast.makeText(this,
            if (sharedPrefs.getBoolean("useLiveData", false)) "Use Live Data" else "Offline Data Only",
            Toast.LENGTH_LONG).show()
        useLiveData = sharedPrefs.getBoolean("useLiveData", false)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)

        menuInflater.inflate(R.menu.main_menu_light, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(findNavController(R.id.nav_host))
            || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host).navigateUp(appBarConfiguration)
    }

}



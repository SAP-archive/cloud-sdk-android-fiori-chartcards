package com.example.fiorichartcardsapp

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        if (p1 != null) {
            findPreference<SwitchPreferenceCompat>(p1)?.let { updatePreferences(it) }
        }
    }

    private fun updatePreferences(p: Preference) {
        if (p is SwitchPreferenceCompat) {
            val switchPreference: SwitchPreferenceCompat = p
            val prefEditor = preferenceScreen.sharedPreferences.edit()
            prefEditor.putBoolean(switchPreference.key, switchPreference.isChecked)
            prefEditor.apply()
        }
    }
}
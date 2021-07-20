package com.cybershark.drawingsapp.ui.settings

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import androidx.fragment.app.viewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.cybershark.drawingsapp.BuildConfig
import com.cybershark.drawingsapp.R
import com.cybershark.drawingsapp.databinding.SettingsActivityBinding
import com.cybershark.drawingsapp.ui.settings.viewmodel.SettingsViewModel
import com.cybershark.drawingsapp.util.UIState
import com.cybershark.drawingsapp.util.observe
import com.cybershark.drawingsapp.util.showToast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: SettingsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarSettings)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // Settings custom anims
    override fun onBackPressed() {
        super.onBackPressed()
        setCustomAnims()
    }

    private fun setCustomAnims() = overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

    // Settings Up bottom listener
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }
}

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    private val settingsViewModel by viewModels<SettingsViewModel>()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        setVersionCode()
        setDeleteOnClick()
        setDarkThemePrefs()
    }

    private fun setObservers() {
        observe(settingsViewModel.uiState) { state ->
            when (state) {
                is UIState.COMPLETED -> showToast(state.message)
                is UIState.ERROR -> showToast(state.message)
                else -> Unit
            }
        }
    }

    // Sets theme on changing preference
    private fun setDarkThemePrefs() {
        findPreference<Preference>("darkTheme")?.setOnPreferenceChangeListener { _, newValue ->
            setDefaultNightMode(if (newValue as Boolean) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
            true
        }
    }

    // Delete all data on click
    private fun setDeleteOnClick() {
        findPreference<Preference>("deleteEverything")?.setOnPreferenceClickListener {
            openDeleteAlertDialog()
            true
        }
    }

    // Confirmation dialog to delete all data
    private fun openDeleteAlertDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_everything)
            .setMessage(R.string.irreversible)
            .setIcon(R.drawable.ic_delete_forever)
            .setPositiveButton(R.string.delete) { _, _ ->
                settingsViewModel.deleteAllData()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    // sets app version code in settings
    private fun setVersionCode() {
        findPreference<Preference>("versionCode")?.summary =
            "v" + BuildConfig.VERSION_NAME
    }
}
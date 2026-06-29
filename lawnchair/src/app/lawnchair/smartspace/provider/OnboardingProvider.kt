package app.lawnchair.smartspace.provider

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Icon
import androidx.core.content.edit
import app.lawnchair.preferences.preferenceManager
import app.lawnchair.smartspace.model.SmartspaceAction
import app.lawnchair.smartspace.model.SmartspaceScores
import app.lawnchair.smartspace.model.SmartspaceTarget
import app.lawnchair.util.getApkVersionComparison
import com.android.launcher3.LauncherPrefs.Companion.getPrefs
import com.android.launcher3.R
import com.android.launcher3.util.OnboardingPrefs
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class OnboardingProvider(context: Context) :
    SmartspaceDataSource(
        context,
        R.string.smartspace_onboarding,
        { smartspaceOnboarding },
    ) {

    companion object {
        const val PREF_LAWNCHAIR_MAJOR_VERSION = "pref_lawnchairMajorVersion"
        const val PREF_HAS_OPENED_SETTINGS = "pref_hasOpenedSettings"

        private val HOME_BOUNCE_KEY = OnboardingPrefs.HOME_BOUNCE_SEEN.sharedPrefKey
        private val PREF_KEYS = setOf(
            PREF_LAWNCHAIR_MAJOR_VERSION,
            PREF_HAS_OPENED_SETTINGS,
            HOME_BOUNCE_KEY,
        )

        private const val REQUEST_CODE_SETTINGS = 1
    }
    private val prefs = getPrefs(context)

    private val lawnSettingsIntent: Intent = Intent(Intent.ACTION_APPLICATION_PREFERENCES)
        .setPackage(context.packageName)

    private val lawnSettingsPendingIntent: PendingIntent = PendingIntent.getActivity(
        context,
        REQUEST_CODE_SETTINGS,
        lawnSettingsIntent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
    )

    /** No-op. */
    private val lawnOnboardingPendingIntent: PendingIntent = lawnSettingsPendingIntent

    override val internalTargets = kotlinx.coroutines.flow.flowOf(emptyList<SmartspaceTarget>())

    private fun getSmartspaceTarget(): SmartspaceTarget? = null
}

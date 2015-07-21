package benwaffle.srht;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Config extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
    public static String getApiKey(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx)
                .getString(
                        ctx.getString(R.string.apikey_pref_key),
                        ctx.getString(R.string.apikey_pref_default));
    }

    public static String getUrl(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx)
                .getString(
                        ctx.getString(R.string.url_pref_key),
                        ctx.getString(R.string.url_pref_default));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        addPreferencesFromResource(R.xml.prefs);
        bindPrefToValue(findPreference(getString(R.string.url_pref_key)));
        bindPrefToValue(findPreference(getString(R.string.apikey_pref_key)));

        return root;
    }

    private void bindPrefToValue(Preference pref) {
        pref.setOnPreferenceChangeListener(this);
        onPreferenceChange(pref, PreferenceManager // set UI from saved values
                .getDefaultSharedPreferences(pref.getContext())
                .getString(pref.getKey(), ""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        // update UI
        preference.setSummary(newValue.toString());
        return true;
    }
}

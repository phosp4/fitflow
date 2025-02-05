package sk.upjs.ics.utilities;

import lombok.Getter;

import java.util.List;
import java.util.Locale;
import java.util.prefs.Preferences;

/**
 * The `LocaleManager` class is responsible for managing the locale settings of the application.
 * It provides methods to save and retrieve the current locale using the `Preferences` API.
 * The supported locales are predefined and can be accessed via the `getSupportedLocales` method.
 */
public class LocaleManager {
    private static final String LOCALE_KEY = "appLocale";
    private static final Preferences prefs = Preferences.userNodeForPackage(LocaleManager.class);

    @Getter
    private static final List<Locale> supportedLocales = List.of(
            new Locale("sk"),
            new Locale("en"),
            new Locale("de")
    );

    /**
     * Saves the specified locale to the preferences.
     *
     * @param locale the locale to be saved
     */
    public static void saveLocale(Locale locale) {
        prefs.put(LOCALE_KEY, locale.toLanguageTag());
    }

    /**
     * Retrieves the current locale from the preferences.
     * If no locale is set, the default locale is returned.
     *
     * @return the current locale
     */
    public static Locale getLocale() {
        String localeTag = prefs.get(LOCALE_KEY, Locale.getDefault().toLanguageTag());
        return Locale.forLanguageTag(localeTag);
    }
}
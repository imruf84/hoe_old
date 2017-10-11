package hoe;

import java.util.HashMap;

/**
 * Üzeneteket kezelő osztály.
 *
 * @author imruf84
 */
public class Language {

    /**
     * Üzenetek tárolója.
     */
    private static final HashMap<String, HashMap<LanguageMessageKey, String>> MESSAGES = new HashMap<>();
    /**
     * Nyelvek.
     */
    private static final String LANG_HU = "hu";
    /**
     * Aktuális nyelv.
     */
    private static String currentLanguage = LANG_HU;

    /**
     * Inicializálás.
     */
    public static void init() {
        MESSAGES.put(LANG_HU, new HashMap<>());
        MESSAGES.get(LANG_HU).put(LanguageMessageKey.NAME, "Név");
        MESSAGES.get(LANG_HU).put(LanguageMessageKey.PASSWORD, "Jelszó");
        MESSAGES.get(LANG_HU).put(LanguageMessageKey.PASSWORD_CONFIRM, "Jelszó megerősítése");
        MESSAGES.get(LANG_HU).put(LanguageMessageKey.PASSWORDS_NOT_EQUALS, "A jelszavak nem egyeznek meg!");
        MESSAGES.get(LANG_HU).put(LanguageMessageKey.INVALID_PASSWORD, "Hibás felhasználónév vagy jelszó!");
        MESSAGES.get(LANG_HU).put(LanguageMessageKey.LOGIN, "Bejelentkezés");
        MESSAGES.get(LANG_HU).put(LanguageMessageKey.LOGOUT, "Kijelentkezés");
        MESSAGES.get(LANG_HU).put(LanguageMessageKey.LOGOUT_FAILED, "Kijelentkezés sikertelen");
        MESSAGES.get(LANG_HU).put(LanguageMessageKey.REGISTER, "Regisztráció");
        MESSAGES.get(LANG_HU).put(LanguageMessageKey.SEND_MESSAGE, "Küld");
        MESSAGES.get(LANG_HU).put(LanguageMessageKey.DONT_SUPPORT_CANVAS, "A böngésző nem támogatja a HTML5-öt!");
        MESSAGES.get(LANG_HU).put(LanguageMessageKey.USER_ALREADY_REGISTERED, "Már létezik ilyen néven regisztrált felhasználó!");
        MESSAGES.get(LANG_HU).put(LanguageMessageKey.INVALID_USER_NAME, "Hibás felhasználónév vagy jelszó!");
        MESSAGES.get(LANG_HU).put(LanguageMessageKey.STORING_USER_FAILED, "A felhasználó tárolása sikertelen!");
        MESSAGES.get(LANG_HU).put(LanguageMessageKey.USER_ALREADY_LOGGED_IN, "Már létezik ilyen néven bejelentkezett felhasználó!");
        MESSAGES.get(LANG_HU).put(LanguageMessageKey.USER_ALREADY_HAS_AN_ACTIVE_CONNECTION, "Már rendelkezik egy aktív kapcsolattal!<br>Az oldal %time% másodperc múlva újratöltődik.");
        MESSAGES.get(LANG_HU).put(LanguageMessageKey.USERNAME_MUST_BE_SET, "Felhasználónév megadása kötelező!");
        MESSAGES.get(LANG_HU).put(LanguageMessageKey.CREATING_SERVER_FAILED, "Szerver létrehozása sikertelen!");
        MESSAGES.get(LANG_HU).put(LanguageMessageKey.MESSAGE, "Üzenet");
        MESSAGES.get(LANG_HU).put(LanguageMessageKey.STORE_MESSAGE_FAILED, "Az üzenet tárolása sikertelen");
        MESSAGES.get(LANG_HU).put(LanguageMessageKey.GETTING_MESSAGES, "Beszélgetés letöltése...");
    }

    /**
     * Aktuális nyelv lekérdezése.
     *
     * @return aktuális nyelv
     */
    public static String getCurrentLanguage() {
        return currentLanguage;
    }

    /**
     * Aktuális nyelv megadása.
     *
     * @param currentLanguage aktuális nyelv
     */
    public static void setCurrentLanguage(String currentLanguage) {
        Language.currentLanguage = currentLanguage;
    }

    /**
     * Szöveg lekérdezése.
     *
     * @param language nyelv
     * @param messageKey szöveg kódja
     * @return szöveg
     */
    public static String getText(String language, LanguageMessageKey messageKey) {
        return MESSAGES.get(language).get(messageKey);
    }

    /**
     * Szöveg lekérdezése az aktuális nyelven.
     *
     * @param messageKey szöveg kódja
     * @return szöveg
     */
    public static String getText(LanguageMessageKey messageKey) {
        return Language.getText(currentLanguage, messageKey);
    }

}

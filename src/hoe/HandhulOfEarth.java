package hoe;

import java.util.Arrays;

/**
 * Handful Of Meteors alkalmazás alaposztálya.
 *
 * @author imruf84
 */
public class HandhulOfEarth {

    /**
     * Belépési pont.
     *
     * @param args parancssori argumentumok -p 8086:port meagadása
     * @throws java.lang.Exception kivétel
     */
    public static void main(String[] args) throws Exception {
        
        // Kapcsolók megjelenítése.
        if (!(Arrays.asList(args).indexOf("-h") < 0)) {
            System.out.println("-d: show debug messages");
            System.out.println("-p number: use alternative port number");
            return;
        }
        
        // Debug üzenetek megjelenítése.
        Log.showDebugMessages = !(Arrays.asList(args).indexOf("-d") < 0);
        
        Language.init();
        
        try {
            // Megadott port használata.
            int portIndex = Arrays.asList(args).indexOf("-p");
            int port = (!(portIndex < 0) ? Integer.parseInt(args[portIndex + 1]) : 80);

            HttpServer srv = new HttpServer(port);

        } catch (Exception ex) {
            Log.error(Language.getText(LanguageMessageKey.CREATING_SERVER_FAILED), ex);
        }
    }

}

package hoe;

import hoe.servers.GameServer;
import hoe.editor.Editor;
import hoe.servers.ContentServer;
import hoe.servers.RedirectServer;
import hoe.servers.RenderServer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class HandfulOfEarth {

    public static final int DEFAULT_RENDER_SERVER_PORT = 8083;
    public static final int DEFAULT_REDIRECT_SERVER_PORT = 8084;
    public static final int DEFAULT_CONTENT_SERVER_PORT = 8085;

    public static final String CL_SHOW_HELP = "help";
    public static final String CL_ENABLE_DEBUG = "enableDebug";
    public static final String CL_SET_GAME_SERVER_PORT = "setGameServerPort";
    public static final String CL_RUN_DATABASE_SERVER = "runDbServer";
    public static final String CL_RUN_GAME_SERVER = "runGameServer";
    public static final String CL_CONNECT_USER_DATABASE = "connectUserDb";
    public static final String CL_CONNECT_SCENE_DATABASE = "connectSceneDb";
    public static final String CL_RUN_REDIRECT_SERVER = "runRedirectServer";
    public static final String CL_RUN_CONTENT_SERVER = "runContentServer";
    public static final String CL_RUN_RENDER_SERVER = "runRenderServer";
    public static final String CL_SET_REDIRECT_SERVER_URL = "redirectServerUrl";
    public static final String CL_RUN_EDITOR = "runEditor";
    public static final String CL_GET_CURRENT_IP = "getIp";

    public static String sc(String s) {
        return "-" + s;
    }

    public static void main(String[] args) throws Exception {

        if (!(Arrays.asList(args).indexOf(sc(CL_SHOW_HELP)) < 0) || args.length == 0) {
            Log.print(sc(CL_SHOW_HELP) + ": show this messages");
            Log.print(sc(CL_ENABLE_DEBUG) + ": show debug messages");
            Log.print(sc(CL_SET_GAME_SERVER_PORT) + " number: use alternative port number");
            Log.print(sc(CL_RUN_DATABASE_SERVER) + ": run database server");
            Log.print(sc(CL_RUN_GAME_SERVER) + ": run game server");
            Log.print(sc(CL_CONNECT_USER_DATABASE) + " ip: ip of users database server");
            Log.print(sc(CL_CONNECT_SCENE_DATABASE) + " ip: ip of scene database server");
            Log.print(sc(CL_RUN_REDIRECT_SERVER) + ": run redirect server");
            Log.print(sc(CL_RUN_CONTENT_SERVER) + ": run content server");
            Log.print(sc(CL_RUN_RENDER_SERVER) + ": run render server");
            Log.print(sc(CL_SET_REDIRECT_SERVER_URL) + ": set redirect server's url");
            Log.print(sc(CL_RUN_EDITOR) + ": run editor");
            Log.print(sc(CL_GET_CURRENT_IP) + ": get current ip address");
            return;
        }

        //for (int i = 0; i < 10; i++) System.out.println(UUID.randomUUID());
        Log.showDebugMessages = !(Arrays.asList(args).indexOf(sc(CL_ENABLE_DEBUG)) < 0);

        Language.init();
        Cryptography.setKey("key");
        
        boolean getIp = !(Arrays.asList(args).indexOf(sc(CL_GET_CURRENT_IP)) < 0);
        if (getIp) {
            try {
                InetAddress ipAddr = InetAddress.getLocalHost();
                System.out.println(ipAddr.getHostAddress());
            } catch (UnknownHostException ex) {
                Log.error(ex);
            }
        }

        boolean runDatabaseServer = !(Arrays.asList(args).indexOf(sc(CL_RUN_DATABASE_SERVER)) < 0);
        if (runDatabaseServer) {
            hoe.servers.DatabaseServer.startServers();
        }

        boolean hasUsersDataBaseIp = !(Arrays.asList(args).indexOf(sc(CL_CONNECT_USER_DATABASE)) < 0);
        if (hasUsersDataBaseIp) {
            int usersDataBaseIndex = Arrays.asList(args).indexOf(sc(CL_CONNECT_USER_DATABASE));
            if (!(usersDataBaseIndex < 0)) {
                UserManager.setDataBaseIp(args[usersDataBaseIndex + 1]);
            }
        }

        boolean hasSceneDataBaseIp = !(Arrays.asList(args).indexOf(sc(CL_CONNECT_SCENE_DATABASE)) < 0);
        if (hasSceneDataBaseIp) {
            int sceneDataBaseIndex = Arrays.asList(args).indexOf(sc(CL_CONNECT_SCENE_DATABASE));
            if (!(sceneDataBaseIndex < 0)) {
                SceneManager.setDataBaseIp(args[sceneDataBaseIndex + 1]);
            }
        }

        String redirectServerUrl = "http://localhost:" + DEFAULT_REDIRECT_SERVER_PORT;
        boolean isRedirectServerUrlSet = !(Arrays.asList(args).indexOf(sc(CL_SET_REDIRECT_SERVER_URL)) < 0);
        if (isRedirectServerUrlSet) {
            int redirectServerUrlIndex = Arrays.asList(args).indexOf(sc(CL_SET_REDIRECT_SERVER_URL));
            redirectServerUrl = "http://" + args[redirectServerUrlIndex + 1];
        }

        boolean runRedirectServer = !(Arrays.asList(args).indexOf(sc(CL_RUN_REDIRECT_SERVER)) < 0);
        if (runRedirectServer) {
            RedirectServer server = new RedirectServer(DEFAULT_REDIRECT_SERVER_PORT);
            server.start();
        }

        boolean runContentServer = !(Arrays.asList(args).indexOf(sc(CL_RUN_CONTENT_SERVER)) < 0);
        if (runContentServer) {
            ContentServer server = new ContentServer(DEFAULT_CONTENT_SERVER_PORT);
            server.setRedirectServerUrl(redirectServerUrl);
            server.start();
        }

        boolean runRenderServer = !(Arrays.asList(args).indexOf(sc(CL_RUN_RENDER_SERVER)) < 0);
        if (runRenderServer) {
            RenderServer server = new RenderServer(DEFAULT_RENDER_SERVER_PORT);
            server.setRedirectServerUrl(redirectServerUrl);
            server.start();
        }

        boolean runGameServer = !(Arrays.asList(args).indexOf(sc(CL_RUN_GAME_SERVER)) < 0);
        if (runGameServer) {
            try {
                int portIndex = Arrays.asList(args).indexOf(sc(CL_SET_GAME_SERVER_PORT));
                int port = (!(portIndex < 0) ? Integer.parseInt(args[portIndex + 1]) : 80);

                GameServer server = new GameServer(port);
                server.setRedirectServerUrl(redirectServerUrl);
                server.start();

            } catch (Exception ex) {
                Log.error(Language.getText(LanguageMessageKey.CREATING_SERVER_FAILED), ex);
            }
        }

        boolean runEditor = !(Arrays.asList(args).indexOf(sc(CL_RUN_EDITOR)) < 0);
        if (runEditor) {
            Editor editor = new Editor();
            editor.show();
        }

    }

}

package hoe;

import hoe.servers.GameServer;
import hoe.editor.Editor;
import hoe.servers.RenderServer;
import java.util.Arrays;

public class HandhulOfEarth {

    public static String CL_HELP = "h";
    public static String CL_DEBUG = "d";
    public static String CL_PORT = "p";
    public static String CL_DATABASE_SERVER = "db";
    public static String CL_RENDER_SERVER = "r";
    public static String CL_GAME_SERVER = "g";
    public static String CL_USER_DATABASE = "u";
    public static String CL_SCENE_DATABASE = "s";
    public static String CL_EDITOR = "e";

    public static String sc(String s) {
        return "-" + s;
    }

    public static void main(String[] args) throws Exception {

        if (!(Arrays.asList(args).indexOf(sc(CL_HELP)) < 0) || args.length == 0) {
            Log.print(sc(CL_HELP) + ": show this messages");
            Log.print(sc(CL_DEBUG) + ": show debug messages");
            Log.print(sc(CL_PORT) + " number: use alternative port number");
            Log.print(sc(CL_DATABASE_SERVER) + ": run database server");
            Log.print(sc(CL_RENDER_SERVER) + ": run render server");
            Log.print(sc(CL_GAME_SERVER) + ": run game server");
            Log.print(sc(CL_USER_DATABASE) + " ip: ip of users database server");
            Log.print(sc(CL_SCENE_DATABASE) + " ip: ip of scene database server");
            Log.print(sc(CL_EDITOR) + ": run editor");
            return;
        }

        //for (int i = 0; i < 10; i++) System.out.println(UUID.randomUUID());
        Log.showDebugMessages = !(Arrays.asList(args).indexOf(sc(CL_DEBUG)) < 0);

        Language.init();

        boolean runDatabaseServer = !(Arrays.asList(args).indexOf(sc(CL_DATABASE_SERVER)) < 0);
        if (runDatabaseServer) {
            hoe.servers.DatabaseServer.startServers();
        }

        boolean hasUsersDataBaseIp = !(Arrays.asList(args).indexOf(sc(CL_USER_DATABASE)) < 0);
        if (hasUsersDataBaseIp) {
            int usersDataBaseIndex = Arrays.asList(args).indexOf(sc(CL_USER_DATABASE));
            if (!(usersDataBaseIndex < 0)) {
                UserManager.setDataBaseIp(args[usersDataBaseIndex + 1]);
            }
        }

        boolean hasSceneDataBaseIp = !(Arrays.asList(args).indexOf(sc(CL_SCENE_DATABASE)) < 0);
        if (hasSceneDataBaseIp) {
            int sceneDataBaseIndex = Arrays.asList(args).indexOf(sc(CL_SCENE_DATABASE));
            if (!(sceneDataBaseIndex < 0)) {
                SceneManager.setDataBaseIp(args[sceneDataBaseIndex + 1]);
            }
        }

        boolean runRenderServer = !(Arrays.asList(args).indexOf(sc(CL_RENDER_SERVER)) < 0);
        if (runRenderServer) {
            RenderServer server = new RenderServer(8083);
            server.start();
        }

        boolean runGameServer = !(Arrays.asList(args).indexOf(sc(CL_GAME_SERVER)) < 0);
        if (runGameServer) {
            try {
                int portIndex = Arrays.asList(args).indexOf(sc(CL_PORT));
                int port = (!(portIndex < 0) ? Integer.parseInt(args[portIndex + 1]) : 80);

                GameServer server = new GameServer(port);
                server.start();

            } catch (Exception ex) {
                Log.error(Language.getText(LanguageMessageKey.CREATING_SERVER_FAILED), ex);
            }
        }

        boolean runEditor = !(Arrays.asList(args).indexOf(sc(CL_EDITOR)) < 0);
        if (runEditor) {
            Editor editor = new Editor();
            editor.show();
        }

    }

}

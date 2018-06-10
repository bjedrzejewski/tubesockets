import io.javalin.Javalin;
import io.javalin.embeddedserver.jetty.websocket.WsSession;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Main {

    private static Set<WsSession> sessions = ConcurrentHashMap.newKeySet();
    public static int newPlayerId = 1;

    public static void main(String[] args) {

        Javalin.create()
                .port(7070)
                .ws("/game/:session-id", ws -> {
                    ws.onConnect(session -> {
                        session.send("{\"playerId\":"+newPlayerId+++"}");
                        sessions.add(session);
                    });
                    ws.onMessage((session, message) -> {
                        System.out.println("received message: "+ message);
                        for(WsSession wsSession : sessions){
                            //broadcast message to everyone
                            if(wsSession.getId() != session.getId()){
                                wsSession.send(message);
                            }
                        }
                    });
                    ws.onError(((wsSession, throwable) ->
                            System.out.println("Something went wrong " + throwable.getStackTrace())
                    ));
                    ws.onClose((session, status, message) -> {
                        sessions.remove(session);
                        //clean-up
                    });
                })
                .start();

    }

}

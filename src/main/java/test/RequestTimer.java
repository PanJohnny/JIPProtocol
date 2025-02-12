package test;

import me.panjohnny.jip.transport.packet.ResponsePacket;

public class RequestTimer {
    private static final System.Logger LOGGER = System.getLogger("RequestTimer");
    public static ResponsePacket watch(Task task, String title) {
        long current = System.nanoTime();
        ResponsePacket r;
        try {
            r = task.run();
        } catch (Exception e) {
            LOGGER.log(System.Logger.Level.INFO, "[ŽÁDOST SELHALA] " + title, e);
            return null;
        }
        long elapsed = System.nanoTime() - current;
        LOGGER.log(System.Logger.Level.INFO, "[MĚŘENÍ ÚSPĚŠNÉ] {0} $$ {1} ms", title, elapsed/1_000_000);
        return r;
    }

    @SuppressWarnings("RedundantThrows")
    public interface Task {
        ResponsePacket run() throws Exception;
    }
}

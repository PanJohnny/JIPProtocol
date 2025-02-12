package test;

import me.panjohnny.jip.client.Client;
import me.panjohnny.jip.commons.StatusCode;
import me.panjohnny.jip.server.JIPServer;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetSocketAddress;
import java.nio.file.Files;

public class FileTransmission {
    public static void main(String[] args) throws Exception {
        var serverIP = new InetSocketAddress(8080);
        JIPServer server = JIPServer.create(serverIP);
        server.getRouter().route("/shrek", (req, res, _) -> {
            try {
                res.sendFile(new File("shrek.jpg"));
            } catch (FileNotFoundException e) {
                res.setStatus(StatusCode.ERROR);
                res.sendString("Nepovedlo se otevřít soubor");
            }
        });
        server.start();

        Client client = Client.create(serverIP);
        client.connect();

        var response = RequestTimer.watch(() -> client.fetch("/shrek"), "Shrek Image");
        client.close();
        server.stop();

        assert response != null;
        Files.write(new File("shrek2.jpg").toPath(), response.getBody());

        // Open shrek2.jpg
        JFrame frame = new JFrame("Shrek");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new JLabel(new ImageIcon("shrek2.jpg")));

        frame.pack();
        frame.setVisible(true);
    }
}

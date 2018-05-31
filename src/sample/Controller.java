package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import sample.Server.Server;

public class Controller {
    private Server server;

    @FXML
    private TextArea textArea;

    @FXML
    private Button startServerButton;

    @FXML
    private void startServer(){
        startServerButton.setDisable(true);
        server = new Server(textArea);
        Thread t = new Thread(server);
        t.start();
    }
}

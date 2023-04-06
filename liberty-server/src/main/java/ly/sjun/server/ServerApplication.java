package ly.sjun.server;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ly.sjun.server.contants.NodePool;

import java.awt.*;
import java.io.IOException;

public class ServerApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException, ClassNotFoundException, InterruptedException, AWTException {

        FXMLLoader fxmlLoader = new FXMLLoader(ServerApplication.class.getResource("index-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);

        NodePool.put(scene);//将scene加入常量池

        stage.setTitle("liberty server");
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                stage.close();
                System.exit(1);
            }
        });
    }

}
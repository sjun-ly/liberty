package ly.sjun.client;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.extern.slf4j.Slf4j;
import ly.sjun.client.contants.IntelContant;
import ly.sjun.client.contants.NodePool;
import ly.sjun.client.server.ConcurrentClient;
import ly.sjun.common.entity.event.KeyEventEntity;
import ly.sjun.common.entity.event.MouseEventEntity;

import java.awt.*;
import java.io.IOException;

@Slf4j
public class ClientApplication extends Application {


    @Override
    public void start(Stage stage) throws IOException, ClassNotFoundException, InterruptedException, AWTException {

        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("index-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);

        NodePool.put(scene);//将scene加入常量池
        scene.getStylesheets().add("main.css");
        stage.setTitle("liberty remote");
        stage.setScene(scene);
        stage.show();

        setStagerLister(stage);
    }

    public static void setImageListen(Node node) {
        //鼠标单击
        node.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    node.requestFocus();
                    log.info("triggered a mouse click event");
                    MouseEventEntity mouseEventEntity = new MouseEventEntity(mouseEvent);
                    ConcurrentClient.send(mouseEventEntity);
                } catch (Exception e) {
                    log.error("An exception occurred in the mouse event{}", e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        });

        //鼠标释放
        node.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    log.info("triggered mouse release event");
                    MouseEventEntity mouseEventEntity = new MouseEventEntity(mouseEvent);
                    ConcurrentClient.send(mouseEventEntity);
                } catch (Exception e) {
                    log.error("An exception occurred in the mouse event{}", e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        });

//        鼠标移动
        node.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    log.info("triggered mouse movement event");
                    MouseEventEntity mouseEventEntity = new MouseEventEntity(mouseEvent);
                    ConcurrentClient.send(mouseEventEntity);
                } catch (Exception e) {
                    log.error("An exception occurred in the mouse event{}", e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        });

        //鼠标按下
        node.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    log.info("triggered a mouse down event");
                    MouseEventEntity mouseEventEntity = new MouseEventEntity(mouseEvent);
                    ConcurrentClient.send(mouseEventEntity);
                } catch (Exception e) {
                    log.error("An exception occurred in the mouse event{}", e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        });

        //鼠标拖动
        node.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    log.info("triggered mouse drag event");
                    MouseEventEntity mouseEventEntity = new MouseEventEntity(mouseEvent);
                    ConcurrentClient.send(mouseEventEntity);
                } catch (Exception e) {
                    log.error("An exception occurred in the mouse event{}", e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        });

        //键盘按下
        node.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                KeyEventEntity keyEventEntity = new KeyEventEntity(keyEvent);
                try {
                    log.info("triggered keyboard press event");
                    ConcurrentClient.send(keyEventEntity);
                } catch (Exception e) {
                    log.error("An exception occurred in the mouse event{}", e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        });

        //键盘释放
        node.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                KeyEventEntity keyEventEntity = new KeyEventEntity(keyEvent);
                try {
                    log.info("triggered keyboard release event");
                    ConcurrentClient.send(keyEventEntity);
                } catch (Exception e) {
                    log.error("An exception occurred in the mouse event{}", e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        });

        //字符串存值事件 暂无作用
        node.setOnKeyTyped(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {

            }
        });

    }


    private void setStagerLister(Stage stage) {

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                stage.close();
                System.exit(1);
            }
        });

        stage.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                if (IntelContant.IP != null && IntelContant.PORT != null) {
                    ConcurrentClient.send();
                }
            }
        });

        stage.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                if (IntelContant.IP != null && IntelContant.PORT != null) {
                    ConcurrentClient.send();
                }
            }
        });
    }

}
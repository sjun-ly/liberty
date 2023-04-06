package ly.sjun.server.fxcontroller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import ly.sjun.common.controller.FxController;
import ly.sjun.server.ServerApplication;
import ly.sjun.server.contants.IntelContant;
import ly.sjun.server.contants.NodePool;
import ly.sjun.server.server.ConcurrentServer;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.util.UUID;

import static ly.sjun.server.contants.IntelContant.connect_key;

/**
 * @ClassName: IndexController
 * @Author: ly
 * @Date: 2023/3/3-15:28
 * @Version: 1.0
 * @Description TODO
 **/
public final class IndexController implements FxController {

    @FXML
    private TextField portField;

    @FXML
    private TextField key;

    @FXML
    protected void connect() {
        Notifications notifications = Notifications.create();
        String port = portField.getText();
        if (port == null || "".equals(port)) {
            notifications.text("端口不能为空！");
            notifications.position(Pos.TOP_CENTER);
            notifications.showWarning();
            return;
        }
        int portIntValue = 0;
        try {
            portIntValue = Integer.parseInt(port);
            if (portIntValue > 65535 || portIntValue <= 5000) {
                notifications.text("请使用 5001 - 65535 之间的端口号!");
                notifications.position(Pos.TOP_CENTER);
                notifications.showWarning();
                return;
            }
        } catch (NumberFormatException e) {
            notifications.text("请输入正确的端口号！");
            notifications.position(Pos.TOP_CENTER);
            notifications.showWarning();
            return;
        }

        if (key.getText() == null || "".equals(key.getText())) {
            notifications.text("请输入安全密钥！");
            notifications.position(Pos.TOP_CENTER);
            notifications.showWarning();
            return;
        }
        if (key.getText().length() < 8) {
            notifications.text("安全密钥长度过低，请输入至少8位！");
            notifications.position(Pos.TOP_CENTER);
            notifications.showWarning();
            return;
        }
        connect_key = key.getText().trim();

        notifications.text("准备开启服务！");
        notifications.position(Pos.TOP_CENTER);
        notifications.showInformation();

        IntelContant.SERVER_START = true;
        IntelContant.PORT = portIntValue;

        Platform.runLater(new Thread() {
            @Override
            public void run() {
                FXMLLoader fxmlLoader = new FXMLLoader(ServerApplication.class.getResource("server-view.fxml"));
                try {
                    Object load = fxmlLoader.load();
                    if (load instanceof Pane) {
                        Pane root = (Pane) load;
                        Scene sceneClass = (Scene) NodePool.get(Scene.class);
                        sceneClass.setRoot(root);
                        NodePool.put("root", root);
                        ServerController serverController = (ServerController)NodePool.get(ServerController.class);
                        serverController.getKey().setText(connect_key);
                    } else {
                        notifications.text("配置文件加载错误,请检查文件完整性！");
                        notifications.position(Pos.TOP_CENTER);
                        notifications.showError();
                        return;
                    }

                    Thread r = new Thread() {
                        @Override
                        public void run() {
                            try {
                                new ConcurrentServer();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    };
                    IntelContant.serverThread = r;
                    r.start();

                    notifications.text("开启成功！");
                    notifications.position(Pos.TOP_CENTER);
                    notifications.showInformation();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    @FXML
    protected void exitLiberty() {
        System.exit(1);
    }


    @FXML
    protected void generateKey() {
        final String REQUEST_ID = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        key.setText(REQUEST_ID);
    }

}

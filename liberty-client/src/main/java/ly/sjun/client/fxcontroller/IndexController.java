package ly.sjun.client.fxcontroller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import lombok.extern.slf4j.Slf4j;
import ly.sjun.client.ClientApplication;
import ly.sjun.client.contants.IntelContant;
import ly.sjun.client.contants.NodePool;
import ly.sjun.client.server.ConcurrentClient;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName: IndexController
 * @Author: ly
 * @Date: 2023/3/3-15:28
 * @Version: 1.0
 * @Description TODO
 **/
@Slf4j
public final class IndexController {


    @FXML
    private TextField ipField;

    @FXML
    private TextField portField;

    @FXML
    private TextField key;

    @FXML
    protected void connect() {
        String ip = ipField.getText();
        Notifications notifications = Notifications.create();
        String port = portField.getText();
        if (ip == null || "".equals(ip)) {
            notifications.text("ip不能为空！");
            notifications.position(Pos.TOP_CENTER);
            notifications.showWarning();
            return;
        }
        Pattern ipPattern = Pattern.compile("^\\d{0,3}.\\d{0,3}.\\d{0,3}.\\d{0,3}$");
        Matcher ipMatcher = ipPattern.matcher(ip);
        if (!ipMatcher.find()) {
            notifications.text("请输入正确的ip地址！");
            notifications.position(Pos.TOP_CENTER);
            notifications.showWarning();
            return;
        }
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
            notifications.text("密钥不能为空！");
            notifications.position(Pos.TOP_CENTER);
            notifications.showWarning();
            return;
        }

        IntelContant.key = key.getText().trim();

        if (key.getText().length() < 8) {
            notifications.text("安全密钥长度过低，请输入至少8位！");
            notifications.position(Pos.TOP_CENTER);
            notifications.showWarning();
            return;
        }

        notifications.text("正在尝试连接请等待！");
        notifications.position(Pos.TOP_CENTER);
        notifications.showInformation();

        notifications.text("准备开启！");
        notifications.position(Pos.TOP_CENTER);
        notifications.showInformation();

        IntelContant.IP = ip;
        IntelContant.PORT = portIntValue;

        int finalPortIntValue = portIntValue;
        Platform.runLater(new Thread() {
            @Override
            public void run() {
                boolean isConnect = connectHost(ip, finalPortIntValue);
                if (isConnect) {
                    notifications.text("连接成功！！！ \n" +
                            "✈___\uD83D\uDEE9___\uD83D\uDEEB___\uD83D\uDEE6___\uD83D\uDEE8");
                    notifications.position(Pos.TOP_CENTER);
                    notifications.showInformation();
                    FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("client-view.fxml"));
                    try {
                        Object load = fxmlLoader.load();
                        if (load instanceof BorderPane) {
                            BorderPane root = (BorderPane) load;
                            Scene scene = (Scene) NodePool.get(Scene.class);
                            scene.setRoot(root);
                            ImageView imageView = (ImageView) root.getCenter();
                            ClientApplication.setImageListen(imageView);
                            imageView.setFitWidth(root.getWidth());
                            imageView.setFitHeight(root.getHeight()-30);
                            imageView.setSmooth(true);
                            imageView.setPreserveRatio(true);
                            NodePool.put("root", root);
                            NodePool.put("imageView", imageView);
                            ConcurrentClient.send();
                        } else {
                            notifications.text("配置文件加载错误,请检查文件完整性！");
                            notifications.position(Pos.TOP_CENTER);
                            notifications.showError();
                            return;
                        }
                    } catch (IOException e) {
                        notifications.text(e.getMessage());
                        notifications.position(Pos.TOP_CENTER);
                        notifications.showError();
                        log.error("An exception occurred in the IOException{}", e.getMessage());
                        throw new RuntimeException(e);
                    }
                } else {
                    notifications.text("连接失败，请检查ip/端口是否正确，或者关闭防火墙！");
                    notifications.position(Pos.TOP_CENTER);
                    notifications.showWarning();
                }
            }
        });

    }

    @FXML
    protected void exitLiberty() {
        System.exit(1);
    }

    private boolean connectHost(String ip, int port) {
        try {
            log.info("start to detect whether the server is online");
            Socket socket = new Socket(ip, port);
            if (socket.isConnected()) {
                socket.close();
                return true;
            } else {
                socket.close();
                return false;
            }
        } catch (IOException e) {
            log.error("An exception occurred in the connect socket");
            return false;
        }
    }

}

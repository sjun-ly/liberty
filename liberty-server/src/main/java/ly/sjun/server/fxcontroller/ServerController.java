package ly.sjun.server.fxcontroller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import lombok.Data;
import ly.sjun.common.controller.FxController;
import ly.sjun.server.ServerApplication;
import ly.sjun.server.contants.IntelContant;
import ly.sjun.server.contants.NodePool;
import ly.sjun.server.entity.ConnectDetail;
import ly.sjun.server.entity.Historty;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

@Data
public class ServerController implements FxController {

    public ServerController() {
        NodePool.put(this);
    }

    private Notifications notification = Notifications.create();


    @FXML
    private Label currentConnect;

    @FXML
    private Label havingConnect;

    @FXML
    private Label key;

    @FXML
    private VBox history;

    @FXML
    protected void exitLiberty() {
        while (!IntelContant.serverThread.isInterrupted()) {
            IntelContant.serverThread.interrupt();
        }
        FXMLLoader fxmlLoader = new FXMLLoader(ServerApplication.class.getResource("index-view.fxml"));
        try {
            Object load = fxmlLoader.load();
            Pane root = (Pane) load;
            Scene sceneClass = (Scene) NodePool.get(Scene.class);
            sceneClass.setRoot(root);
            NodePool.put("root", root);

            notification.text("服务已经关闭!");
            notification.position(Pos.TOP_CENTER);
            notification.showWarning();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setCurrent(Historty historty) {
        havingConnect.setText("当前已连接");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        currentConnect.setWrapText(true);
        currentConnect.setFont(new Font("宋体", 15));
        currentConnect.setTextFill(Color.rgb(255, 0, 0, 1));
        currentConnect.setText("ip : [ " + historty.getIp() + " ] " +
                "\nport : [ " + historty.getPort() + " ]" +
                "\nconnect : [ " + sdf.format(historty.getConnectTime()) + " ]" +
                "\nendTime : [ " + historty.getEndTime() + " ]");
    }

    public void setNoConnect() {
        havingConnect.setText("当前没有连接");
        currentConnect.setText(null);
    }

    public void addHistory(Historty historty) {
        ConnectDetail detail = ConnectDetail.getDetail();
        detail.add(historty);

        ObservableList<Node> children = this.history.getChildren();
        Label label = new Label();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        label.setWrapText(true);
        label.setTextFill(Color.rgb(255, 0, 0, 1));
        label.setText("ip : [" + historty.getIp() + " ] , port : [ " + historty.getPort() + " ]" +
                "\n\t\tconnect : [" + sdf.format(historty.getConnectTime()) + " ]" +
                "\n\t\tendTime : [" + historty.getEndTime() + " ]");
        children.add(0, label);
    }


    public void reloadHistory() {
        ObservableList<Node> children = this.history.getChildren();
        boolean removeAll = children.removeAll();
        if (removeAll) {
            ConnectDetail detail = ConnectDetail.getDetail();
            List<Historty> connectList = detail.getConnectList();
            for (int i = 0; i < connectList.size(); i++) {
                Historty historty = connectList.get(i);
                Label label = new Label();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                label.setWrapText(true);
                label.setTextFill(Color.rgb(255, 0, 0, 1));
                label.setText("ip : [" + historty.getIp() + " ] , port : [ " + historty.getPort() + " ]" +
                        "\n\t\tconnect : [" + sdf.format(historty.getConnectTime()) + " ]" +
                        "\n\t\tendTime : [" + historty.getEndTime() + " ]");
                children.add(0, label);
            }
        }

    }

}
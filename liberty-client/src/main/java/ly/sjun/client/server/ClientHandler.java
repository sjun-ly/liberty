package ly.sjun.client.server;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;
import ly.sjun.client.ClientApplication;
import ly.sjun.client.contants.IntelContant;
import ly.sjun.client.contants.NodePool;
import ly.sjun.common.entity.connect.EventEntity;
import ly.sjun.common.entity.connect.Receive;
import ly.sjun.common.entity.connect.Safety;
import ly.sjun.common.entity.event.Event;
import org.controlsfx.control.Notifications;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @ClassName: ClientHandler
 * @Author: ly
 * @Date: 2023/3/8-11:15
 * @Version: 1.0
 * @Description TODO
 **/
@Slf4j
public class ClientHandler extends ChannelInboundHandlerAdapter {

    private Event event = null;

    private byte[] bytes = null;

    public ClientHandler() {
    }

    public ClientHandler(Event event) {
        this.event = event;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("The client has been started and starts sending detection messages to the server");
        IntelContant.channel = ctx;
        Safety safety = new Safety();
        safety.setKey(IntelContant.key);
        safety.setExit(false);
        if (event != null) {
            EventEntity eventEntity = new EventEntity();
            eventEntity.setEvent(JSONObject.toJSONString(event));
            eventEntity.setEventClass(event.getClass().getName());
            ImageView imageView = (ImageView) NodePool.get("imageView");
            eventEntity.setImageHeight(imageView.getFitHeight());
            eventEntity.setImageWidth(imageView.getFitWidth());
            safety.setEventEntity(eventEntity);
        }
        ctx.writeAndFlush(safety);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        onException();
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof byte[]) {
            byte[] f = (byte[]) msg;
            if (bytes == null) {
                bytes = f;
                return;
            }
            byte[] flag = new byte[bytes.length + f.length];
            System.arraycopy(bytes, 0, flag, 0, bytes.length);
            System.arraycopy(f, 0, flag, bytes.length, f.length);
            bytes = flag;
        }
        if (msg instanceof Receive) {
            log.info("The client receives a message from the server : [{}]", msg);
            try {
                ImageView imageView = (ImageView) NodePool.get("imageView");
                BorderPane root = (BorderPane) NodePool.get("root");
                imageView.setImage(new Image(new ByteArrayInputStream(bytes)));
                bytes = null;
                imageView.setFitWidth(root.getWidth());
                imageView.setFitHeight(root.getHeight() - 30);
                imageView.setSmooth(true);
                imageView.setPreserveRatio(true);
                log.info("A client request has been completed");
            } catch (Exception e) {
                log.error("An exception occurred on the client side", e);
                ctx.close();
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("An exception occurred on the client side", cause);
        onException();
        ctx.close();
    }


    public static void send(Event event) {
        Safety safety = new Safety();
        safety.setKey(IntelContant.key);
        safety.setExit(false);
        if (event != null) {
            EventEntity eventEntity = new EventEntity();
            eventEntity.setEvent(JSONObject.toJSONString(event));
            eventEntity.setEventClass(event.getClass().getName());
            ImageView imageView = (ImageView) NodePool.get("imageView");
            eventEntity.setImageHeight(imageView.getFitHeight());
            eventEntity.setImageWidth(imageView.getFitWidth());
            safety.setEventEntity(eventEntity);
        }
        ChannelFuture channelFuture = IntelContant.channel.channel().writeAndFlush(safety);
        try {
            channelFuture.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isDone()) {
                        if (future.isSuccess()) {
                        } else if (future.cause() != null) {
                            log.error("Task execution exception ：" + future.cause());
                            onException();
                        } else {
                            onException();
                        }
                    } else {
                        onException();
                    }
                }
            });
        } catch (Exception e) {
            onException();
            log.error("listener is exception : ", e);
        }
    }

    protected static void onException() {
        if(IntelContant.channel != null){
            IntelContant.channel = null;
        }
        Platform.runLater(() -> {
            FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("index-view.fxml"));
            try {
                Notifications notifications = Notifications.create();
                Object load = fxmlLoader.load();
                Pane root = (Pane) load;
                Scene sceneClass = (Scene) NodePool.get(Scene.class);
                sceneClass.setRoot(root);

                notifications.text("断开连接！！！\n" +
                        "\uD83D\uDEE8___\uD83D\uDEE6____\uD83D\uDEEB___\uD83D\uDEE9___✈___\uD83D\uDEEC");
                notifications.position(Pos.TOP_CENTER);
                notifications.showError();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}

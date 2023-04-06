package ly.sjun.server.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import ly.sjun.common.entity.connect.EventEntity;
import ly.sjun.common.entity.connect.Receive;
import ly.sjun.common.entity.connect.Safety;
import ly.sjun.server.contants.IntelContant;
import ly.sjun.server.contants.NodePool;
import ly.sjun.server.entity.Historty;
import ly.sjun.server.fxcontroller.ServerController;

import java.net.InetSocketAddress;
import java.util.Date;

/**
 * @ClassName: ServerHandler
 * @Author: ly
 * @Date: 2023/3/8-11:13
 * @Version: 1.0
 * @Description TODO
 **/
@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private static int isRegister = 0;

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = socketAddress.getAddress().getHostAddress();
        log.info("A registration request has been received at : {}", clientIp);
        if (isRegister == 0) {
            isRegister += 1;
            return;
        } else {
            isRegister += 1;
        }
//        if (IntelContant.IS_CONNECT) {
//            ctx.channel().close();
//        }

        IntelContant.IS_CONNECT = true;
        Historty historty = new Historty();
        historty.setIp(clientIp);
        historty.setPort(socketAddress.getPort());
        historty.setConnectTime(new Date());

        Platform.runLater(() -> {
            ServerController controller = (ServerController) NodePool.get(ServerController.class);
            controller.addHistory(historty);
            controller.setCurrent(historty);
        });
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = socketAddress.getAddress().getHostAddress();
        disconnect();
        log.info("The client actively disconnects, address : {}", clientIp);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = socketAddress.getAddress().getHostAddress();
        log.info("received a message from the client({}) : [{}]", clientIp, msg);

        if (msg instanceof Safety) {

            Safety safety = (Safety) msg;
            EventEntity eventEntity = safety.getEventEntity();

            if (safety.getKey() == null || !IntelContant.connect_key.equals(safety.getKey().trim())) {
                log.warn("The key of a client is abnormal, the key of the server is[{}], and the key received by the client is[{}],The IP address of the client is[{}]", IntelContant.connect_key, safety.getKey(), clientIp);
                disconnect();
                ctx.channel().close();
                return;
            }

            if(eventEntity != null){
                RobotService.actionEvent(eventEntity);
            }

            if (ctx.channel().isWritable()) {
                byte[] caputure = RobotService.caputure();
                log.info("screenshot successful, total size [{}] bit", caputure.length);
                int len = 0;
                while (len < caputure.length) {
                    byte[] b = null;
                    if ((len + 1024) >= caputure.length) {
                        b = new byte[caputure.length - len];
                    } else {
                        b = new byte[1024];
                    }
                    for (int i = 0; i < b.length; i++) {
                        b[i] = caputure[len + i];
                    }
                    len += b.length;
                    ctx.writeAndFlush(b);
                }
                ctx.writeAndFlush(Receive.builder().message("Sent successfully").normal(true).build());
                log.info("client response completed");
            }
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("channel is close...", cause);
        disconnect();
        //发生异常，关闭通道
        ctx.close();
    }

    private void disconnect() {
        IntelContant.IS_CONNECT = false;
        if (isRegister != 1) {
            isRegister = 0;
        }
        Platform.runLater(() -> {
            ServerController controller = (ServerController) NodePool.get(ServerController.class);
            controller.setNoConnect();
        });
    }

}

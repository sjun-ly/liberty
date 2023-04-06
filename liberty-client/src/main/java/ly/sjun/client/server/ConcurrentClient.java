package ly.sjun.client.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;
import ly.sjun.client.contants.IntelContant;
import ly.sjun.common.entity.event.Event;


/**
 * @ClassName: ConcurrentClient
 * @Author: ly
 * @Description TODO
 **/
@Slf4j
public class ConcurrentClient {

    public static void send() {
        try {
            if (IntelContant.channel == null) {
                new ConcurrentClient(null);
            } else {
                ClientHandler.send(null);
            }
        } catch (InterruptedException e) {
            ClientHandler.onException();
            throw new RuntimeException(e);
        }
    }

    public static void send(Event event) {
        try {
            if (IntelContant.channel == null) {
                new ConcurrentClient(null);
            } else {
                ClientHandler.send(event);
            }
        } catch (InterruptedException e) {
            ClientHandler.onException();
            throw new RuntimeException(e);
        }
    }

    private ConcurrentClient() throws Exception {
        throw new Exception("illegal activation");
    }


    private ConcurrentClient(Event event) throws InterruptedException {
        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
        try {
            //创建bootstrap对象，配置参数
            //设置线程组
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventExecutors)
                    //设置客户端的通道实现类型
                    .channel(NioSocketChannel.class)
                    //使用匿名内部类初始化通道
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ObjectEncoder());
                            ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(null)));
                            //添加客户端通道的处理器
                            ch.pipeline().addLast(new ClientHandler(event));
                        }
                    });
            log.info("client is start...");
            //连接服务端
            ChannelFuture channelFuture = bootstrap.connect(IntelContant.IP, IntelContant.PORT).sync();

            //对通道关闭进行监听
            channelFuture.channel().closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    channelFuture.channel().close();
                    eventExecutors.shutdownGracefully();
                }
            });
        } finally {
            //关闭线程组
        }
    }


}

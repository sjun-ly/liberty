package ly.sjun.test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * @ClassName: sever
 * @Author: sjun-ly
 * @Description TODO
 **/
public class sever {


    public void bind(int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<Channel>() {

                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast(new ObjectEncoder());
                    ch.pipeline().addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(null))); // 最大长度
                    ch.pipeline().addLast(new FileUploadServerHandler());
                }
            });
            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        try {
            new sever().bind(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class FileUploadServerHandler extends ChannelInboundHandlerAdapter {
        private int byteRead;
        private volatile int start = 0;
        private String file_dir = "E:"+File.separator+"netty";

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("read");
            if (msg instanceof FileUploadFile) {
                FileUploadFile ef = (FileUploadFile) msg;
                byte[] bytes = ef.getBytes();
                byteRead = ef.getEndPos();
                String md5 = ef.getFile_md5();//文件名
                String path = file_dir + File.separator + md5;
                System.out.println(path);
                File file = new File(path);
                if(!file.exists()){
                    file.createNewFile();
                }
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                randomAccessFile.seek(start);
                randomAccessFile.write(bytes);
                start = start + byteRead;
                if (byteRead > 0) {
                    ctx.writeAndFlush(start);
                } else {
                    randomAccessFile.close();
                    ctx.close();
                }
            }
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            super.channelReadComplete(ctx);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
    }

}

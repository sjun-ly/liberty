package ly.sjun.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @ClassName: client
 * @Author: sjun-ly
 * @Description TODO
 **/
public class client {
    public void connect(int port, String host, final FileUploadFile fileUploadFile) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true).handler(new ChannelInitializer<Channel>() {

                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast(new ObjectEncoder());
                    ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(null)));
                    ch.pipeline().addLast(new FileUploadClientHandler(fileUploadFile));
                }
            });
            ChannelFuture f = b.connect(host, port).sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
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
            FileUploadFile uploadFile = new FileUploadFile();
            File file = new File("e:\\runxin_service_oss-0.0.1-SNAPSHOT.jar");
            String fileMd5 = file.getName();// 文件名
            uploadFile.setFile(file);
            uploadFile.setFile_md5(fileMd5);
            uploadFile.setStarPos(0);// 文件开始位置
            new client().connect(port, "127.0.0.1", uploadFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class FileUploadClientHandler extends ChannelInboundHandlerAdapter {
        private int byteRead;
        private volatile int start = 0;
        private volatile int lastLength = 0;
        public RandomAccessFile randomAccessFile;
        private FileUploadFile fileUploadFile;

        public FileUploadClientHandler(FileUploadFile ef) {
            if (ef.getFile().exists()) {
                if (!ef.getFile().isFile()) {
                    System.out.println("Not a file :" + ef.getFile());
                    return;
                }
            }
            this.fileUploadFile = ef;
        }

        public void channelActive(ChannelHandlerContext ctx) {
            System.out.println("write");
            try {
                randomAccessFile = new RandomAccessFile(fileUploadFile.getFile(), "r");
                randomAccessFile.seek(fileUploadFile.getStarPos());
                lastLength = (int) randomAccessFile.length() / 10;
                byte[] bytes = new byte[lastLength];
                if ((byteRead = randomAccessFile.read(bytes)) != -1) {
                    System.out.println("zaidu");
                    fileUploadFile.setEndPos(byteRead);
                    fileUploadFile.setBytes(bytes);
                    ctx.writeAndFlush(fileUploadFile);
                } else {
                    System.out.println("文件已经读完");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException i) {
                i.printStackTrace();
            }
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("client read");
            if (msg instanceof Integer) {
                start = (Integer) msg;
                if (start != -1) {
                    randomAccessFile = new RandomAccessFile(fileUploadFile.getFile(), "r");
                    randomAccessFile.seek(start);
                    System.out.println("块儿长度：" + (randomAccessFile.length() / 10));
                    System.out.println("长度：" + (randomAccessFile.length() - start));
                    int a = (int) (randomAccessFile.length() - start);
                    int b = (int) (randomAccessFile.length() / 10);
                    if (a < b) {
                        lastLength = a;
                    }
                    byte[] bytes = new byte[lastLength];
                    System.out.println("-----------------------------" + bytes.length);
                    if ((byteRead = randomAccessFile.read(bytes)) != -1 && (randomAccessFile.length() - start) > 0) {
                        System.out.println("byte 长度：" + bytes.length);
                        fileUploadFile.setEndPos(byteRead);
                        fileUploadFile.setBytes(bytes);
                        try {
                            ctx.writeAndFlush(fileUploadFile);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        randomAccessFile.close();
//                        ctx.close();
                        System.out.println("文件已经读完--------" + byteRead);
                    }
                }
            }
        }

        // @Override
        // public void channelRead(ChannelHandlerContext ctx, Object msg) throws
        // Exception {
        // System.out.println("Server is speek ："+msg.toString());
        // FileRegion filer = (FileRegion) msg;
        // String path = "E://Apk//APKMD5.txt";
        // File fl = new File(path);
        // fl.createNewFile();
        // RandomAccessFile rdafile = new RandomAccessFile(path, "rw");
        // FileRegion f = new DefaultFileRegion(rdafile.getChannel(), 0,
        // rdafile.length());
        //
        // System.out.println("This is" + ++counter + "times receive server:["
        // + msg + "]");
        // }

        // @Override
        // public void channelReadComplete(ChannelHandlerContext ctx) throws
        // Exception {
        // ctx.flush();
        // }

        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
        // @Override
        // protected void channelRead0(ChannelHandlerContext ctx, String msg)
        // throws Exception {
        // String a = msg;
        // System.out.println("This is"+
        // ++counter+"times receive server:["+msg+"]");
        // }
    }

}

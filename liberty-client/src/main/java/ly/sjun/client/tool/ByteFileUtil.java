package ly.sjun.client.tool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @ClassName: ByteFileUtil
 * @Author: ly
 * @Date: 2023/3/3-11:01
 * @Version: 1.0
 * @Description TODO
 **/
public class ByteFileUtil {

    /**
     * 得到屏幕截图数据,并返回二进制
     *
     * @return
     */
    public static byte[] getCapture(Robot robot, Rectangle rectangle) {
        BufferedImage bufferedImage = robot.createScreenCapture(rectangle);
        //获得一个内存输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        //将图片数据写入内存流中
        try {
            //原始图片，现在用下面的压缩图片法替换了
            ImageIO.write(bufferedImage, "jpg", baos);
            //进行图片压缩，图片尺寸不变，压缩图片文件大小outputQuality实现,参数1为最高质量
//            Thumbnails.of(bufferedImage).scale(1f).outputQuality(0.25f).outputFormat("jpg").toOutputStream(baos);
        } catch (IOException e) {
        }
        return baos.toByteArray();
    }

}

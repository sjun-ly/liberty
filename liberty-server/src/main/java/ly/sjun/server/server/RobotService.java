package ly.sjun.server.server;

import com.alibaba.fastjson.JSONObject;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import lombok.extern.slf4j.Slf4j;
import ly.sjun.common.entity.connect.EventEntity;
import ly.sjun.common.entity.event.KeyEventEntity;
import ly.sjun.common.entity.event.MouseEventEntity;
import ly.sjun.server.tool.ByteFileUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;

/**
 * @ClassName: RobotService
 * @Author: ly
 * @Description 机器操作类
 **/
@Slf4j
public class RobotService {

    private static Robot robot = null;

    private static int remoteImageWidth; //远程服务端的屏幕宽
    private static int remoteImageHeigth; //远程服务端的屏幕高

    static {
        try {
            robot = new Robot();//核心机器人类，用于截图，键盘或鼠标事件的重放执行。
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    public RobotService() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dimension = toolkit.getScreenSize();//获取到远程桌面的屏幕大小信息
    }

    /**
     * 截图方法
     */
    public static byte[] caputure() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dimension = toolkit.getScreenSize();
        Rectangle rectangle = new Rectangle(0, 0, (int) dimension.getWidth(), (int) dimension.getHeight());
        byte[] data = ByteFileUtil.getCapture(robot, rectangle);

        ImageIcon icon = new ImageIcon(data);
        remoteImageWidth = icon.getIconWidth();
        remoteImageHeigth = icon.getIconHeight();
        return data;
    }


    /**
     * 回放处理客户端发送过来的键盘或鼠标事件
     * 这里为什么要这样转？说明如下：
     * 假如浏览器的image区域为1200*800,远程桌面的截图区为900*700
     * 那么在浏览器上点击了clientX=77,clientY=88这个坐标时，实际上在远程
     * 桌面上正确的坐标应该为：
     * remoteClientX = clientX * remoteImageWidth/imageWidth;
     * 即：remoteClientX = 77 * 900 / 1200
     * remoteClientY同理.
     *
     * @param eventEntity
     */
    public static void actionEvent(EventEntity eventEntity) {
//        float whRate = (float) remoteImageHeigth / (float) remoteImageWidth;//屏幕的比例基本是 宽大于高
//        NodeContant.getImageView().setFitHeight(NodeContant.getPane().getHeight() * whRate);

        String eventClass = eventEntity.getEventClass();
        /**
         * 鼠标事件
         */
        if (MouseEventEntity.class.getName().equals(eventClass)) {
            MouseEventEntity mouseEvent = JSONObject.parseObject(eventEntity.getEvent(), MouseEventEntity.class);
            String eventTypeName = mouseEvent.getEventType();
            if ("MOUSE_CLICKED".equals(eventTypeName)) {
                log.info("triggered a mouse click event ,{}", eventTypeName);
                double clientX = mouseEvent.getX();
                double clientY = mouseEvent.getY();
                MouseButton button = mouseEvent.getButton();
                double imageWidth = eventEntity.getImageWidth();
                double imageHeight = eventEntity.getImageHeight();
                double remoteClientX = clientX * remoteImageWidth / imageWidth;
                double remoteClientY = clientY * remoteImageHeigth / imageHeight;
                //  移动鼠标到正确的坐标
                robot.mouseMove((int) remoteClientX, (int) remoteClientY);
                // 然后进行鼠标的按下
                if (button == MouseButton.PRIMARY) {
                    robot.mousePress(InputEvent.BUTTON1_MASK);//左键
                } else if (button == MouseButton.MIDDLE) {
                    robot.mousePress(InputEvent.BUTTON2_MASK);//中间键
                } else if (button == MouseButton.SECONDARY) {
                    robot.mousePress(InputEvent.BUTTON3_MASK);//右键
                }
            } else if ("MOUSE_RELEASED".equals(eventTypeName)) {
                log.info("triggered mouse release event ,{}", eventTypeName);
                double clientX = mouseEvent.getX();
                double clientY = mouseEvent.getY();
                MouseButton button = mouseEvent.getButton();
                double imageWidth = eventEntity.getImageWidth();
                double imageHeight = eventEntity.getImageHeight();
                double remoteClientX = clientX * remoteImageWidth / imageWidth;
                double remoteClientY = clientY * remoteImageHeigth / imageHeight;

                //移动鼠标到正确的坐标
                robot.mouseMove((int) remoteClientX, (int) remoteClientY);

                //然后进行鼠标的弹起
                if (button == MouseButton.PRIMARY) {
                    robot.mouseRelease(InputEvent.BUTTON1_MASK);//左键
                } else if (button == MouseButton.MIDDLE) {
                    robot.mouseRelease(InputEvent.BUTTON2_MASK);//中间键
                } else if (button == MouseButton.SECONDARY) {
                    robot.mouseRelease(InputEvent.BUTTON3_MASK);//右键
                }
            } else if ("MOUSE_MOVED".equals(eventTypeName)) {
                log.info("triggered mouse movement event,{}", eventTypeName);
                double clientX = mouseEvent.getX();
                double clientY = mouseEvent.getY();
                double imageWidth = eventEntity.getImageWidth();
                double imageHeight = eventEntity.getImageHeight();
                double remoteClientX = clientX * remoteImageWidth / imageWidth;
                double remoteClientY = clientY * remoteImageHeigth / imageHeight;
                //将鼠标进行移动
                robot.mouseMove((int) remoteClientX, (int) remoteClientY);
            }
        }
        /**
         * 键盘事件
         */
        else if (KeyEventEntity.class.getName().equals(eventClass)) {
            KeyEventEntity keyEvent = JSONObject.parseObject(eventEntity.getEvent(), KeyEventEntity.class);
            String eventTypeName = keyEvent.getEventType();
            KeyCode code = keyEvent.getCode();
            if ("KEY_PRESSED".equals(eventTypeName)) {
                log.info("triggered keyboard press event,{}", eventTypeName);
//                robot.keyPress(changeKeyCode(code.getCode()));
                robot.keyPress(code.getCode());
            } else if ("KEY_RELEASED".equals(eventTypeName)) {
                log.info("triggered keyboard release event,{}", eventTypeName);
                robot.keyPress(code.getCode());
//                robot.keyRelease(changeKeyCode(code.getCode()));
            }
        }
    }

    /**
     * 进行keyCode的改变，因为浏览器的键盘事件和Java的awt的事件代码，有些是不一样的，需要进行转换，
     * 比如浏览器中13表示回车，但在Java的awt中是用10表示
     * 这里可能转换不全，比如F1-F12键都没有处理，因为浏览器现在没有禁用这些键，如果需要支持，可以继续在这里加上
     *
     * @param sourceKeyCode
     * @return
     */
    private static int changeKeyCode(int sourceKeyCode) {
        //回车
        if (sourceKeyCode == 13) return 10;

        //,< 188 -> 44
        if (sourceKeyCode == 188) return 44;

        //.>在Js中为190，但在Java中为46
        if (sourceKeyCode == 190) return 46;

        // /?在Js中为191，但在Java中为47
        if (sourceKeyCode == 191) return 47;

        //;: 186 -> 59
        if (sourceKeyCode == 186) return 59;

        //[{ 219 -> 91
        if (sourceKeyCode == 219) return 91;

        //\| 220 -> 92
        if (sourceKeyCode == 220) return 92;

        //-_ 189->45
        if (sourceKeyCode == 189) return 45;

        //=+ 187->61
        if (sourceKeyCode == 187) return 61;

        //]} 221 -> 93
        if (sourceKeyCode == 221) return 93;

        //DEL
        if (sourceKeyCode == 46) return 127;

        //Ins
        if (sourceKeyCode == 45) return 155;

        return sourceKeyCode;
    }

}

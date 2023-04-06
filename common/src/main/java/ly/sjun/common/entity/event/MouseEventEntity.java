package ly.sjun.common.entity.event;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: MouseEventEntity
 * @Author: ly
 * @Date: 2023/3/3-9:57
 * @Description 自定义鼠标事件
 **/
@Data
public class MouseEventEntity implements Serializable, Event {
    private static final long serialVersionUID = -6849794470754663344L;

    private  double x;
    private  double y;
    private  double z;
    private double screenX;//相对于电脑屏幕左上角的水平偏移量
    private double screenY;//相对于电脑屏幕左上角的垂直偏移量
    private double sceneX;//相对于窗体坐标的水平偏移量
    private double sceneY;//相对于窗体坐标的垂直偏移量
    private MouseButton button;
    private int clickCount;
//    private PickResult pickResult;

    private String  eventType;

    private MouseEventEntity() {
//        throw new RuntimeException("鼠标事件禁止手动实现");
    }

    public MouseEventEntity(MouseEvent event) {
        this.setX(event.getX());
        this.setY(event.getY());
        this.setZ(event.getZ());

        this.setSceneX(event.getSceneX());
        this.setSceneY(event.getSceneY());

        this.setScreenX(event.getScreenX());
        this.setScreenY(event.getScreenY());

//        this.setPickResult(event.getPickResult());
        this.setButton(event.getButton());

        this.setEventType(event.getEventType().getName());

        this.setClickCount(event.getClickCount());
    }



}

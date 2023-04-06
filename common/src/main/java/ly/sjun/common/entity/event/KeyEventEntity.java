package ly.sjun.common.entity.event;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: KeyEventEntity
 * @Author: ly
 * @Date: 2023/3/3-14:05
 * @Description 自定义键盘事件
 **/
@Data
public class KeyEventEntity implements Serializable, Event {
    private static long serialVersionUID = -6849794470345663344L;

    private String eventType;//事件类型 ANY ，KEY_PRESSED，KEY_RELEASED，KEY_TYPED
    private String character;//字符集
    private String text;//内容
    private KeyCode code;//键盘key
    private boolean shiftDown;//shift
    private boolean controlDown;//ctrl
    private boolean altDown;//alt
    private boolean metaDown;//mata

    private KeyEventEntity() {
    }

    public KeyEventEntity(KeyEvent event) {
        this.setEventType(event.getEventType().getName());
        this.setCharacter(event.getCharacter());
        this.setText(event.getText());
        this.setCode(event.getCode());
        this.setShiftDown(event.isShiftDown());
        this.setControlDown(event.isControlDown());
        this.setAltDown(event.isAltDown());
        this.setMetaDown(event.isMetaDown());
    }


}

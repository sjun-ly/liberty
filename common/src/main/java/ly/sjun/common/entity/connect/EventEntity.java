package ly.sjun.common.entity.connect;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: Message
 * @Author: ly
 * @Date: 2023/3/2-16:44
 * @Version: 1.0
 * @Description TODO
 **/
@Data
public class EventEntity implements Serializable {

    private String event;

    private String eventClass;

    private byte[] file;

    private double imageWidth;

    private double imageHeight;

}

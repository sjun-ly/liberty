package ly.sjun.common.entity.connect;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: Safety
 * @Author: ly
 * @Date: 2023/3/6-10:49
 * @Description 接收消息安全校验类
 **/
@Data
public class Safety implements Serializable {

    private String key;

    private Boolean exit;

    private EventEntity eventEntity;

}

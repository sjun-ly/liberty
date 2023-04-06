package ly.sjun.common.entity.connect;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: Receive
 * @Author: ly
 * @Date: 2023/3/6-10:51
 * @Description 消息返回类
 **/
@Data
@Builder
public class Receive implements Serializable {

    private static final long serialVersionUID = 1L;

    private String message;

    private boolean normal;//是否正常连接

    private byte[] file;

}

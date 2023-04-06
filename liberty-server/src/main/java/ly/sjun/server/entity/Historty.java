package ly.sjun.server.entity;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName: Historty
 * @Author: sjun-ly
 * @Description 服务端消息记录类
 **/
@Data
public class Historty {

    private String ip;

    private int port;

    private Date connectTime;

    private Date endTime;

}

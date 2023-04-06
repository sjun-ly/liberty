package ly.sjun.server.entity;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName: WarnConnect
 * @Author: ly
 * @Date: 2023/3/6-10:53
 * @Description 连接信息预警类
 **/
@Data
public class WarnConnect {

    private int count;//尝试连接数

    private String ip;

    private Integer port;

    private String message;//预警信息

    private Date date;

}

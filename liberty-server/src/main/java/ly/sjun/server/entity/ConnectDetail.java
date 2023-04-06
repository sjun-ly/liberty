package ly.sjun.server.entity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @ClassName: ConnectDetail
 * @Author: ly
 * @Description 控制方请求信息, 全局单例
 **/
public class ConnectDetail {

    private static List<Historty> connectList = new ArrayList<>();//高查询，低写入

    private static ConnectDetail connectDetail;

    private ConnectDetail() {
    }

    //懒汉加载
    public static ConnectDetail getDetail() {
        if (connectDetail == null) {
            connectDetail = new ConnectDetail();
        }
        return connectDetail;
    }

    /**
     * return connectList;(X)
     * 禁止删除记录，所以重新新建一个对象返回
     */
    public List<Historty> getConnectList() {
        List<Historty> back = new LinkedList<>();//高写入
        back.addAll(connectList);
        return back;
    }

    /**
     * 只允许添加不许删除
     */
    public void add(Historty historty) {
        connectList.add(historty);
    }

}

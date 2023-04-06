package ly.sjun.server.contants;

import ly.sjun.server.entity.Historty;

import java.util.LinkedList;
import java.util.List;

/**
 * @ClassName: IntelContant
 * @Author: ly
 * @Date: 2023/3/2-16:59
 * @Version: 1.0
 * @Description TODO
 **/
public class IntelContant {

    public static Integer PORT = 7777;

    public static Boolean IS_CONNECT = Boolean.FALSE;

    public static Boolean SERVER_START = Boolean.FALSE;

    public final static List<Historty> historties = new LinkedList<>();

    public static Thread serverThread;

    public static String connect_key = null;

}

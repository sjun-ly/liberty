package ly.sjun.client.contants;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ly.sjun.common.controller.FxController;

import java.util.HashMap;

/**
 * @ClassName: NodePool
 * @Author: ly
 * @Description TODO
 **/
final public class NodePool {

    private static final HashMap<String, Object> map = new HashMap<>();

    public static void put(Node node) {
        String name = node.getClass().getName();
        map.put(name, node);
    }

    public static void put(Scene scene) {
        String name = scene.getClass().getName();
        map.put(name, scene);
    }

    public static void put(FxController controller) {
        String name = controller.getClass().getName();
        map.put(name, controller);
    }

    public static void put(Stage stage) {
        String name = stage.getClass().getName();
        map.put(name, stage);
    }

    public static void put(String name, Node node) {
        map.put(name, node);
    }

    public static Object get(String name) {
        return map.get(name);
    }

    public static Object get(Class obj) {
        return map.get(obj.getName());
    }

}

package ly.sjun.client.fxcontroller;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import ly.sjun.client.contants.IntelContant;

public class ClientController {
    @FXML
    private ImageView imageView;

    @FXML
    protected void closeConnect() {
        IntelContant.channel.channel().close();
        IntelContant.channel = null;
    }
}
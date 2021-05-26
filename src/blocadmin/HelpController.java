package blocadmin;

import blocadmin.utils.Utils;
import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HelpController {
    
    private static final Logger LOGGER = LogManager.getLogger(HelpController.class);
    
    @FXML
    private JFXButton helpCloseBtn;

    @FXML
    private Label versionTxt;
    
    private FXMLLoader fxmlLoader;
    
    public HelpController() {
        Parent parent = null;
        
        if (fxmlLoader == null) {
            fxmlLoader = new FXMLLoader(getClass().getResource("/blocadmin/fxml/HelpDialog.fxml"));
            fxmlLoader.setController(this);
            
            try {
                parent = fxmlLoader.load();
            } catch (IOException e) {
                LOGGER.error("Exception creating the dialog: " + e.getMessage());
            }
        }
        
        versionTxt.setText(Utils.APP_VERSION);
        
        helpCloseBtn.setGraphic(new ImageView(new Image("/blocadmin/images/closeDialog.png")));
        helpCloseBtn.setOnMouseClicked((MouseEvent event) -> {
            Stage stageDetails = (Stage) helpCloseBtn.getScene().getWindow();
            stageDetails.close();
        });
        
        Stage stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(new Scene(parent));
        stage.show();
    }
}

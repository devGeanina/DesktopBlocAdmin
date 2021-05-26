package blocadmin;

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


public class DetailsController {

    private static final Logger LOGGER = LogManager.getLogger(DetailsController.class);

    @FXML
    private Label detailsName, detailsBody;

    @FXML
    private JFXButton closeBtn;

    private FXMLLoader fxmlLoader;

    public DetailsController(String detailsLabel, String detailsTxt) {
        Parent parent = null;

        if (fxmlLoader == null) {
            fxmlLoader = new FXMLLoader(getClass().getResource("/blocadmin/fxml/DetailsDialog.fxml"));
            fxmlLoader.setController(this);

            try {
                parent = fxmlLoader.load();
            } catch (IOException e) {
                LOGGER.error("Exception creating the details dialog: " + e.getMessage());
            }
        }
        
        detailsName.setText(detailsLabel);
        if(detailsTxt!= null && !detailsTxt.isEmpty())
            detailsBody.setText(detailsTxt);
        else
            detailsBody.setText("No details provided.");

        detailsBody.setWrapText(true);
        detailsBody.setPrefHeight(150);
        detailsBody.setPrefWidth(298);

        closeBtn.setGraphic(new ImageView(new Image("/blocadmin/images/closeDialog.png")));

        closeBtn.setOnMouseClicked((MouseEvent event) -> {
            Stage stageDetails = (Stage) closeBtn.getScene().getWindow();
            stageDetails.close();
        });

        Stage stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(new Scene(parent));
        stage.show();
    }
}

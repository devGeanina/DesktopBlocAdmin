package blocadmin;

import blocadmin.entities.User;
import blocadmin.entities.DatabaseHandler;
import blocadmin.utils.ToastMessage;
import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserListCell extends ListCell<User> {

    private static final Logger LOGGER = LogManager.getLogger(UserListCell.class);
    private DatabaseHandler databaseHandler = new DatabaseHandler();

    @FXML
    private Label name, sumOrType, relevantDetails;

    @FXML
    private ImageView icon;
    
    @FXML
    private JFXButton editBtn, detailsBtn, deleteBtn;

    @FXML
    private GridPane gridPane;

    private FXMLLoader fxmlLoader;

    @Override
    protected void updateItem(User user, boolean empty) {
        super.updateItem(user, empty);

        if (empty || user == null) {

            setText(null);
            setGraphic(null);

        } else {
            if (fxmlLoader == null) {
                fxmlLoader = new FXMLLoader(getClass().getResource("/blocadmin/fxml/ListCell.fxml"));
                fxmlLoader.setController(this);

                try {
                    fxmlLoader.load();
                } catch (IOException e) {
                    LOGGER.error("Exception creating the list cell: " + e.getMessage());
                }
            }
            name.setMinWidth(Region.USE_PREF_SIZE);
            sumOrType.setMinWidth(Region.USE_PREF_SIZE);
            relevantDetails.setMinWidth(Region.USE_PREF_SIZE);
            
            deleteBtn.setOnMouseClicked((MouseEvent event) -> {
                databaseHandler.deleteUser(user.getId());
                ToastMessage.makeText("Item deleted.");
            });
            
            name.setText(user.getFirstName().concat(" ").concat(user.getLastName()).concat(" - ").concat(user.getUserType()));
            sumOrType.setText("Bl. ".concat(String.valueOf(user.getBuildingNr())).concat(", ap. ").concat(String.valueOf(user.getAppartmentNr())));
            relevantDetails.setText(user.getDetails());
            
            icon.setImage(new Image("/blocadmin/images/userList.png"));
            
            editBtn.setGraphic(new ImageView(new Image("/blocadmin/images/edit.png")));
            editBtn.setOnMouseClicked((MouseEvent event) -> {
                   new UserController(user);
            });

            deleteBtn.setGraphic(new ImageView(new Image("/blocadmin/images/delete.png")));

            detailsBtn.setGraphic(new ImageView(new Image("/blocadmin/images/details.png")));
            detailsBtn.setOnMouseClicked((MouseEvent event) -> {
                   new DetailsController("User Details - ".concat(user.getFirstName().concat(" ").concat(user.getLastName())), user.getDetails());
            });

            setText(null);
            setGraphic(gridPane);
            gridPane.setCache(true);
        }
    }
}

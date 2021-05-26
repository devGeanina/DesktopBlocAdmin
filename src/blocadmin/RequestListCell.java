package blocadmin;

import blocadmin.entities.Request;
import blocadmin.entities.DatabaseHandler;
import blocadmin.utils.ToastMessage;
import blocadmin.utils.Utils;
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

/**
 *
 * @author Geanina
 */
public class RequestListCell extends ListCell<Request>{
    private static final Logger LOGGER = LogManager.getLogger(RequestListCell.class);
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
    protected void updateItem(Request request, boolean empty) {
        super.updateItem(request, empty);

        if (empty || request == null) {

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
            
            name.setText(request.getName());
            sumOrType.setText(request.getRequestType().concat(" - resolved: ").concat(request.isResolved() ? "yes" : "no"));
            StringBuilder details = new StringBuilder();
            details.append(request.getOwner().getFirstName()).append(" ").append(request.getOwner().getLastName());
            details.append(". Due: ");
            details.append(Utils.convertDateToString(request.getDueDate()));
            relevantDetails.setText(details.toString());
            
            deleteBtn.setOnMouseClicked((MouseEvent event) -> {
                databaseHandler.deleteRequest(request.getId());
                ToastMessage.makeText("Item deleted.");
            });
            
            if(request.isResolved())
                icon.setImage(new Image("/blocadmin/images/solved_requestList.png"));
            else
                icon.setImage(new Image("/blocadmin/images/unsolved_requestList.png"));
            
            editBtn.setGraphic(new ImageView(new Image("/blocadmin/images/edit.png")));
            editBtn.setOnMouseClicked((MouseEvent event) -> {
                 new RequestController(request);
            });

            deleteBtn.setGraphic(new ImageView(new Image("/blocadmin/images/delete.png")));

            detailsBtn.setGraphic(new ImageView(new Image("/blocadmin/images/details.png")));
            detailsBtn.setOnMouseClicked((MouseEvent event) -> {
                   new DetailsController("Request details - ".concat(request.isResolved() ? "solved" : "unsolved"), request.getDetails());
            });

            setText(null);
            setGraphic(gridPane);
            gridPane.setCache(true);
        }
    }
}

package blocadmin;

import blocadmin.entities.Household;
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

/**
 *
 * @author Geanina
 */
public class HouseholdListCell extends ListCell<Household>{
    
    private static final Logger LOGGER = LogManager.getLogger(HouseholdListCell.class);
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
    protected void updateItem(Household household, boolean empty) {
        super.updateItem(household, empty);

        if (empty || household == null) {

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
                databaseHandler.deleteHousehold(household.getId());
                ToastMessage.makeText("Item deleted.");
            });
            

            name.setText(String.valueOf(household.getBuildingNr()).concat(", ").concat(String.valueOf(household.getAppartmentNr())));
            StringBuilder ownerName = new StringBuilder();
            if(household.getOwner() != null && household.getOwner().getFirstName() != null && household.getOwner().getLastName() != null){
                ownerName.append("Owner: ");
                ownerName.append(household.getOwner().getFirstName());
                ownerName.append(" ");
                ownerName.append(household.getOwner().getLastName());
            }else{
                ownerName.append("No owner.");
            }
            sumOrType.setText(ownerName.toString());
            StringBuilder details = new StringBuilder();
            details.append("Capacity: ");
            details.append(household.getTotalCapacity());
            details.append(" , current ocuppants: ");
            details.append(household.getNrCurrentOccupants());
            relevantDetails.setText(details.toString());
            
            icon.setImage(new Image("/blocadmin/images/householdList.png"));
            
            editBtn.setGraphic(new ImageView(new Image("/blocadmin/images/edit.png")));
            editBtn.setOnMouseClicked((MouseEvent event) -> {
                  new HouseholdController(household);
            });

            deleteBtn.setGraphic(new ImageView(new Image("/blocadmin/images/delete.png")));
            detailsBtn.setGraphic(new ImageView(new Image("/blocadmin/images/details.png")));
            
            detailsBtn.setOnMouseClicked((MouseEvent event) -> {
                   new DetailsController("Details - ".concat("B: ").concat(String.valueOf(household.getBuildingNr())).concat(", Ap. ").concat(String.valueOf(household.getAppartmentNr())), household.getDetails());
            });

            setText(null);
            setGraphic(gridPane);
            gridPane.setCache(true);
        }
    }
}

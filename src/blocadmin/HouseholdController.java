package blocadmin;

import blocadmin.entities.Household;
import blocadmin.entities.User;
import blocadmin.entities.DatabaseHandler;
import blocadmin.utils.ToastMessage;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.util.function.UnaryOperator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Geanina
 */
public class HouseholdController {
    
    private static final Logger LOGGER = LogManager.getLogger(HouseholdController.class);
    private DatabaseHandler databaseHandler = new DatabaseHandler();
    private ObservableList<User> users;
    private User selectedUser;
    
    @FXML
    private JFXTextField buildingNr, appNr, rooms, capacity, occupants;

    @FXML
    private JFXButton saveBtn, closeBtn;
    
    @FXML
    private JFXTextArea details;
    
    @FXML
    private JFXComboBox owner;
    
    private FXMLLoader fxmlLoader;

    public HouseholdController(Household household) {
        Parent parent = null;

        if (fxmlLoader == null) {
            fxmlLoader = new FXMLLoader(getClass().getResource("/blocadmin/fxml/NewHouseholdDialog.fxml"));
            fxmlLoader.setController(this);

            try {
                parent = fxmlLoader.load();
            } catch (IOException e) {
                LOGGER.error("Exception creating the dialog: " + e.getMessage());
            }
        }

        // must be numeric
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();
            if (text.matches("[0-9]*")) {
                return change;
            }
            return null;
        };
        
        buildingNr.setTextFormatter(new TextFormatter<>(filter));
        appNr.setTextFormatter(new TextFormatter<>(filter));
        occupants.setTextFormatter(new TextFormatter<>(filter));
        rooms.setTextFormatter(new TextFormatter<>(filter));
        capacity.setTextFormatter(new TextFormatter<>(filter));
        
        users = FXCollections.observableArrayList(databaseHandler.getUsers());
        owner.setItems(users);
        owner.getSelectionModel().selectedItemProperty()
           .addListener(new ChangeListener<User>() {
               public void changed(ObservableValue<? extends User> observable,
                                   User oldValue, User newValue) {
                    selectedUser = (User) newValue;
               }
        });
        
        Callback<ListView<User>, ListCell<User>> factory = lv -> new ListCell<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getFirstName() + " " + item.getLastName());
            }
        };

        owner.setCellFactory(factory);
        owner.setButtonCell(factory.call(null));
        
        closeBtn.setGraphic(new ImageView(new Image("/blocadmin/images/closeDialog.png")));
        closeBtn.setOnMouseClicked((MouseEvent event) -> {
            Stage stageClose = (Stage) closeBtn.getScene().getWindow();
            stageClose.close();
        });
        
        if(household != null){
            appNr.setText(String.valueOf(household.getAppartmentNr()));
            buildingNr.setText(String.valueOf(household.getBuildingNr()));
            rooms.setText(String.valueOf(household.getRoomsNr()));
            occupants.setText(String.valueOf(household.getNrCurrentOccupants()));
            capacity.setText(String.valueOf(household.getTotalCapacity()));
            owner.setValue(household.getOwner().getFirstName() + " " + household.getOwner().getLastName());
            
            if(household.getDetails() != null)
                details.setText(household.getDetails());
         
        }

        saveBtn.setGraphic(new ImageView(new Image("/blocadmin/images/save.png")));
        saveBtn.setOnMouseClicked((MouseEvent event) -> {
          
            Household newHousehold = new Household();
            if(household != null)
                newHousehold = household;
            newHousehold.setAppartmentNr(Integer.valueOf(appNr.getText()));
            newHousehold.setBuildingNr(Integer.valueOf(buildingNr.getText()));
            newHousehold.setNrCurrentOccupants(Integer.valueOf(occupants.getText()));
            newHousehold.setRoomsNr(Integer.valueOf(rooms.getText()));
            newHousehold.setTotalCapacity(Integer.valueOf(capacity.getText()));
            newHousehold.setOwner((User)selectedUser);
            
            if(details.getText() != null){
                newHousehold.setDetails(details.getText());
            }
            if(household != null && household.getId() != null)
                databaseHandler.updateHousehold(newHousehold);
            else
                databaseHandler.saveHousehold(newHousehold);
            
            //hide dialog
            Stage stageSave = (Stage) saveBtn.getScene().getWindow();
            ToastMessage.makeText("Household saved.");
            stageSave.close();
        });

        buildingNr.setStyle("-fx-text-inner-color: white");
        appNr.setStyle("-fx-text-inner-color: white");
        capacity.setStyle("-fx-text-inner-color: white");
        occupants.setStyle("-fx-text-inner-color: white");
        details.setStyle("-fx-text-inner-color: white");
        rooms.setStyle("-fx-text-inner-color: white");

        Stage stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(new Scene(parent));
        stage.show();
    }
}

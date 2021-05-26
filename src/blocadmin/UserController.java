package blocadmin;

import blocadmin.entities.User;
import blocadmin.entities.DatabaseHandler;
import blocadmin.utils.Constants;
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
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Geanina
 */
public class UserController {
    
    private static final Logger LOGGER = LogManager.getLogger(UserController.class);
    private DatabaseHandler databaseHandler = new DatabaseHandler();
    private ObservableList<Constants.USER_TYPE> userTypeOptions = FXCollections.observableArrayList(Constants.USER_TYPE.values());
    private String selectedType = Constants.USER_TYPE.OWNER.getName();
    
    @FXML
    private JFXTextField firstName, lastName, buildingNr, appNr;

    @FXML
    private JFXButton saveBtn, closeBtn;
    
    @FXML
    private JFXTextArea details;
    
    @FXML
    private JFXComboBox userType;
    
     private FXMLLoader fxmlLoader;

    public UserController(User user) {
        Parent parent = null;

        if (fxmlLoader == null) {
            fxmlLoader = new FXMLLoader(getClass().getResource("/blocadmin/fxml/NewUserDialog.fxml"));
            fxmlLoader.setController(this);

            try {
                parent = fxmlLoader.load();
            } catch (IOException e) {
                LOGGER.error("Exception creating the dialog: " + e.getMessage());
            }
        }
        
        userType.setValue(Constants.USER_TYPE.OWNER);
        userType.setItems(userTypeOptions);

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
        
        closeBtn.setGraphic(new ImageView(new Image("/blocadmin/images/closeDialog.png")));
        closeBtn.setOnMouseClicked((MouseEvent event) -> {
            Stage stageClose = (Stage) closeBtn.getScene().getWindow();
            stageClose.close();
        });
        
        userType.getSelectionModel().selectedItemProperty()
           .addListener(new ChangeListener<Constants.USER_TYPE>() {
               public void changed(ObservableValue<? extends Constants.USER_TYPE> observable,
                                   Constants.USER_TYPE oldValue, Constants.USER_TYPE newValue) {
                    switch (newValue) {
                            case OWNER:
                               selectedType = Constants.USER_TYPE.OWNER.getName();
                               break;
                            case ADMIN:
                               selectedType = Constants.USER_TYPE.ADMIN.getName();
                               break;
                            case ASSOCIATE:
                               selectedType = Constants.USER_TYPE.ASSOCIATE.getName();
                               break;
                            case EMPLOYEE:
                               selectedType = Constants.USER_TYPE.EMPLOYEE.getName();
                               break;
                            case OTHER:
                               selectedType = Constants.USER_TYPE.OTHER.getName();
                               break;   
                            default:
                                break;
                    }
               }
        });
        
        if(user != null){
            appNr.setText(String.valueOf(user.getAppartmentNr()));
            buildingNr.setText(String.valueOf(user.getBuildingNr()));
            firstName.setText(user.getFirstName());
            lastName.setText(user.getLastName());
            if(user.getDetails() != null)
                details.setText(user.getDetails());
            Constants.USER_TYPE existingUserType = null;
            switch (user.getUserType()) {
                            case "Owner":
                               existingUserType = Constants.USER_TYPE.OWNER;
                               break;
                            case "Admin":
                               existingUserType = Constants.USER_TYPE.ADMIN;
                               break;
                            case "Associate":
                               existingUserType = Constants.USER_TYPE.ASSOCIATE;
                               break;
                            case "Employee":
                               existingUserType = Constants.USER_TYPE.EMPLOYEE;
                               break;
                            case "Other":
                               existingUserType = Constants.USER_TYPE.OTHER;
                               break;   
                            default:
                                break;
            }
           userType.setValue(existingUserType);
        }

        saveBtn.setGraphic(new ImageView(new Image("/blocadmin/images/save.png")));
        saveBtn.setOnMouseClicked((MouseEvent event) -> {
          
            User newUser = new User();
            if(user != null)
                newUser = user;
            newUser.setAppartmentNr(Integer.valueOf(appNr.getText()));
            newUser.setBuildingNr(Integer.valueOf(buildingNr.getText()));
            newUser.setFirstName(firstName.getText());
            newUser.setLastName(lastName.getText());
            newUser.setUserType(selectedType);
            if(details.getText() != null){
                newUser.setDetails(details.getText());
            }
            
            if(user != null && user.getId() != null)
                databaseHandler.updateUser(user);
            else
                databaseHandler.saveUser(newUser);
            
            //hide dialog
            Stage stageSave = (Stage) saveBtn.getScene().getWindow();
            ToastMessage.makeText("User saved.");
            stageSave.close();
        });

        buildingNr.setStyle("-fx-text-inner-color: white");
        appNr.setStyle("-fx-text-inner-color: white");
        firstName.setStyle("-fx-text-inner-color: white");
        lastName.setStyle("-fx-text-inner-color: white");
        details.setStyle("-fx-text-inner-color: white");

        Stage stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(new Scene(parent));
        stage.show();
    }
}

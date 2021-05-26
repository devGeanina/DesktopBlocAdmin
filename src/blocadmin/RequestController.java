package blocadmin;

import blocadmin.entities.Request;
import blocadmin.entities.User;
import blocadmin.entities.DatabaseHandler;
import blocadmin.utils.Constants;
import blocadmin.utils.ToastMessage;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.time.ZoneId;
import java.util.Date;
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
public class RequestController {
    
    private static final Logger LOGGER = LogManager.getLogger(RequestController.class);
    private DatabaseHandler databaseHandler = new DatabaseHandler();
    private ObservableList<Constants.HOUSEHOLD_REQUEST_TYPE>  requestTypeOptions = FXCollections.observableArrayList(Constants.HOUSEHOLD_REQUEST_TYPE.values());
    private String selectedType = Constants.HOUSEHOLD_REQUEST_TYPE.COMPLAINT.getName();
    private ObservableList<User> users;
    private User selectedUser;
    
    @FXML
    private JFXTextField name;

    @FXML
    private JFXButton saveBtn, closeBtn;
    
    @FXML
    private JFXTextArea details;
    
    @FXML
    private JFXDatePicker dueDate;
    
    @FXML
    private JFXCheckBox isResolved;
    
    @FXML
    private JFXComboBox requester, requestType;
    
    private FXMLLoader fxmlLoader;

    public RequestController(Request request) {
        Parent parent = null;

        if (fxmlLoader == null) {
            fxmlLoader = new FXMLLoader(getClass().getResource("/blocadmin/fxml/NewRequestDialog.fxml"));
            fxmlLoader.setController(this);

            try {
                parent = fxmlLoader.load();
            } catch (IOException e) {
                LOGGER.error("Exception creating the dialog: " + e.getMessage());
            }
        }
        
        users = FXCollections.observableArrayList(databaseHandler.getUsers());
        requester.setItems(users);
        requester.getSelectionModel().selectedItemProperty()
           .addListener(new ChangeListener<User>() {
               public void changed(ObservableValue<? extends User> observable,
                                   User oldValue, User newValue) {
                    selectedUser = (User)newValue;
               }
        });
        
        Callback<ListView<User>, ListCell<User>> factory = lv -> new ListCell<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getFirstName() + " " + item.getLastName());
            }
        };

        requester.setCellFactory(factory);
        requester.setButtonCell(factory.call(null));
        
        requestType.setValue(Constants.HOUSEHOLD_REQUEST_TYPE.COMPLAINT);
        requestType.setItems(requestTypeOptions);

        
        closeBtn.setGraphic(new ImageView(new Image("/blocadmin/images/closeDialog.png")));
        closeBtn.setOnMouseClicked((MouseEvent event) -> {
            Stage stageClose = (Stage) closeBtn.getScene().getWindow();
            stageClose.close();
        });
        
        requestType.getSelectionModel().selectedItemProperty()
           .addListener(new ChangeListener<Constants.HOUSEHOLD_REQUEST_TYPE>() {
               public void changed(ObservableValue<? extends Constants.HOUSEHOLD_REQUEST_TYPE> observable,
                                   Constants.HOUSEHOLD_REQUEST_TYPE oldValue, Constants.HOUSEHOLD_REQUEST_TYPE newValue) {
                    switch (newValue) {
                            case COMPLAINT:
                               selectedType = Constants.HOUSEHOLD_REQUEST_TYPE.COMPLAINT.getName();
                               break;
                            case DOC_RELEASE:
                               selectedType = Constants.HOUSEHOLD_REQUEST_TYPE.DOC_RELEASE.getName();
                               break;
                            case OTHER:
                               selectedType = Constants.HOUSEHOLD_REQUEST_TYPE.OTHER.getName();
                               break;   
                            default:
                                break;
                    }
               }
        });
        
        if(request != null){
            if(request.isResolved())
                isResolved.setSelected(true);
            
            if(request.getDetails() != null)
                details.setText(request.getDetails());
            
            //TO DO set date
            Constants.HOUSEHOLD_REQUEST_TYPE existingReqType = null;
            switch (request.getRequestType()) {
                         case "Complaint":
                               existingReqType = Constants.HOUSEHOLD_REQUEST_TYPE.COMPLAINT;
                               break;
                            case "Document release":
                               existingReqType = Constants.HOUSEHOLD_REQUEST_TYPE.DOC_RELEASE;
                               break;
                            case "Other":
                               existingReqType = Constants.HOUSEHOLD_REQUEST_TYPE.OTHER;
                               break;   
                            default:
                                break;
            }
           requestType.setValue(existingReqType);
           requester.setValue(request.getOwner().getFirstName() + " " + request.getOwner().getLastName());
           name.setText(request.getName());
           dueDate.setValue(request.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }

        saveBtn.setGraphic(new ImageView(new Image("/blocadmin/images/save.png")));
        saveBtn.setOnMouseClicked((MouseEvent event) -> {
          
        Request newRequest = new Request();
        if(request != null)
            newRequest = request;
        newRequest.setDueDate(Date.from(dueDate.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
        newRequest.setIsResolved(isResolved.isSelected());
        newRequest.setRequestType(selectedType);
        if(details.getText() != null){
            newRequest.setDetails(details.getText());
        }
        newRequest.setName(name.getText());
        newRequest.setOwner((User)selectedUser);

        if(request!=null  && request.getId() != null)
            databaseHandler.updateRequest(request);
        else
            databaseHandler.saveRequest(newRequest);

        //hide dialog
        Stage stageSave = (Stage) saveBtn.getScene().getWindow();
        ToastMessage.makeText("User saved.");
        stageSave.close();
        });

        name.setStyle("-fx-text-inner-color: white");
        details.setStyle("-fx-text-inner-color: white");

        Stage stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(new Scene(parent));
        stage.show();
    }
}

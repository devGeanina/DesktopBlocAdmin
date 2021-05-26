package blocadmin;

import blocadmin.entities.DatabaseHandler;
import blocadmin.entities.Expense;
import blocadmin.entities.Household;
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
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
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
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author Geanina
 */
public class ExpenseController{
    
    private static final Logger LOGGER = LogManager.getLogger(ExpenseController.class);
    private DatabaseHandler databaseHandler = new DatabaseHandler();
    private ObservableList<Constants.EXPENSE_TYPE> expenseTypeOptions = FXCollections.observableArrayList(Constants.EXPENSE_TYPE.values());
    private String selectedType = Constants.EXPENSE_TYPE.MONTHLY.getName();
    private Household selectedHousehold;
    
    @FXML
    private JFXButton closeBtn;
    
    @FXML
    private JFXButton saveBtn;
    
    @FXML
    private JFXTextArea details;
    
    @FXML
    private JFXComboBox expenseType, householdExpense;
    
    @FXML
    private JFXTextField totalSum;
    
    @FXML
    private JFXTextField leftSum;
    
    @FXML
    private JFXCheckBox payed;
    
    @FXML
    private JFXDatePicker requestDueDate;
    
    private FXMLLoader fxmlLoader;

    public ExpenseController(Expense expense) {
        Parent parent = null;

        if (fxmlLoader == null) {
            fxmlLoader = new FXMLLoader(getClass().getResource("/blocadmin/fxml/NewExpenseDialog.fxml"));
            fxmlLoader.setController(this);

            try {
                parent = fxmlLoader.load();
            } catch (IOException e) {
                LOGGER.error("Exception creating the dialog: " + e.getMessage());
            }
        }
        
        // must be double numeric
        Pattern validEditingState = Pattern.compile("-?(([1-9][0-9]*)|0)?(\\.[0-9]*)?");

        UnaryOperator<TextFormatter.Change> filter = c -> {
            String text = c.getControlNewText();
            if (validEditingState.matcher(text).matches()) {
                return c ;
            } else {
                return null ;
            }
        };

        StringConverter<Double> converter = new StringConverter<Double>() {

            @Override
            public Double fromString(String s) {
                if (s.isEmpty() || "-".equals(s) || ".".equals(s) || "-.".equals(s)) {
                    return 0.0 ;
                } else {
                    return Double.valueOf(s);
                }
            }

            @Override
            public String toString(Double d) {
                return d.toString();
            }
        };

        totalSum.setTextFormatter(new TextFormatter<>(converter, 0.0, filter));
        leftSum.setTextFormatter(new TextFormatter<>(converter, 0.0, filter));
        
        closeBtn.setGraphic(new ImageView(new Image("/blocadmin/images/closeDialog.png")));
        closeBtn.setOnMouseClicked((MouseEvent event) -> {
            Stage stageClose = (Stage) closeBtn.getScene().getWindow();
            stageClose.close();
        });
        
        expenseType.setValue(Constants.EXPENSE_TYPE.MONTHLY);
        expenseType.setItems(expenseTypeOptions);
        
        
        expenseType.getSelectionModel().selectedItemProperty()
           .addListener(new ChangeListener<Constants.EXPENSE_TYPE>() {
               public void changed(ObservableValue<? extends Constants.EXPENSE_TYPE> observable,
                                   Constants.EXPENSE_TYPE oldValue, Constants.EXPENSE_TYPE newValue) {
                    switch (newValue) {
                            case BUILDING_MAINTAINANCE:
                               selectedType = Constants.EXPENSE_TYPE.BUILDING_MAINTAINANCE.getName();
                               break;
                            case COMMON_FOND:
                               selectedType = Constants.EXPENSE_TYPE.COMMON_FOND.getName();
                               break;
                            case MONTHLY:
                               selectedType = Constants.EXPENSE_TYPE.MONTHLY.getName();
                               break;
                            case YEARLY:
                               selectedType = Constants.EXPENSE_TYPE.YEARLY.getName();
                               break;
                            case OTHER:
                               selectedType = Constants.EXPENSE_TYPE.OTHER.getName();
                               break;   
                            default:
                                break;
                    }
               }
        });

        householdExpense.setItems(FXCollections.observableArrayList(FXCollections.observableArrayList(databaseHandler.getHouseholds())));
        householdExpense.getSelectionModel().selectedItemProperty()
           .addListener(new ChangeListener<Household>() {
               public void changed(ObservableValue<? extends Household> observable,
                                   Household oldValue, Household newValue) {
                    selectedHousehold = (Household) newValue;
               }
        });
        
         Callback<ListView<Household>, ListCell<Household>> factory = lv -> new ListCell<Household>() {
            @Override
            protected void updateItem(Household item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : "B. " + item.getBuildingNr()+ ", Ap. " + item.getAppartmentNr());
            }
        };

        householdExpense.setCellFactory(factory);
        householdExpense.setButtonCell(factory.call(null));
        
        
        if(expense != null){
            householdExpense.setValue("B. ".concat(String.valueOf(expense.getHousehold().getBuildingNr())).concat(", Ap. ").concat(String.valueOf(expense.getHousehold().getAppartmentNr())));
            leftSum.setText(String.valueOf(expense.getLeftoverSum()));
            totalSum.setText(String.valueOf(expense.getTotalSum()));
            if(expense.isPayedInFull())
                payed.setSelected(true);
            
            requestDueDate.setValue(expense.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            
            if(expense.getDetails() != null)
                details.setText(expense.getDetails());
            Constants.EXPENSE_TYPE existingExpenseType = null;
            switch (expense.getExpenseType()) {
                             case "Building Maintainance":
                               existingExpenseType = Constants.EXPENSE_TYPE.BUILDING_MAINTAINANCE;
                               break;
                            case "Common fond":
                               existingExpenseType = Constants.EXPENSE_TYPE.COMMON_FOND;
                               break;
                            case "Monthly":
                               existingExpenseType = Constants.EXPENSE_TYPE.MONTHLY;
                               break;
                            case "Yearly":
                               existingExpenseType = Constants.EXPENSE_TYPE.YEARLY;
                               break;
                            case "Other":
                               existingExpenseType = Constants.EXPENSE_TYPE.OTHER;
                               break; 
            }
           expenseType.setValue(existingExpenseType);
        }

        saveBtn.setGraphic(new ImageView(new Image("/blocadmin/images/save.png")));
        saveBtn.setOnMouseClicked((MouseEvent event) -> {
          
            Expense newExpense = new Expense();
            if(expense != null)
                newExpense = expense;
            newExpense.setLeftoverSum(Double.valueOf(leftSum.getText()));
            newExpense.setTotalSum(Double.valueOf(totalSum.getText()));
            newExpense.setExpenseType(selectedType);
            newExpense.setPayedInFull(payed.isSelected());
            newExpense.setDueDate(Date.from(requestDueDate.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
            newExpense.setHousehold((Household)selectedHousehold);
            if(details.getText() != null){
                newExpense.setDetails(details.getText());
            }
            
            if(expense != null && expense.getId() != null)
                databaseHandler.updateExpense(expense);
            else
                databaseHandler.saveExpense(newExpense);
            
            //hide dialog
            Stage stageSave = (Stage) saveBtn.getScene().getWindow();
            ToastMessage.makeText("Expense saved.");
            stageSave.close();
        });

        totalSum.setStyle("-fx-text-inner-color: white");
        leftSum.setStyle("-fx-text-inner-color: white");
        details.setStyle("-fx-text-inner-color: white");

        Stage stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(new Scene(parent));
        stage.show();
    }
    
}

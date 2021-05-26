package blocadmin;

import blocadmin.entities.Budget;
import blocadmin.entities.DatabaseHandler;
import blocadmin.utils.Constants;
import blocadmin.utils.ToastMessage;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
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
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Geanina
 */
public class BudgetController {
    
    private static final Logger LOGGER = LogManager.getLogger(BudgetController.class);
    private DatabaseHandler databaseHandler = new DatabaseHandler();
    private ObservableList<Constants.BUDGET_TYPE> budgetTypeOptions = FXCollections.observableArrayList(Constants.BUDGET_TYPE.values());
    private String selectedType = Constants.BUDGET_TYPE.MONTHLY_HOUSEHOLD.getName();
    
    @FXML
    private JFXTextField totalSum, leftSum;

    @FXML
    private JFXButton saveBtn, closeBtn;
    
    @FXML
    private JFXTextArea details;
    
    @FXML
    private JFXComboBox budgetType;
    
    private FXMLLoader fxmlLoader;

    public BudgetController(Budget budget) {
        Parent parent = null;

        if (fxmlLoader == null) {
            fxmlLoader = new FXMLLoader(getClass().getResource("/blocadmin/fxml/NewBudgetDialog.fxml"));
            fxmlLoader.setController(this);

            try {
                parent = fxmlLoader.load();
            } catch (IOException e) {
                LOGGER.error("Exception creating the dialog: " + e.getMessage());
            }
        }
        
        budgetType.setValue(Constants.BUDGET_TYPE.MONTHLY_HOUSEHOLD);
        budgetType.setItems(budgetTypeOptions);

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
        
        budgetType.getSelectionModel().selectedItemProperty()
           .addListener(new ChangeListener<Constants.BUDGET_TYPE>() {
               public void changed(ObservableValue<? extends Constants.BUDGET_TYPE> observable,
                                   Constants.BUDGET_TYPE oldValue, Constants.BUDGET_TYPE newValue) {
                    switch (newValue) {
                            case EMPLOYEE_SALARY:
                               selectedType = Constants.BUDGET_TYPE.EMPLOYEE_SALARY.getName();
                               break;
                            case MONTHLY_HOUSEHOLD:
                               selectedType = Constants.BUDGET_TYPE.MONTHLY_HOUSEHOLD.getName();
                               break;
                            case OTHER:
                               selectedType = Constants.BUDGET_TYPE.OTHER.getName();
                               break;
                            case REPAIRS:
                               selectedType = Constants.BUDGET_TYPE.REPAIRS.getName();
                               break;
                            case WORKING_CAPITAL:
                               selectedType = Constants.BUDGET_TYPE.WORKING_CAPITAL.getName();
                               break;   
                            default:
                                break;
                    }
               }
        });

        if(budget != null){
            leftSum.setText(String.valueOf(budget.getLeftoverSum()));
            totalSum.setText(String.valueOf(budget.getTotalSum()));
            if(budget.getDetails() != null)
                details.setText(budget.getDetails());
            Constants.BUDGET_TYPE existingBudgetType = null;
            switch (budget.getType()) {
                            case "Employee Salary":
                               existingBudgetType = Constants.BUDGET_TYPE.EMPLOYEE_SALARY;
                               break;
                            case "Monthly Household":
                               existingBudgetType = Constants.BUDGET_TYPE.MONTHLY_HOUSEHOLD;
                               break;
                            case "Other":
                               existingBudgetType = Constants.BUDGET_TYPE.OTHER;
                               break;
                            case "Repairs":
                               existingBudgetType = Constants.BUDGET_TYPE.REPAIRS;
                               break;
                            case "Working capital":
                               existingBudgetType = Constants.BUDGET_TYPE.WORKING_CAPITAL;
                               break;   
                            default:
                                break;
                    }
           budgetType.setValue(existingBudgetType);
        }

        saveBtn.setGraphic(new ImageView(new Image("/blocadmin/images/save.png")));
        saveBtn.setOnMouseClicked((MouseEvent event) -> {
          
            Budget newBudget = new Budget();
            if(budget != null)
                newBudget = budget;
            newBudget.setLeftoverSum(Double.valueOf(leftSum.getText()));
            newBudget.setTotalSum(Double.valueOf(totalSum.getText()));
            if(details.getText() != null){
                newBudget.setDetails(details.getText());
            }
            newBudget.setType(selectedType);
            if(budget != null && budget.getId() != null)
                databaseHandler.updateBudget(budget);
            else
                databaseHandler.saveBudget(newBudget);
            
            //hide dialog
            Stage stageSave = (Stage) saveBtn.getScene().getWindow();
            ToastMessage.makeText("Budget saved.");
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

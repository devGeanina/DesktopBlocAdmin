package blocadmin;

import blocadmin.entities.DatabaseHandler;
import blocadmin.entities.Expense;
import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
public class RecentPayController {
    
    private static final Logger LOGGER = LogManager.getLogger(RecentPayController.class);
    private DatabaseHandler dbHandler = new DatabaseHandler();
    
    @FXML
    private JFXButton closeBtn;

    @FXML
    private TableView<ExpenseHousehold> recentPays;
    
    @FXML
    private TableColumn<ExpenseHousehold, String> type;
    
    @FXML
    private TableColumn<ExpenseHousehold, String> household;
    
    @FXML
    private TableColumn<ExpenseHousehold, String> owner;
    
    @FXML
    private TableColumn<ExpenseHousehold, String> sum;
    
    @FXML
    private TableColumn<ExpenseHousehold, String> payed;
    
    private FXMLLoader fxmlLoader;
    
    public RecentPayController() {
        Parent parent = null;
        
        if (fxmlLoader == null) {
            fxmlLoader = new FXMLLoader(getClass().getResource("/blocadmin/fxml/RecentPayDialog.fxml"));
            fxmlLoader.setController(this);
            
            try {
                parent = fxmlLoader.load();
            } catch (IOException e) {
                LOGGER.error("Exception creating the dialog: " + e.getMessage());
            }
        }
        
        List<Expense> expenses = dbHandler.getExpenses();
        Collections.sort(expenses);
        List<ExpenseHousehold> expenseHouseholds = new ArrayList<>();
    
        expenses.stream().map(expense -> {
            ExpenseHousehold expenseHousehold = new ExpenseHousehold();
            expenseHousehold.setExpenseType(expense.getExpenseType());
            
            if(expense.getHousehold() != null && expense.getHousehold().getBuildingNr() == 0 && expense.getHousehold().getAppartmentNr() == 0){
                expenseHousehold.setAddress("B.".concat(String.valueOf(expense.getHousehold().getBuildingNr())).concat(", Ap. ").concat(String.valueOf(expense.getHousehold().getAppartmentNr())));
            }else{
                expenseHousehold.setAddress("-");
            }
            
            if(expense.getHousehold()!= null && expense.getHousehold().getOwner() != null && ((expense.getHousehold().getOwner().getLastName() != null && !expense.getHousehold().getOwner().getFirstName().isEmpty()) && !expense.getHousehold().getOwner().getLastName().isEmpty())){
                expenseHousehold.setOwner(expense.getHousehold().getOwner().getFirstName().concat(" ").concat(expense.getHousehold().getOwner().getLastName()));
            }else{
                expenseHousehold.setOwner("-");
            }
            
            expenseHousehold.setPayedSum(expense.getTotalSum() - expense.getLeftoverSum());
            expenseHousehold.setPayed(expense.isPayedInFull() ? "Yes" : "No");
            return expenseHousehold;
        }).forEachOrdered(expenseHousehold -> {
            expenseHouseholds.add(expenseHousehold);
        });
        recentPays.setItems(FXCollections.observableArrayList(FXCollections.observableArrayList(expenseHouseholds)));
        
        type.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getExpenseType()));
        
        household.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getAddress()));
        
        owner.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getOwner()));
        
        sum.setCellValueFactory(e -> new SimpleStringProperty(String.valueOf(e.getValue().getPayedSum())));
        
        payed.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getPayed()));
        
        recentPays.getColumns().clear();
        recentPays.getColumns().addAll(type, household, owner, sum, payed);
        
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
    
    class ExpenseHousehold{
        
        private String expenseType;
        private String address;
        private String owner;
        private double payedSum;
        private String payed;

        public String getExpenseType() {
            return expenseType;
        }

        public void setExpenseType(String expenseType) {
            this.expenseType = expenseType;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public double getPayedSum() {
            return payedSum;
        }

        public void setPayedSum(double payedSum) {
            this.payedSum = payedSum;
        }

        public String getPayed() {
            return payed;
        }

        public void setPayed(String payed) {
            this.payed = payed;
        }
    }
}

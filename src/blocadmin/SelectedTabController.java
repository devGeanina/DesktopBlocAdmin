package blocadmin;

import blocadmin.entities.Budget;
import blocadmin.entities.Expense;
import blocadmin.entities.Household;
import blocadmin.entities.Request;
import blocadmin.entities.User;
import blocadmin.entities.DatabaseHandler;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author Geanina
 * 
 *pane.getChildren().get(0).getId() //listview
  pane.getChildren().get(1).getId() //addbtn
  pane.getChildren().get(2).getId()//refreshBtn
 */
public class SelectedTabController {
    
    DatabaseHandler databaseHandler = new DatabaseHandler();
    ListView<User> usersView = new ListView<>();
    ListView<Budget> budgetView = new ListView<>();
    ListView<Expense> expenseView = new ListView<>();
    ListView<Household> householdView = new ListView<>();
    ListView<Request> requestsView = new ListView<>();
    AnchorPane mainPane = null;

    public SelectedTabController(String selectedTab, AnchorPane anchorPane) {
        this.mainPane = anchorPane;
        switch(selectedTab){
            case "usersTab":
                setUsersData(anchorPane);
                break;
            case "householdTab":
                setHouseholdsData(anchorPane);
                break;
            case "budgetTab":
                setBudgetsData(anchorPane);
                break;
            case "expenseTab":
                setExpensesData(anchorPane);
                break;
            case "requestsTab":
                setRequestsData(anchorPane);
                break;
            default:
                break;
        }
    }

    public ListView getContent(String selectedTab) {
          switch(selectedTab){
            case "usersTab":
                return usersView ;
            case "householdTab":
                return householdView;
            case "budgetTab":
                return budgetView;
            case "expenseTab":
                return expenseView;
            case "requestsTab":
                return requestsView;
            default:
                break;
        }
        return new ListView();
    }
    
      public AnchorPane getContent() {
          return mainPane;
    }

    private void setUsersData(AnchorPane anchorPane){
        usersView = (JFXListView < User >) anchorPane.getChildren().get(0);
        List<User> entityList = databaseHandler.getUsers();

        List<User> observableList = FXCollections.observableArrayList(entityList);

        ObservableList<User> listviewItems = FXCollections.observableArrayList(observableList);
        usersView.setItems(listviewItems);
        usersView.setCellFactory((ListView<User> listView) -> new UserListCell());

        JFXButton addBtn = (JFXButton)anchorPane.getChildren().get(1);
        addBtn.setGraphic(new ImageView(new Image("/blocadmin/images/add.png")));

        addBtn.setOnMouseClicked((MouseEvent event) -> {
           new UserController(null);
        });

        JFXButton refreshBtn = (JFXButton)anchorPane.getChildren().get(2);
        refreshBtn.setGraphic(new ImageView(new Image("/blocadmin/images/refresh.png")));
        refreshBtn.setText("");
        
        refreshBtn.setOnMouseClicked((MouseEvent event) -> {
            usersView.setItems(null);
            usersView.setItems(FXCollections.observableArrayList(FXCollections.observableArrayList(databaseHandler.getUsers())));
            usersView.refresh();
        });

        anchorPane.getChildren().set(0, usersView);
        anchorPane.getChildren().set(1, addBtn);
        anchorPane.getChildren().set(2, refreshBtn);
        mainPane = anchorPane;
    }
    
    private void setBudgetsData(AnchorPane anchorPane){
        budgetView = (JFXListView < Budget >) anchorPane.getChildren().get(0);
        
        List<Budget> entityList = databaseHandler.getBudgets();
        List<Budget> observableList = FXCollections.observableArrayList(entityList);

        ObservableList<Budget> listviewItems = FXCollections.observableArrayList(observableList);
        budgetView.setItems(listviewItems);
        budgetView.setCellFactory((ListView<Budget> listView) -> new BudgetListCell());

        JFXButton addBtn = (JFXButton)anchorPane.getChildren().get(1);
        addBtn.setGraphic(new ImageView(new Image("/blocadmin/images/add.png")));

        JFXButton refreshBtn = (JFXButton)anchorPane.getChildren().get(2);
        refreshBtn.setGraphic(new ImageView(new Image("/blocadmin/images/refresh.png")));
        refreshBtn.setText("");
         
        addBtn.setOnMouseClicked((MouseEvent event) -> {
           new BudgetController(null);
        });

        refreshBtn.setOnMouseClicked((MouseEvent event) -> {
            budgetView.setItems(null);
            budgetView.setItems(FXCollections.observableArrayList(FXCollections.observableArrayList(databaseHandler.getBudgets())));
            budgetView.refresh();
        });
         
        anchorPane.getChildren().set(0, budgetView);
        anchorPane.getChildren().set(1, addBtn);
        anchorPane.getChildren().set(2, refreshBtn);
        mainPane = anchorPane;
    }
    
    private void setExpensesData(AnchorPane anchorPane){
        expenseView = (JFXListView < Expense >) anchorPane.getChildren().get(0);
        List<Expense> entityList = databaseHandler.getExpenses();
        List<Expense> observableList = FXCollections.observableArrayList(entityList);

        ObservableList<Expense> listviewItems = FXCollections.observableArrayList(observableList);
        expenseView.setItems(listviewItems);
        expenseView.setCellFactory((ListView<Expense> listView) -> new ExpenseListCell());
        
        JFXButton addBtn = (JFXButton)anchorPane.getChildren().get(1);
        addBtn.setGraphic(new ImageView(new Image("/blocadmin/images/add.png")));

        JFXButton refreshBtn = (JFXButton)anchorPane.getChildren().get(2);
        refreshBtn.setGraphic(new ImageView(new Image("/blocadmin/images/refresh.png")));
        refreshBtn.setText("");
        
        addBtn.setOnMouseClicked((MouseEvent event) -> {
           new ExpenseController(null);
        });
        
        refreshBtn.setOnMouseClicked((MouseEvent event) -> {
            expenseView.setItems(null);
            expenseView.setItems(FXCollections.observableArrayList(FXCollections.observableArrayList(databaseHandler.getExpenses())));
            expenseView.refresh();
        });

        anchorPane.getChildren().set(0, expenseView);
        anchorPane.getChildren().set(1, addBtn);
        anchorPane.getChildren().set(2, refreshBtn);
        mainPane = anchorPane;
    }
    
    private void setHouseholdsData(AnchorPane anchorPane){
        householdView = (JFXListView < Household >) anchorPane.getChildren().get(0);
        List<Household> entityList = databaseHandler.getHouseholds();

        List<Household> observableList = FXCollections.observableArrayList(entityList);
        
        ObservableList<Household> listviewItems = FXCollections.observableArrayList(observableList);
        householdView.setItems(listviewItems);
        householdView.setCellFactory((ListView<Household> listView) -> new HouseholdListCell());
        
        JFXButton addBtn = (JFXButton)anchorPane.getChildren().get(1);
        addBtn.setGraphic(new ImageView(new Image("/blocadmin/images/add.png")));

        JFXButton refreshBtn = (JFXButton)anchorPane.getChildren().get(2);
        refreshBtn.setGraphic(new ImageView(new Image("/blocadmin/images/refresh.png")));
        refreshBtn.setText("");
        
        addBtn.setOnMouseClicked((MouseEvent event) -> {
           new HouseholdController(null);
        });
        
        refreshBtn.setOnMouseClicked((MouseEvent event) -> {
            householdView.setItems(null);
            householdView.setItems(FXCollections.observableArrayList(FXCollections.observableArrayList(databaseHandler.getHouseholds())));
            householdView.refresh();
        });

        anchorPane.getChildren().set(0, householdView);
        anchorPane.getChildren().set(1, addBtn);
        anchorPane.getChildren().set(2, refreshBtn);
        mainPane = anchorPane;
    }
    
    private void setRequestsData(AnchorPane anchorPane){
        requestsView = (JFXListView < Request >) anchorPane.getChildren().get(0);
        List<Request> entityList = databaseHandler.getRequests();
        List<Request> observableList = FXCollections.observableArrayList(entityList);

        ObservableList<Request> listviewItems = FXCollections.observableArrayList(observableList);
        requestsView.setItems(listviewItems);
        requestsView.setCellFactory((ListView<Request> listView) -> new RequestListCell());
        
        JFXButton addBtn = (JFXButton)anchorPane.getChildren().get(1);
        addBtn.setGraphic(new ImageView(new Image("/blocadmin/images/add.png")));

        JFXButton refreshBtn = (JFXButton)anchorPane.getChildren().get(2);
        refreshBtn.setGraphic(new ImageView(new Image("/blocadmin/images/refresh.png")));
        refreshBtn.setText("");
        
        addBtn.setOnMouseClicked((MouseEvent event) -> {
           new RequestController(null);
        });
        
        refreshBtn.setOnMouseClicked((MouseEvent event) -> {
            requestsView.setItems(null);
            requestsView.setItems(FXCollections.observableArrayList(FXCollections.observableArrayList(databaseHandler.getRequests())));
            requestsView.refresh();
        });

        anchorPane.getChildren().set(0, requestsView);
        anchorPane.getChildren().set(1, addBtn);
        anchorPane.getChildren().set(2, refreshBtn);
        mainPane = anchorPane;
    }
}

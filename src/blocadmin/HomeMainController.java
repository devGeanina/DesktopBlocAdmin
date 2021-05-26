package blocadmin;

import blocadmin.entities.Household;
import blocadmin.entities.DatabaseHandler;
import blocadmin.utils.ToastMessage;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextAlignment;


public class HomeMainController extends ListView<Household> implements Initializable {
    
    private SelectedTabController tabController;
    private DatabaseHandler databaseHandler;

    @FXML
    private JFXTabPane appTabs;
    
    @FXML
    private JFXButton dbSettingsBtn, infoBtn, recentPayBtn;
    
    @FXML
    private ImageView homeImage;
    
    @FXML
    private Tab homeTab, usersTab, householdTab, budgetTab, expenseTab, requestsTab;
    
    private double tabWidth = 145.0;
    public static int lastSelectedTabIndex = 0;
   

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        ObservableList<Tab> tabs = appTabs.getTabs();
        for(Tab tab: tabs){
            switch(tab.getId()){
                    case "homeTab":
                        homeTab = tab;
                        configureTab(tab, tab.getText(), "/blocadmin/images/home.png");
                        break;
                    case "usersTab":
                        usersTab = tab;
                        configureTab(tab, tab.getText(), "/blocadmin/images/users.png");
                        AnchorPane userPane = (AnchorPane)usersTab.getContent();
                        tabController = new SelectedTabController(usersTab.getId(), userPane);
                        usersTab.setContent(tabController.getContent());
                        break;
                    case "householdTab":
                        householdTab = tab;
                        configureTab(tab, tab.getText(), "/blocadmin/images/apartment.png");
                        AnchorPane householdPane = (AnchorPane)householdTab.getContent();
                        tabController = new SelectedTabController(householdTab.getId(), householdPane);
                        householdTab.setContent(tabController.getContent());
                        break;
                    case "budgetTab":
                        budgetTab = tab;
                        configureTab(tab, tab.getText(), "/blocadmin/images/budget.png");
                        AnchorPane budgetPane = (AnchorPane)budgetTab.getContent();
                        tabController = new SelectedTabController(budgetTab.getId(), budgetPane);
                        budgetTab.setContent(tabController.getContent());
                        break;
                    case "expenseTab":
                        expenseTab = tab;
                        configureTab(tab, tab.getText(), "/blocadmin/images/expense.png");
                        AnchorPane expensePane = (AnchorPane)expenseTab.getContent();
                        tabController = new SelectedTabController(expenseTab.getId(), expensePane);
                        expenseTab.setContent(tabController.getContent());
                        break;
                    case "requestsTab":
                        requestsTab = tab;
                        configureTab(tab, tab.getText(), "/blocadmin/images/request.png");
                        AnchorPane requestPane = (AnchorPane)requestsTab.getContent();
                        tabController = new SelectedTabController(requestsTab.getId(), requestPane);
                        requestsTab.setContent(tabController.getContent());
                        break;
                    default:
                        break;
            }
        }

        dbSettingsBtn.setGraphic(new ImageView(new Image("/blocadmin/images/db.png")));
        infoBtn.setGraphic(new ImageView(new Image("/blocadmin/images/info.png")));
        recentPayBtn.setGraphic(new ImageView(new Image("/blocadmin/images/recentPay.png")));
        homeImage.setImage(new Image("/blocadmin/images/homeImage.png"));

        infoBtn.setOnMouseClicked((MouseEvent event) -> {
            new HelpController();
        });
        
        recentPayBtn.setOnMouseClicked((MouseEvent event) -> {
            new RecentPayController();
        });

        Preferences preferences = Preferences.userNodeForPackage(DatabaseSettingsController.class); 
        databaseHandler = new DatabaseHandler();
        if(preferences != null){
               databaseHandler.createDBSchema(); //create DB schema if it doesn't exist
        } else{
            ToastMessage.makeText("Please add the database settings.");
        }

        dbSettingsBtn.setOnMouseClicked((MouseEvent event) -> {
        DatabaseSettingsController dbSettingsController = new DatabaseSettingsController();
        boolean isSettingsSaved = dbSettingsController.showAndWait();
            if(isSettingsSaved){
                 databaseHandler.createDBSchema(); //create DB schema if it doesn't exist and the settings have just been set for the first time
            }
        });  
    }
    
    private void configureTab(Tab tab, String title, String iconPath) {
        ImageView imageView = new ImageView(new Image(iconPath));
        Label label = new Label(title);
        label.setMaxWidth(tabWidth - 20);
        label.setPadding(new Insets(5, 0, 0, 0));
        label.setStyle("-fx-text-fill: white; -fx-font-size: 10pt; -fx-font-weight: bold; -fx-font-family: Courier New;");
        label.setTextAlignment(TextAlignment.CENTER);

        BorderPane tabPane = new BorderPane();
        tabPane.setCenter(imageView);
        tabPane.setBottom(label);

        tab.setText("");
        tab.setGraphic(tabPane);
    }
}

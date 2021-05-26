package blocadmin;

import blocadmin.entities.Budget;
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
public class BudgetListCell extends ListCell<Budget>{
    
    private static final Logger LOGGER = LogManager.getLogger(BudgetListCell.class);
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
    protected void updateItem(Budget budget, boolean empty) {
        super.updateItem(budget, empty);

        if (empty || budget == null) {

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
            
            StringBuilder nameBuilder = new StringBuilder();
            nameBuilder.append("Total: ");
            nameBuilder.append(budget.getTotalSum());
            nameBuilder.append(". Left: ");
            nameBuilder.append(budget.getLeftoverSum());
            name.setText(nameBuilder.toString());
            sumOrType.setText(budget.getType());
            relevantDetails.setText(budget.getDetails());
            
            deleteBtn.setOnMouseClicked((MouseEvent event) -> {
                databaseHandler.deleteBudget(budget.getId());
                ToastMessage.makeText("Item deleted.");
            });
            
            icon.setImage(new Image("/blocadmin/images/budgetList.png"));
            
            editBtn.setGraphic(new ImageView(new Image("/blocadmin/images/edit.png")));
            editBtn.setOnMouseClicked((MouseEvent event) -> {
                   new BudgetController(budget);
            });

            deleteBtn.setGraphic(new ImageView(new Image("/blocadmin/images/delete.png")));
            detailsBtn.setGraphic(new ImageView(new Image("/blocadmin/images/details.png")));
            detailsBtn.setOnMouseClicked((MouseEvent event) -> {
                   new DetailsController("Budget Details", budget.getDetails());
            });

            setText(null);
            setGraphic(gridPane);
            gridPane.setCache(true);
        }
    }
}

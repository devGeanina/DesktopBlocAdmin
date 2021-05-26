package blocadmin;

import blocadmin.entities.Expense;
import blocadmin.entities.DatabaseHandler;
import blocadmin.utils.ToastMessage;
import blocadmin.utils.Utils;
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
public class ExpenseListCell extends ListCell<Expense>{
    
    private static final Logger LOGGER = LogManager.getLogger(ExpenseListCell.class);
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
    protected void updateItem(Expense expense, boolean empty) {
        super.updateItem(expense, empty);

        if (empty || expense == null) {

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

            if(expense.getHousehold() == null || (expense.getHousehold() != null && expense.getHousehold().getBuildingNr() == 0 && expense.getHousehold().getAppartmentNr() == 0)){
                name.setText("-");
            }else{
                StringBuilder expenseBuilder = new StringBuilder();
                expenseBuilder.append("B. ");
                expenseBuilder.append(expense.getHousehold().getBuildingNr());
                expenseBuilder.append(", Ap. ");
                expenseBuilder.append(expense.getHousehold().getAppartmentNr());
                expenseBuilder.append(" - payed: ");
                expenseBuilder.append(expense.isPayedInFull() ? "yes" : "no");
                name.setText(expenseBuilder.toString());
            }

            sumOrType.setText(expense.getExpenseType());
            StringBuilder detailsBuilder = new StringBuilder();
            detailsBuilder.append("Total: ");
            detailsBuilder.append(expense.getTotalSum());
            detailsBuilder.append(", Left: ");
            detailsBuilder.append(expense.getLeftoverSum());
            detailsBuilder.append(". Due: ");
            detailsBuilder.append(Utils.convertDateToString(expense.getDueDate()));
            relevantDetails.setText(detailsBuilder.toString());
            
            deleteBtn.setOnMouseClicked((MouseEvent event) -> {
                databaseHandler.deleteExpense(expense.getId());
                ToastMessage.makeText("Item deleted.");
            });
            
            if(expense.isPayedInFull())
                icon.setImage(new Image("/blocadmin/images/payed_expense.png"));
            else
                icon.setImage(new Image("/blocadmin/images/unpayed_expense.png"));
            
            editBtn.setGraphic(new ImageView(new Image("/blocadmin/images/edit.png")));
            editBtn.setOnMouseClicked((MouseEvent event) -> {
                  new ExpenseController(expense);
            });

            deleteBtn.setGraphic(new ImageView(new Image("/blocadmin/images/delete.png")));

            detailsBtn.setGraphic(new ImageView(new Image("/blocadmin/images/details.png")));
            detailsBtn.setOnMouseClicked((MouseEvent event) -> {
                   new DetailsController("Expense details - ".concat(expense.isPayedInFull() ? "payed" : "not payed"), expense.getDetails().concat(" Due on: ").concat(Utils.convertDateToString(expense.getDueDate())));
            });

            setText(null);
            setGraphic(gridPane);
            gridPane.setCache(true);
        }
    }
}

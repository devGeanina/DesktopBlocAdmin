package blocadmin.entities;

import blocadmin.DatabaseSettingsController;
import blocadmin.utils.Constants;
import blocadmin.utils.ToastMessage;
import blocadmin.utils.Utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Geanina
 */
public class DatabaseHandler {
    private static final Logger LOGGER = LogManager.getLogger(DatabaseHandler.class);
      
    private Map<String, String> getDBConnection(){
        Preferences preferences = Preferences.userRoot().node(DatabaseSettingsController.class.getName());
        String dbURL = "jdbc:postgresql://localhost/blocAdmin"; //default URL
        String user = "postgres";
        String password = ""; // replace with your user and password if you don't configure it in the settings
        
        if(preferences != null){
         if(preferences.get(Utils.DB_URL_PREF, "jdbc:postgresql://localhost/blocAdmin") != null){
                dbURL = preferences.get(Utils.DB_URL_PREF, "jdbc:postgresql://localhost/blocAdmin");
            }

            if(preferences.get(Utils.DB_USER_PREF, "postgres") != null){
                user = preferences.get(Utils.DB_USER_PREF, "postgres");
            }

            if(preferences.get(Utils.DB_PASS_PREF, "admin") != null){
                password = preferences.get(Utils.DB_PASS_PREF, (String) null);
            }
        }else{
             preferences.put(Utils.DB_URL_PREF, dbURL);
             preferences.put(Utils.DB_USER_PREF, user);
             preferences.put(Utils.DB_PASS_PREF, password);
        }    
        
        Map<String, String> dbSettings = new HashMap<>();
        dbSettings.put(Utils.DB_URL_PREF, dbURL);
        dbSettings.put(Utils.DB_USER_PREF, user);
        dbSettings.put(Utils.DB_PASS_PREF, password);
        return dbSettings;
    }

    public void createDBSchema() {
        String createIDSeq = "CREATE SEQUENCE IF NOT EXISTS id_seq"
                + "    START WITH 1"
                + "    INCREMENT BY 10"
                + "    NO MINVALUE"
                + "    NO MAXVALUE"
                + "    CACHE 1";
        
        String createHouseholdTable = "CREATE TABLE IF NOT EXISTS household(id bigint"
                + "    DEFAULT nextval('id_seq'::regclass) NOT NULL,"
                + "    bl_nr integer NOT NULL,"
                + "    app_nr integer NOT NULL,"
                + "    room_nr integer NOT NULL,"
                + "    nr_occupants integer NOT NULL,"
                + "    total_capacity integer NOT NULL,"
                + "    details varchar(255) NOT NULL,"
                + "    user_id bigint,"
                + "    version bigint DEFAULT 0 NOT NULL)";
        
        String createUserTable = "CREATE TABLE IF NOT EXISTS app_user(id bigint"
                + "    DEFAULT nextval('id_seq'::regclass) NOT NULL,"
                + "    first_name varchar(150) NOT NULL,"
                + "    last_name varchar(150) NOT NULL,"
                + "    type smallint NOT NULL,"
                + "    bl_nr integer,"
                + "    app_nr integer,"
                + "    details varchar(255),"
                + "    version bigint DEFAULT 0 NOT NULL)";

        String createRequestTable = "CREATE TABLE IF NOT EXISTS request(id bigint"
                + "    DEFAULT nextval('id_seq'::regclass) NOT NULL,"
                + "    type smallint NOT NULL,"
                + "    name varchar(255) NOT NULL,"
                + "    resolved bool NOT NULL,"
                + "    due_date date NOT NULL,"
                + "    details varchar(255),"
                + "    user_id bigint,"
                + "    version bigint DEFAULT 0 NOT NULL)";

        String createExpenseTable = "CREATE TABLE IF NOT EXISTS expense(id bigint"
                + "    DEFAULT nextval('id_seq'::regclass) NOT NULL,"
                + "    total double precision NOT NULL,"
                + "    leftover double precision NOT NULL,"
                + "    payed bool NOT NULL,"
                + "    due_date date NOT NULL,"
                + "    household_id bigint,"
                + "    type smallint NOT NULL,"    
                + "    details varchar(255),"
                + "    version bigint DEFAULT 0 NOT NULL)";
        
        String createBudgetTable = "CREATE TABLE IF NOT EXISTS budget(id bigint"
                + "    DEFAULT nextval('id_seq'::regclass) NOT NULL,"
                + "    type smallint NOT NULL,"
                + "    total double precision NOT NULL,"
                + "    leftover double precision NOT NULL,"
                + "    details varchar(255),"
                + "    version bigint DEFAULT 0 NOT NULL)";
        
        String createDocTable = "CREATE TABLE IF NOT EXISTS document(id bigint"
                + "    DEFAULT nextval('id_seq'::regclass) NOT NULL,"
                + "    name varchar(255) NOT NULL,"
                + "    doc bytea NOT NULL,"
                + "    version bigint DEFAULT 0 NOT NULL)";
        
        String addUniqueUserID = "ALTER TABLE app_user DROP CONSTRAINT IF EXISTS unique_id CASCADE; ALTER TABLE app_user ADD CONSTRAINT unique_id UNIQUE (id)";
        
        String addForeignKeyUserHousehold = "ALTER TABLE ONLY household" 
                + "    ADD CONSTRAINT user_household FOREIGN KEY (user_id) REFERENCES app_user(id)";
        
        String addForeignKeyUserRequest = "ALTER TABLE ONLY request" 
                + "    ADD CONSTRAINT user_request FOREIGN KEY (user_id) REFERENCES app_user(id)";
        
        String addForeignKeyExpenseHousehold = "ALTER TABLE ONLY expense" 
                + "    ADD CONSTRAINT household_expense FOREIGN KEY (household_id) REFERENCES household(id)";

        Map<String, String> dbSettings = getDBConnection();
        
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF));
            Statement st = con.createStatement();) {
            st.executeUpdate(createIDSeq);
            st.executeUpdate(createUserTable);
            st.executeUpdate(addUniqueUserID);
            st.executeUpdate(createHouseholdTable);
            st.executeUpdate(createRequestTable);
            st.executeUpdate(createExpenseTable);
            st.executeUpdate(createBudgetTable);
            st.executeUpdate(createDocTable);
            st.executeUpdate(addForeignKeyUserHousehold);
            st.executeUpdate(addForeignKeyUserRequest);
        } catch (SQLException ex) {
            LOGGER.error("Exception creating the db schema: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }else{
                ToastMessage.makeText("Can't create database, check settings.");
            }
        }
    }
 
    public void saveUser(User user) {
        String query = "INSERT INTO app_user(first_name, last_name, type, bl_nr, app_nr, details) VALUES(?, ?, ?, ?, ?, ?)";
        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF));
                PreparedStatement pst = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pst.setString(1, user.getFirstName());
            pst.setString(2, user.getLastName());
            pst.setShort(3, Constants.USER_TYPE.valueOfLabel(user.getUserType()).getType());
            pst.setInt(4,user.getBuildingNr());
            pst.setInt(5, user.getAppartmentNr());
            pst.setString(6, user.getDetails());
            
            pst.executeUpdate();
        
        } catch (SQLException ex) {
            LOGGER.error("Exception saving the item: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
    }
    
    public void updateUser(User user) {
        String query = "UPDATE app_user set first_name = ?, last_name = ?, type = ?, bl_nr = ?, app_nr = ?, details = ? WHERE id = ?";

        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF))){
            try (PreparedStatement pst = con.prepareStatement(query)) {
                pst.setString(1, user.getFirstName());
                pst.setString(2, user.getLastName());
                pst.setShort(3, Constants.USER_TYPE.valueOfLabel(user.getUserType()).getType());
                pst.setInt(4,user.getBuildingNr());
                pst.setInt(5, user.getAppartmentNr());
                pst.setString(6, user.getDetails());
                pst.setLong(7, user.getId());
                int updatedRows = pst.executeUpdate();
                if (updatedRows > 0) {
                    LOGGER.info("Item updated successfully.");
                }
            }
        } catch (SQLException ex) {
            LOGGER.error("Exception updating item: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
    }
    
    public User getUser(Long id) {
        User entity = new User();
        String selectStatusSql = "SELECT * FROM app_user u WHERE id = ?";
        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF))){
            try (PreparedStatement st = con.prepareStatement(selectStatusSql)) {
                st.setLong(1, id);
                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next()) {
                        entity.setId(rs.getLong(1));
                        entity.setFirstName(rs.getString(2));
                        entity.setLastName(rs.getString(3));
                        entity.setUserType(Constants.USER_TYPE.getNameByCode(rs.getShort(4)));
                        entity.setBuildingNr(rs.getInt(5));
                        entity.setAppartmentNr(rs.getInt(6));
                        entity.setDetails(rs.getString(7));
                    }
                }
            }
        } catch (SQLException ex) {
            LOGGER.error("Exception retrieving the item for id " + id + ". Excepion: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
        return entity;
    }
      
    public List<User> getUsers() {
        List<User> entities = new ArrayList<>();
        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF));
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM app_user")) {

            while (rs.next()) {
                User entity = new User();
                entity.setId(rs.getLong(1));
                entity.setFirstName(rs.getString(2));
                entity.setLastName(rs.getString(3));
                entity.setUserType(Constants.USER_TYPE.getNameByCode(rs.getShort(4)));
                entity.setBuildingNr(rs.getInt(5));
                entity.setAppartmentNr(rs.getInt(6));
                entity.setDetails(rs.getString(7));
                entities.add(entity);
            }
        } catch (SQLException ex) {
            LOGGER.error("Exception retrieving the item list: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
        return entities;
    }
    
    public void deleteUser(Long id) {
        String query = "DELETE FROM app_user WHERE id = ?";

        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF))){
            try (PreparedStatement pst = con.prepareStatement(query)) {
                pst.setLong(1, id);
                int updatedRows = pst.executeUpdate();
                if (updatedRows > 0) {
                    LOGGER.info("Item deleted successfully.");
                }
            }
        } catch (SQLException ex) {
            LOGGER.error("Exception deleting the item with id: " + id + " and exception: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
    }
    
    public void saveHousehold(Household household) {
        String query = "INSERT INTO household(bl_nr, app_nr, room_nr, nr_occupants, total_capacity, details, user_id) VALUES(?, ?, ?, ?, ?, ?, ?)";
        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF));
                PreparedStatement pst = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, household.getBuildingNr());
            pst.setInt(2, household.getAppartmentNr());
            pst.setInt(3, household.getRoomsNr());
            pst.setInt(4, household.getNrCurrentOccupants());
            pst.setInt(5, household.getTotalCapacity());
            pst.setString(6, household.getDetails());
            pst.setLong(7, household.getOwner().getId());
            
            pst.executeUpdate();
        
        } catch (SQLException ex) {
            LOGGER.error("Exception saving the item: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
    }
    
    public void updateHousehold(Household household) {
        String query = "UPDATE household set bl_nr = ?, app_nr = ?, room_nr = ?, nr_occupants = ?, total_capacity = ?, details = ?, user_id = ? WHERE id = ?";

        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF))){
            try (PreparedStatement pst = con.prepareStatement(query)) {
                pst.setInt(1, household.getBuildingNr());
                pst.setInt(2, household.getAppartmentNr());
                pst.setInt(3, household.getRoomsNr());
                pst.setInt(4, household.getNrCurrentOccupants());
                pst.setInt(5, household.getTotalCapacity());
                pst.setString(6, household.getDetails());
                pst.setLong(7, household.getOwner().getId());
                pst.setLong(8, household.getId());
                int updatedRows = pst.executeUpdate();
                if (updatedRows > 0) {
                    LOGGER.info("Item updated successfully.");
                }
            }
        } catch (SQLException ex) {
            LOGGER.error("Exception updating item: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
    }
    
    public Household getHousehold(Long id) {
        Household entity = new Household();
        String selectStatusSql = "SELECT * FROM household h WHERE id = ?";
        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF))){
            try (PreparedStatement st = con.prepareStatement(selectStatusSql)) {
                st.setLong(1, id);
                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next()) {
                        entity.setId(rs.getLong(1));
                        entity.setBuildingNr(rs.getInt(2));
                        entity.setAppartmentNr(rs.getInt(3));
                        entity.setRoomsNr(rs.getInt(4));
                        entity.setNrCurrentOccupants(rs.getInt(5));
                        entity.setTotalCapacity(rs.getInt(6));
                        entity.setDetails(rs.getString(7));
                        User user = getUser(rs.getLong(8));
                        entity.setOwner(user);
                    }
                }
            }
        } catch (SQLException ex) {
            LOGGER.error("Exception retrieving the item for id " + id + ". Excepion: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
        return entity;
    }
    
    public List<Household> getHouseholds() {
        List<Household> entities = new ArrayList<>();
        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF));
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM household")) {

            while (rs.next()) {
                Household entity = new Household();
                entity.setId(rs.getLong(1));
                entity.setBuildingNr(rs.getInt(2));
                entity.setAppartmentNr(rs.getInt(3));
                entity.setRoomsNr(rs.getInt(4));
                entity.setNrCurrentOccupants(rs.getInt(5));
                entity.setTotalCapacity(rs.getInt(6));
                entity.setDetails(rs.getString(7));
                User user = getUser(rs.getLong(8));
                entity.setOwner(user);
                
                entities.add(entity);
            }
        } catch (SQLException ex) {
            LOGGER.error("Exception retrieving the item list: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
        return entities;
    }
    
    public void deleteHousehold(Long id) {
        String query = "DELETE FROM household WHERE id = ?";

        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF))){
            try (PreparedStatement pst = con.prepareStatement(query)) {
                pst.setLong(1, id);
                int updatedRows = pst.executeUpdate();
                if (updatedRows > 0) {
                    LOGGER.info("Item deleted successfully.");
                }
            }
        } catch (SQLException ex) {
            LOGGER.error("Exception deleting the item with id: " + id + " and exception: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
    }
        
    public void saveExpense(Expense expense) {
        String query = "INSERT INTO expense(total, leftover, payed, due_date, household_id, type, details) VALUES(?, ?, ?, ?, ?, ?, ?)";
        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF));
                PreparedStatement pst = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pst.setDouble(1, expense.getTotalSum());
            pst.setDouble(2, expense.getLeftoverSum());
            pst.setBoolean(3, expense.isPayedInFull());
            pst.setDate(4, new java.sql.Date(expense.getDueDate().getTime()));
            if(expense.getHousehold() != null && expense.getHousehold().getId() != null)
                pst.setLong(5, expense.getHousehold().getId());
            else
                pst.setNull(5, 0);
            pst.setShort(6, Constants.EXPENSE_TYPE.valueOfLabel(expense.getExpenseType()).getType());
            pst.setString(7, expense.getDetails());
            
            pst.executeUpdate();
        
        } catch (SQLException ex) {
            LOGGER.error("Exception saving the item: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
    }
    
    public void deleteExpense(Long id) {
        String query = "DELETE FROM expense WHERE id = ?";

        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF))){
            try (PreparedStatement pst = con.prepareStatement(query)) {
                pst.setLong(1, id);
                int updatedRows = pst.executeUpdate();
                if (updatedRows > 0) {
                    LOGGER.info("Item deleted successfully.");
                }
            }
        } catch (SQLException ex) {
            LOGGER.error("Exception deleting the item with id: " + id + " and exception: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
    }
    
    public void updateExpense(Expense expense) {
        String query = "UPDATE expense set total = ?, leftover = ?, payed = ?, due_date = ?, household_id = ?, type = ?, details = ? WHERE id = ?";

        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF))){
            try (PreparedStatement pst = con.prepareStatement(query)) {
                pst.setDouble(1, expense.getTotalSum());
                pst.setDouble(2, expense.getLeftoverSum());
                pst.setBoolean(3, expense.isPayedInFull());
                pst.setDate(4, new java.sql.Date(expense.getDueDate().getTime()));
                if(expense.getHousehold() != null && expense.getHousehold().getId() != null)
                    pst.setLong(5, expense.getHousehold().getId());
                else
                    pst.setNull(5, 0);
                pst.setShort(6, Constants.EXPENSE_TYPE.valueOfLabel(expense.getExpenseType()).getType());
                pst.setString(7, expense.getDetails());
                pst.setLong(8, expense.getId());
                int updatedRows = pst.executeUpdate();
                if (updatedRows > 0) {
                    LOGGER.info("Item updated successfully.");
                }
            }
        } catch (SQLException ex) {
            LOGGER.error("Exception updating item: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
    }
    
    public List<Expense> getExpenses() {
        List<Expense> entities = new ArrayList<>();
        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF));
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM expense")) {

            while (rs.next()) {
                Expense entity = new Expense();
                entity.setId(rs.getLong(1));
                entity.setTotalSum(rs.getDouble(2));
                entity.setLeftoverSum(rs.getDouble(3));
                entity.setPayedInFull(rs.getBoolean(4));
                entity.setDueDate(new Date(rs.getDate(5).getTime()));
                Household household = getHousehold(rs.getLong(6));
                entity.setHousehold(household);
                entity.setExpenseType(Constants.EXPENSE_TYPE.getNameByCode(rs.getShort(7)));
                entity.setDetails(rs.getString(8));
    
                entities.add(entity);
            }
        } catch (SQLException ex) {
            LOGGER.error("Exception retrieving the item list: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
        return entities;
    }
            
    public void saveBudget(Budget budget) {
        String query = "INSERT INTO budget(type, total, leftover, details) VALUES(?, ?, ?, ?)";
        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF));
                PreparedStatement pst = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pst.setShort(1, Constants.BUDGET_TYPE.valueOfLabel(budget.getType()).getType());
            pst.setDouble(2, budget.getTotalSum());
            pst.setDouble(3, budget.getLeftoverSum());
            pst.setString(4, budget.getDetails());
            
            pst.executeUpdate();
        
        } catch (SQLException ex) {
            LOGGER.error("Exception saving the item: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
    }
    
    public void updateBudget(Budget budget) {
        String query = "UPDATE budget set type = ?, total = ?, leftover = ?, details = ? WHERE id = ?";

        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF))){
            try (PreparedStatement pst = con.prepareStatement(query)) {
                pst.setShort(1, Constants.BUDGET_TYPE.valueOfLabel(budget.getType()).getType());
                pst.setDouble(2, budget.getTotalSum());
                pst.setDouble(3, budget.getLeftoverSum());
                pst.setString(4, budget.getDetails());
                pst.setLong(5, budget.getId());
                int updatedRows = pst.executeUpdate();
                if (updatedRows > 0) {
                    LOGGER.info("Item updated successfully.");
                }
            }
        } catch (SQLException ex) {
            LOGGER.error("Exception updating item: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
    }
    
    public List<Budget> getBudgets() {
        List<Budget> entities = new ArrayList<>();
        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF));
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM budget")) {

            while (rs.next()) {
                Budget entity = new Budget();
                entity.setId(rs.getLong(1));
                entity.setType(Constants.BUDGET_TYPE.getNameByCode(rs.getShort(2)));
                entity.setTotalSum(rs.getDouble(3));
                entity.setLeftoverSum(rs.getDouble(4));
                entity.setDetails(rs.getString(5));

                entities.add(entity);
            }
        } catch (SQLException ex) {
            LOGGER.error("Exception retrieving the item list: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
        return entities;
    }
        
    public void deleteBudget(Long id) {
        String query = "DELETE FROM budget WHERE id = ?";

        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF))){
            try (PreparedStatement pst = con.prepareStatement(query)) {
                pst.setLong(1, id);
                int updatedRows = pst.executeUpdate();
                if (updatedRows > 0) {
                    LOGGER.info("Item deleted successfully.");
                }
            }
        } catch (SQLException ex) {
            LOGGER.error("Exception deleting the item with id: " + id + " and exception: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
    }
        
    public void saveRequest(Request request) {
        String query = "INSERT INTO request(type, name, resolved, due_date, details, user_id) VALUES(?, ?, ?, ?, ?, ?)";
        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF));
                PreparedStatement pst = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pst.setShort(1, Constants.HOUSEHOLD_REQUEST_TYPE.valueOfLabel(request.getRequestType()).getType());
            pst.setString(2, request.getName());
            pst.setBoolean(3, request.isResolved());
            pst.setDate(4, new java.sql.Date(request.getDueDate().getTime()));
            pst.setString(5, request.getDetails());
            pst.setLong(6, request.getOwner().getId());
            
            pst.executeUpdate();
        
        } catch (SQLException ex) {
            LOGGER.error("Exception saving the item: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
    }    
    
    public void updateRequest(Request request) {
        String query = "UPDATE request set type = ?, name = ?, resolved = ?, due_date = ?, details = ?, user_id = ? WHERE id = ?";

        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF))){
            try (PreparedStatement pst = con.prepareStatement(query)) {
                    pst.setShort(1, Constants.HOUSEHOLD_REQUEST_TYPE.valueOfLabel(request.getRequestType()).getType());
                    pst.setString(2, request.getName());
                    pst.setBoolean(3, request.isResolved());
                    pst.setDate(4, new java.sql.Date(request.getDueDate().getTime()));
                    pst.setString(5, request.getDetails());
                    pst.setLong(6, request.getOwner().getId());
                    pst.setLong(7, request.getId());
                int updatedRows = pst.executeUpdate();
                if (updatedRows > 0) {
                    LOGGER.info("Item updated successfully.");
                }
            }
        } catch (SQLException ex) {
            LOGGER.error("Exception updating item: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
    }
    
    public List<Request> getRequests() {
        List<Request> entities = new ArrayList<>();
        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF));
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM request")) {

            while (rs.next()) {
                Request entity = new Request();
                entity.setId(rs.getLong(1));
                entity.setRequestType(Constants.HOUSEHOLD_REQUEST_TYPE.getNameByCode(rs.getShort(2)));
                entity.setName(rs.getString(3));
                entity.setIsResolved(rs.getBoolean(4));
                entity.setDueDate(new Date(rs.getDate(5).getTime()));
                entity.setDetails(rs.getString(6));
                  
                User user = getUser(rs.getLong(7));
                entity.setOwner(user);
                entities.add(entity);
            }
        } catch (SQLException ex) {
            LOGGER.error("Exception retrieving the item list: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
        return entities;
    }
    
    public void deleteRequest(Long id) {
        String query = "DELETE FROM request WHERE id = ?";

        Map<String, String> dbSettings = getDBConnection();
        try (Connection con = DriverManager.getConnection(dbSettings.get(Utils.DB_URL_PREF), dbSettings.get(Utils.DB_USER_PREF), dbSettings.get(Utils.DB_PASS_PREF))){
            try (PreparedStatement pst = con.prepareStatement(query)) {
                pst.setLong(1, id);
                int updatedRows = pst.executeUpdate();
                if (updatedRows > 0) {
                    LOGGER.info("Item deleted successfully.");
                }
            }
        } catch (SQLException ex) {
            LOGGER.error("Exception deleting the item with id: " + id + " and exception: " + ex.getMessage());
            if(ex.getMessage().contains("authentication failed")){
                ToastMessage.makeText("Database authentication failed. Check settings.");
            }
        }
    }
}

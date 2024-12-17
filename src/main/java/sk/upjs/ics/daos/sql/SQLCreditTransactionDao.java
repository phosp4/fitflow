package sk.upjs.ics.daos.sql;

import sk.upjs.ics.daos.interfaces.CreditTransactionDao;
import sk.upjs.ics.entities.*;
import sk.upjs.ics.exceptions.CouldNotAccessDatabaseException;
import sk.upjs.ics.exceptions.CouldNotAccessFileException;
import sk.upjs.ics.exceptions.NotFoundException;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * SQL implementation of the CreditTransactionDao interface.
 */
public class SQLCreditTransactionDao implements CreditTransactionDao {

    private final Connection connection;

    /**
     * Constructs a new SQLCreditTransactionDao with the specified database connection.
     *
     * @param connection the database connection
     */
    public SQLCreditTransactionDao(Connection connection) {
        this.connection = connection;
    }


    /**
     * Extracts a list of CreditTransaction objects from the given ResultSet.
     *
     * @param rs the ResultSet to extract data from
     * @return a list of CreditTransaction objects
     * @throws SQLException if a database access error occurs
     */
    private ArrayList<CreditTransaction> extractFromResultSet(ResultSet rs) throws SQLException {
        ArrayList<CreditTransaction> creditTransactions = new ArrayList<>();

        HashMap<Long, CreditTransaction> creditTransactionsProcessed= new HashMap<>();
        HashMap<Long, User> usersProcessed = new HashMap<>();
        HashMap<Long, Role> rolesProcessed = new HashMap<>();
        HashMap<Long, Specialization> specializationsProcessed = new HashMap<>();
        HashMap<Long, CreditTransactionType> creditTransactionTypesProcessed = new HashMap<>();

        while (rs.next()) {
            Long creditTransactionId = rs.getLong("ct_id");
            CreditTransaction creditTransaction = creditTransactionsProcessed.get(creditTransactionId);

            if (creditTransaction == null) {
                creditTransaction = CreditTransaction.fromResultSet(rs, "ct_");
                creditTransactionsProcessed.put(creditTransactionId, creditTransaction);
                creditTransactions.add(creditTransaction);
            }

            Long userId = rs.getLong("us_id");
            User user = usersProcessed.get(userId);
            if (user == null) {
                user = User.fromResultSet(rs, "us_");
                usersProcessed.put(userId, user);
            }

            Long roleId = rs.getLong("r_id");
            Role role = rolesProcessed.get(roleId);
            if (role == null) {
                role = Role.fromResultSet(rs, "r_");
                rolesProcessed.put(roleId, role);
            }

            Long specializationId = rs.getLong("tsp_id");
            Specialization specialization = specializationsProcessed.get(specializationId);
            if (specialization == null) {
                specialization = Specialization.fromResultSet(rs, "tsp_");
                specializationsProcessed.put(specializationId, specialization);
            }

            Long creditTransactionTypeId = rs.getLong("ctt_id");
            CreditTransactionType creditTransactionType = creditTransactionTypesProcessed.get(creditTransactionTypeId);
            if (creditTransactionType == null) {
                creditTransactionType = CreditTransactionType.fromResultSet(rs, "ctt_");
                creditTransactionTypesProcessed.put(creditTransactionTypeId, creditTransactionType);
            }

            if (user != null && role != null) {
                user.setRole(role);
            }

            if (user != null && specialization != null) {
                user.getTrainerSpecializationSet().add(specialization);
            }

            if (creditTransaction != null && creditTransaction.getUser() == null) {
                creditTransaction.setUser(user);
            }

            if (creditTransaction != null && creditTransaction.getCreditTransactionType() == null) {
                creditTransaction.setCreditTransactionType(creditTransactionType);
            }
        }

//        if (creditTransactions.isEmpty()) {
//            throw new NotFoundException("No credit transactions found");
//        }
        
        return creditTransactions;
    }
    
    private final String creditTransactionColumns = "ct.id AS ct_id, ct.amount AS ct_amount, ct.created_at AS ct_created_at, ct.updated_at AS ct_updated_at";
    String userColumns = "us.id AS us_id, us.email AS us_email, us.first_name AS us_first_name, " +
            "us.last_name AS us_last_name, us.credit_balance AS us_credit_balance, us.phone AS us_phone, " +
            "us.birth_date AS us_birth_date, us.active AS us_active, us.created_at AS us_created_at, us.updated_at AS us_updated_at";
    private final String roleColumns = "r.id AS r_id, r.name AS r_name";
    private final String specializationColumns = "tsp.id AS tsp_id, tsp.name AS tsp_name";
    private final String creditTransactionTypeColumns = "ctt.id AS ctt_id, ctt.name AS ctt_name";

    private final String joins = "LEFT JOIN users us ON us.id = ct.user_id " +
            "LEFT JOIN roles r ON r.id = us.role_id " +
            "LEFT JOIN trainers_have_specializations ts ON ts.trainer_id = us.id " +
            "LEFT JOIN trainer_specializations tsp ON tsp.id = ts.specialization_id " +
            "LEFT JOIN credit_transaction_types ctt ON ctt.id = ct.credit_transaction_type_id";

    private final String selectQuery = "SELECT " + creditTransactionColumns + ", " + userColumns + ", " + roleColumns + ", " + specializationColumns + ", " + creditTransactionTypeColumns + " FROM credit_transactions ct " + joins;
    private final String insertQuery = "INSERT INTO credit_transactions (user_id, amount, credit_transaction_type_id) VALUES (?, ?, ?)";

    /**
     * Loads credit transactions from a CSV file and inserts them into the database.
     *
     * @param file the CSV file to load data from
     * @throws CouldNotAccessFileException if the file cannot be accessed
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     */
    @Override
    public void loadFromCsv(File file) {
        try (Scanner scanner = new Scanner(file)) {
            // skip header
            scanner.nextLine();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",");

                try (PreparedStatement pstm = connection.prepareStatement(insertQuery)) {
                    pstm.setLong(1, Long.parseLong(parts[0]));
                    pstm.setFloat(2, Float.parseFloat(parts[1]));
                    pstm.setLong(3, Long.parseLong(parts[2]));
                    pstm.executeUpdate();
                } catch (SQLException e) {
                    throw new CouldNotAccessDatabaseException("Database not accessible", e);
                }
            }
        } catch (FileNotFoundException e) {
            throw new CouldNotAccessFileException("Could not access file");
        }
    }

    /**
     * Creates a new credit transaction in the database.
     *
     * @return the ID of the created credit transaction
     * @param creditTransaction the credit transaction to create
     * @throws IllegalArgumentException if the credit transaction or its user or type is null, or if the user or type does not have an ID
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     */
    @Override
    public Long create(CreditTransaction creditTransaction) {
        if (creditTransaction == null) {
            throw new IllegalArgumentException("CreditTransaction cannot be null");
        }

        if (creditTransaction.getUser() == null) {
            throw new IllegalArgumentException("The user cannot be null");
        }

        if (creditTransaction.getUser().getId() == null) {
            throw new IllegalArgumentException("The user does not have an id");
        }

        if (creditTransaction.getCreditTransactionType().getId() == null) {
            throw new IllegalArgumentException("The credit transaction type does not have an id");
        }

//        if (findById(creditTransaction.getId()) != null) {
//            throw new IllegalArgumentException("Credit transaction with id " + creditTransaction.getId() + " already exists");
//        }

        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setLong(1, creditTransaction.getUser().getId());
            pstmt.setDouble(2, creditTransaction.getAmount());
            pstmt.setLong(3, creditTransaction.getCreditTransactionType().getId());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Creating credit transaction failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    /**
     * Deletes a credit transaction from the database.
     *
     * @param creditTransaction the credit transaction to delete
     * @throws IllegalArgumentException if the credit transaction or its ID is null
     * @throws NotFoundException if the credit transaction does not exist
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     */
    @Override
    public void delete(CreditTransaction creditTransaction) {
        if (creditTransaction == null) {
            throw new IllegalArgumentException("Attendance cannot be null");
        }

        if (creditTransaction.getId() == null) {
            throw new IllegalArgumentException("The credit transaction does not have an id");
        }

        if (findById(creditTransaction.getId()) == null) {
            throw new NotFoundException("The credit transaction does not exist");
        }

        String deleteQuery = "DELETE FROM credit_transactions WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setLong(1, creditTransaction.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    /**
     * Updates an existing credit transaction in the database.
     *
     * @param creditTransaction the credit transaction to update
     * @throws IllegalArgumentException if the credit transaction or its ID, user, or type is null, or if the user or type does not have an ID
     * @throws NotFoundException if the credit transaction does not exist
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     */
    @Override
    public void update(CreditTransaction creditTransaction) {
        if (creditTransaction == null) {
            throw new IllegalArgumentException("Attendance cannot be null");
        }

        if (creditTransaction.getId() == null) {
            throw new IllegalArgumentException("The credit transaction does not have an id");
        }

        if (findById(creditTransaction.getId()) == null) {
            throw new NotFoundException("The credit transaction does not exist");
        }

        if (creditTransaction.getUser() == null) {
            throw new IllegalArgumentException("The user cannot be null");
        }

        if (creditTransaction.getUser().getId() == null) {
            throw new IllegalArgumentException("The user does not have an id");
        }

        if (creditTransaction.getCreditTransactionType().getId() == null) {
            throw new IllegalArgumentException("The credit transaction type does not have an id");
        }

        String updateQuery = "UPDATE credit_transactions SET user_id = ?, amount = ?, credit_transaction_type_id = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setLong(1, creditTransaction.getUser().getId());
            pstmt.setDouble(2, creditTransaction.getAmount());
            pstmt.setLong(3, creditTransaction.getCreditTransactionType().getId());
            pstmt.setLong(4, creditTransaction.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    /**
     * Finds a credit transaction by its ID.
     *
     * @param id the ID of the credit transaction to find
     * @return the found credit transaction, or null if not found
     * @throws IllegalArgumentException if the ID is null
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     */
    @Override
    public CreditTransaction findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery + " WHERE ct_id = ?")) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            return extractFromResultSet(rs).getFirst();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    /**
     * Finds all credit transactions in the database.
     *
     * @return a list of all credit transactions
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     */
    @Override
    public ArrayList<CreditTransaction> findAll() {
       try (Statement stmt = connection.createStatement()) {
           ResultSet rs = stmt.executeQuery(selectQuery);
           return extractFromResultSet(rs);
       } catch (SQLException e) {
           throw new CouldNotAccessDatabaseException("Database not accessible", e);
       }
    }
}

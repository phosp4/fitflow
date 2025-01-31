package sk.upjs.ics.daos.sql;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import sk.upjs.ics.daos.interfaces.CreditTransactionDao;
import sk.upjs.ics.entities.*;
import sk.upjs.ics.exceptions.CouldNotAccessFileException;
import sk.upjs.ics.exceptions.EntityCreationFailedException;
import sk.upjs.ics.exceptions.NotFoundException;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.springframework.jdbc.core.JdbcOperations;

/**
 * SQL implementation of the CreditTransactionDao interface.
 */
public class SQLCreditTransactionDao implements CreditTransactionDao {

    private final JdbcOperations jdbcOperations;

    /**
     * Constructs a new SQLCreditTransactionDao with the specified database connection.
     *
     * @param jdbcOperations the database connection via jdbc
     */
    public SQLCreditTransactionDao(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }


    /**
     * Extracts a list of CreditTransaction objects from the given ResultSet.
     *
     */
    private final ResultSetExtractor<ArrayList<CreditTransaction>> resultSetExtractor = rs -> {
        ArrayList<CreditTransaction> creditTransactions = new ArrayList<>();

        HashMap<Long, CreditTransaction> creditTransactionsProcessed= new HashMap<>();
        HashMap<Long, User> usersProcessed = new HashMap<>();
        HashMap<Long, Role> rolesProcessed = new HashMap<>();
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

            Long creditTransactionTypeId = rs.getLong("ctt_id");
            CreditTransactionType creditTransactionType = creditTransactionTypesProcessed.get(creditTransactionTypeId);
            if (creditTransactionType == null) {
                creditTransactionType = CreditTransactionType.fromResultSet(rs, "ctt_");
                creditTransactionTypesProcessed.put(creditTransactionTypeId, creditTransactionType);
            }

            if (user != null && role != null) {
                user.setRole(role);
            }

            if (creditTransaction != null && creditTransaction.getUser() == null) {
                creditTransaction.setUser(user);
            }

            if (creditTransaction != null && creditTransaction.getCreditTransactionType() == null) {
                creditTransaction.setCreditTransactionType(creditTransactionType);
            }
        }

        return creditTransactions;
    };
    
    private final String creditTransactionColumns = "ct.id AS ct_id, ct.amount AS ct_amount, ct.created_at AS ct_created_at, ct.updated_at AS ct_updated_at";
    String userColumns = "us.id AS us_id, us.email AS us_email, us.first_name AS us_first_name, " +
            "us.last_name AS us_last_name, us.credit_balance AS us_credit_balance, us.phone AS us_phone, " +
            "us.birth_date AS us_birth_date, us.active AS us_active, us.created_at AS us_created_at, us.updated_at AS us_updated_at";
    private final String roleColumns = "r.id AS r_id, r.name AS r_name";
    private final String creditTransactionTypeColumns = "ctt.id AS ctt_id, ctt.name AS ctt_name";

    private final String joins = "LEFT JOIN users us ON us.id = ct.user_id " +
            "LEFT JOIN roles r ON r.id = us.role_id " +
            "LEFT JOIN credit_transaction_types ctt ON ctt.id = ct.credit_transaction_type_id";

    private final String selectQuery = "SELECT " + creditTransactionColumns + ", " + userColumns + ", " + roleColumns + ", " + creditTransactionTypeColumns + " FROM credit_transactions ct " + joins;
    private final String insertQuery = "INSERT INTO credit_transactions (user_id, amount, credit_transaction_type_id) VALUES (?, ?, ?)";

    /**
     * Loads credit transactions from a CSV file and inserts them into the database.
     *
     * @param file the CSV file to load data from
     * @throws CouldNotAccessFileException if the file cannot be accessed
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

                jdbcOperations.update(connection -> {
                    PreparedStatement pstmt = connection.prepareStatement(insertQuery);
                    pstmt.setLong(1, Long.parseLong(parts[0]));
                    pstmt.setLong(2, Long.parseLong(parts[1]));
                    pstmt.setLong(3, Long.parseLong(parts[2]));
                    return pstmt;
                });
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
     * @throws EntityCreationFailedException if the creation of the credit transaction fails
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

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcOperations.update(connection -> {
            PreparedStatement pstmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            pstmt.setLong(1, creditTransaction.getUser().getId());
            pstmt.setLong(2, creditTransaction.getAmount());
            pstmt.setLong(3, creditTransaction.getCreditTransactionType().getId());
            return pstmt;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            return keyHolder.getKey().longValue();
        } else {
            throw new EntityCreationFailedException("Creating credit transaction failed, no ID obtained.");
        }
    }

    /**
     * Deletes a credit transaction from the database.
     *
     * @param creditTransaction the credit transaction to delete
     * @throws IllegalArgumentException if the credit transaction or its ID is null
     * @throws NotFoundException if the credit transaction does not exist
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

        jdbcOperations.update("DELETE FROM credit_transactions WHERE id = ?", creditTransaction.getId());
    }

    /**
     * Updates an existing credit transaction in the database.
     *
     * @param creditTransaction the credit transaction to update
     * @throws IllegalArgumentException if the credit transaction or its ID, user, or type is null, or if the user or type does not have an ID
     * @throws NotFoundException if the credit transaction does not exist
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

        jdbcOperations.update(connection -> {
            PreparedStatement pstmt = connection.prepareStatement(updateQuery);
            pstmt.setLong(1, creditTransaction.getUser().getId());
            pstmt.setLong(2, creditTransaction.getAmount());
            pstmt.setLong(3, creditTransaction.getCreditTransactionType().getId());
            pstmt.setLong(4, creditTransaction.getId());
            return pstmt;
        });
    }

    /**
     * Finds a credit transaction by its ID.
     *
     * @param id the ID of the credit transaction to find
     * @return the found credit transaction
     * @throws IllegalArgumentException if the ID is null
     */
    @Override
    public CreditTransaction findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        ArrayList<CreditTransaction> creditTransactions = jdbcOperations.query(selectQuery + " WHERE ct_id = ?", resultSetExtractor, id);

        if (creditTransactions == null || creditTransactions.isEmpty()) {
            throw new NotFoundException("Credit transaction with id " + id + " not found!");
        }

        return creditTransactions.getFirst();
    }

    /**
     * Finds all credit transactions in the database.
     *
     * @return a list of all credit transactions
     */
    @Override
    public ArrayList<CreditTransaction> findAll() {
        ArrayList<CreditTransaction> creditTransactions = jdbcOperations.query(selectQuery, resultSetExtractor);

        if (creditTransactions == null) {
            throw new NotFoundException("Error while finding transactions!");
        }

        return creditTransactions;
    }
}

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

public class SQLCreditTransactionDao implements CreditTransactionDao {

    private final Connection connection;

    public SQLCreditTransactionDao(Connection connection) {
        this.connection = connection;
    }
    
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

        if (creditTransactions.isEmpty()) {
            throw new NotFoundException("No credit transactions found");
        }
        
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

    @Override
    public void create(CreditTransaction creditTransaction) {
        if (creditTransaction == null) {
            throw new IllegalArgumentException("Attendance cannot be null");
        }
        
        if (creditTransaction.getId() != null) {
            throw new IllegalArgumentException("The credit transaction already has an id");
        }

        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            pstmt.setLong(1, creditTransaction.getUser().getId());
            pstmt.setDouble(2, creditTransaction.getAmount());
            pstmt.setLong(3, creditTransaction.getCreditTransactionType().getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }

    }

    @Override
    public void delete(CreditTransaction creditTransaction) {
        if (creditTransaction == null) {
            throw new IllegalArgumentException("Attendance cannot be null");
        }

        String deleteQuery = "DELETE FROM credit_transactions WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setLong(1, creditTransaction.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    @Override
    public void update(CreditTransaction creditTransaction) {
        if (creditTransaction == null) {
            throw new IllegalArgumentException("Attendance cannot be null");
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

    @Override
    public CreditTransaction findById(Long id) {
        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery + " WHERE ct_id = ?")) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            return extractFromResultSet(rs).getFirst();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

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

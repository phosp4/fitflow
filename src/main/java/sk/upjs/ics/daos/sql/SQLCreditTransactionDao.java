package sk.upjs.ics.daos.sql;

import sk.upjs.ics.daos.interfaces.CreditTransactionDao;
import sk.upjs.ics.entities.*;
import sk.upjs.ics.exceptions.CouldNotAccessDatabaseException;
import sk.upjs.ics.exceptions.NotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class SQLCreditTransactionDao implements CreditTransactionDao {

    private final Connection connection;

    public SQLCreditTransactionDao(Connection connection) {
        this.connection = connection;
    }
    
    String creditTransactionColumns = "ct.id AS ct_id, ct.amount AS ct_amount, ct.created_at AS ct_created_at, ct.updated_at AS ct_updated_at";
    String userColumns = "us.id AS us_id, us.email AS us_email, us.first_name AS us_first_name, " +
            "us.last_name AS us_last_name, us.credit_balance AS us_credit_balance, us.phone AS us_phone, " +
            "us.birth_date AS us_birth_date, us.active AS us_active, us.created_at AS us_created_at, us.updated_at AS us_updated_at";
    String roleColumns = "r.id AS r_id, r.name AS r_name";
    String specializationColumns = "tsp.id AS tsp_id, tsp.name AS tsp_name";
    String creditTransactionTypeColumns = "ctt.id AS ctt_id, ctt.name AS ctt_name";

    String joins = "LEFT JOIN users us ON us.id = ct.user_id " +
            "LEFT JOIN roles r ON r.id = us.role_id " +
            "LEFT JOIN trainers_have_specializations ts ON ts.trainer_id = us.id " +
            "LEFT JOIN trainer_specializations tsp ON tsp.id = ts.specialization_id " +
            "LEFT JOIN credit_transaction_types ctt ON ctt.id = ct.credit_transaction_type_id";
    
    @Override
    public void create(CreditTransaction creditTransaction) {
        String insertQuery = "INSERT INTO credit_transactions (user_id, amount, credit_transaction_type_id) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            pstmt.setLong(1, creditTransaction.getUser().getId());
            pstmt.setDouble(2, creditTransaction.getAmount());
            pstmt.setLong(3, creditTransaction.getCreditTransactionType().getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible");
        }

    }

    @Override
    public void delete(CreditTransaction creditTransaction) {
        String deleteQuery = "DELETE FROM credit_transactions WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setLong(1, creditTransaction.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible");
        }
    }

    @Override
    public void update(CreditTransaction creditTransaction) {
        String updateQuery = "UPDATE credit_transactions SET user_id = ?, amount = ?, credit_transaction_type_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setLong(1, creditTransaction.getUser().getId());
            pstmt.setDouble(2, creditTransaction.getAmount());
            pstmt.setLong(3, creditTransaction.getCreditTransactionType().getId());
            pstmt.setLong(4, creditTransaction.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible");
        }
    }

    @Override
    public CreditTransaction findById(Long id) {
        String query = "SELECT " + creditTransactionColumns + ", " + userColumns + ", " + creditTransactionTypeColumns + " FROM credit_transactions ct " + joins;

        try (PreparedStatement pstmt = connection.prepareStatement(query + " WHERE id = ?")) {
            CreditTransaction creditTransaction = new CreditTransaction();
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                throw new NotFoundException("Credit transaction with id " + id + " not found");
            }

            return CreditTransaction.fromResultSet(rs);

        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible");
        }
    }

    @Override
    public ArrayList<CreditTransaction> findAll() {
       try (Statement stmt = connection.createStatement()) {
           String query = "SELECT " + creditTransactionColumns + ", " + userColumns + ", " + roleColumns + ", " + specializationColumns + ", " + creditTransactionTypeColumns + " FROM credit_transactions ct " + joins;
           ResultSet rs = stmt.executeQuery(query);

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
       } catch (SQLException e) {
           throw new CouldNotAccessDatabaseException("Database not accessible");
       }
    }
}

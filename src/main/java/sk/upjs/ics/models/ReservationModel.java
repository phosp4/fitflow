package sk.upjs.ics.models;

import sk.upjs.ics.Factory;
import sk.upjs.ics.daos.interfaces.CreditTransactionDao;
import sk.upjs.ics.daos.interfaces.ReservationDao;
import sk.upjs.ics.daos.interfaces.TrainerIntervalDao;
import sk.upjs.ics.daos.interfaces.UserDao;
import sk.upjs.ics.entities.CreditTransaction;
import sk.upjs.ics.entities.Reservation;
import sk.upjs.ics.entities.TrainerInterval;
import sk.upjs.ics.entities.User;
import sk.upjs.ics.security.Auth;
import sk.upjs.ics.security.AuthDao;
import sk.upjs.ics.security.Principal;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReservationModel {

    private final UserDao userDao = Factory.INSTANCE.getUserDao();
    private final TrainerIntervalDao trainerIntervalDao = Factory.INSTANCE.getTrainerIntervalDao();
    private final ReservationDao reservationDao = Factory.INSTANCE.getReservationDao();
    private final CreditTransactionDao transactionDao = Factory.INSTANCE.getCreditTransactionDao();

    private final AuthDao authDao = Factory.INSTANCE.getAuthDao();
    private final Principal principal = Auth.INSTANCE.getPrincipal();

    private final Long userId = principal.getId();

    private final UserModel userModel = new UserModel();

    public void createReservation(User trainer, String date, String startTime, String note) {
        // Prepare the reservation
        Reservation reservation = new Reservation();
        reservation.setCustomer(trainer);
        reservation.setReservationStatus(Factory.INSTANCE.getReservationStatusDao().findById(1L)); // pending
        reservation.setNoteToTrainer(note);

        // Create a credit transaction
        CreditTransaction creditTransaction = new CreditTransaction();
        creditTransaction.setUser(userModel.getUserById(userId));
        creditTransaction.setAmount(-1);
        creditTransaction.setCreditTransactionType(Factory.INSTANCE.getTransactionTypeDao().findById(1L)); // visit??
        transactionDao.create(creditTransaction);
        reservation.setCreditTransaction(creditTransaction);

        // Create a trainer interval
        TrainerInterval trainerInterval = new TrainerInterval();
        trainerInterval.setTrainer(trainer);
        trainerInterval.setDay(LocalDate.parse(date));
        trainerInterval.setStartTime(LocalTime.parse(startTime));
        LocalTime endTime = LocalTime.parse(startTime).plusHours(1);
        trainerInterval.setEndTime(endTime);
        trainerInterval.setReservation(reservation);
        trainerIntervalDao.create(trainerInterval);

        // Create the reservation
        reservationDao.create(reservation);
    }
}

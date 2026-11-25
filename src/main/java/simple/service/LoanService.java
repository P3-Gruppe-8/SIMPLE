package simple.service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.ArrayList;

import simple.domain.dao.LoanableDAO;
import simple.domain.itemmanagement.Loanable;
import simple.domain.repository.LoanAgreementRepository;
import simple.domain.transactionmanagement.LoanAgreementRegistration;
import simple.domain.transactionmanagement.LoanAgreement;

public class LoanService {

    LoanAgreementRepository repo;
    LoanableDAO dao;

    public LoanService(LoanAgreementRepository repo, LoanableDAO dao) {
        this.repo = repo;
        this.dao = dao;
    }

    public void receive(String barcode) {
        repo.softDelete(barcode);
    }

    public boolean lend(LoanAgreementRegistration loanAgreementRegistration) {

        Optional<Loanable> loanable = dao.findByBarcode(loanAgreementRegistration.loanable_barcode());

        if (loanable.isEmpty())
            return false;

        if (loanable.get().deleted() != null)
            return false;

        repo.create(loanAgreementRegistration, loanable.get().id());

        return true;
    }

    public boolean changeDate(OffsetDateTime newDate, int loanAgreementId) {

        if (newDate.isAfter(OffsetDateTime.now()))
            repo.changeReturnDate(newDate, loanAgreementId);
        else
            return false;

        return true;
    }

    public ArrayList<LoanAgreement> getAllLoans(int officeId) {

        return repo.findAll(officeId);

    }
}

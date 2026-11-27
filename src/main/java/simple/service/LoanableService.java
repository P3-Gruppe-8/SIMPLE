package simple.service;

import java.util.Optional;
import java.util.ArrayList;

import simple.domain.dao.LoanableDAO;
import simple.domain.itemmanagement.Loanable;
import simple.domain.itemmanagement.LoanableRegistration;

public class LoanableService {

    LoanableDAO dao;

    public LoanableService(LoanableDAO dao) {
        this.dao = dao;
    }

    public Optional<Loanable> getLoanable(String barcode) {

        return dao.findByBarcode(barcode);
    }

    public void create(LoanableRegistration loanableRegistration) {

        ArrayList<Loanable> loanables = dao.findAllByDescriptor(loanableRegistration.descriptorId());

        int expected = 1;


        for (Loanable loanable : loanables) {
            if (loanable.unitNumber() == expected)
                expected++;
            else break;
        }

        dao.create(loanableRegistration, expected);
    }
}

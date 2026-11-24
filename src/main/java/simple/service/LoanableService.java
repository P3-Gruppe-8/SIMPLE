package simple.service;

import java.util.Optional;
import simple.domain.dao.LoanableDAO;
import simple.domain.itemmanagement.Loanable;

public class LoanableService {

    LoanableDAO dao;

    public LoanableService(LoanableDAO dao) {
        this.dao = dao;
    }

    public Optional<Loanable> getLoanable(String barcode) {

        return dao.findByBarcode(barcode);
    }
}

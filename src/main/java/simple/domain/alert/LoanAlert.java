package simple.domain.alert;

import simple.domain.transactionmanagement.LoanAgreement;

public record LoanAlert(
                int id,
                Urgency urgency,
                boolean active,
                boolean muted,
                int minQuantity,
                LoanAgreement loanAgreement) {
}

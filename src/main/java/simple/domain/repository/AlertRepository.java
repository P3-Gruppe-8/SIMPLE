package simple.domain.repository;

import simple.domain.alert.AlertOverview;

public interface AlertRepository {
    AlertOverview findActiveAlertsByOffice(int officeId);
}

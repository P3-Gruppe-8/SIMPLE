package simple.domain.dao;

import java.util.List;
import simple.domain.LogEntry;

public interface LogEntriesDAO {
    public void create(LogEntry logEntry);

    public List<LogEntry> findAll();

    public List<LogEntry> findTypeOf(String type);
}

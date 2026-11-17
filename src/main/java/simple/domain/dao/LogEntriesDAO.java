package simple.domain.dao;

import simple.domain.LogEntry;
import java.util.List;

public interface LogEntriesDAO {
    public void create(LogEntry logEntry);

    public List<LogEntry> findAll();

    public List<LogEntry> findTypeOf(String type);
}

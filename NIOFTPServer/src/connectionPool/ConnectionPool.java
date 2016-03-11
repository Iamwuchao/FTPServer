package connectionPool;

import java.sql.Connection;

public interface ConnectionPool {
	Connection getConnection();
	void giveBackConnection(Connection connection);
	void close();
	void Init();
}

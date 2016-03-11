package connectionPool;

import java.sql.Connection;

public interface ConnectionFactory {
		Connection getConnection();
		void close();
}

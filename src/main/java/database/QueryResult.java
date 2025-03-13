package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QueryResult
{
	private final Connection connection;
	private final ResultSet resultSet;

	public QueryResult(final Connection connection, final ResultSet resultSet)
	{
		this.connection = connection;
		this.resultSet = resultSet;
	}

	public Connection getConnection()
	{
		return connection;
	}

	public ResultSet getResultSet()
	{
		return resultSet;
	}

	public void close()
	{
		try
		{
			if (resultSet != null)
				resultSet.close();
			if (connection != null)
				connection.close();
		} catch (SQLException e) {}
	}
}
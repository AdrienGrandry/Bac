package ressources.dataBase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
* @author Grandry Adrien
*/
public class QueryResult implements AutoCloseable
{
	/**
	 * Variable de la connexion sql.
	 */
	private final Connection connection;
	/**
	 * Variable de la réponse sql.
	 */
	private final ResultSet resultSet;

	/**
	 * Classe pour récuperer le resultat
	 * de la requete sql.
	 */
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
	
	/**
	 * Fonction pour fermer le
	 * resultset et le connnexion
	 */
	public void close()
	{
		try
		{
			if (resultSet != null)
			{
				resultSet.close();
			}
			if (connection != null)
			{
				connection.close();
			}
		} catch (SQLException e) {}
	}
}
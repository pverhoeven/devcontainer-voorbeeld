package nl.voorbeeld.applicatietesten;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Directe JDBC-toegang tot de Postgres database van de applicatie.
 */
public class Database {

    public void voerUit(String sql, Object... parameters) {
        try (Connection connectie = verbind(); var statement = connectie.prepareStatement(sql)) {
            bind(statement, parameters);
            statement.execute();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public List<Map<String, Object>> query(String sql, Object... parameters) {
        try (Connection connectie = verbind(); var statement = connectie.prepareStatement(sql)) {
            bind(statement, parameters);
            try (var resultSet = statement.executeQuery()) {
                var metaData = resultSet.getMetaData();
                List<Map<String, Object>> rijen = new ArrayList<>();
                while (resultSet.next()) {
                    Map<String, Object> rij = new LinkedHashMap<>();
                    for (int i = 1; i <= metaData.getColumnCount(); i++) {
                        rij.put(metaData.getColumnName(i), resultSet.getObject(i));
                    }
                    rijen.add(rij);
                }
                return rijen;
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Connection verbind() throws SQLException {
        return DriverManager.getConnection(Configuratie.DB_URL, Configuratie.DB_USER, Configuratie.DB_PASSWORD);
    }

    private static void bind(PreparedStatement statement, Object... parameters) throws SQLException {
        for (int i = 0; i < parameters.length; i++) {
            statement.setObject(i + 1, parameters[i]);
        }
    }
}

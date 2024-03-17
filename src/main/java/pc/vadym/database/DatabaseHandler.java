package pc.vadym.database;

import java.sql.*;
import java.util.Arrays;
import java.util.stream.Collectors;

public class DatabaseHandler {

    public static int storeFullExpression(String fullExpression) {
        int expressionId = 0;
        try {
            Connection connection = DatabaseConnection.getInstance().getExisitingConnection();
            String insertExpressionQuery = "INSERT INTO Expression (expression) VALUES (?)";

            try (PreparedStatement statement = connection.prepareStatement(insertExpressionQuery, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, fullExpression);
                statement.executeUpdate();

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        expressionId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Failed to get ID of inserted expression");
                    }
                }
            }

            if (expressionId == 0) {
                throw new SQLException("Failed to get real ID.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("The expression: " + fullExpression + " was stored in the database");
        return expressionId;
    }

    public static int storeRootOfExpression(double root) {
        int rootId = 0;
        try {
            Connection connection = DatabaseConnection.getInstance().getExisitingConnection();
            String insertExpressionQuery = "INSERT INTO Root (value) VALUES (?)";

            try (PreparedStatement statement = connection.prepareStatement(insertExpressionQuery, Statement.RETURN_GENERATED_KEYS)) {
                statement.setDouble(1, root);
                statement.executeUpdate();

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        rootId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Failed to get ID of inserted root");
                    }
                }
            }
            if (rootId == 0) {
                throw new SQLException("Failed to get real ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("The root = " + root + " was stored in the database");
        return rootId;
    }

    public static void connectedInsertedExpressionAndRootByIds(int expressionId, int rootId) {
        try {
            Connection connection = DatabaseConnection.getInstance().getExisitingConnection();
            String sql = "INSERT INTO Expression_Root (expression_id, root_id) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, expressionId);
                statement.setInt(2, rootId);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("The expression and root are connected.");
    }

    public static void findAllExpressionThatHaveAnyOfTheRoot(double[] roots) {
        try {
            Connection connection = DatabaseConnection.getInstance().getExisitingConnection();
            String findQuery = "SELECT DISTINCT e.id, e.expression " +
                    "FROM Expression e " +
                    "JOIN Expression_Root er ON e.id = er.expression_id " +
                    "JOIN Root r ON er.root_id = r.id " +
                    "WHERE r.value IN (" + (Arrays.stream(roots)
                    .mapToObj(root -> "?")
                    .collect(Collectors.joining(", ")))
                    + ")";
            try (PreparedStatement statement = connection.prepareStatement(findQuery)) {
                for (int i = 0; i < roots.length; ++i) {
                    statement.setDouble(i + 1, roots[i]);
                }

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        int expressionId = resultSet.getInt("id");
                        String expression = resultSet.getString("expression");
                        System.out.println("Expression ID: " + expressionId + ", Expression: " + expression + ". Has one of roots: " + Arrays.toString(roots));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void findAllExpressionThatHaveOnlyOneRoot() {
        try {
            Connection connection = DatabaseConnection.getInstance().getExisitingConnection();
            String query = "SELECT e.id, e.expression " +
                    "FROM Expression e " +
                    "JOIN (SELECT expression_id, COUNT(root_id) AS root_count " +
                    "      FROM Expression_Root " +
                    "      GROUP BY expression_id " +
                    "      HAVING COUNT(root_id) = 1) AS er ON e.id = er.expression_id";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        int expressionId = resultSet.getInt("id");
                        String expression = resultSet.getString("expression");
                        System.out.println("Expression ID: " + expressionId + ", Expression: " + expression + ". With one root.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

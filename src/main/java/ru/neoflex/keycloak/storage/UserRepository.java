package ru.neoflex.keycloak.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.UserModel;
import ru.neoflex.keycloak.util.Constants;
import ru.neoflex.keycloak.util.Converters;

import javax.annotation.PreDestroy;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class UserRepository implements AutoCloseable {
    private final HikariDataSource dataSource;
    private static final String DB_DRIVER = "org.postgresql.Driver";

    public UserRepository(ComponentModel model) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(model.get(Constants.UserStorage.URL));
        config.setUsername(model.get(Constants.UserStorage.USERNAME));
        config.setPassword(model.get(Constants.UserStorage.PASSWORD));
        config.setDriverClassName(DB_DRIVER);

        //TODO:
        // вынести в конфиг
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        this.dataSource = new HikariDataSource(config);

    }


    public ExteranalUser getUserByUsername(String username) {
        String sql = "select *" +
                "from users " +
                "where username = ?";
        try (Connection connection = getConnection();
             PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, username);
            st.execute();
            ResultSet rs = st.getResultSet();
            if (rs.next()) {
                return Converters.mapToExternalUser(rs);
            } else {
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Database error:" + ex.getMessage(), ex);
        }
    }

    public ExteranalUser getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Converters.mapToExternalUser(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error:" + e.getMessage(), e);
        }
        return null;
    }

    public List<ExteranalUser> getAllUsers() {
        List<ExteranalUser> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(Converters.mapToExternalUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error:" + e.getMessage(), e);
        }
        return users;
    }

    public void save(ExteranalUser user) {
        String sql = "INSERT INTO users (" + prepareFieldsForInsert() +
                ") VALUES (?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, Constants.KeycloakConfiguration.DEFAULT_USER_PASSWORD);
            stmt.setBoolean(3, true);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Database error:" + e.getMessage(), e);
        }
    }

   public boolean updateEntity(UserModel userModel) {
        ExteranalUser user = Converters.mapToExternalUser(userModel);
        String sql = "UPDATE users SET " +
                Constants.dbColumn.EMAIL +
                "= ?, " +
                Constants.dbColumn.FIRST_NAME +
                " = ?, " +
                Constants.dbColumn.LAST_NAME +
                " = ?, " +
                Constants.dbColumn.BIRTHDAY +
                " = ?, " +
                Constants.dbColumn.SESSION_ID +
                " = ?, " +
                Constants.dbColumn.MANZANA_ID +
                " = ? " +
                "WHERE " +
                Constants.dbColumn.USERMAME +
                " = ?";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getFirstName());
            stmt.setString(3, user.getLastName());
            stmt.setString(4, user.getBirthDate());
            stmt.setString(5, user.getSessionId());
            stmt.setString(6, user.getManzanaId());
            stmt.setString(7, user.getUsername());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Database error:" + e.getMessage(), e);
        }
    }

    public boolean updateOTP(UserModel userModel) {
        ExteranalUser user = Converters.mapToExternalUser(userModel);
        String sql = "UPDATE users SET " +
                Constants.dbColumn.SMS_CODE +
                "= ?, " +
                Constants.dbColumn.EXPIRY_DATE +
                " = ? " +
                "WHERE " +
                Constants.dbColumn.USERMAME +
                " = ?";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getSmsCode());
            stmt.setString(2, user.getExpiryDate());
            stmt.setString(3, user.getUsername());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Database error:" + e.getMessage(), e);
        }
    }

    public boolean delete(String id) {
        String sql = "DELETE FROM users WHERE username = ?";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Database error:" + e.getMessage(), e);
        }
    }


    @Override
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    @PreDestroy
    public void cleanup() {
        try {
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private String prepareFieldsForInsert() {
        StringBuilder builder = new StringBuilder();
        return builder
                .append(Constants.dbColumn.USERMAME + ",")
                .append(Constants.dbColumn.PASSWORD + ",")
                .append(Constants.dbColumn.ENABLED)
                .toString();


    }
}

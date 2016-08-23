

// For convenience, always static import your generated tables and
// jOOQ functions to decrease verbosity:
import static JooqMap.Tables.AUTHOR;

import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import JooqMap.tables.records.AuthorRecord;
import org.jooq.*;
import org.jooq.impl.*;

public class JOOQMain {

    /**
     * @param args
     */
    public static void main(String[] args) {
        new JOOQMain().Start();
    }

    public void Start()
    {

        String userName = "root";
        String password = "cp8482617";
        String url = "jdbc:mysql://localhost:3306/library";
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            Logger.getGlobal().log(Level.SEVERE, "", e);
            return;
        }
        // Connection is the only JDBC resource that we need
        // PreparedStatement and ResultSet are handled by jOOQ, internally
        try (Connection conn = DriverManager.getConnection(url, userName, password)) {
            DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
            Optional<Table<?>> table = this.GetTable(create, AUTHOR.getName());
            if (!table.isPresent())
            {
                create.createTable(AUTHOR).columns(AUTHOR.fields()).execute();
            }
            Result<Record> result = create.select().from(AUTHOR).fetch();

            for (Record r : result) {
                Integer id = r.getValue(AUTHOR.ID);
                String firstName = r.getValue(AUTHOR.FIRST_NAME);
                String lastName = r.getValue(AUTHOR.LAST_NAME);

                System.out.println("ID: " + id + " first name: " + firstName + " last name: " + lastName);
            }
        }

        // For the sake of this tutorial, let's keep exception handling simple
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Optional<Table<?>> GetTable(DSLContext create, String tableName)
    {
        return create.meta().getTables().stream().filter(p -> p.getName().equals(tableName)).findFirst();
    }

    public void AddMissingCol(DSLContext create, String tableName)
    {
        create.createTable(tableName).
                column(AUTHOR.ID).
                column(AUTHOR.FIRST_NAME).
                column(AUTHOR.LAST_NAME).
                execute();
    }
}
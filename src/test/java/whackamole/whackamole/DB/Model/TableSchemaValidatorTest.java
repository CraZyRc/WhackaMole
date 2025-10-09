package whackamole.whackamole.DB.Model;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import whackamole.whackamole.DB.SQLite;

import java.io.File;
import java.sql.SQLException;

import org.assertj.core.api.JUnitBDDSoftAssertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
public class TableSchemaValidatorTest {
    final static File DBfile = new File("./test/SchemaValidation.db");
    final static SQLite sql = SQLite.getInstance();
    static public final JUnitBDDSoftAssertions softly = new JUnitBDDSoftAssertions();

    @BeforeAll
    public static void SetupDB()
    {
        sql.setUrl("jdbc:sqlite:" + DBfile.toPath());
        if (DBfile.exists()) {
            DBfile.delete();
        }
    }

    @AfterAll
    public static void ClearDB()
    {
        sql.setUrl("");
        if (DBfile.exists()) {
            DBfile.delete();
        }
    }
    
    @AfterEach
    public void assertAll() {
        softly.assertAll();
    }

    @Test
    @Order(1)
    public void TableIsCreated() throws SQLException
    {
        var table = TableCreator.CreateTable(sql, "TestTableSchemaValidator", new Column[] {
            new Column<Integer>("key", Integer.class)
        });
        var data = sql.executeQuery("select count(name) from sqlite_schema where name = ?", table.GetName());
        softly.then(data.getInt("count(name)")).as("Table has not been created in the DB").isEqualTo(1);
    }
    
    @Test
    @Order(2)
    public void TableAddColumn() throws SQLException
    {
        var table = TableCreator.CreateTable(sql, "TestTableSchemaValidator", new Column[] {
            new Column<Integer>("key", Integer.class),
            new Column<String>("name", String.class)
        });
        var data = sql.executeQuery("select count(name) from pragma_table_info(?) where name = ?", table.GetName(), "name");
        softly.then(data.getInt("count(name)")).as("Table has not added column name in DB").isEqualTo(1);
    }
    
    @Test
    @Order(3)
    public void TableRemoveColumn() throws SQLException
    {
        var table = TableCreator.CreateTable(sql, "TestTableSchemaValidator", new Column[] {
            new Column<Integer>("key", Integer.class),
        });
        var data = sql.executeQuery("select count(name) from pragma_table_info(?) where name = ?", table.GetName(), "name");
        softly.then(data.getInt("count(name)")).as("Table has not added column name in DB").isEqualTo(0);
    }
}

package by.bsu.mmf.badthrowgame.datasource;

import com.mysql.cj.jdbc.MysqlDataSource;

import javax.activation.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


/**
 * Created by Lenovo on 08/16/2017.
 */
public class DataSourceFactory {
    public static MysqlDataSource getDataSource() {
        Properties properties = new Properties();
        FileInputStream fis = null;
        MysqlDataSource mysqlDataSource = null;
        try {
            fis = new FileInputStream("db.properties");
            properties.load(fis);
            mysqlDataSource = new MysqlDataSource();
            mysqlDataSource.setURL(properties.getProperty("MYSQL_DB_URL"));
            mysqlDataSource.setUser(properties.getProperty("MYSQL_DB_USERNAME"));
            mysqlDataSource.setPassword(properties.getProperty("MYSQL_DB_PASSWORD"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mysqlDataSource;
    }
}

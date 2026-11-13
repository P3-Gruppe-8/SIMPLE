package simple.dataaccess.jdbc;

import simple.dataaccess.config.Databaseconnection;
import java.sql.*;

public class jdbchej {

    public jdbchej() {

        System.out.println("HJE MED DIG HVOR ER DU1");
        Databaseconnection db = new Databaseconnection();

        try {
            System.out.println("HJE MED DIG HVOR ER Du AOSÆFNMAÆDFM 2");
            Connection conn = db.getDbConnection();
            System.out.println("HJE MED DIG HVOR ER DU DFSDF 4");
            Statement st = conn.createStatement();
            System.out.println("HJE MED DIG HVOR ER DU FDFD F 5");
            String sql = "INSERT INTO offices (office_address, office_name) VALUES ('Allern', 'STATE PROPERTY')";
            ResultSet hej = st.executeQuery(sql);
            System.out.println(hej);

        } catch (SQLException e) {
            System.out.println(e);
        }

    }
}

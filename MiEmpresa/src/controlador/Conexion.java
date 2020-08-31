package controlador;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class Conexion {

    static Connection conexion = null;

    public static Connection getConexion() {

        String Url = "jdbc:sqlserver://localhost:1433;databaseName=MiEmpresa;user=omarquinigo;password=Febrero_14";

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            conexion = DriverManager.getConnection(Url);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al conectar a BD: \n" + e);
        }
        return conexion;
    }

}

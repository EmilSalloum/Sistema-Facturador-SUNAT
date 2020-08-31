
package modelo;

import controlador.Conexion;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

public class Cliente {

    public static ResultSet Consulta(String query) {
        try {
            Connection con = Conexion.getConexion();
            Statement stmt;
            stmt = con.createStatement();
            ResultSet respuesta = stmt.executeQuery(query);
            return respuesta;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: \n" + e);
        }
        return null;
    }

    public static void Registrar(String ruc, String razonSocial, String direccion) throws SQLException {
        try {
            Connection con = Conexion.getConexion();
            Statement stmt = con.createStatement();
            String sql = "insert into cliente (ruc,razonSocial,direccion) "
                    + "values ('" + ruc + "','" + razonSocial + "','" + direccion + "');";
            stmt.execute(sql);
            con.close();
            JOptionPane.showMessageDialog(null, "Cliente registrado");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: \n" + e);
        }
    }
    
    public static void Actualizar(String id, String ruc, String razonSocial, String direccion) throws SQLException {
        try {
            Connection con = Conexion.getConexion();
            Statement stmt = con.createStatement();
            String sql = "update cliente set "
                    + "ruc = '" + ruc + "', "
                    + "razonSocial = '" + razonSocial + "', "
                    + "direccion = '" + direccion + "' "
                    + "where id = '" + id + "';";
            stmt.execute(sql);
            con.close();
            JOptionPane.showMessageDialog(null, "Cliente actualizado");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: \n" + e);
        }
    }

}

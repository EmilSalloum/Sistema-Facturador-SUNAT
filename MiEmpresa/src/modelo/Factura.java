
package modelo;

import controlador.Conexion;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javax.swing.JOptionPane;

public class Factura {

    public static ResultSet Consulta(String query) {
        try {
            Connection con = Conexion.getConexion();
            Statement stmt;
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            return rs;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: \n" + e);
        }
        return null;
    }

    public static void Registrar(int idCliente, String nombre, String fecha,
            String moneda, Double igv, Double importe) throws SQLException {
        try {
            Connection con = Conexion.getConexion();
            Statement stmt = con.createStatement();
            String sql = "insert into factura (idCliente,nombre,fecha,moneda,igv,importe) "
                    + "values ('" + idCliente + "','" + nombre + "','" + fecha + "',"
                    + "'" + moneda + "','" + igv + "','" + importe + "');";
            stmt.execute(sql);
            con.close();
            JOptionPane.showMessageDialog(null, "Factura registrada");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: \n" + e);
        }
    }
    
    public static String getRUC(){
        String ruc = "xxxxxxxxxxx";
        return ruc;
    }
    
    public static String getRutaAP(String nombre, String ext){
        String ruta = "C:\\SFS_v1.3.3\\sunat_archivos\\sfs\\DATA\\"+getRUC()+"-01-" + nombre + ext;
        return ruta;
    }
    
    public static String getRutaRepo(String nombre){
        String ruta = "C:\\SFS_v1.3.3\\sunat_archivos\\sfs\\REPO\\"+getRUC()+"-01-" + nombre + ".pdf";
        return ruta;
    }
    
    public static String getFechaSunat() {
        //se crea objeto de tipo Date
        Date fecha = new Date();
        //envio al text field al mismo tiempo se cambia el formato
        String fechaSunat = (new SimpleDateFormat("yyyy-MM-dd").format(fecha));
        return fechaSunat;
    } 
    
    public static String getHoraEmision(){
        LocalTime hora = LocalTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        return hora.format(dtf);
    }
    
    public static String ConvertirNumLetras(String numero, String moneda) {
        String monedaTexto;
        String importeTexto;
        ConvertirNumeroLetras cnl = new ConvertirNumeroLetras();
        if (moneda.equalsIgnoreCase("PEN")) {
            monedaTexto = "SOLES.";
        } else if (moneda.equalsIgnoreCase("USD")) {
            monedaTexto = "DÓLARES AMERICANOS.";
        } else {
            monedaTexto = "";
        }
        importeTexto = (cnl.Convertir(numero, band()));
        String texto = importeTexto + monedaTexto;
        return texto;
    }
    
    //para el numero en letras
    private static boolean band() {
        if (Math.random() > .5) {
            return true;
        } else {
            return false;
        }
    }

}

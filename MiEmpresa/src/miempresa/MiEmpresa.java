
package miempresa;

import javax.swing.UIManager;
import vista.Login;

public class MiEmpresa {

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Login login = new Login();
        login.setVisible(true);

    }

}


package vista;

import java.awt.FlowLayout;
import vista.cliente.ClienteListar;

public class FormularioPrincipal extends javax.swing.JFrame {

    public FormularioPrincipal() {
        initComponents();
        ConfigurarVentana();
        menItComprobantes.doClick();
    }

    void ConfigurarVentana() {
        //alineación centrada
        this.setLayout(new FlowLayout(0));
        //posiciono el frame al centro de la pantalla
        this.setLocationRelativeTo(null);
        //desactiva el cambio de tamaño de la ventana
        this.setResizable(false);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuBar1 = new javax.swing.JMenuBar();
        menArchivo = new javax.swing.JMenu();
        menItSalir = new javax.swing.JMenuItem();
        menEditar = new javax.swing.JMenu();
        menAdministrar = new javax.swing.JMenu();
        menItComprobantes = new javax.swing.JMenuItem();
        menItClientes = new javax.swing.JMenuItem();
        menAcercaDe = new javax.swing.JMenu();
        menItVersion = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        menArchivo.setText("File");

        menItSalir.setText("Salir");
        menItSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menItSalirActionPerformed(evt);
            }
        });
        menArchivo.add(menItSalir);

        jMenuBar1.add(menArchivo);

        menEditar.setText("Edit");
        jMenuBar1.add(menEditar);

        menAdministrar.setText("Administrar");

        menItComprobantes.setText("Comprobantes");
        menItComprobantes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menItComprobantesActionPerformed(evt);
            }
        });
        menAdministrar.add(menItComprobantes);

        menItClientes.setText("Clientes");
        menItClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menItClientesActionPerformed(evt);
            }
        });
        menAdministrar.add(menItClientes);

        jMenuBar1.add(menAdministrar);

        menAcercaDe.setText("Acerca de");

        menItVersion.setText("Versión");
        menItVersion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menItVersionActionPerformed(evt);
            }
        });
        menAcercaDe.add(menItVersion);

        jMenuBar1.add(menAcercaDe);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 900, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 479, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menItSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menItSalirActionPerformed
        System.exit(0);
    }//GEN-LAST:event_menItSalirActionPerformed

    private void menItClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menItClientesActionPerformed
        ClienteListar cl = new ClienteListar();
        //Si ya está el otro Jpanel añadido al contenedor, entonces se elimina
        this.getContentPane().removeAll();
        this.repaint();
        //Agregamos la instancia al JFrame, con un layout al centro
        this.add(cl);
        //Hacemos que el JFrame tenga el tamaño de todos sus elementos
        this.pack();
    }//GEN-LAST:event_menItClientesActionPerformed

    private void menItComprobantesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menItComprobantesActionPerformed
        Comprobantes c = new Comprobantes();
        //Si ya está el otro Jpanel añadido al contenedor, entonces se elimina
        this.getContentPane().removeAll();
        this.repaint();
        //Agregamos la instancia al JFrame, con un layout al centro
        this.add(c);
        //Hacemos que el JFrame tenga el tamaño de todos sus elementos
        this.pack();
    }//GEN-LAST:event_menItComprobantesActionPerformed

    private void menItVersionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menItVersionActionPerformed
        
        Version v = new Version(this, true);
        v.setVisible(true);
    }//GEN-LAST:event_menItVersionActionPerformed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FormularioPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormularioPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormularioPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormularioPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormularioPrincipal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu menAcercaDe;
    private javax.swing.JMenu menAdministrar;
    private javax.swing.JMenu menArchivo;
    private javax.swing.JMenu menEditar;
    private javax.swing.JMenuItem menItClientes;
    private javax.swing.JMenuItem menItComprobantes;
    private javax.swing.JMenuItem menItSalir;
    private javax.swing.JMenuItem menItVersion;
    // End of variables declaration//GEN-END:variables
}

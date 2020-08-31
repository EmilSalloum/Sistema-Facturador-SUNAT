package vista.factura;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import modelo.Cliente;
import modelo.Factura;
import vista.Comprobantes;

public class FacturaNueva extends javax.swing.JPanel {
    
    static ResultSet rs;
    int idCliente;

    public FacturaNueva() {
        initComponents();
        CargarNombreFactura();
        CargarFecha();
    }
    
    void CargarNombreFactura(){
        try {
            rs = Factura.Consulta("SELECT ident_current('factura')+1;");
            if (rs.next()) {
                int id = rs.getInt(1);
                String numero = String.format("%08d" , id);
                lblNombreFactura.setText("FF01-"+ numero);
            }
            rs.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: \n" + e);
        }
    }
    
    void CargarFecha(){
        //se crea objeto de tipo Date
        Date d = new Date();
        //formato
        String fecha = (new SimpleDateFormat("dd-MM-yyyy").format(d));
        //envio al text field
        txtFecha.setText(fecha);
    }
    
    public void CargaClienteSeleccionado(int id){
        try {
            idCliente = id;
            rs = Cliente.Consulta("select ruc,razonSocial,direccion "
                    + "from cliente "
                    + "where id = '" + id + "';");
            while (rs.next()) {
                String ruc = rs.getString(1);
                String razonSocial = rs.getString(2);
                String direccion = rs.getString(3);
                txtRuc.setText(ruc);
                txtRazonSocial.setText(razonSocial);
                txtDireccion.setText(direccion);
            }
            rs.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: \n" + e);
        }
        
    }

    void AgregarDetalle(double cantidad, String descripcion, double precioUnitario) {
        DefaultTableModel dtmDetalle = (DefaultTableModel) tblDetalle.getModel();
        Object[] fila = new Object[4];
        DecimalFormat df = new DecimalFormat("0.00");
        fila[0] = df.format(cantidad);
        fila[1] = descripcion;
        fila[2] = df.format(precioUnitario);
        double importeTotal = cantidad * precioUnitario;
        fila[3] = df.format(importeTotal);
        dtmDetalle.addRow(fila);
        tblDetalle.setModel(dtmDetalle);
        ActualizarTotales();
        txtCantidad.setText("");
        txtDescripcion.setText("");
        txtPrecioUnitario.setText("");
    }

    void ActualizarTotales() {
        double importe = 0.00;
        double igv;
        double importeTotal;
        int filas = tblDetalle.getRowCount();
        for (int i = 0; i < filas; i++) {
            //operamos para obtener los totales
            double importeT = Double.parseDouble(tblDetalle.getValueAt(i, 3).toString());
            importe = importe + importeT;
            igv = (0.18 * importe);
            importeTotal = importe + igv;
            //mostrando datos actualizados
            DecimalFormat df = new DecimalFormat("0.00");
            txtImporte.setText(String.valueOf(df.format(importe)));
            txtIgv.setText(String.valueOf(df.format(igv)));
            txtImporteTotal.setText(String.valueOf(df.format(importeTotal)));
        }
        String importeTexto = Factura.ConvertirNumLetras(txtImporteTotal.getText(), cbxMoneda.getSelectedItem().toString());
        lblImporteTotalTexto.setText(importeTexto);
    }
    
    void RegistrarFactura() {
        String ruc = txtRuc.getText();
        String razonSocial = txtRazonSocial.getText();
        String direccion = txtDireccion.getText();

        int fila = tblDetalle.getRowCount();

        if (ruc.equals("")
                || razonSocial.equals("")
                || direccion.equals("")
                || fila == 0) {
            JOptionPane.showMessageDialog(null, "Verificar datos.");
        } else {
            try {
                String nombre = lblNombreFactura.getText();
                String fecha = txtFecha.getText();
                String moneda = cbxMoneda.getSelectedItem().toString();
                double igv = Double.parseDouble(txtIgv.getText());
                double importe = Double.parseDouble(txtImporteTotal.getText());
                Factura.Registrar(idCliente, nombre, fecha, moneda, igv, importe);
                JFrame FormularioPrincipal = (JFrame) SwingUtilities.getWindowAncestor(this);
                FormularioPrincipal.getContentPane().removeAll();
                FormularioPrincipal.repaint();
                Comprobantes c = new Comprobantes();
                //Agregamos la instancia al JFrame, con un layout al centro
                FormularioPrincipal.add(c);
                //Hacemos que el JFrame tenga el tamaño de todos sus elementos
                FormularioPrincipal.pack();
                CrearArchivosPlanos();
            } catch (SQLException ex) {
                Logger.getLogger(FacturaNueva.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    void CrearArchivosPlanos(){
        ap_cab();
        ap_det();
        ap_tri();
        ap_ley();
    }
    
    void ap_cab() {
        try {
            String ruta = Factura.getRutaAP(lblNombreFactura.getText(), ".CAB");
            File archivoPlano = new File(ruta);
            // almacenar datos
            BufferedWriter bufferedWriter;
            bufferedWriter = new BufferedWriter(new FileWriter(archivoPlano));
            //.CAB: 18 valores
            String tipOperacion = "0101";//venta interna
            String fecEmision = Factura.getFechaSunat();
            String horEmision = Factura.getHoraEmision();
            String fecVencimiento = "-";
            String codLocalEmisor = "0000";
            String tipDocUsuario = "6";//RUC
            String numDocUsuario = txtRuc.getText();
            String rznSocialUsuario = txtRazonSocial.getText();
            String tipMoneda = cbxMoneda.getSelectedItem().toString();
            String sumTotTributos = txtIgv.getText();
            String sumTotalVenta = txtImporte.getText();
            String sumPrecioVenta = txtImporteTotal.getText();
            String sumDescTotal = "0.00";
            String sumOtrosCargos = "0.00";
            String sumTotalAnticipos = "0.00";
            String sumImpVenta = txtImporteTotal.getText();
            String ublVersionId = "2.1";
            String customizationId = "2.0";
            //se esccribe la linea en el archivo
            bufferedWriter.write(
                    tipOperacion + "|"
                    + fecEmision + "|"
                    + horEmision + "|"
                    + fecVencimiento + "|"
                    + codLocalEmisor + "|"
                    + tipDocUsuario + "|"
                    + numDocUsuario + "|"
                    + rznSocialUsuario + "|"
                    + tipMoneda + "|"
                    + sumTotTributos + "|"
                    + sumTotalVenta + "|"
                    + sumPrecioVenta + "|"
                    + sumDescTotal + "|"
                    + sumOtrosCargos + "|"
                    + sumTotalAnticipos + "|"
                    + sumImpVenta + "|"
                    + ublVersionId + "|"
                    + customizationId + "|"
            );
            bufferedWriter.close();
        } catch (Exception e) {
        }
    }
    
    void ap_det() {
        try {
            String ruta = Factura.getRutaAP(lblNombreFactura.getText(), ".DET");
            File archivoPlano = new File(ruta);
            // almacenar datos
            BufferedWriter bufferedWriter;
            bufferedWriter = new BufferedWriter(new FileWriter(archivoPlano));
            DecimalFormat df = new DecimalFormat("0.00");
            for (int i = 0; i < tblDetalle.getRowCount(); i++) {
                //guardamos los campos en variables
                String Cantidad = String.valueOf(tblDetalle.getValueAt(i, 0).toString());
                String Descripcion = tblDetalle.getValueAt(i, 1).toString();
                String PrecioUnitario = String.valueOf(tblDetalle.getValueAt(i, 2).toString());
                String Importe = String.valueOf(tblDetalle.getValueAt(i, 3).toString());
                //.CAB: 36 valores
                String codUnidadMedida = "EA";
                String ctdUnidadItem = Cantidad;
                String codProducto = "-";
                String codProductoSUNAT = "-";
                String desItem = Descripcion;
                String mtoValorUnitario = PrecioUnitario;
                String sumTotTributosItem = String.valueOf(df.format(Double.parseDouble(Importe) * 0.18));//IGV único tributo
                String codTriIGV = "1000";//IGV
                String mtoIgvItem = sumTotTributosItem;
                String mtoBaseIgvItem = Importe;
                String nomTributoIgvItem = "IGV";
                String codTipTributoIgvItem = "VAT";
                String tipAfeIGV = "10";
                String porIgvItem = "18.00";
                String codTriISC = "-";
                String mtoIscItem = "0.00";
                String mtoBaseIscItem = "";
                String nomTributoIscItem = "ISC";
                String codTipTributoIscItem = "EXC";
                String tipSisISC = "01";
                String porIscItem = "15.00";
                String codTriOtro = "-";
                String mtoTriOtroItem = "";
                String mtoBaseTriOtroItem = "";
                String nomTributoOtroItem = "";
                String codTipTributoOtroItem = "";
                String porTriOtroItem = "";
                String codTriIcbper = "-";
                String mtoTriIcbperItem = "";
                String ctdBolsasTriIcbperItem = "";
                String nomTributoIcbperItem = "";
                String codTipTributoIcbperItem = "";
                String mtoTriIcbperUnidad = "";
                String mtoPrecioVentaUnitario = String.valueOf(df.format(Double.parseDouble(PrecioUnitario) + (Double.parseDouble(PrecioUnitario) * 0.18)));
                String mtoValorVentaItem = mtoBaseIgvItem;
                String mtoValorReferencialUnitario = "0.00";
                bufferedWriter.write(
                            codUnidadMedida + "|"
                            + ctdUnidadItem + "|"
                            + codProducto + "|"
                            + codProductoSUNAT + "|"
                            + desItem + "|"
                            + mtoValorUnitario + "|"
                            + sumTotTributosItem + "|"
                            + codTriIGV +"|"
                            + mtoIgvItem + "|"
                            + mtoBaseIgvItem + "|"
                            + nomTributoIgvItem + "|"
                            + codTipTributoIgvItem + "|"
                            + tipAfeIGV + "|"
                            + porIgvItem + "|"
                            + codTriISC +"|"
                            + mtoIscItem + "|"
                            + mtoBaseIscItem + "|"
                            + nomTributoIscItem + "|"
                            + codTipTributoIscItem + "|"
                            + tipSisISC + "|"
                            + porIscItem + "|"
                            + codTriOtro + "|"
                            + mtoTriOtroItem + "|"
                            + mtoBaseTriOtroItem + "|"
                            + nomTributoOtroItem + "|"
                            + codTipTributoOtroItem + "|"
                            + porTriOtroItem + "|"
                            + codTriIcbper + "|"
                            + mtoTriIcbperItem + "|"
                            + ctdBolsasTriIcbperItem + "|"
                            + nomTributoIcbperItem + "|"
                            + codTipTributoIcbperItem + "|"
                            + mtoTriIcbperUnidad + "|"
                            + mtoPrecioVentaUnitario + "|"
                            + mtoValorVentaItem + "|"
                            + mtoValorReferencialUnitario + "|\n");
                }
                bufferedWriter.close();
        } catch (Exception e) {
        }
    }
    
    void ap_tri() {
        try {
            String ruta = Factura.getRutaAP(lblNombreFactura.getText(), ".TRI");
            File archivoPlano = new File(ruta);
            // almacenar datos
            BufferedWriter bufferedWriter;
            bufferedWriter = new BufferedWriter(new FileWriter(archivoPlano));
            //.TRI: 5 valores
            String ideTributo = "1000";
            String nomTributo = "IGV";
            String codTipTributo = "VAT";
            String mtoBaseImponible = txtImporte.getText();
            String mtoTributo = txtIgv.getText();
            //se esccribe la linea en el archivo
            bufferedWriter.write(
                    ideTributo + "|"
                    + nomTributo + "|"
                    + codTipTributo + "|"
                    + mtoBaseImponible + "|"
                    + mtoTributo + "|"
            );
            bufferedWriter.close();
        } catch (Exception e) {
        }
    }
    
    void ap_ley() {
        try {
            String ruta = Factura.getRutaAP(lblNombreFactura.getText(), ".LEY");
            File archivoPlano = new File(ruta);
            // almacenar datos
            BufferedWriter bufferedWriter;
            bufferedWriter = new BufferedWriter(new FileWriter(archivoPlano));
            //.TRI: 2 valores
            String codLeyenda = "1000";//monto en letras
            String desLeyenda = lblImporteTotalTexto.getText();
            //se esccribe la linea en el archivo
            bufferedWriter.write(
                    codLeyenda + "|"
                    + desLeyenda + "|"
            );
            bufferedWriter.close();
        } catch (Exception e) {
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtRazonSocial = new javax.swing.JTextField();
        txtRuc = new javax.swing.JTextField();
        txtDireccion = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        cbxMoneda = new javax.swing.JComboBox<>();
        btnBuscar = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDetalle = new javax.swing.JTable();
        btnNuevo = new javax.swing.JButton();
        btnQuitar = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtPrecioUnitario = new javax.swing.JTextField();
        txtDescripcion = new javax.swing.JTextField();
        txtCantidad = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtFecha = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtImporte = new javax.swing.JTextField();
        txtIgv = new javax.swing.JTextField();
        txtImporteTotal = new javax.swing.JTextField();
        btnRegistrar = new javax.swing.JButton();
        lblNombreFactura = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        lblImporteTotalTexto = new javax.swing.JLabel();

        jLabel1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel1.setText("Factura nueva");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Cliente:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 11))); // NOI18N
        jPanel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel2.setText("RUC:");

        jLabel3.setText("Razón Social:");

        jLabel4.setText("Dirección:");

        txtRazonSocial.setEnabled(false);

        txtRuc.setEnabled(false);

        txtDireccion.setEnabled(false);

        jLabel6.setText("Moneda:");

        cbxMoneda.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cbxMoneda.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "PEN", "USD" }));

        btnBuscar.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnBuscar.setText("Buscar");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtRazonSocial, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                    .addComponent(txtRuc, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDireccion))
                .addGap(72, 72, 72)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnBuscar, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                    .addComponent(cbxMoneda, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtRuc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(cbxMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtRazonSocial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Detalle", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 11))); // NOI18N

        tblDetalle.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tblDetalle.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Cantidad", "Descripción", "Precio unitario", "Importe"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDetalle.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblDetalle);

        btnNuevo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnNuevo.setText("Agregar");
        btnNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoActionPerformed(evt);
            }
        });

        btnQuitar.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnQuitar.setText("Quitar");
        btnQuitar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuitarActionPerformed(evt);
            }
        });

        jLabel7.setText("Catidad:");

        jLabel8.setText("Descripción:");

        jLabel9.setText("Precio unitario:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnQuitar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel8)
                            .addComponent(jLabel7))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPrecioUnitario)
                            .addComponent(txtDescripcion)
                            .addComponent(txtCantidad))))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 624, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(txtPrecioUnitario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnQuitar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Fecha:");

        txtFecha.setEnabled(false);

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel10.setText("Importe:");

        jLabel11.setText("IGV:");

        jLabel12.setText("Importe Total:");

        txtImporte.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtImporte.setText("0.00");
        txtImporte.setEnabled(false);

        txtIgv.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtIgv.setText("0.00");
        txtIgv.setEnabled(false);

        txtImporteTotal.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtImporteTotal.setText("0.00");
        txtImporteTotal.setEnabled(false);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12)
                    .addComponent(jLabel10))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtImporte, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                    .addComponent(txtImporteTotal)
                    .addComponent(txtIgv))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtImporte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txtIgv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(txtImporteTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnRegistrar.setText("Registrar");
        btnRegistrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarActionPerformed(evt);
            }
        });

        lblNombreFactura.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        lblNombreFactura.setText(" ");
        lblNombreFactura.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel13.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel13.setText("SON:");

        lblImporteTotalTexto.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        lblImporteTotalTexto.setText("?");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(38, 38, 38)
                        .addComponent(lblNombreFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnRegistrar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addGap(18, 18, 18)
                        .addComponent(lblImporteTotalTexto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel5)
                    .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNombreFactura))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(lblImporteTotalTexto))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRegistrar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoActionPerformed

        if (txtCantidad.getText().isEmpty()
                || txtDescripcion.getText().isEmpty()
                || txtPrecioUnitario.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingresar datos.");
        } else {

            double cantidad = Double.parseDouble(txtCantidad.getText());
            String descripcion = txtDescripcion.getText();
            double precioUnitario = Double.parseDouble(txtPrecioUnitario.getText());

            if (cantidad <= 0
                    || descripcion.equals("")
                    || precioUnitario <= 0) {
                JOptionPane.showMessageDialog(null, "Verificar datos.");
            } else {
                AgregarDetalle(cantidad, descripcion, precioUnitario);
            }

        }

    }//GEN-LAST:event_btnNuevoActionPerformed

    private void btnQuitarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuitarActionPerformed

        int fila = tblDetalle.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione un detalle.");
        } else if (fila != -1) {
            DefaultTableModel dtmDetalle = (DefaultTableModel) tblDetalle.getModel();
            dtmDetalle.removeRow(fila);
            ActualizarTotales();
        }
    }//GEN-LAST:event_btnQuitarActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        
        JFrame FormularioPrincipal = (JFrame) SwingUtilities.getWindowAncestor(this);
        FacturaBuscarCliente fbc = new FacturaBuscarCliente(FormularioPrincipal, true);
        fbc.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    CargaClienteSeleccionado(fbc.idCliente);
                }
            });
        fbc.setVisible(true);
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void btnRegistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarActionPerformed
        RegistrarFactura();
    }//GEN-LAST:event_btnRegistrarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JButton btnQuitar;
    private javax.swing.JButton btnRegistrar;
    private javax.swing.JComboBox<String> cbxMoneda;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblImporteTotalTexto;
    private javax.swing.JLabel lblNombreFactura;
    private javax.swing.JTable tblDetalle;
    private javax.swing.JTextField txtCantidad;
    private javax.swing.JTextField txtDescripcion;
    private javax.swing.JTextField txtDireccion;
    private javax.swing.JTextField txtFecha;
    private javax.swing.JTextField txtIgv;
    private javax.swing.JTextField txtImporte;
    private javax.swing.JTextField txtImporteTotal;
    private javax.swing.JTextField txtPrecioUnitario;
    private javax.swing.JTextField txtRazonSocial;
    private javax.swing.JTextField txtRuc;
    // End of variables declaration//GEN-END:variables
}

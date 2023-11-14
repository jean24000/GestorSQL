package formulario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class formulario4 extends JFrame {

    private Connection conexion;
    private String nombreBase;
    private List<JTextField> nombreColumnasFields;
    private List<JComboBox<String>> tipoDatoComboBoxes;
    private List<JTextField> longitudColumnasFields;
    private int numColumnas;

    public formulario4(String usuario, String contrasena, String host, String puerto, String nombreBase) {
        this.nombreBase = nombreBase;
        this.nombreColumnasFields = new ArrayList<>();
        this.tipoDatoComboBoxes = new ArrayList<>();
        this.longitudColumnasFields = new ArrayList<>();
        this.numColumnas = 2;

        setTitle("FORMULARIO 4: Crear Tabla en " + nombreBase);
        setSize(800, 600);

        JPanel panel = new JPanel(new BorderLayout());

        JPanel panelNombre = new JPanel();
        JLabel lblNombreTabla = new JLabel("Nombre de la tabla:");
        JTextField txtNombreTabla = new JTextField(20);
        panelNombre.add(lblNombreTabla);
        panelNombre.add(txtNombreTabla);

        JPanel panelColumnas = new JPanel(new GridLayout(0, 4));
        JLabel lblNumColumnas = new JLabel("Número de columnas:");
        JSpinner numColumnasSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 10, 1));
        JButton btnGenerarCampos = new JButton("Generar Campos");
        panelColumnas.add(lblNumColumnas);
        panelColumnas.add(numColumnasSpinner);
        panelColumnas.add(btnGenerarCampos);

        JButton btnCrearTabla = new JButton("Crear Tabla");

        panel.add(panelNombre, BorderLayout.NORTH);
        panel.add(panelColumnas, BorderLayout.CENTER);

        btnGenerarCampos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                numColumnas = (int) numColumnasSpinner.getValue();
                crearCamposDeTextoYTiposDeDato(numColumnas, panelColumnas);
            }
        });

        btnCrearTabla.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombreTabla = txtNombreTabla.getText();

                if (!nombreTabla.isEmpty()) {
                    crearTabla(nombreTabla, numColumnas);
                } else {
                    JOptionPane.showMessageDialog(formulario4.this, "Por favor, ingrese un nombre para la tabla.");
                }
            }
        });

        panel.add(btnCrearTabla, BorderLayout.SOUTH);

        add(panel);
        
        String url = "jdbc:mysql://" + host + ":" + puerto + "/" + nombreBase + "?useSSL=false";
        try {
            conexion = DriverManager.getConnection(url, usuario, contrasena);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar a la base de datos: " + e.getMessage());
        }

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void crearCamposDeTextoYTiposDeDato(int numColumnas, JPanel panel) {
        nombreColumnasFields.clear();
        tipoDatoComboBoxes.clear();
        longitudColumnasFields.clear();
        panel.removeAll();

        for (int i = 0; i < numColumnas; i++) {
            JTextField campoTexto = new JTextField(20);
            JLabel label = new JLabel("Nombre de columna " + (i + 1) + ":");
            JComboBox<String> tipoDatoComboBox = new JComboBox<>(new String[]{"INT", "VARCHAR", "DATE", "DOUBLE", "BOOLEAN"});
            JLabel tipoDatoLabel = new JLabel("Tipo de dato:");
            JTextField longitudColumna = new JTextField(5);
            JLabel longitudLabel = new JLabel("Longitud:");
            JPanel campoPanel = new JPanel();
            campoPanel.add(label);
            campoPanel.add(campoTexto);
            campoPanel.add(tipoDatoLabel);
            campoPanel.add(tipoDatoComboBox);
            campoPanel.add(longitudLabel);
            campoPanel.add(longitudColumna);
            panel.add(campoPanel);
            nombreColumnasFields.add(campoTexto);
            tipoDatoComboBoxes.add(tipoDatoComboBox);
            longitudColumnasFields.add(longitudColumna);
        }

        panel.revalidate();
        panel.repaint();
    }

    private void crearTabla(String nombreTabla, int numColumnas) {
        try {
            Statement statement = conexion.createStatement();
            StringBuilder query = new StringBuilder("CREATE TABLE " + nombreTabla + " (id INT AUTO_INCREMENT PRIMARY KEY");

            for (int i = 0; i < numColumnas; i++) {
                JTextField columna = nombreColumnasFields.get(i);
                JComboBox<String> tipoDatoCombo = tipoDatoComboBoxes.get(i);
                JTextField longitudColumna = longitudColumnasFields.get(i);
                String nombreColumna = columna.getText().trim();
                String tipoDato = tipoDatoCombo.getSelectedItem().toString();
                String longitud = longitudColumna.getText().trim();
                if (nombreColumna.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Por favor, ingrese un nombre para todas las columnas.");
                    return;
                }
                if (tipoDato.equals("VARCHAR")) {
                    if (longitud.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Por favor, ingrese la longitud para las columnas VARCHAR.");
                        return;
                    }
                    tipoDato += "(" + longitud + ")";
                }
                query.append(", ").append(nombreColumna).append(" ").append(tipoDato);
            }

            query.append(")");

            statement.execute(query.toString());
            JOptionPane.showMessageDialog(this, "Tabla " + nombreTabla + " creada con éxito.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al crear la tabla: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            formulario4 form = new formulario4("usuario", "contrasena", "localhost", "3306", "nombre_base");
            form.setVisible(true);
        });
    }
}

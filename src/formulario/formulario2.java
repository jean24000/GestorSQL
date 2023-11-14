package formulario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class formulario2 extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JTextField txtHost;
    private JTextField txtPuerto;
    private JComboBox<String> cboBasesDeDatos;
    private Connection conexion;

    public formulario2() {
        // Configurar el título y el tamaño de la ventana
        setTitle("CONECTAR A BASE DE DATOS");
        setSize(800, 600);

        // Crear un panel para organizar los componentes
        JPanel panel = new JPanel(new GridLayout(5, 2)); // Cinco filas, dos columnas
        panel.setBackground(Color.red);
        // Etiquetas
        JLabel lblUsuario = new JLabel("Usuario:");
        JLabel lblContrasena = new JLabel("Contraseña:");
        JLabel lblHost = new JLabel("Host:");
        JLabel lblPuerto = new JLabel("Puerto");
        JLabel lblBase = new JLabel("Base de datos");

        // Cajas de texto
        txtUsuario = new JTextField(20);
        txtContrasena = new JPasswordField(20);
        txtHost = new JTextField(20);
        txtPuerto = new JTextField(20);
        cboBasesDeDatos = new JComboBox<>();
        txtUsuario.setBounds(50, 0, 50, 30);

        // Botones
        JButton btnTest = new JButton("Test");
        JButton btnConectar = new JButton("Conectar");
        JButton btnCrear = new JButton("Crear BD");
        JButton btnAbrirFormulario4 = new JButton("Crear tablas");
        JButton btnAbrirFormulario5 = new JButton("Insertar datos");

        // Agregar etiquetas y componentes al panel
        panel.add(lblUsuario);
        panel.add(txtUsuario);
        panel.add(lblContrasena);
        panel.add(txtContrasena);
        panel.add(lblHost);
        panel.add(txtHost);
        panel.add(lblPuerto);
        panel.add(txtPuerto);
        panel.add(lblBase);
        panel.add(cboBasesDeDatos);

        // Crear un segundo panel para los botones
        JPanel panelBotones = new JPanel();

        // Agregar un ActionListener al botón "Test" para probar la conexión
        btnTest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                testConnection();
            }
        });

        // Agregar un ActionListener al botón "Conectar" para establecer la conexión
        btnConectar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                conectar();
            }
        });
        // Agregar un ActionListener al botón "Crear" para establecer la conexión
        btnCrear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
// Obtener los datos necesarios
                String usuario = txtUsuario.getText();
                String contrasena = new String(txtContrasena.getPassword());
                String host = txtHost.getText();
                String puerto = txtPuerto.getText();
                String nombreBase = cboBasesDeDatos.getSelectedItem().toString();

                // Crear una instancia del formulario3 y pasar la conexión
                formulario3 nuevoFormulario = new formulario3(usuario, contrasena, host, puerto, nombreBase);

                // Hacer visible el nuevo formulario
                nuevoFormulario.setVisible(true);
            }
        });
        btnAbrirFormulario4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirFormulario4();
            }
        });
        // Agregar un ActionListener al botón "Test" para probar la conexión
        btnAbrirFormulario5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirFormulario5();
            }
        });

        // Agregar los botones al segundo panel
        panelBotones.add(btnTest);
        panelBotones.add(btnConectar);
        panelBotones.add(btnCrear);
        panelBotones.add(btnAbrirFormulario4);
        panelBotones.add(btnAbrirFormulario5);

        // Configurar el layout del formulario principal como BorderLayout
        setLayout(new BorderLayout());

        // Agregar el panel de componentes al centro
        add(panel, BorderLayout.CENTER);

        // Agregar el panel de botones en la parte inferior
        add(panelBotones, BorderLayout.SOUTH);

        // Cierra la ventana cuando se hace clic en el botón de cierre
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    // Método para probar la conexión
    private void testConnection() {
        String usuario = txtUsuario.getText();
        String contrasena = new String(txtContrasena.getPassword());
        String host = txtHost.getText();
        String puerto = txtPuerto.getText();
        String url = "jdbc:mysql://" + host + ":" + puerto ;

// En el método testConnection
        try {
            conexion = DriverManager.getConnection(url, usuario, contrasena);
            JOptionPane.showMessageDialog(this, "Conexión exitosa");
            // Llenar la lista de bases de datos después de una conexión exitosa
            llenarListaBasesDeDatos(conexion);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar a la base de datos: " + e.getMessage());
        }

    }

    // Método para establecer la conexión
    private void conectar() {
        String usuario = txtUsuario.getText();
        String contrasena = new String(txtContrasena.getPassword());
        String host = txtHost.getText();
        String puerto = txtPuerto.getText();
        String nombreBase = cboBasesDeDatos.getSelectedItem().toString();
        String url = "jdbc:mysql://" + host + ":" + puerto + "/" + nombreBase + "?useSSL=false";
        

        try {
            Connection conexion = DriverManager.getConnection(url, usuario, contrasena);
            JOptionPane.showMessageDialog(this, "Conexión establecida");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar a la base de datos: " + e.getMessage());
        }
    }

    // Método para llenar la lista de bases de datos disponibles
    private void llenarListaBasesDeDatos(Connection conexion) {
        cboBasesDeDatos.removeAllItems(); // Limpia la lista previa

        List<String> basesDeDatos = new ArrayList<>();

        try {
            Statement statement = conexion.createStatement();
            ResultSet resultSet = statement.executeQuery("SHOW DATABASES");

            while (resultSet.next()) {
                String nombreBase = resultSet.getString(1);
                basesDeDatos.add(nombreBase);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al obtener la lista de bases de datos: " + e.getMessage());
        }

        // Llenar el JComboBox con las bases de datos
        for (String dbName : basesDeDatos) {
            cboBasesDeDatos.addItem(dbName);
        }
    }

    private void abrirFormulario4() {
        String usuario = txtUsuario.getText();
        String contrasena = new String(txtContrasena.getPassword());
        String host = txtHost.getText();
        String puerto = txtPuerto.getText();
        String nombreBase = cboBasesDeDatos.getSelectedItem().toString();

        if (nombreBase.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una base de datos primero.");
            return;
        }

        // Crear una instancia del formulario4 y pasar la conexión y el nombre de la base
        formulario4 nuevoFormulario4 = new formulario4(usuario, contrasena, host, puerto, nombreBase);

        // Hacer visible el nuevo formulario4
        nuevoFormulario4.setVisible(true);
    }

    private void abrirFormulario5() {
        String usuario = txtUsuario.getText();
        String contrasena = new String(txtContrasena.getPassword());
        String host = txtHost.getText();
        String puerto = txtPuerto.getText();
        String nombreBase = cboBasesDeDatos.getSelectedItem().toString();

        if (nombreBase.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una base de datos primero.");
            return;
        }

        // Crear una instancia del formulario4 y pasar la conexión y el nombre de la base
        formulario5 nuevoFormulario5 = new formulario5(usuario, contrasena, host, puerto, nombreBase);

        // Hacer visible el nuevo formulario4
        nuevoFormulario5.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            formulario2 ventana = new formulario2();
            ventana.setVisible(true);
        });
    }
}

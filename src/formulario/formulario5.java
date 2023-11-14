package formulario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class formulario5 extends JFrame {

    private Connection conexion;
    private String nombreBase;
    private JComboBox<String> cboTablas;
    private JTable tablaColumnas;
    private JScrollPane scrollPane;
    private DefaultTableModel tableModel;
    private JButton btnModificar; // Botón de Modificar
    private JButton btnEliminar; // Botón de Eliminar
    private List<JCheckBox> columnCheckBoxes = new ArrayList<>();
    private JTextField queryTextField;
    private JButton executeQueryButton;
    private JCheckBox selectAllCheckBox;
    private JLabel consultaAEjecutar;

    public formulario5(String usuario, String contrasena, String host, String puerto, String nombreBase) {
        this.nombreBase = nombreBase;

        // Crear y configurar el JComboBox para mostrar las tablas
        cboTablas = new JComboBox<>();
        cboTablas.setBounds(10, 10, 200, 30);
        add(cboTablas);

        // Crear el JTable y JScrollPane con un modelo de tabla personalizado
        tableModel = new DefaultTableModel();
        tablaColumnas = new JTable(tableModel);
        scrollPane = new JScrollPane(tablaColumnas);
        scrollPane.setBounds(10, 50, 380, 200);
        add(scrollPane);

        JLabel consultaAEjecutar = new JLabel("CONSULTA A EJECUTAR");
        consultaAEjecutar.setBounds(10, 340, 150, 30);
        add(consultaAEjecutar);

        // Agregar un ActionListener al JComboBox
        cboTablas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarColumnasDeTabla((String) cboTablas.getSelectedItem());
                cargarDatosDeTabla((String) cboTablas.getSelectedItem());
            }
        });

        // Personalizar la cabecera de la tabla para permitir la selección de columnas
        JTableHeader header = tablaColumnas.getTableHeader();
        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int columnIndex = header.columnAtPoint(e.getPoint());
                if (columnIndex >= 0) {
                    seleccionarColumna(columnIndex);
                }
            }
        });

        // Crear un botón para registrar los datos
        JButton btnRegistrar = new JButton("Registrar");
        btnRegistrar.setBounds(10, 260, 150, 30);
        btnRegistrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarDatos();
            }
        });
        add(btnRegistrar);

        // Crear un botón para modificar los datos
        btnModificar = new JButton("Modificar");
        btnModificar.setBounds(180, 260, 150, 30);
        btnModificar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modificarRegistro();
            }
        });
        add(btnModificar);

        // Crear un botón para eliminar los datos
        btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(10, 300, 150, 30);
        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirmacion = JOptionPane.showConfirmDialog(null, "¿Estás seguro de que deseas eliminar este registro?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
                if (confirmacion == JOptionPane.YES_OPTION) {
                    eliminarRegistro();
                }
            }
        });
        add(btnEliminar);

        // Crear un botón para describir la tabla
        JButton btnDescribir = new JButton("CONSULTA");
        btnDescribir.setBounds(180, 300, 150, 30);
        btnDescribir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                describirTabla((String) cboTablas.getSelectedItem());
            }
        });
        add(btnDescribir);

        // Establecer la conexión
        String url = "jdbc:mysql://" + host + ":" + puerto + "/" + nombreBase + "?useSSL=false";
        try {
            conexion = DriverManager.getConnection(url, usuario, contrasena);
            mostrarTablasDisponibles();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar a la base de datos: " + e.getMessage());
        }

        // Configurar el formulario5
        setTitle("Seleccionar Tabla");
        setSize(800, 600); // Aumenté la altura del formulario para dar espacio al botón "Eliminar"
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        queryTextField = new JTextField();
        queryTextField.setBounds(10, 390, 700, 30);
        add(queryTextField);

        executeQueryButton = new JButton("Ejecutar Consulta");
        executeQueryButton.setBounds(10, 430, 150, 30);
        add(executeQueryButton);

        executeQueryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Obtener la consulta desde el JTextField
                String consulta = queryTextField.getText();

                if (!consulta.isEmpty()) {
                    // Realizar la consulta a la base de datos
                    ejecutarConsulta(consulta);
                } else {
                    JOptionPane.showMessageDialog(formulario5.this, "La consulta está vacía.");
                }
            }
        });
        selectAllCheckBox = new JCheckBox("Seleccionar Todo");
        selectAllCheckBox.setBounds(10, 920, 200, 30);
        add(selectAllCheckBox);

        selectAllCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean selected = selectAllCheckBox.isSelected();
                for (JCheckBox checkBox : columnCheckBoxes) {
                    checkBox.setSelected(selected);
                }
            }
        });

    }

    private void obtenerConsulta() {
        String nombreTabla = (String) cboTablas.getSelectedItem();
        StringBuilder consulta = new StringBuilder("SELECT ");

        if (selectAllCheckBox.isSelected()) {
            consulta.append("*");
        } else {
            boolean alMenosUnaColumnaSeleccionada = false; // Bandera para controlar la coma
            for (int i = 0; i < columnCheckBoxes.size(); i++) {
                JCheckBox checkBox = columnCheckBoxes.get(i);

                if (checkBox.isSelected()) {
                    if (alMenosUnaColumnaSeleccionada) {
                        consulta.append(", ");
                    }

                    consulta.append(checkBox.getText());
                    alMenosUnaColumnaSeleccionada = true;
                }
            }
        }

        consulta.append(" FROM " + nombreTabla + ";");

        // Mostrar la consulta en el JTextField
        queryTextField.setText(consulta.toString());
    }

    private void ejecutarConsulta(String consulta) {
        try {
            Statement statement = conexion.createStatement();
            ResultSet resultSet = statement.executeQuery(consulta);

            // Obtener los metadatos de la consulta para obtener el número de columnas
            int columnCount = resultSet.getMetaData().getColumnCount();

            // Crear un nuevo modelo de tabla para mostrar los resultados
            DefaultTableModel resultTableModel = new DefaultTableModel();

            // Agregar las columnas al nuevo modelo de tabla
            for (int i = 1; i <= columnCount; i++) {
                resultTableModel.addColumn(resultSet.getMetaData().getColumnName(i));
            }

            // Agregar las filas al nuevo modelo de tabla
            while (resultSet.next()) {
                Vector<Object> row = new Vector<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(resultSet.getObject(i));
                }
                resultTableModel.addRow(row);
            }

            // Crear una nueva ventana para mostrar los resultados
            JFrame resultFrame = new JFrame("Resultados de la Consulta");
            JTable resultTable = new JTable(resultTableModel);
            JScrollPane resultScrollPane = new JScrollPane(resultTable);
            resultFrame.add(resultScrollPane);
            resultFrame.pack();
            resultFrame.setVisible(true);
            queryTextField.setText("");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al ejecutar la consulta: " + ex.getMessage());
        }
    }

    private void mostrarTablasDisponibles() {
        try {
            java.sql.DatabaseMetaData metaData = conexion.getMetaData();
            ResultSet tables = metaData.getTables(nombreBase, null, null, new String[]{"TABLE"});

            cboTablas.removeAllItems();

            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                cboTablas.addItem(tableName);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al obtener la lista de tablas: " + e.getMessage());
        }
    }

    private void cargarColumnasDeTabla(String nombreTabla) {
        tableModel.setColumnCount(0);
        tableModel.setRowCount(0);
        columnCheckBoxes.clear();

        try {
            Statement statement = conexion.createStatement();
            ResultSet resultSet = statement.executeQuery("DESCRIBE " + nombreTabla);

            while (resultSet.next()) {
                String nombreColumna = resultSet.getString("Field");
                JCheckBox checkBox = new JCheckBox(nombreColumna);
                columnCheckBoxes.add(checkBox);
                tableModel.addColumn(nombreColumna);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al obtener los nombres de las columnas: " + e.getMessage());
        }
    }

    private void registrarDatos() {
        int columnCount = tableModel.getColumnCount();

        if (columnCount > 0) {
            JTextField[] inputFields = new JTextField[columnCount];
            JPanel panel = new JPanel(new GridLayout(1, columnCount));

            for (int i = 0; i < columnCount; i++) {
                inputFields[i] = new JTextField();
                panel.add(inputFields[i]);
            }

            // Personalizar el JOptionPane para que sea más grande
            JPanel customPanel = new JPanel();
            customPanel.setLayout(new BorderLayout());
            customPanel.add(panel, BorderLayout.NORTH);

            int result = JOptionPane.showConfirmDialog(null, customPanel, "Ingrese los datos:",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String nombreTabla = (String) cboTablas.getSelectedItem();
                String[] valores = new String[columnCount];

                for (int i = 0; i < columnCount; i++) {
                    valores[i] = inputFields[i].getText();
                }

                guardarDatos(nombreTabla, valores);
            }
        }
    }

    private void guardarDatos(String nombreTabla, String[] valores) {
        try {
            StringBuilder insertQuery = new StringBuilder("INSERT INTO " + nombreTabla + " (");
            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                insertQuery.append(tableModel.getColumnName(i));
                if (i < tableModel.getColumnCount() - 1) {
                    insertQuery.append(", ");
                }
            }
            insertQuery.append(") VALUES (");

            for (int i = 0; i < valores.length; i++) {
                insertQuery.append("'" + valores[i] + "'");
                if (i < valores.length - 1) {
                    insertQuery.append(", ");
                }
            }
            insertQuery.append(")");

            Statement statement = conexion.createStatement();
            statement.executeUpdate(insertQuery.toString());

            // Actualizar el JTextField antes de mostrar el mensaje
            queryTextField.setText(insertQuery.toString());

            // Mostrar la consulta después de guardar los datos
            JOptionPane.showMessageDialog(this, "Datos guardados correctamente. Consulta SQL: \n" + insertQuery.toString());

            cargarDatosDeTabla(nombreTabla); // Recargar los datos en la tabla
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar los datos: " + e.getMessage());
        }
    }

    private void cargarDatosDeTabla(String nombreTabla) {
        tableModel.setRowCount(0);

        try {
            Statement statement = conexion.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + nombreTabla);

            while (resultSet.next()) {
                Vector<String> fila = new Vector<>();
                for (int i = 1; i <= tableModel.getColumnCount(); i++) {
                    fila.add(resultSet.getString(i));
                }
                tableModel.addRow(fila);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los datos: " + e.getMessage());
        }
    }

    private void seleccionarColumna(int columna) {
        if (columna >= 0) {
            tablaColumnas.setColumnSelectionAllowed(true);
            tablaColumnas.setRowSelectionAllowed(false);
            tablaColumnas.setColumnSelectionInterval(columna, columna);
        }
    }

    private void modificarRegistro() {
        int selectedRow = tablaColumnas.getSelectedRow();
        int columnCount = tableModel.getColumnCount();

        if (selectedRow >= 0) {
            JTextField[] inputFields = new JTextField[columnCount];
            JPanel panel = new JPanel(new GridLayout(1, columnCount));

            for (int i = 0; i < columnCount; i++) {
                inputFields[i] = new JTextField(tableModel.getValueAt(selectedRow, i).toString());
                panel.add(inputFields[i]);
            }

            int result = JOptionPane.showConfirmDialog(null, panel, "Modificar datos:", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String nombreTabla = (String) cboTablas.getSelectedItem();
                String[] valores = new String[columnCount];

                for (int i = 0; i < columnCount; i++) {
                    valores[i] = inputFields[i].getText();
                }

                // Actualizar los datos en la base de datos
                actualizarRegistro(nombreTabla, valores, selectedRow);
            }
        }
    }

    private void actualizarRegistro(String nombreTabla, String[] valores, int fila) {
        try {
            StringBuilder updateQuery = new StringBuilder("UPDATE " + nombreTabla + " SET ");
            for (int i = 1; i < tableModel.getColumnCount(); i++) {
                updateQuery.append(tableModel.getColumnName(i) + "='" + valores[i] + "'");
                if (i < tableModel.getColumnCount() - 1) {
                    updateQuery.append(", ");
                }
            }
            updateQuery.append(" WHERE " + tableModel.getColumnName(0) + "='" + valores[0] + "'");

            // Actualizar el JTextField antes de mostrar el mensaje de confirmación
            queryTextField.setText(updateQuery.toString());

            int confirmacion = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que deseas actualizar este registro?", "Confirmar Actualización", JOptionPane.YES_NO_OPTION);
            if (confirmacion == JOptionPane.YES_OPTION) {
                Statement statement = conexion.createStatement();
                statement.executeUpdate(updateQuery.toString());

                // Mostrar mensaje después de actualizar el registro
                JOptionPane.showMessageDialog(this, "Registro actualizado correctamente. Consulta SQL: \n" + updateQuery.toString());

                cargarDatosDeTabla(nombreTabla); // Recargar los datos en la tabla
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar el registro: " + e.getMessage());
        }
    }

    private void eliminarRegistro() {
        int selectedRow = tablaColumnas.getSelectedRow();
        if (selectedRow >= 0) {
            String nombreTabla = (String) cboTablas.getSelectedItem();
            String columnaClave = tableModel.getColumnName(0); // Suponemos que la clave primaria está en la primera columna
            String valorClave = tableModel.getValueAt(selectedRow, 0).toString(); // Obtener el valor de la clave primaria

            // Actualizar el JTextField antes de mostrar el mensaje de confirmación
            queryTextField.setText("DELETE FROM " + nombreTabla + " WHERE " + columnaClave + " = '" + valorClave + "'");

            int confirmacion = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que deseas eliminar este registro?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
            if (confirmacion == JOptionPane.YES_OPTION) {
                try {
                    Statement statement = conexion.createStatement();
                    String deleteQuery = "DELETE FROM " + nombreTabla + " WHERE " + columnaClave + " = '" + valorClave + "'";
                    statement.executeUpdate(deleteQuery);

                    // Mostrar mensaje después de eliminar el registro
                    JOptionPane.showMessageDialog(this, "Registro eliminado correctamente. Consulta SQL: \n" + deleteQuery);

                    cargarDatosDeTabla(nombreTabla); // Recargar los datos en la tabla
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error al eliminar el registro: " + e.getMessage());
                }
            }
        }
    }

    private void describirTabla(String nombreTabla) {
        try {
            Statement statement = conexion.createStatement();
            ResultSet resultSet = statement.executeQuery("DESCRIBE " + nombreTabla);

            StringBuilder description = new StringBuilder();
            JPanel checkBoxPanel = new JPanel(new GridLayout(0, 1));

            // Agregar el JCheckBox "Seleccionar Todo" a la ventana emergente
            checkBoxPanel.add(selectAllCheckBox);

            for (int i = 0; i < columnCheckBoxes.size(); i++) {
                JCheckBox checkBox = columnCheckBoxes.get(i);
                description.append("Nombre de columna: " + checkBox.getText() + "\n");

                if (checkBox.isSelected()) {
                    description.append("Seleccionado: Sí\n");
                } else {
                    description.append("Seleccionado: No\n");
                }

                checkBoxPanel.add(checkBox);
            }

            // Muestra la descripción en un cuadro de diálogo con los JCheckBox
            int result = JOptionPane.showConfirmDialog(this, checkBoxPanel, "Ejecuta tu consulta", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                // Actualizar el JTextField con la consulta SQL
                obtenerConsulta();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al describir la tabla: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            formulario5 ventana = new formulario5("usuario", "contrasena", "host", "puerto", "nombreBase");
            ventana.setVisible(true);
        });
    }
}

package vista;

import modelo.Cliente;
import modelo.Producto;
import persistencia.GestorFicheros;
import servicio.TiendaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class VentanaPrincipal extends JFrame {
    private final TiendaService servicio = new TiendaService();
    private JTable tablaClientes, tablaProductos;
    private DefaultTableModel modeloClientes, modeloProductos;

    public VentanaPrincipal() {
        setTitle("Gestión de Clientes y Productos - Acceso a Datos");
        setSize(950, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Clientes", crearPanelClientes());
        tabs.addTab("Productos", crearPanelProductos());
        add(tabs);
    }

    private JPanel crearPanelClientes() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        modeloClientes = new DefaultTableModel(new Object[]{"ID", "Nombre", "Email", "Teléfono"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaClientes = new JTable(modeloClientes);
        tablaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(tablaClientes), BorderLayout.CENTER);

        JPanel botones = new JPanel();
        JButton añadir = new JButton("Añadir");
        JButton editar = new JButton("Editar");
        JButton eliminar = new JButton("Eliminar");
        JButton exportar = new JButton("Exportar...");
        JButton importar = new JButton("Importar...");
        botones.add(añadir); botones.add(editar); botones.add(eliminar);
        botones.add(exportar); botones.add(importar);
        panel.add(botones, BorderLayout.SOUTH);

        añadir.addActionListener(e -> añadirCliente());
        editar.addActionListener(e -> editarCliente());
        eliminar.addActionListener(e -> eliminarCliente());
        exportar.addActionListener(e -> exportarClientes());
        importar.addActionListener(e -> importarClientes());
        return panel;
    }

    private JPanel crearPanelProductos() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        modeloProductos = new DefaultTableModel(new Object[]{"ID", "Nombre", "Precio", "Stock"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaProductos = new JTable(modeloProductos);
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(tablaProductos), BorderLayout.CENTER);

        JPanel botones = new JPanel();
        JButton añadir = new JButton("Añadir");
        JButton editar = new JButton("Editar");
        JButton eliminar = new JButton("Eliminar");
        JButton exportar = new JButton("Exportar...");
        JButton importar = new JButton("Importar...");
        botones.add(añadir); botones.add(editar); botones.add(eliminar);
        botones.add(exportar); botones.add(importar);
        panel.add(botones, BorderLayout.SOUTH);

        añadir.addActionListener(e -> añadirProducto());
        editar.addActionListener(e -> editarProducto());
        eliminar.addActionListener(e -> eliminarProducto());
        exportar.addActionListener(e -> exportarProductos());
        importar.addActionListener(e -> importarProductos());
        return panel;
    }

    private void añadirCliente() {
        try {
            String idTxt = pedir("ID:");
            if (idTxt == null) return;
            int id = Integer.parseInt(idTxt);
            if (servicio.buscarCliente(id) != null) {
                mostrarError("Ya existe un cliente con ese ID.");
                return;
            }
            String nombre = pedir("Nombre:");
            String email = pedir("Email:");
            String telefono = pedir("Teléfono:");
            if (nombre == null || email == null || telefono == null) return;
            if (nombre.isBlank() || email.isBlank()) {
                mostrarError("Nombre y email no pueden estar vacíos.");
                return;
            }
            servicio.añadirCliente(new Cliente(id, nombre.trim(), email.trim(), telefono.trim()));
            actualizarTablaClientes();
        } catch (NumberFormatException ex) {
            mostrarError("El ID debe ser un número entero.");
        }
    }

    private void editarCliente() {
        int fila = tablaClientes.getSelectedRow();
        if (fila == -1) { mostrarError("Selecciona un cliente."); return; }
        int id = (int) modeloClientes.getValueAt(fila, 0);
        Cliente c = servicio.buscarCliente(id);
        if (c == null) return;

        String nombre = JOptionPane.showInputDialog(this, "Nombre:", c.getNombre());
        if (nombre == null) return;
        String email = JOptionPane.showInputDialog(this, "Email:", c.getEmail());
        if (email == null) return;
        String telefono = JOptionPane.showInputDialog(this, "Teléfono:", c.getTelefono());
        if (telefono == null) return;

        if (nombre.isBlank() || email.isBlank()) {
            mostrarError("Nombre y email no pueden estar vacíos.");
            return;
        }
        c.setNombre(nombre.trim());
        c.setEmail(email.trim());
        c.setTelefono(telefono.trim());
        actualizarTablaClientes();
    }

    private void eliminarCliente() {
        int fila = tablaClientes.getSelectedRow();
        if (fila == -1) { mostrarError("Selecciona un cliente."); return; }
        int id = (int) modeloClientes.getValueAt(fila, 0);
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar el cliente " + id + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            servicio.eliminarCliente(id);
            actualizarTablaClientes();
        }
    }

    private void añadirProducto() {
        try {
            String idTxt = pedir("ID:");
            if (idTxt == null) return;
            int id = Integer.parseInt(idTxt);
            if (servicio.buscarProducto(id) != null) {
                mostrarError("Ya existe un producto con ese ID.");
                return;
            }
            String nombre = pedir("Nombre:");
            String precioTxt = pedir("Precio:");
            String stockTxt = pedir("Stock:");
            if (nombre == null || precioTxt == null || stockTxt == null) return;
            double precio = Double.parseDouble(precioTxt);
            int stock = Integer.parseInt(stockTxt);
            if (nombre.isBlank() || precio < 0 || stock < 0) {
                mostrarError("Nombre obligatorio; precio y stock no pueden ser negativos.");
                return;
            }
            servicio.añadirProducto(new Producto(id, nombre.trim(), precio, stock));
            actualizarTablaProductos();
        } catch (NumberFormatException ex) {
            mostrarError("ID y stock deben ser enteros; precio debe ser numérico.");
        }
    }

    private void editarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila == -1) { mostrarError("Selecciona un producto."); return; }
        int id = (int) modeloProductos.getValueAt(fila, 0);
        Producto p = servicio.buscarProducto(id);
        if (p == null) return;
        try {
            String nombre = JOptionPane.showInputDialog(this, "Nombre:", p.getNombre());
            if (nombre == null) return;
            String precioTxt = JOptionPane.showInputDialog(this, "Precio:", p.getPrecio());
            if (precioTxt == null) return;
            String stockTxt = JOptionPane.showInputDialog(this, "Stock:", p.getStock());
            if (stockTxt == null) return;
            double precio = Double.parseDouble(precioTxt);
            int stock = Integer.parseInt(stockTxt);
            if (nombre.isBlank() || precio < 0 || stock < 0) {
                mostrarError("Nombre obligatorio; precio y stock no pueden ser negativos.");
                return;
            }
            p.setNombre(nombre.trim()); p.setPrecio(precio); p.setStock(stock);
            actualizarTablaProductos();
        } catch (NumberFormatException ex) {
            mostrarError("Precio o stock tienen un formato incorrecto.");
        }
    }

    private void eliminarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila == -1) { mostrarError("Selecciona un producto."); return; }
        int id = (int) modeloProductos.getValueAt(fila, 0);
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar el producto " + id + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            servicio.eliminarProducto(id);
            actualizarTablaProductos();
        }
    }

    private void actualizarTablaClientes() {
        modeloClientes.setRowCount(0);
        for (Cliente c : servicio.getClientes())
            modeloClientes.addRow(new Object[]{c.getId(), c.getNombre(), c.getEmail(), c.getTelefono()});
    }

    private void actualizarTablaProductos() {
        modeloProductos.setRowCount(0);
        for (Producto p : servicio.getProductos())
            modeloProductos.addRow(new Object[]{p.getId(), p.getNombre(), p.getPrecio(), p.getStock()});
    }

    // El usuario elige el formato antes de importar/exportar.
    private String elegirFormato() {
        Object seleccion = JOptionPane.showInputDialog(
                this, "Selecciona el formato:", "Formato de fichero",
                JOptionPane.QUESTION_MESSAGE, null,
                new String[]{"TXT", "CSV", "JSON", "XML"}, "TXT");
        return seleccion == null ? null : seleccion.toString();
    }

    private String extension(String formato) {
        return formato.toLowerCase();
    }

    private void exportarClientes() {
        String formato = elegirFormato();
        if (formato == null) return;
        Path ruta = elegirFichero(true, "clientes." + extension(formato));
        if (ruta == null) return;
        try {
            switch (formato) {
                case "TXT" ->{
                    System.out.println("TXT todavía no implementado");

                }
                case "CSV" -> {
                    System.out.println("CSV todavía no implementado");

                }
                case "JSON" -> {
                    System.out.println("JSON todavía no implementado");

                }
                case "XML" -> {
                    System.out.println("XML todavía no implementado");

                }
            }
            informar("Clientes exportados en " + formato + ":\n" + ruta.toAbsolutePath());
        } catch (Exception ex) {
            mostrarError("Error exportando:\n" + ex.getMessage());
        }
    }

    private void importarClientes() {
        String formato = elegirFormato();
        if (formato == null) return;
        Path ruta = elegirFichero(false, "clientes." + extension(formato));
        if (ruta == null) return;
        try {
            List<Cliente> datos = switch (formato) {
                case "CSV" -> {
                    System.out.println("CSV todavía no implementado");
                    yield new ArrayList<>();
                }
                case "JSON" -> {
                    System.out.println("JSON todavía no implementado");
                    yield new ArrayList<>();
                }
                case "XML" -> {
                    System.out.println("XML todavía no implementado");
                    yield new ArrayList<>();
                }
                default -> {
                    System.out.println("TXT todavía no implementado");
                    yield new ArrayList<>();
                }
            };
            servicio.getClientes().clear();
            servicio.getClientes().addAll(datos);
            actualizarTablaClientes();
            informar("Clientes importados desde " + formato + ": " + datos.size());
        } catch (Exception ex) {
            mostrarError("Error importando:\n" + ex.getMessage());
        }
    }

    private void exportarProductos() {
        String formato = elegirFormato();
        if (formato == null) return;
        Path ruta = elegirFichero(true, "productos." + extension(formato));
        if (ruta == null) return;
        try {
            switch (formato) {
                case "TXT" -> System.out.println("No implementado");
                case "CSV" -> System.out.println("No implementado");
                case "JSON" -> System.out.println("No implementado");
                case "XML" -> System.out.println("No implementado");
            }
            informar("Productos exportados en " + formato + ":\n" + ruta.toAbsolutePath());
        } catch (Exception ex) {
            mostrarError("Error exportando:\n" + ex.getMessage());
        }
    }

    private void importarProductos() {
        String formato = elegirFormato();
        if (formato == null) return;
        Path ruta = elegirFichero(false, "productos." + extension(formato));
        if (ruta == null) return;
        try {
            List<Producto> datos = switch (formato) {
                case "CSV" -> {
                    System.out.println("CSV todavía no implementado");
                    yield new ArrayList<>();
                }
                case "JSON" -> {
                    System.out.println("JSON todavía no implementado");
                    yield new ArrayList<>();
                }
                case "XML" -> {
                    System.out.println("XML todavía no implementado");
                    yield new ArrayList<>();
                }
                default -> {
                    System.out.println("TXT todavía no implementado");
                    yield new ArrayList<>();
                }
            };
            servicio.getProductos().clear();
            servicio.getProductos().addAll(datos);
            actualizarTablaProductos();
            informar("Productos importados desde " + formato + ": " + datos.size());
        } catch (Exception ex) {
            mostrarError("Error importando:\n" + ex.getMessage());
        }
    }

    private Path elegirFichero(boolean guardar, String nombreSugerido) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File(nombreSugerido));
        int resultado = guardar ? chooser.showSaveDialog(this) : chooser.showOpenDialog(this);
        return resultado == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile().toPath() : null;
    }

    private String pedir(String mensaje) {
        return JOptionPane.showInputDialog(this, mensaje);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void informar(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }
}

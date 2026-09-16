package servicio;

import modelo.Cliente;
import modelo.Producto;
import java.util.ArrayList;
import java.util.List;

public class TiendaService {
    private final List<Cliente> clientes = new ArrayList<>();
    private final List<Producto> productos = new ArrayList<>();

    public List<Cliente> getClientes() { return clientes; }
    public List<Producto> getProductos() { return productos; }

    public void añadirCliente(Cliente cliente) { clientes.add(cliente); }

    public Cliente buscarCliente(int id) {
        for (Cliente c : clientes) if (c.getId() == id) return c;
        return null;
    }

    public boolean eliminarCliente(int id) {
        return clientes.removeIf(c -> c.getId() == id);
    }

    public void añadirProducto(Producto producto) { productos.add(producto); }

    public Producto buscarProducto(int id) {
        for (Producto p : productos) if (p.getId() == id) return p;
        return null;
    }

    public boolean eliminarProducto(int id) {
        return productos.removeIf(p -> p.getId() == id);
    }
}

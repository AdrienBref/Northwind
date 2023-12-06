package database;

import models.Producto;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class GestorDdbb {

    private Connection conexion  = null;
    private Statement statement = null;
    private String nombreDB = "";
    private  Scanner scanner = new Scanner(System.in);
    ResultSet resultSetShowProducts = null;


    public GestorDdbb(String jdbcURL, String usuario, String contraseña, String nombreBD) {
        try {
            conexion = DriverManager.getConnection(jdbcURL, usuario, contraseña);
            statement = conexion.createStatement();
            this.nombreDB = nombreBD;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createDdbb(String nombreBD) {

        try {
            // Consulta SQL para crear la base de datos si no existe
            String sqlCrearBD = "CREATE DATABASE IF NOT EXISTS " + nombreBD;
            statement.executeUpdate(sqlCrearBD);
            System.out.println("Base de datos creada con éxito.");


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTables(String nombreDdbb) {

        //TODO: Hacer los finally

        try {
            // Seleccionar la base de datos
            String sqlSeleccionarBD = "USE " + nombreDdbb;
            statement.execute(sqlSeleccionarBD);

            // Crear tabla Productos
            String sqlCrearTablaProductos = "CREATE TABLE IF NOT EXISTS Productos ("
                    + "id INT NOT NULL AUTO_INCREMENT, "
                    + "nombre VARCHAR(100) NOT NULL, "
                    + "descripcion TEXT NOT NULL, "
                    + "cantidad INT NOT NULL, "
                    + "precio DECIMAL(10, 2) NOT NULL, "
                    + "PRIMARY KEY (id)"
                    + ")";
            statement.executeUpdate(sqlCrearTablaProductos);

            // Crear tabla Empleados
            String sqlCrearTablaEmpleados = "CREATE TABLE IF NOT EXISTS Empleados ("
                    + "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,"
                    + "nombre VARCHAR(255),"
                    + "apellidos VARCHAR(255),"
                    + "email VARCHAR(255)"
                    + ")";
            statement.executeUpdate(sqlCrearTablaEmpleados);

            // Crear tabla Pedidos
            String sqlCrearTablaPedidos = "CREATE TABLE IF NOT EXISTS Pedidos ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "id_producto INT,"
                    + "descripcion TEXT,"
                    + "precio_total DECIMAL(10, 2),"
                    + "FOREIGN KEY (id_producto) REFERENCES Productos(id)"
                    + ")";
            statement.executeUpdate(sqlCrearTablaPedidos);

            // Crear tabla Productos_Fav
            String sqlCrearTablaProductosFav = "CREATE TABLE IF NOT EXISTS Productos_Fav ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "id_producto INT,"
                    + "FOREIGN KEY (id_producto) REFERENCES Productos(id)"
                    + ")";
            statement.executeUpdate(sqlCrearTablaProductosFav);

            System.out.println("Tablas creadas con éxito.");

/*            statement.close();
            conexion.close();*/
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertarJsonEnDdbb(ArrayList<Producto> arrayProductosEnMemoria) {
        String insertQuery = "INSERT INTO Productos (id, nombre, descripcion, cantidad, precio) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement statementInsert = conexion.prepareStatement(insertQuery);
            for(Producto producto : arrayProductosEnMemoria) {
                if(!existeProducto(producto)) {
                    statementInsert.setInt(1, producto.getId());
                    statementInsert.setString(2, producto.getNombre());
                    statementInsert.setString(3, producto.getDescripcion());
                    statementInsert.setInt(4, producto.getCantidad());
                    statementInsert.setDouble(5, producto.getPrecio());
                    statementInsert.execute();
                } else {
                    //System.out.println("La referencia con valores " + producto.toString() + " ya existente en " + nombreDB);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean existeProducto(Producto producto) {

        String existenceQuery = "SELECT COUNT(*) FROM Productos WHERE id = ? AND nombre = ? AND descripcion = ? AND cantidad = ? AND precio = ?";
        int count = 0;
        try(PreparedStatement preparedStatementExist = conexion.prepareStatement(existenceQuery)) {
            preparedStatementExist.setInt(1, producto.getId());
            preparedStatementExist.setString(2, producto.getNombre());
            preparedStatementExist.setString(3, producto.getDescripcion());
            preparedStatementExist.setInt(4, producto.getCantidad());
            preparedStatementExist.setDouble(5, producto.getPrecio());

            try(ResultSet resultSet = preparedStatementExist.executeQuery()) {
                resultSet.next();
                count = resultSet.getInt(1);

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return count > 0;
    }

    public void añadirEmpleado(){

        PreparedStatement preparedStatementEmployees = null;

        String nombre      = "";
        String apellidos   = "";
        String email       = "";
        String consultaSQL = "INSERT INTO Empleados (nombre, apellidos, email) VALUES (?,?,?)";

        System.out.println("\n***************************************");
        System.out.println("*            _N_o_r_t_h_W_i_n_d_        *");
        System.out.println("*       MENÚ DE AGREGAR EMPLEADOS       *");
        System.out.println("*****************************************");

        System.out.println("Introduzca su nombre");
        nombre = scanner.nextLine();
        System.out.println("Intoduzca su primer apellido");
        apellidos = scanner.nextLine();
        System.out.println("Introduzca Email");
        email = scanner.nextLine();

        try {
            preparedStatementEmployees = conexion.prepareStatement(consultaSQL);
            System.out.println("Procesando petición...");
            preparedStatementEmployees.setString(1, nombre);
            preparedStatementEmployees.setString(2, apellidos);
            preparedStatementEmployees.setString(3, email);
            preparedStatementEmployees.execute();

            System.out.println("Empleado introducido en base de datos");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
    public void añadirPedido(){

        PreparedStatement preparedStatementProducts = null;

        int idProducto     = 0;
        String consultaSQLIntroducePedido = "INSERT INTO Pedidos (id_producto, descripcion, precio_total) VALUES (?,?,?)";
        String consultaSQLBuscaPorID = "SELECT * FROM Productos WHERE id = ?";

        System.out.println("\n***************************************");
        System.out.println("*            _N_o_r_t_h_W_i_n_d_        *");
        System.out.println("*          MENÚ DE AGREGAR PEDIDOS      *");
        System.out.println("*****************************************");

        try {
            //Buscar los productos que tengo en la tabla Productos
            resultSetShowProducts = statement.executeQuery("SELECT id, nombre, descripcion, cantidad, precio FROM Productos");
            while(resultSetShowProducts.next()) {
                System.out.print("Producto con ID: " + resultSetShowProducts.getInt("id") + " ");
                System.out.println(resultSetShowProducts.getString("nombre"));
                System.out.println("");
            }
            //Pido el id del producto para introducirlo en la busqueda
            System.out.println("Introduzca id_Producto");
            idProducto = scanner.nextInt();
            //Busco el producto
            PreparedStatement preparedStatementBusquedaProducto = conexion.prepareStatement(consultaSQLBuscaPorID);
            preparedStatementBusquedaProducto.setInt(1, idProducto);
            ResultSet resultado = preparedStatementBusquedaProducto.executeQuery();

            while (resultado.next()) {
                preparedStatementProducts = conexion.prepareStatement(consultaSQLIntroducePedido);
                int id = resultado.getInt("id");
                String nombre = resultado.getString("nombre");
                String descripcion = resultado.getString("descripcion");
                int cantidad = resultado.getInt("cantidad");
                double precio = resultado.getDouble("precio");

                preparedStatementProducts.setInt(1, id);
                preparedStatementProducts.setString(2, descripcion);
                preparedStatementProducts.setDouble(3, precio);
                preparedStatementProducts.execute();

            }

            System.out.println("Pedido introducido en base de datos");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void mostrarEmpleados(){

        System.out.println("Empleados en Base de Datos");
        ResultSet resultSetShowEmployees = null;

        try {
            resultSetShowEmployees = statement.executeQuery("SELECT id, nombre, apellidos, email FROM Empleados");
            while(resultSetShowEmployees.next()) {
                System.out.println("Empleado con ID = " + resultSetShowEmployees.getInt("id"));
                System.out.println(resultSetShowEmployees.getString("nombre"));
                System.out.println(resultSetShowEmployees.getString("apellidos"));
                System.out.println(resultSetShowEmployees.getString("email"));
                System.out.println("");
            }

            System.out.println("Pulsar Enter para continuar");
            scanner.nextLine();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    public void mostrarPedidos(){
        System.out.println("Pedidos en Base de Datos");
        ResultSet resultSetShowOrders = null;

        try {
            resultSetShowOrders = statement.executeQuery("SELECT id, id_producto, descripcion, precio_total FROM Pedidos");
            while(resultSetShowOrders.next()) {
                System.out.println("Pedido con ID = " + resultSetShowOrders.getInt("id"));
                System.out.println("id_producto " + resultSetShowOrders.getInt("id_producto"));
                System.out.println(resultSetShowOrders.getString("descripcion"));
                System.out.println(resultSetShowOrders.getString("precio_total"));
                System.out.println("");
            }

            System.out.println("Pulsar Enter para continuar");
            scanner.nextLine();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void mostrarProductos(){

        System.out.println("Productos");


        try {
            resultSetShowProducts = statement.executeQuery("SELECT id, nombre, descripcion, cantidad, precio FROM Productos");
            while(resultSetShowProducts.next()) {
                System.out.println("Producto con ID: " + resultSetShowProducts.getInt("id") + " ");
                System.out.println(resultSetShowProducts.getString("nombre"));
                System.out.println(resultSetShowProducts.getString("descripcion"));
                System.out.println(resultSetShowProducts.getInt("cantidad"));
                System.out.println(resultSetShowProducts.getDouble("precio"));
                System.out.println("");
            }

            System.out.println("Pulsar Enter para continuar");
            scanner.nextLine();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    public void mostrarProductosMenores600(){

        String consultaSQLBuscaPorPrecio = "SELECT * FROM Productos WHERE precio <= 600";
        PreparedStatement preparedStatementBusquedaProducto = null;
        try {
            preparedStatementBusquedaProducto = conexion.prepareStatement(consultaSQLBuscaPorPrecio);
            ResultSet resultado = preparedStatementBusquedaProducto.executeQuery();

            System.out.println("Productos de menos de 600€");

            while (resultado.next()) {

                int id = resultado.getInt("id");
                String nombre = resultado.getString("nombre");
                String descripcion = resultado.getString("descripcion");
                int cantidad = resultado.getInt("cantidad");
                double precio = resultado.getDouble("precio");

                System.out.println("");
                System.out.println("identificador: " + id);
                System.out.println("nombre: " + nombre);
                System.out.println("descripcion: " + descripcion);
                System.out.println("cantidad en Stock: " + cantidad);
                System.out.println("precio: " + precio);
                System.out.println("");

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    public void insertarProductosFav(){

        String consultaSQLIntroduceProductoFav = "INSERT INTO Productos_Fav (id_producto) VALUES (?)";
        String consultaSQLBuscaPorPrecio = "SELECT * FROM Productos WHERE precio > 1000";
        PreparedStatement preparedStatementBusquedaProducto = null;
        PreparedStatement preparedStatementInsertarProducto = null;
        try {
            preparedStatementBusquedaProducto = conexion.prepareStatement(consultaSQLBuscaPorPrecio);
            preparedStatementInsertarProducto = conexion.prepareStatement(consultaSQLIntroduceProductoFav);
            ResultSet resultado = preparedStatementBusquedaProducto.executeQuery();

            System.out.println("Productos de más de 1000€ ");

            while (resultado.next()) {

                int id = resultado.getInt("id");
                String nombre = resultado.getString("nombre");
                String descripcion = resultado.getString("descripcion");
                int cantidad = resultado.getInt("cantidad");
                double precio = resultado.getDouble("precio");

                System.out.println("");
                System.out.println("identificador: " + id);
                System.out.println("nombre: " + nombre);
                System.out.println("descripcion: " + descripcion);
                System.out.println("cantidad en Stock: " + cantidad);
                System.out.println("precio: " + precio);
                System.out.println("");

                preparedStatementInsertarProducto.setInt(1,id);
                preparedStatementInsertarProducto.execute();

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    public void cerrarConexion(){
        try {
            statement.close();
            conexion.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}




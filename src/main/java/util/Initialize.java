package util;

import client.HttpClient;
import database.GestorDdbb;
import models.Producto;
import java.util.ArrayList;
import java.util.Scanner;

public class Initialize {

    private String jdbcURL = "jdbc:mysql://localhost:3309/";
    private String usuario = "root";
    private String contraseña = "example";
    private String nombreBD = "NorthwindDdbb";
    private String apiurl = "https://dummyjson.com/products";
    GestorDdbb gestorDdbb = null;
    HttpClient httpClient = null;
    JsonParser jsonParser = null;

    public Initialize() {

        StringBuilder apiData = null;
        ArrayList<Producto> arrayDeProductos = new ArrayList<Producto>();

        //Connect to Ddbb
        gestorDdbb = new GestorDdbb(jdbcURL, usuario, contraseña, nombreBD);
        System.out.println("Connected to " + nombreBD);

        //Create ddbb and Tables with columns if don't Exist
        gestorDdbb.createDdbb(nombreBD);
        gestorDdbb.createTables(nombreBD);

        //Conecto con la Api y extraigo el Json
        httpClient = new HttpClient();
        apiData = httpClient.getAPIData(httpClient.clientHttpConnect(apiurl));
        System.out.println("Se ha conectado al servicio Api y se han extraido los datos correctamente");

        //Inserto los productos en un Array de tipo Producto
        jsonParser = new JsonParser();
        jsonParser.toProductArray(apiData, arrayDeProductos);
        System.out.println("Volcado de datos JSON a la memoria");

        //Inserta productos en base de datos
        gestorDdbb.insertarJsonEnDdbb(arrayDeProductos);
        System.out.println("Productos cargados correctamente");
    }

    public void initMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        do {
            System.out.println("\u001B[94m" + "===== NORTHWIND DB =====\n" + "\u001B[0m" +
                    "Seleccione una opción:\n" +
                    " 1 - Agregar un empleado\n" +
                    " 2 - Agregar un pedido\n" +
                    " 3 - Mostrar empleados\n" +
                    " 4 - Mostrar pedidos\n" +
                    " 5 - Mostrar productos\n" +
                    " 6 - Mostrar productos de menos de 600 euros\n" +
                    " 7 - Insertar productos de más de 1000 euros en productos_fav\n" +
                    " 8 - Salir\n" +
                    "\u001B[94m" + "========================\n" + "\u001B[0m"
            );

            String opcion = scanner.nextLine();
            switch (opcion) {
                    case "1":
                        gestorDdbb.añadirEmpleado();
                        break;
                    case "2":
                        gestorDdbb.añadirPedido();
                        break;
                    case "3":
                        gestorDdbb.mostrarEmpleados();
                        break;
                    case "4":
                        gestorDdbb.mostrarPedidos();
                        break;
                    case "5":
                        gestorDdbb.mostrarProductos();
                        break;
                    case "6":
                        gestorDdbb.mostrarProductosMenores600();
                        break;
                    case "7":
                        gestorDdbb.insertarProductosFav();
                        break;
                    case "8":
                        gestorDdbb.cerrarConexion();
                        exit = true;
                        break;
                }

        } while (!exit);
    }
}

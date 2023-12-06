package util;

import models.Producto;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonParser {

    JSONObject jsonObjectproducts = null;
    JSONArray jsonArrayProducts = null;

    public JsonParser() {

    }

    public ArrayList<Producto> toProductArray(StringBuilder stringJson, ArrayList<Producto> arrayProduct) {

        try {
            jsonObjectproducts = new JSONObject(stringJson.toString());
            jsonArrayProducts  = jsonObjectproducts.getJSONArray("products");

            for(int i = 0; i < jsonArrayProducts.length(); i++) {

                JSONObject jsonProduct = jsonArrayProducts.getJSONObject(i);
                int id = jsonProduct.getInt("id");
                String nombre = jsonProduct.getString("title");
                String descripcion = jsonProduct.getString("description");
                int cantidad = jsonProduct.getInt("stock");
                double precio = jsonProduct.getDouble("price");

                arrayProduct.add(new Producto(id, nombre, descripcion, cantidad,precio));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return arrayProduct;
    }



}

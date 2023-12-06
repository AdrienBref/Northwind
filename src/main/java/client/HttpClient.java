package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpClient {

    HttpURLConnection httpUrlConnection = null;
    InputStreamReader inputStreamReader = null;
    BufferedReader bufferedReader = null;

    public HttpClient() {

    }

    public HttpURLConnection clientHttpConnect(String urlDirecction) {

        try {
            URL url = new URL(urlDirecction);
            httpUrlConnection = (HttpURLConnection) url.openConnection();

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return httpUrlConnection;
    }

    public StringBuilder getAPIData (HttpURLConnection httpURLConnection) {

        StringBuilder stringBuilder = new StringBuilder();

        try {
            inputStreamReader = new InputStreamReader(httpUrlConnection.getInputStream());
            bufferedReader = new BufferedReader(inputStreamReader);

            while(bufferedReader.ready()) {
                stringBuilder.append(bufferedReader.readLine());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return stringBuilder;
    }

}

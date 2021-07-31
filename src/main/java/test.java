import com.wavjaby.json.JsonArray;
import com.wavjaby.json.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class test {
    public static void main(String[] args) {
        String result = getDataFromUrl("https://data.ecochia.io/api/explorer/price/?days=" + 90);
        if (result == null) return;

        JsonObject response = new JsonObject(result);
        System.out.println(response.toStringBeauty());
        JsonArray netSpaceData = response.getJsonArray("message");

    }

    public static String getDataFromUrl(String urlString) {
        try {
            //connection api
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setConnectTimeout(6000);

            int code = connection.getResponseCode();
            if (code > 299)
                return null;

            //read result
            StringBuilder result = new StringBuilder();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String readLine;
            while ((readLine = in.readLine()) != null) {
                result.append(readLine);
            }
            //close connection
            in.close();
            connection.disconnect();
            return result.toString();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }
}

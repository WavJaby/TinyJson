import com.wavjaby.json.JsonArray;
import com.wavjaby.json.JsonObject;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class test {

    @Test
    public void emptyTest() {
        JsonObject jsonObjectButArray = new JsonObject("[]");
        JsonObject jsonObject = new JsonObject("{}");

        assertTrue(jsonObjectButArray.isJsonArray());
        assertFalse(jsonObject.isJsonArray());
    }

    @Test
    public void jsonArrayTest() {
        JsonArray jsonArray = new JsonArray("[1,2,3]");
        System.out.println(jsonArray.toStringBeauty());
        jsonArray = new JsonArray("[[1,2,3],[4,5,6]]");
        System.out.println(jsonArray.toStringBeauty());
    }

    @Test
    public void jsonObjectTest() {
        JsonObject jsonObject = new JsonObject("{\"a\":\"abc\"}");
        System.out.println(jsonObject.toStringBeauty());
        jsonObject = new JsonObject("{\"a\":{\"a\":\"abc\",\"b\":[123,\"456\"]}}");
        System.out.println(jsonObject.toStringBeauty());
    }

    //    @Test
    public void speedTest() {
        String result = getDataFromUrl("https://data.ecochia.io/api/explorer/price/?days=" + 10);
        if (result == null) return;
        JsonArray price = new JsonObject(result).getArray("message");
        System.out.println(price.toStringBeauty());
    }


    //        String result = getDataFromUrl("https://data.ecochia.io/api/explorer/price/?days=" + 1);
//        if (result == null) return;
//
//        JsonObject response = new JsonObject(result);
////        System.out.println(response.toStringBeauty());
//        JsonArray netSpaceData = response.get("message");
//        System.out.println(netSpaceData.toStringBeauty());
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

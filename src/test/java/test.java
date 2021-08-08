import com.wavjaby.json.JsonArray;
import com.wavjaby.json.JsonObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.*;

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
        String input = "[1,2,3]";
        JsonArray jsonArray = new JsonArray(input);
        assertEquals(input, jsonArray.toString());
        input = "[[1,2,3],[4,5,6]]";
        jsonArray = new JsonArray(input);
        assertEquals(input, jsonArray.toString());
    }

    @Test
    public void jsonObjectTest() {
        String input = "{\"a\":\"abc\"}";
        JsonObject jsonObject = new JsonObject(input);
        assertEquals(input, jsonObject.toString());
        input = "{\"a\":{\"a\":\"abc\",\"b\":[123,\"456\"]}}";
        jsonObject = new JsonObject(input);
        assertEquals(input, jsonObject.toString());
    }

    @Test
    public void jsonArrayValueTest() {
        JsonArray jsonArray = new JsonArray("[[1,2,3],[4,5,6]]");
        assertEquals(1, (int) jsonArray.getArray(0).get(0));
        assertEquals(1, jsonArray.getArray(0).getInt(0));
    }

    @Test
    public void speedTest() {
        String result = getDataFromUrl("https://data.ecochia.io/api/explorer/price/?days=" + 90);
        if (result == null) return;
        int times = 1000;
        long startTime;

        for (int i = 0; i < times; i++) {
            String a = "abc";
            for (int j = 0; j < 1000; j++) {
                a += "abc";
            }
        }


        startTime = System.nanoTime();
        for (int i = 0; i < times; i++) {
            JsonArray price = new JsonObject(result).getArray("message");
            for (Object j : price) {
                JsonArray arr = (JsonArray) j;
                assertNotNull(arr.getString(0));
                assertNotNull(arr.getDouble(1));
            }
        }
        System.out.println((double) ((System.nanoTime() - startTime) / times) / 1000000d);

        startTime = System.nanoTime();
        for (int i = 0; i < times; i++) {
            JSONArray price = new JSONObject(result).getJSONArray("message");
            for (Object j : price) {
                JSONArray arr = (JSONArray) j;
                assertNotNull(arr.getString(0));
                assertNotNull(arr.getDouble(1));
            }
        }
        System.out.println((double) ((System.nanoTime() - startTime) / times) / 1000000d);
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

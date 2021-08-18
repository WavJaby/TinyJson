import com.wavjaby.json.JsonArray;
import com.wavjaby.json.JsonObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

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
        long eTime = System.currentTimeMillis() / 1000;
        long sTime = eTime - 60 * 60 * 24 * 5;
        int step = (int) ((eTime - sTime) / 100);
        String popUrl = "https://grafana.tipsy.coffee/api/datasources/proxy/1/api/v1/query_range?query=" +
                (true ? "max(popcat)" : "sum(rate(popcat%5B5m%5D))") +
                "%20by%20(region)&start=" + sTime + "&end=" + eTime + "&step=" + step;
        String result = getDataFromUrl(popUrl);
        System.out.println(popUrl);
        System.out.println(result.getBytes(StandardCharsets.UTF_8).length / 1000f / 1000f + "MB");
        if (result == null) return;

        for (int i = 0; i < 10000; i++) {
            String a = "abc";
            for (int j = 0; j < 1000; j++) {
                a += "abc";
            }
        }

        int avgTimes = 10;
        int times = 100;
        long startTime;
        long endTime;

        long tinyJsonTime = 0;
        long orgJsonTime = 0;

        System.out.println("loop " + times + " times");
        System.out.println("avg " + avgTimes + " times");
        for (int k = 0; k < avgTimes; k++) {
            startTime = System.nanoTime();
            for (int i = 0; i < times; i++) {
                JSONObject data = new JSONObject(result).getJSONObject("data");
                JSONArray value = data.getJSONArray("result");
                for (Object j : value) {
                    JSONObject info = (JSONObject) j;
                    assertTrue(info.has("metric"));
                    assertTrue(info.has("values"));
                }
            }
            endTime = System.nanoTime();
            orgJsonTime += ((endTime - startTime) / times);
            System.out.println("OrgJson: " + (double) ((endTime - startTime) / times) / 1000000d + "ms");
        }
        System.out.println("OrgJson avg: " + (double) (orgJsonTime / avgTimes) / 1000000d + "ms");

        for (int k = 0; k < avgTimes; k++) {
            startTime = System.nanoTime();
            for (int i = 0; i < times; i++) {
                JsonObject data = new JsonObject(result).getJson("data");
                JsonArray value = data.getArray("result");
                for (Object j : value) {
                    JsonObject info = (JsonObject) j;
                    assertTrue(info.containsKey("metric"));
                    assertTrue(info.containsKey("values"));
                }
            }
            endTime = System.nanoTime();
            tinyJsonTime += ((endTime - startTime) / times);
            System.out.println("TinyJson: " + (double) ((endTime - startTime) / times) / 1000000d + "ms");
        }
        System.out.println("TinyJson avg: " + (double) (tinyJsonTime / avgTimes) / 1000000d + "ms");
    }

    @Test
    public void test() {
        //get video info
        String url = "https://youtubei.googleapis.com/youtubei/v1/player?key=AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8";
        String payload = "{\"videoId\":\"" + "hw1b6fnWYc4" + "\",\"context\":{\"client\":{\"hl\":\"zh\",\"gl\":\"TW\",\"clientName\":\"WEB\",\"clientVersion\":\"2.20210330.08.00\"}}}";
        String result = getUrl(url, payload);
        new JsonObject(result);
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

    public String getUrl(String input, String payload) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(input).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            //post
            OutputStream payloadOut = connection.getOutputStream();
            payloadOut.write(payload.getBytes(StandardCharsets.UTF_8));
            payloadOut.flush();
            //get
            InputStream in;
            if (connection.getResponseCode() > 399)
                in = connection.getErrorStream();
            else
                in = connection.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int length;
            while ((length = in.read(buff)) > 0) {
                out.write(buff, 0, length);
            }
            return out.toString("UTF8");
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }
}

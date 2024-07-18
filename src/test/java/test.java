import com.wavjaby.json.JsonArray;
import com.wavjaby.json.JsonObject;
import com.wavjaby.json.ListedJsonObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class test {

    @Test
    public void emptyTest() {
        ListedJsonObject listedJsonObjectButArray = new ListedJsonObject("[]");
        ListedJsonObject listedJsonObject = new ListedJsonObject("{}");
        JsonObject jsonObject = new JsonObject("{}");

        assertTrue(listedJsonObjectButArray.isJsonArray());
        assertFalse(listedJsonObject.isJsonArray());
        assertEquals("[]", listedJsonObjectButArray.toString());
        assertEquals("{}", listedJsonObject.toString());
        assertEquals("{}", jsonObject.toString());
    }

    @Test
    public void jsonErrorTest() {
        String jsonWithError = "{\n" +
                "    \"registered\\u0077\": null,\n" +
                "    \"_id\": \"\\\"63d238c2de7eacc4b1ad9fa7\",\n" +
                "    \"guid\": \"5a68422f-955d-4082-9bf7-8d4c6f804764\",\n" +
                "    \"isActive\": false,\n" +
                "    \"name\": \"Christensen Merrill\",\n" +
                "    \"latitude\": 29.222857,\n" +
                "    \"longitude\": -24.634618,\n" +
                "    \"tags\": [\n" +
                "        \"nulla\",\n" +
                "        \"adipisicing\",\n" +
                "        \"laborum\",\n" +
                "        \"dolore\"\n" +
                "    ],\n" +
                "    \"greeting\": \"Hello, Christensen Merrill! \\nYou have 10 unread messages.\",\n" +
                "    \"float1\": -5.282470793050557e-10,\n" +
                "    \"float2\": 2.767696079401829e+24,\n" +
                "    \"int\": -1811226837,\n" +
                "    \"long\": 3006307585353351000\n" +
                "}";
//        System.out.println(jsonWithError);
        try {
            JsonObject jsonObject = new JsonObject(jsonWithError);
            System.out.println(jsonObject.getString("greeting"));
            System.out.println(jsonObject.getString("_id"));
            System.out.println(jsonObject.toStringBeauty());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            JSONObject jsonObject = new JSONObject(jsonWithError);
            System.out.println(jsonObject.getString("greeting"));
            System.out.println(jsonObject.toString(4));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        input = "{\"a\":{\"a\":\"abc\",\"b\":[123,\"456\",{\"a\":1}]}}";
        jsonObject = new JsonObject(input);
        assertEquals("abc", jsonObject.get("a").getString("a"));
        assertEquals(input, jsonObject.toString());
        System.out.println(jsonObject.toStringBeauty());
    }

    @Test
    public void jsonArrayValueTest() {
        JsonArray jsonArray = new JsonArray("[[1,2,3],[4,5,6]]");
        assertEquals(1, jsonArray.getArray(0).getInt(0));
    }

    @Test
    public void jsonArrayRemoveTest() {
        JsonArray jsonArray = new JsonArray("[1,2,3,4,5,6,7]");
        jsonArray.remove(0);
        assertEquals("[2,3,4,5,6,7]", jsonArray.toString());
        jsonArray.remove(1);
        assertEquals("[2,4,5,6,7]", jsonArray.toString());
        jsonArray.remove(4);
        assertEquals("[2,4,5,6]", jsonArray.toString());
        new ArrayList<>();
        JsonArray jsonArray1 = new JsonArray();
//        Field field;
//        try {
//            field = JsonArray.class.getDeclaredField("items");
//            field.setAccessible(true);
//        } catch (NoSuchFieldException e) {
//            throw new RuntimeException(e);
//        }

        for (int c = 0; c < 10; c++) {
//            int last = 0;
            for (int i = 0; i < 100000; i++) {
//            try {
//                Object[] o = (Object[]) field.get(jsonArray1);
//                if(last != o.length)
//                    System.out.println(last = o.length);
//            } catch (IllegalAccessException e) {
//                throw new RuntimeException(e);
//            }
                jsonArray1.add(i);
            }
//        System.out.println(jsonArray1.toString());
            long start = System.currentTimeMillis();
            for (; jsonArray1.length > 0; ) {
//            try {
//                Object[] o = (Object[]) field.get(jsonArray1);
//                if(last != o.length)
//                    System.out.println(last = o.length);
//            } catch (IllegalAccessException e) {
//                throw new RuntimeException(e);
//            }
                jsonArray1.indexOf((int) (jsonArray1.length / 2));
                jsonArray1.remove(0);
            }
//        System.out.println(jsonArray1.toString());
            System.out.println(System.currentTimeMillis() - start);
        }
    }

    @Test
    public void bigIntegerTest() {
        String input = "{\"id\":\"BlockchainSummary\",\"netspace\":39700684056485050000,\"difficulty\":2768,\"uniqueCoins\":1605728,\"supply\":22605728000000000000,\"addressCount\":6372031,\"price\":221.15,\"averageFees\":2.99477e-7,\"power\":53075}";
        JsonObject json = new JsonObject(input);
        assertEquals(json.getBigInteger("netspace").toString(), "39700684056485050000");
        assertEquals(json.getBigInteger("difficulty").toString(), "2768");
//        System.out.println(json.getFloat("averageFees"));
    }

    @Test
    public void speedTest() throws InterruptedException {
//        long eTime = System.currentTimeMillis() / 1000;
//        long sTime = eTime - 60 * 60 * 24 * 5;
//        int step = (int) ((eTime - sTime) / 100);
//        String popUrl = "https://grafana.tipsy.coffee/api/datasources/proxy/1/api/v1/query_range?query=" +
//                (true ? "max(popcat)" : "sum(rate(popcat%5B5m%5D))") +
//                "%20by%20(region)&start=" + sTime + "&end=" + eTime + "&step=" + step;
//        String result = getDataFromUrl(popUrl);
//        System.out.println(popUrl);
//        System.out.println(result.getBytes(StandardCharsets.UTF_8).length / 1000f / 1000f + "MB");
//        if (result == null) return;

        String result = null;
        try {
            InputStream stream = Files.newInputStream(Paths.get("src\\test\\resources\\JsonTextBig.json"));
            InputStreamReader inputReader = new InputStreamReader(stream, StandardCharsets.UTF_8);
            CharArrayWriter out = new CharArrayWriter();
            char[] buff = new char[1024];
            int len;
            while ((len = inputReader.read(buff, 0, buff.length)) > 0)
                out.write(buff, 0, len);
            inputReader.close();
            result = new String(out.toCharArray());
        } catch (IOException e) {
            e.printStackTrace();
        }

        int avgTimes = 10;
        int times = 15;
        long startTime;
        long endTime;

        long tinyJsonTime = 0;
        long orgJsonTime = 0;

        System.out.println(result.getBytes(StandardCharsets.UTF_8).length / 1000f / 1000f + "MB");
        System.out.println("loop " + times + " times");
        System.out.println("avg " + avgTimes + " times");

        Thread.sleep(3000);

        for (int k = 0; k < avgTimes; k++) {
            startTime = System.nanoTime();
            for (int i = 0; i < times; i++) {
                JsonArray data = new JsonArray(result);
                for (Object j : data) {
                    JsonObject info = (JsonObject) j;
                    assertTrue(info.containsKey("about"));
                    assertTrue(info.containsKey("_id"));
                }
                String ignore_ = data.toString();
            }
            endTime = System.nanoTime();
            tinyJsonTime += ((endTime - startTime) / times);
            System.out.println("TinyJson: " + (double) ((endTime - startTime) / times) / 1000000d + "ms");

            startTime = System.nanoTime();
            for (int i = 0; i < times; i++) {
                JSONArray data = new JSONArray(result);
                for (Object j : data) {
                    JSONObject info = (JSONObject) j;
                    assertTrue(info.has("about"));
                    assertTrue(info.has("_id"));
                }
                String ignore_ = data.toString();
            }
            endTime = System.nanoTime();
            orgJsonTime += ((endTime - startTime) / times);
            System.out.println("OrgJson: " + (double) ((endTime - startTime) / times) / 1000000d + "ms");
        }
        System.out.println("TinyJson avg: " + (double) (tinyJsonTime / avgTimes) / 1000000d + "ms");
        System.out.println("OrgJson avg: " + (double) (orgJsonTime / avgTimes) / 1000000d + "ms");
    }

    @Test
    public void test() {
        //get video info
        String url = "https://youtubei.googleapis.com/youtubei/v1/player?key=AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8";
        String payload = "{\"videoId\":\"" + "hw1b6fnWYc4" + "\",\"context\":{\"client\":{\"hl\":\"zh\",\"gl\":\"TW\",\"clientName\":\"WEB\",\"clientVersion\":\"2.20210330.08.00\"}}}";
        String result = getUrl(url, payload);
        System.out.println(new JsonObject(result).toStringBeauty());
        System.out.println(new ListedJsonObject(result).toStringBeauty());
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

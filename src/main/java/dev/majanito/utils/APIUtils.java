package dev.majanito.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class APIUtils {
    public static String[] getProfileInfo(String token) throws IOException {
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpGet request = new HttpGet("https://api.minecraftservices.com/minecraft/profile");
            request.setHeader("Authorization", "Bearer " + token);
            CloseableHttpResponse response = client.execute(request);
            String jsonString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
            String IGN = jsonObject.get("name").getAsString();
            String UUID = jsonObject.get("id").getAsString();
            return new String[] {IGN, UUID};
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public static Boolean validateSession(String token) {
        try {
            String[] profileInfo = getProfileInfo(token);
            String ign = profileInfo[0];
            String uuidString = profileInfo[1];

            if (uuidString.length() == 32) {
                uuidString = uuidString.substring(0, 8) + "-" + uuidString.substring(8, 12) + "-" + uuidString.substring(12, 16) + "-" + uuidString.substring(16, 20) + "-" + uuidString.substring(20);
            }

            UUID uuid = UUID.fromString(uuidString);

            return ign.equals(MinecraftClient.getInstance().getSession().getUsername()) && uuid.equals(MinecraftClient.getInstance().getSession().getUuidOrNull());
        } catch (Exception e) {
            return false;
        }
    }

    public static int changeSkin(String url, String token) {
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost request = new HttpPost("https://api.minecraftservices.com/minecraft/profile/skins");
            request.setHeader("Authorization", "Bearer " + token);
            request.setHeader("Content-Type", "application/json");
            String jsonString = String.format("{ \"variant\": \"classic\", \"url\": \"%s\"}", url);
            request.setEntity(new StringEntity(jsonString));
            CloseableHttpResponse response = client.execute(request);
            return response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            return -1;
        }
    }

    public static int changeName(String newName, String token) {
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPut request = new HttpPut("https://api.minecraftservices.com/minecraft/profile/name/" + newName);
            request.setHeader("Authorization", "Bearer " + token);
            CloseableHttpResponse response = client.execute(request);
            return response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            return -1;
        }
    }
}

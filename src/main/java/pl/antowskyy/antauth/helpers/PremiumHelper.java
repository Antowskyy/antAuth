package pl.antowskyy.antauth.helpers;

import java.io.IOException;
import java.net.*;
import java.net.http.*;
import java.util.concurrent.CompletableFuture;

public class PremiumHelper
{
    public static boolean checkPremium(String name) throws IOException {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection)(new URL(String.format("https://api.ashcon.app/mojang/v2/user/%s", name))).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.183 Safari/537.36 Edg/86.0.622.63");
            return connection.getResponseCode() == 200;
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static CompletableFuture<Boolean> checkPremium2(String name) {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://api.ashcon.app/mojang/v2/user/" + name)).build();
        return (HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::statusCode)).thenApply(statusCode -> statusCode == 200);
    }
}

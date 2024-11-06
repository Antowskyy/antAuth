package pl.antowskyy.antauth.helpers;

import com.github.tsohr.JSONObject;
import pl.antowskyy.antauth.AntAuth;

import java.io.IOException;
import java.net.*;
import java.net.http.*;
import java.util.concurrent.CompletableFuture;

public class PremiumHelper
{
    public static CompletableFuture<String> getUUID(String name) {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://api.mojang.com/users/profiles/minecraft/" + name)).build();
        return HttpClient.newHttpClient()
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        JSONObject jsonObject = new JSONObject(response.body());
                        return jsonObject.getString("id");
                    }
                    AntAuth.getInstance().getProxy().getLogger().warning("Failed to fetch UUID for " + name + " with status code: " + response.statusCode());
                    return null;
                }).exceptionally(throwable -> {
                    AntAuth.getInstance().getProxy().getLogger().warning("Exception occurred while fetching UUID for " + name + ": " + throwable.getMessage());
                    return null;
                });
    }

    public static CompletableFuture<Boolean> checkPremium(String uuid) {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid)).build();
        return HttpClient.newHttpClient()
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> response.statusCode() == 200)
                .exceptionally(throwable -> {
                    AntAuth.getInstance().getProxy().getLogger().warning("Exception occurred while checking premium status for UUID: " + uuid + ": " + throwable.getMessage());
                    return Boolean.FALSE;
                });
    }

    public static CompletableFuture<Boolean> checkPremiumPerLogin(String name) {
        return getUUID(name).thenCompose(uuid -> (uuid != null) ? checkPremium(uuid) : CompletableFuture.completedFuture(Boolean.FALSE));
    }

}

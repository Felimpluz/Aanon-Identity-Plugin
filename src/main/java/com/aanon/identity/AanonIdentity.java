package com.aanon.identity;

import org.bukkit.plugin.java.JavaPlugin;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public final class AanonIdentity extends JavaPlugin {

    @Override
    public void onEnable() {
        // Crea el config.yml si no existe
        saveDefaultConfig();
        
        String pluginKey = getConfig().getString("plugin_key");
        String supabaseUrl = getConfig().getString("supabase_url");
        String supabaseKey = getConfig().getString("supabase_key");

        if (pluginKey == null || pluginKey.equals("PON_TU_CLAVE_AQUI")) {
            getLogger().warning("❌ Aanon Identity: Por favor configura el plugin_key en config.yml");
            return;
        }

        getLogger().info("⏳ Conectando con Aanon Identity...");

        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            boolean exito = verificarConexion(supabaseUrl, supabaseKey, pluginKey);
            if (exito) {
                getLogger().info("✅ ¡Servidor conectado exitosamente!");
            } else {
                getLogger().severe("❌ Error al conectar con Aanon. Verifica tus credenciales.");
            }
        });
    }

    private boolean verificarConexion(String url, String key, String pluginKey) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String endpoint = url + "/rest/v1/registered_servers?plugin_key=eq." + pluginKey;
            String jsonBody = "{\"status\": \"connected\"}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("apikey", key)
                    .header("Authorization", "Bearer " + key)
                    .header("Content-Type", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200 || response.statusCode() == 204;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
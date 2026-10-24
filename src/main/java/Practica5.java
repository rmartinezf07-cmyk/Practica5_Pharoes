import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Practica5 {

    static class RespuestaOpenMeteo {
        Daily daily;
    }

    static class Daily {
        List<String> time;
        @SerializedName("cloud_cover_mean")
        List<Double> cloudCoverMean;
    }

    public static void main(String[] args) throws Exception {
        RespuestaOpenMeteo respuesta = obtenerDatosMeteorologicos();
        List<String> dias = respuesta.daily.time;
        List<Double> nubosidades = respuesta.daily.cloudCoverMean;

        List<Double> usos = calcularUsoIluminacion(nubosidades);

        // 3. Escribir CSV
        escribirCSV(dias, nubosidades, usos);
        System.out.println("¡Archivo datos.csv generado correctamente!");
    }

    static RespuestaOpenMeteo obtenerDatosMeteorologicos() throws Exception {
        String url = "https://api.open-meteo.com/v1/forecast"
                + "?latitude=40.4168&longitude=-3.7038"
                + "&daily=cloud_cover_mean&timezone=auto&forecast_days=7";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Gson gson = new Gson();
        return gson.fromJson(response.body(), RespuestaOpenMeteo.class);
    }

    static List<Double> calcularUsoIluminacion(List<Double> nubosidades) {
        List<Double> usos = new ArrayList<>();
        for (double nub : nubosidades) {
            if (nub >= 50) {
                usos.add((nub - 50) * 0.5);
            } else {
                usos.add(0.0);
            }
        }
        return usos;
    }

    // --- MÉTODO 3: GUARDAR CSV ---
    static void escribirCSV(List<String> dias, List<Double> nubs, List<Double> usos) throws Exception {
        StringBuilder sb = new StringBuilder("Dia,Nubosidad_media,Uso_iluminacion_kWh\n");
        for (int i = 0; i < dias.size(); i++) {
            sb.append(dias.get(i)).append(",")
                    .append(nubs.get(i)).append(",")
                    .append(usos.get(i)).append("\n");
        }
        Files.writeString(Path.of("datos.csv"), sb.toString());
    }
}
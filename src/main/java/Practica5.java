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

        // 4. Generar Dashboard HTML
        generarDashboardDesdeCSV();

        System.out.println("¡Listo! Se han generado datos.csv y dashboard.html correctamente.");
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

    // --- MÉTODO 4: LEER CSV Y GENERAR EL HTML ---
    static void generarDashboardDesdeCSV() throws Exception {
        List<String> lineas = Files.readAllLines(Path.of("datos.csv"));

        List<String> dias = new ArrayList<>();
        List<String> nubs = new ArrayList<>();
        List<String> usos = new ArrayList<>();

        // Saltar la cabecera (i = 1)
        for (int i = 1; i < lineas.size(); i++) {
            String[] partes = lineas.get(i).split(",");
            dias.add("'" + partes[0] + "'"); // comillas simples para JS
            nubs.add(partes[1]);
            usos.add(partes[2]);
        }

        String diasJS = String.join(", ", dias);
        String nubsJS = String.join(", ", nubs);
        String usosJS = String.join(", ", usos);

        // Plantilla HTML con los gráficos de ECharts
        String html = """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <title>Dashboard Uso de Iluminación</title>
                    <script src="https://cdn.jsdelivr.net/npm/echarts@5/dist/echarts.min.js"></script>
                </head>
                <body>
                    <h2 style="text-align:center">Nubosidad media diaria</h2>
                    <div id="grafico-nubosidad" style="width:100%%; height:400px;"></div>

                    <h2 style="text-align:center">Uso estimado de iluminación</h2>
                    <div id="grafico-uso" style="width:100%%; height:400px;"></div>

                    <script>
                        var dias = [%s];
                        var nubosidades = [%s];
                        var usos = [%s];

                        var chartNub = echarts.init(document.getElementById('grafico-nubosidad'));
                        chartNub.setOption({
                            title: { text: 'Nubosidad media (%%)' },
                            tooltip: {},
                            xAxis: { type: 'category', data: dias, name: 'Día' },
                            yAxis: { type: 'value', name: '%%' },
                            series: [{ type: 'line', data: nubosidades, name: 'Nubosidad' }]
                        });

                        var chartUso = echarts.init(document.getElementById('grafico-uso'));
                        chartUso.setOption({
                            title: { text: 'Uso de iluminación (kWh)' },
                            tooltip: {},
                            xAxis: { type: 'category', data: dias, name: 'Día' },
                            yAxis: { type: 'value', name: 'kWh' },
                            series: [{ type: 'bar', data: usos, name: 'Uso' }]
                        });
                    </script>
                </body>
                </html>
                """.formatted(diasJS, nubsJS, usosJS);

        Files.writeString(Path.of("dashboard.html"), html);
    }
}
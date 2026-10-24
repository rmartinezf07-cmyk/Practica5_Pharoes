# Prueba 5: Estimación del uso de iluminación y dashboard

## Descripción

Aplicación desarrollada en Java que estima el uso diario de iluminación a partir de la nubosidad media prevista para los próximos siete días en Madrid. Los resultados se almacenan en un archivo CSV y se visualizan en un dashboard web generado automáticamente con Apache ECharts.

## Metodología

El programa ejecuta de forma secuencial las siguientes fases:

1. **Obtención de datos.** Consulta a la API pública Open-Meteo para recuperar la nubosidad media diaria (`cloud_cover_mean`, en %) de los próximos siete días.
2. **Cálculo del uso estimado.** Para cada día, si la nubosidad media (N) es igual o superior al 50 %, el uso estimado es (N − 50) × 0,5 kWh; en caso contrario, es 0 kWh.
3. **Almacenamiento.** Los resultados se guardan en `datos.csv` con las columnas *Día*, *Nubosidad media (%)* y *Uso estimado de iluminación (kWh)*.
4. **Visualización.** El programa lee `datos.csv` y genera `dashboard.html` a partir de una plantilla, con dos gráficas que comparten el eje temporal: nubosidad media (superior) y uso estimado de iluminación (inferior).

Dado que el HTML se genera a partir del CSV, cualquier cambio en los datos se refleja volviendo a ejecutar el programa, sin editar el HTML manualmente.

## Tecnologías

- **Java**: lógica de la aplicación.
- **Maven**: gestión de dependencias.
- **Gson**: deserialización de la respuesta JSON de la API.
- **Apache ECharts**: representación gráfica de los datos.

## Ejecución

1. Abrir el proyecto en un IDE compatible con Maven (por ejemplo, IntelliJ IDEA).
2. Esperar a que se descarguen las dependencias.
3. Ejecutar el método `main` de la clase `Practica5`.
4. Abrir el archivo `dashboard.html`, generado en la raíz del proyecto, en un navegador.

## Resultados

| Archivo          | Contenido                                                |
|------------------|----------------------------------------------------------|
| `datos.csv`      | Nubosidad media y uso estimado de iluminación por día    |
| `dashboard.html` | Dashboard con las gráficas de nubosidad y uso estimado   |

## Autor

Rafael Martínez Fernández · [GitHub] https://github.com/rmartinezf07-cmyk

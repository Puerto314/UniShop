package co.edu.unbosque.unishop.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import co.edu.unbosque.unishop.dto.AmazonItemDTO;
import co.edu.unbosque.unishop.dto.MercadoLibreItemDTO;
import co.edu.unbosque.unishop.dto.RespuestaMercadoLibreDTO;

public class ManipuladorDeSolicitudesHTTPExternas {

    // ── HTTP client compartido ──────────────────────────────────────────
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(15))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(BigDecimal.class, (JsonDeserializer<BigDecimal>) (json, type, ctx) -> {
                try {
                    return json.getAsBigDecimal();
                } catch (Exception e) {
                    return BigDecimal.ZERO;
                }
            }).create();

    /** User-Agent compartido para todas las peticiones */
    private static final String UA_BROWSER =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
            + "AppleWebKit/537.36 (KHTML, like Gecko) "
            + "Chrome/124.0.0.0 Safari/537.36";

    // ─────────────────────────────────────────────────────────────────────
    // MercadoLibre — API REST publica SIN autenticacion
    //
    // El endpoint GET /sites/MCO/search es completamente publico.
    // NO debe enviarse cabecera Authorization.
    // SÍ debe enviarse User-Agent; sin él ML puede responder 403 o lista vacía.
    // ─────────────────────────────────────────────────────────────────────

    public static RespuestaMercadoLibreDTO doGetMercadoLibre(String url) {
        HttpRequest solicitud = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .header("Accept",          "application/json")
                .header("Accept-Language", "es-CO,es;q=0.9")
                .header("User-Agent",      UA_BROWSER)
                .build();

        try {
            HttpResponse<String> r = HTTP_CLIENT.send(solicitud, HttpResponse.BodyHandlers.ofString());
            System.out.println("MercadoLibre status code -> " + r.statusCode());

            if (r.statusCode() != 200) {
                System.err.println("ML error body: " + r.body().substring(0, Math.min(300, r.body().length())));
                return new RespuestaMercadoLibreDTO();
            }

            return GSON.fromJson(r.body(), RespuestaMercadoLibreDTO.class);

        } catch (IOException | InterruptedException e) {
            System.err.println("Error HTTP MercadoLibre: " + e.getMessage());
            return new RespuestaMercadoLibreDTO();
        }
    }

    public static List<MercadoLibreItemDTO> buscarEnMercadoLibre(String nombreProducto) {
        try {
            String query = URLEncoder.encode(nombreProducto.trim(), StandardCharsets.UTF_8);
            String url   = "https://api.mercadolibre.com/sites/MCO/search?q=" + query + "&limit=10&sort=relevance";
            RespuestaMercadoLibreDTO resp = doGetMercadoLibre(url);
            if (resp != null && resp.getResults() != null && !resp.getResults().isEmpty()) {
                return resp.getResults();
            }
        } catch (Exception e) {
            System.err.println("Error buscando en MercadoLibre: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    /**
     * Obtiene la descripción textual de un producto de MercadoLibre
     * usando su ID (p.ej. "MCO123456789").
     *
     * Endpoint: GET https://api.mercadolibre.com/items/{id}/description
     * Retorna el campo "plain_text". Si falla o está vacío, retorna "".
     */
    public static String obtenerDescripcionML(String itemId) {
        if (itemId == null || itemId.isBlank()) return "";
        try {
            String url = "https://api.mercadolibre.com/items/" + itemId + "/description";
            HttpRequest solicitud = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(url))
                    .header("Accept",      "application/json")
                    .header("User-Agent",  UA_BROWSER)
                    .build();

            HttpResponse<String> r = HTTP_CLIENT.send(solicitud, HttpResponse.BodyHandlers.ofString());
            System.out.println("ML descripcion [" + itemId + "] status -> " + r.statusCode());

            if (r.statusCode() == 200) {
                JsonObject json = JsonParser.parseString(r.body()).getAsJsonObject();
                if (json.has("plain_text") && !json.get("plain_text").isJsonNull()) {
                    String texto = json.get("plain_text").getAsString().trim();
                    // Recortar a 500 chars para no saturar la respuesta
                    return texto.length() > 500 ? texto.substring(0, 497) + "..." : texto;
                }
            }
        } catch (Exception e) {
            System.err.println("Error obteniendo descripcion ML [" + itemId + "]: " + e.getMessage());
        }
        return "";
    }

    // ─────────────────────────────────────────────────────────────────────
    // Amazon — scraping HTML publico
    // ─────────────────────────────────────────────────────────────────────

    private static final List<String> TEXTOS_EXCLUIDOS = List.of(
        "Consulta cada página del producto para ver otras opciones de compra",
        "Consulta cada pagina del producto",
        "Ver otras opciones de compra",
        "Patrocinado",
        "Sponsored",
        "Comprar de nuevo",
        "Agregar al carrito",
        "Ver detalles"
    );

    public static List<AmazonItemDTO> buscarEnAmazon(String nombreProducto) {
        List<AmazonItemDTO> productos = new ArrayList<>();
        try {
            String query = URLEncoder.encode(nombreProducto.trim(), StandardCharsets.UTF_8);
            String url   = "https://www.amazon.com.co/s?k=" + query;

            HttpRequest solicitud = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(url))
                    .setHeader("User-Agent", UA_BROWSER)
                    .setHeader("Accept",
                            "text/html,application/xhtml+xml,"
                            + "application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
                    .setHeader("Accept-Language",  "es-CO,es;q=0.9,en-US;q=0.8,en;q=0.7")
                    .setHeader("Accept-Encoding",  "identity")
                    .setHeader("Cache-Control",    "no-cache")
                    .setHeader("Upgrade-Insecure-Requests", "1")
                    .setHeader("Sec-Fetch-Dest",   "document")
                    .setHeader("Sec-Fetch-Mode",   "navigate")
                    .setHeader("Sec-Fetch-Site",   "none")
                    .setHeader("Sec-Fetch-User",   "?1")
                    .build();

            HttpResponse<String> r = HTTP_CLIENT.send(solicitud, HttpResponse.BodyHandlers.ofString());
            System.out.println("Amazon status code -> " + r.statusCode());
            if (r.statusCode() != 200) {
                System.err.println("Amazon respondio " + r.statusCode());
                return productos;
            }

            productos = parsearProductosAmazon(r.body());

        } catch (Exception e) {
            System.err.println("Error buscando en Amazon: " + e.getMessage());
            e.printStackTrace();
        }
        return productos;
    }

    private static List<AmazonItemDTO> parsearProductosAmazon(String html) {
        List<AmazonItemDTO> productos = new ArrayList<>();

        Pattern bloquePattern = Pattern.compile(
            "data-asin=\"([A-Z0-9]{10})\"[^>]*data-component-type=\"s-search-result\"",
            Pattern.DOTALL
        );

        Map<String, Integer> asinPosiciones = new LinkedHashMap<>();
        Matcher bm = bloquePattern.matcher(html);
        while (bm.find() && asinPosiciones.size() < 10) {
            String asin = bm.group(1);
            if (!asinPosiciones.containsKey(asin)) {
                asinPosiciones.put(asin, bm.start());
            }
        }

        List<String> asinList = new ArrayList<>(asinPosiciones.keySet());
        List<Integer> posList  = new ArrayList<>(asinPosiciones.values());

        for (int i = 0; i < asinList.size(); i++) {
            String asin  = asinList.get(i);
            int inicio   = posList.get(i);
            int fin = (i + 1 < posList.size()) ? posList.get(i + 1) : Math.min(inicio + 8000, html.length());
            String bloque = html.substring(inicio, fin);

            String titulo = extraerTituloDeBloque(bloque);
            BigDecimal precio = extraerPrecioDeBloque(bloque);

            if (titulo == null) continue;

            AmazonItemDTO item = new AmazonItemDTO();
            item.setAsin(asin);
            item.setTitle(titulo);
            item.setUrl("https://www.amazon.com.co/dp/" + asin);
            item.setPrice(precio);
            productos.add(item);
        }

        return productos;
    }

    private static String extraerTituloDeBloque(String bloque) {
        Pattern p1 = Pattern.compile(
            "<a[^>]+class=\"[^\"]*a-link-normal[^\"]*s-underline-text[^\"]*\"[^>]*aria-label=\"([^\"]{10,300})\"",
            Pattern.DOTALL
        );
        Pattern p2 = Pattern.compile(
            "<span[^>]+class=\"[^\"]*(?:a-size-base-plus|a-size-medium)[^\"]*\"[^>]*>\\s*([^<]{10,300})\\s*</span>",
            Pattern.DOTALL
        );
        Pattern p3 = Pattern.compile(
            "<h2[^>]*>[^<]*<a[^>]*>[^<]*<span[^>]*>([^<]{10,300})</span>",
            Pattern.DOTALL
        );

        for (Pattern p : List.of(p1, p2, p3)) {
            Matcher m = p.matcher(bloque);
            while (m.find()) {
                String candidato = m.group(1).trim();
                if (esTituloValido(candidato)) {
                    return candidato;
                }
            }
        }
        return null;
    }

    private static boolean esTituloValido(String texto) {
        if (texto.length() < 10) return false;
        String lower = texto.toLowerCase();
        for (String excluido : TEXTOS_EXCLUIDOS) {
            if (lower.contains(excluido.toLowerCase())) return false;
        }
        if (texto.matches("[\\d\\s\\$\\.\\,\\%\\+\\-]+")) return false;
        return true;
    }

    private static BigDecimal extraerPrecioDeBloque(String bloque) {
        Pattern p1 = Pattern.compile("<span[^>]+class=\"a-price-whole\">([\\d\\.]+)");
        Pattern p2 = Pattern.compile("aria-label=\"\\$([\\d\\.\\,]+)\"");

        for (Pattern p : List.of(p1, p2)) {
            Matcher m = p.matcher(bloque);
            if (m.find()) {
                try {
                    String ps = m.group(1).replace(".", "").replace(",", "");
                    return new BigDecimal(ps);
                } catch (NumberFormatException ignored) {}
            }
        }
        return BigDecimal.ZERO;
    }
}

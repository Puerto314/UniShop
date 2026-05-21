package co.edu.unbosque.unishop.service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.edu.unbosque.unishop.dto.AmazonItemDTO;
import co.edu.unbosque.unishop.dto.AmazonReviewDTO;

/**
 * Scraping de Amazon.com con rotacion de User-Agents y reintentos para evitar
 * 503.
 */
public class ManipuladorDeSolicitudesHTTPExternas {

	private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1)
			.connectTimeout(Duration.ofSeconds(30)).followRedirects(HttpClient.Redirect.NORMAL).build();

	/** Pool de User-Agents reales para rotar y evitar bloqueos */
	private static final List<String> USER_AGENTS = Arrays.asList(
			"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36",
			"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36",
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36",
			"Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:125.0) Gecko/20100101 Firefox/125.0",
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.4 Safari/605.1.15",
			"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36",
			"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36 Edg/122.0.0.0");

	private static final Random RANDOM = new Random();

	private static final List<String> EXCLUIDOS = List.of("Sponsored", "Patrocinado", "See buying options",
			"Ver otras opciones", "Check each product page", "Add to cart", "Agregar al carrito");

	// ── Búsqueda principal con reintentos ─────────────────────────────────

	public static List<AmazonItemDTO> buscarEnAmazon(String nombreProducto) {
		// Intentar múltiples variantes de búsqueda para maximizar resultados
		List<String> variantes = generarVariantesBusqueda(nombreProducto);

		for (String variante : variantes) {
			List<AmazonItemDTO> result = intentarBusqueda(variante, 3);
			if (!result.isEmpty()) {
				System.out.println("Exito con variante: '" + variante + "' -> " + result.size() + " productos");
				return result;
			}
		}
		System.err.println("Ninguna variante produjo resultados para: " + nombreProducto);
		return new ArrayList<>();
	}

	/** Genera variantes de la búsqueda para aumentar probabilidad de éxito */
	private static List<String> generarVariantesBusqueda(String query) {
		List<String> variantes = new ArrayList<>();
		variantes.add(query.trim());
		// Variante en inglés para términos comunes en español
		String lower = query.trim().toLowerCase();
		if (lower.contains("iphone")) {
			variantes.add("Apple iPhone " + lower.replace("iphone", "").trim());
			variantes.add("iPhone");
		}
		if (lower.contains("samsung"))
			variantes.add("Samsung Galaxy");
		if (lower.contains("teclado"))
			variantes.add("mechanical keyboard gaming");
		if (lower.contains("audifono") || lower.contains("auricular"))
			variantes.add("gaming headset wireless");
		if (lower.contains("monitor"))
			variantes.add("gaming monitor 27 inch");
		// Versión simplificada (primera palabra)
		String[] palabras = query.trim().split("\\s+");
		if (palabras.length > 1)
			variantes.add(palabras[0]);
		return variantes;
	}

	/** Intenta la búsqueda hasta maxReintentos veces con distintos UA y delays */
	private static List<AmazonItemDTO> intentarBusqueda(String query, int maxReintentos) {
		for (int intento = 0; intento < maxReintentos; intento++) {
			try {
				if (intento > 0) {
					// Esperar entre 1 y 3 segundos entre reintentos
					Thread.sleep(1000 + RANDOM.nextInt(2000));
				}
				String q = URLEncoder.encode(query.trim(), StandardCharsets.UTF_8);
				String url = "https://www.amazon.com/s?k=" + q + "&language=en_US";

				HttpResponse<String> r = HTTP_CLIENT.send(buildRequest(url, getRandomUA()),
						HttpResponse.BodyHandlers.ofString());
				System.out.println("Amazon /s [intento " + (intento + 1) + "] status -> " + r.statusCode());

				if (r.statusCode() == 200) {
					List<AmazonItemDTO> result = parsearBusqueda(r.body());
					if (!result.isEmpty())
						return result;
					// Si da 200 pero sin productos, intentar con otro UA
					System.out.println("200 OK pero sin productos, reintentando...");
				} else if (r.statusCode() == 503 || r.statusCode() == 429) {
					System.err.println("Amazon devolvio " + r.statusCode() + ", reintentando en breve...");
					Thread.sleep(2000 + RANDOM.nextInt(3000));
				} else {
					System.err.println("Amazon respondio " + r.statusCode());
				}
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
				break;
			} catch (Exception e) {
				System.err.println("intentarBusqueda error (intento " + (intento + 1) + "): " + e.getMessage());
			}
		}
		return new ArrayList<>();
	}

	// ── Reseñas con reintento ─────────────────────────────────────────────

	public static List<AmazonReviewDTO> obtenerResenasAmazon(String asin) {
		for (int intento = 0; intento < 2; intento++) {
			try {
				if (intento > 0)
					Thread.sleep(1500);
				String url = "https://www.amazon.com/product-reviews/" + asin
						+ "?sortBy=recent&reviewerType=all_reviews&language=en_US";

				HttpResponse<String> r = HTTP_CLIENT.send(buildRequest(url, getRandomUA()),
						HttpResponse.BodyHandlers.ofString());
				System.out.println("Amazon /reviews [" + asin + "] status -> " + r.statusCode());

				if (r.statusCode() == 200) {
					List<AmazonReviewDTO> result = parsearResenas(r.body());
					System.out.println("Reseñas encontradas: " + result.size());
					return result;
				}
			} catch (Exception e) {
				System.err.println("obtenerResenasAmazon error: " + e.getMessage());
			}
		}
		return new ArrayList<>();
	}

	// ── Parsers ──────────────────────────────────────────────────────────

	private static List<AmazonItemDTO> parsearBusqueda(String html) {
		List<AmazonItemDTO> lista = new ArrayList<>();

		if (html.contains("robot check") || html.contains("Type the characters")
				|| html.contains("api-services-support@amazon.com")) {
			System.err.println("Amazon: pagina de verificacion (CAPTCHA).");
			return lista;
		}

		Pattern pBloque = Pattern.compile("data-asin=\"([A-Z0-9]{10})\"[^>]*data-component-type=\"s-search-result\"",
				Pattern.DOTALL);

		Map<String, Integer> posiciones = new LinkedHashMap<>();
		Matcher bm = pBloque.matcher(html);
		while (bm.find() && posiciones.size() < 12) {
			String asin = bm.group(1);
			if (!posiciones.containsKey(asin))
				posiciones.put(asin, bm.start());
		}

		List<String> asins = new ArrayList<>(posiciones.keySet());
		List<Integer> starts = new ArrayList<>(posiciones.values());

		for (int i = 0; i < asins.size(); i++) {
			String asin = asins.get(i);
			int ini = starts.get(i);
			int fin = (i + 1 < starts.size()) ? starts.get(i + 1) : Math.min(ini + 12000, html.length());
			String bloque = html.substring(ini, fin);

			String titulo = extraerTitulo(bloque);
			BigDecimal precio = extraerPrecioUSD(bloque);
			String imagen = extraerImagen(bloque);
			Double rating = extraerRating(bloque);
			Integer numRev = extraerNumResenas(bloque);

			if (titulo == null)
				continue;

			AmazonItemDTO item = new AmazonItemDTO();
			item.setAsin(asin);
			item.setTitle(titulo);
			item.setUrl("https://www.amazon.com/dp/" + asin);
			item.setPrice(precio);
			item.setImageUrl(imagen);
			item.setRating(rating);
			item.setReviewCount(numRev);
			lista.add(item);
		}
		return lista;
	}

	private static List<AmazonReviewDTO> parsearResenas(String html) {
		List<AmazonReviewDTO> lista = new ArrayList<>();

		Pattern pBloque = Pattern.compile("data-hook=\"review\"(.*?)(?=data-hook=\"review\"|</ol>|id=\"reviews-medley)",
				Pattern.DOTALL);
		Pattern pAuthor = Pattern.compile("class=\"a-profile-name\"[^>]*>([^<]+)<");
		Pattern pTitle = Pattern.compile("data-hook=\"review-title\"[^>]*>[^<]*<span[^>]*>([^<]{3,200})");
		Pattern pBody = Pattern.compile("data-hook=\"review-body\"[^>]*>\\s*<span[^>]*>([\\s\\S]{10,2000?})</span>");
		Pattern pRating = Pattern.compile("([0-9\\.]+) out of 5 stars");
		Pattern pDate = Pattern.compile("data-hook=\"review-date\"[^>]*>([^<]{5,80})<");

		Matcher bm = pBloque.matcher(html);
		int count = 0;
		while (bm.find() && count < 10) {
			String bloque = bm.group(1);
			AmazonReviewDTO rev = new AmazonReviewDTO();
			Matcher m;

			m = pAuthor.matcher(bloque);
			rev.setAuthor(m.find() ? limpiar(m.group(1)) : "Anonymous");

			m = pTitle.matcher(bloque);
			if (m.find()) {
				String t = limpiar(m.group(1));
				if (t.length() > 3)
					rev.setTitle(t);
			}

			m = pBody.matcher(bloque);
			if (m.find()) {
				String b = limpiar(m.group(1));
				rev.setBody(b.length() > 600 ? b.substring(0, 597) + "..." : b);
			}

			m = pRating.matcher(bloque);
			if (m.find()) {
				try {
					rev.setRating(Double.parseDouble(m.group(1)));
				} catch (Exception ignored) {
				}
			}

			m = pDate.matcher(bloque);
			if (m.find())
				rev.setDate(limpiar(m.group(1)));

			if (rev.getTitle() != null || rev.getBody() != null) {
				lista.add(rev);
				count++;
			}
		}
		return lista;
	}

	// ── Extractores ────────────────────────────────────────────────────────

	private static String extraerTitulo(String bloque) {
		Pattern p1 = Pattern.compile(
				"<a[^>]+class=\"[^\"]*a-link-normal[^\"]*s-underline-text[^\"]*\"[^>]*aria-label=\"([^\"{10,400})\"",
				Pattern.DOTALL);
		Pattern p2 = Pattern.compile("<h2[^>]*>[^<]*<a[^>]*>[^<]*<span[^>]*>([^<]{10,400})</span>", Pattern.DOTALL);
		Pattern p3 = Pattern.compile(
				"<span[^>]+class=\"[^\"]*(?:a-size-base-plus|a-size-medium)[^\"]*\"[^>]*>\\s*([^<]{10,400})\\s*</span>",
				Pattern.DOTALL);
		Pattern p4 = Pattern.compile("data-cy=\"title-recipe-title\"[^>]*>\\s*<[^>]+>([^<]{10,400})</", Pattern.DOTALL);

		for (Pattern p : List.of(p1, p2, p3, p4)) {
			Matcher m = p.matcher(bloque);
			while (m.find()) {
				String c = limpiar(m.group(1));
				if (tituloValido(c))
					return c;
			}
		}
		return null;
	}

	private static BigDecimal extraerPrecioUSD(String bloque) {
		Pattern pW = Pattern.compile("<span[^>]+class=\"a-price-whole\">([\\d,]+)");
		Pattern pF = Pattern.compile("<span[^>]+class=\"a-price-fraction\">([\\d]+)");
		Matcher mW = pW.matcher(bloque);
		if (mW.find()) {
			String entero = mW.group(1).replace(",", "");
			String dec = "00";
			Matcher mF = pF.matcher(bloque);
			if (mF.find(mW.start()))
				dec = mF.group(1);
			try {
				return new BigDecimal(entero + "." + dec);
			} catch (Exception ignored) {
			}
		}
		Pattern pA = Pattern.compile("aria-label=\"\\$([\\d,]+\\.?[\\d]*)\"");
		Matcher mA = pA.matcher(bloque);
		if (mA.find()) {
			try {
				return new BigDecimal(mA.group(1).replace(",", ""));
			} catch (Exception ignored) {
			}
		}
		Pattern pO = Pattern.compile("<span class=\"a-offscreen\">\\$([\\d,]+\\.?[\\d]*)</span>");
		Matcher mO = pO.matcher(bloque);
		if (mO.find()) {
			try {
				return new BigDecimal(mO.group(1).replace(",", ""));
			} catch (Exception ignored) {
			}
		}
		return BigDecimal.ZERO;
	}

	private static String extraerImagen(String bloque) {
		Pattern p1 = Pattern.compile("<img[^>]+class=\"[^\"]*s-image[^\"]*\"[^>]+src=\"([^\"]+)\"");
		Pattern p2 = Pattern.compile("<img[^>]+src=\"(https://m\\.media-amazon\\.com/images/[^\"]+)\"");
		for (Pattern p : List.of(p1, p2)) {
			Matcher m = p.matcher(bloque);
			if (m.find())
				return m.group(1);
		}
		return null;
	}

	private static Double extraerRating(String bloque) {
		Pattern p = Pattern.compile("aria-label=\"([\\d\\.]+) out of 5 stars\"");
		Matcher m = p.matcher(bloque);
		if (m.find()) {
			try {
				return Double.parseDouble(m.group(1));
			} catch (Exception ignored) {
			}
		}
		return null;
	}

	private static Integer extraerNumResenas(String bloque) {
		Pattern p1 = Pattern
				.compile("class=\"[^\"]*s-link-style[^\"]*a-text-normal[^\"]*\"[^>]*aria-label=\"([\\d,]+)\"");
		Matcher m1 = p1.matcher(bloque);
		if (m1.find()) {
			try {
				return Integer.parseInt(m1.group(1).replace(",", ""));
			} catch (Exception ignored) {
			}
		}

		Pattern p2 = Pattern.compile(">([\\d,]+)<[^>]+>\\s*ratings?");
		Matcher m2 = p2.matcher(bloque);
		if (m2.find()) {
			try {
				return Integer.parseInt(m2.group(1).replace(",", ""));
			} catch (Exception ignored) {
			}
		}

		return null;
	}

	// ── Utilidades ─────────────────────────────────────────────────────────

	private static boolean tituloValido(String texto) {
		if (texto == null || texto.length() < 10)
			return false;
		String lower = texto.toLowerCase();
		for (String ex : EXCLUIDOS) {
			if (lower.contains(ex.toLowerCase()))
				return false;
		}
		if (texto.matches("[\\d\\s\\$\\.\\,\\%\\+\\-]+"))
			return false;
		return true;
	}

	private static String limpiar(String s) {
		if (s == null)
			return "";
		return s.replaceAll("<[^>]+>", "").replace("&amp;", "&").replace("&lt;", "<").replace("&gt;", ">")
				.replace("&quot;", "\"").replace("&#39;", "'").replace("&nbsp;", " ").replaceAll("\\s+", " ").trim();
	}

	private static String getRandomUA() {
		return USER_AGENTS.get(RANDOM.nextInt(USER_AGENTS.size()));
	}

	private static HttpRequest buildRequest(String url, String userAgent) {
		return HttpRequest.newBuilder().GET().uri(URI.create(url)).setHeader("User-Agent", userAgent)
				.setHeader("Accept",
						"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
				.setHeader("Accept-Language", "en-US,en;q=0.9").setHeader("Accept-Encoding", "identity")
				.setHeader("Cache-Control", "no-cache").setHeader("Upgrade-Insecure-Requests", "1")
				.setHeader("Sec-Fetch-Dest", "document").setHeader("Sec-Fetch-Mode", "navigate")
				.setHeader("Sec-Fetch-Site", "none").setHeader("Sec-Fetch-User", "?1").setHeader("DNT", "1").build();
	}
}

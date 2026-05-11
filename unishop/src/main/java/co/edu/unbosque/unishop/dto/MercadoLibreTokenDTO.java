package co.edu.unbosque.unishop.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Respuesta del endpoint OAuth de Mercado Libre.
 * POST https://api.mercadolibre.com/oauth/token
 */
public class MercadoLibreTokenDTO {

	@SerializedName("access_token")
	private String accessToken;

	@SerializedName("token_type")
	private String tokenType;

	@SerializedName("expires_in")
	private long expiresIn;

	@SerializedName("scope")
	private String scope;

	@SerializedName("user_id")
	private long userId;

	@SerializedName("refresh_token")
	private String refreshToken;

	public MercadoLibreTokenDTO() {}

	public String getAccessToken()        { return accessToken; }
	public void setAccessToken(String t)  { this.accessToken = t; }

	public String getTokenType()          { return tokenType; }
	public void setTokenType(String t)    { this.tokenType = t; }

	public long getExpiresIn()            { return expiresIn; }
	public void setExpiresIn(long e)      { this.expiresIn = e; }

	public String getScope()              { return scope; }
	public void setScope(String s)        { this.scope = s; }

	public long getUserId()               { return userId; }
	public void setUserId(long u)         { this.userId = u; }

	public String getRefreshToken()       { return refreshToken; }
	public void setRefreshToken(String r) { this.refreshToken = r; }

	@Override
	public String toString() {
		return "MercadoLibreTokenDTO [tokenType=" + tokenType + ", expiresIn=" + expiresIn
				+ ", userId=" + userId + "]";
	}
}

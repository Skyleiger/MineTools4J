package community.opencode.minetools4j.util.http;

import lombok.Getter;

/**
 * Simple enum for {@link HttpRequest}.
 * It's a better way than just write manually the method name
 */
public enum HttpRequestMethod {
	POST("POST"),
	GET("GET"),
	PUT("PUT"),
	HEAD("HEAD"),
	OPTIONS("OPTIONS"),
	DELETE("DELETE"),
	TRACE("TRACE");

	@Getter
	private String type;

	HttpRequestMethod(String type) {
		this.type = type;
	}
}

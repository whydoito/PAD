package org.springframework.web.client;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Assert;
import org.springframework.web.client.*;
import org.springframework.web.util.UriTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;


public class RestTemplateCus extends RestTemplate {

	/**
	 * Execute the given method on the provided URI.
	 * <p>The {@link ClientHttpRequest} is processed using the {@link RequestCallback};
	 * the response with the {@link ResponseExtractor}.
	 * @param url the fully-expanded URL to connect to
	 * @param method the HTTP method to execute (GET, POST, etc.)
	 * @param requestCallback object that prepares the request (can be {@code null})
	 * @param responseExtractor object that extracts the return value from the response (can be {@code null})
	 * @return an arbitrary object, as returned by the {@link ResponseExtractor}
	 */
	protected <T> T doExecute(URI url, HttpMethod method, RequestCallback requestCallback,
			ResponseExtractor<T> responseExtractor) throws RestClientException {

		Assert.notNull(url, "'url' must not be null");
		Assert.notNull(method, "'method' must not be null");
		ClientHttpResponse response = null;
		try {
			ClientHttpRequest request = createRequest(url, method);
			if (requestCallback != null) {
				requestCallback.doWithRequest(request);
			}
			response = request.execute();
			/*if (!getErrorHandler().hasError(response)) {*/
				logResponseStatus(method, url, response);
			/*}
			else {
				handleResponseError(method, url, response);
			}*/
			if (responseExtractor != null) {
				return responseExtractor.extractData(response);
			}
			else {
				return null;
			}
		}
		catch (IOException ex) {
			throw new ResourceAccessException("I/O error on " + method.name() +
					" request for \"" + url + "\":" + ex.getMessage(), ex);
		}
		finally {
			if (response != null) {
				response.close();
			}
		}
	}
	
	private void logResponseStatus(HttpMethod method, URI url, ClientHttpResponse response) {
		if (logger.isDebugEnabled()) {
			try {
				logger.debug(method.name() + " request for \"" + url + "\" resulted in " +
						response.getStatusCode() + " (" + response.getStatusText() + ")");
			}
			catch (IOException e) {
				// ignore
			}
		}
	}
	
	private void handleResponseError(HttpMethod method, URI url, ClientHttpResponse response) throws IOException {
		if (logger.isWarnEnabled()) {
			try {
				logger.warn(method.name() + " request for \"" + url + "\" resulted in " +
						response.getStatusCode() + " (" + response.getStatusText() + "); invoking error handler");
			}
			catch (IOException e) {
				// ignore
			}
		}
		getErrorHandler().handleError(response);
	}
	
	@Override
	public <T> T getForObject(String url, Class<T> responseType, Map<String, ?> urlVariables) throws RestClientException {
		RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
		HttpMessageConverterExtractor<T> responseExtractor = new HttpMessageConverterExtractor<T>(responseType, getMessageConverters(), logger);
		return execute(url, HttpMethod.GET, requestCallback, responseExtractor, urlVariables);
	}
	
	@Override
	public <T> T execute(String url, HttpMethod method, RequestCallback requestCallback,
			ResponseExtractor<T> responseExtractor, Map<String, ?> urlVariables) throws RestClientException {

		URI expanded = new UriTemplate(url).expand(urlVariables);
		return doExecute(expanded, method, requestCallback, responseExtractor);
	}

}

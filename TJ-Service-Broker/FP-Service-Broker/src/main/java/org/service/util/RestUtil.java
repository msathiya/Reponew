package org.service.util;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

/**
 * The <code>RestUtil</code> class is to invoke external services with HTTP and
 * HTTPS protocol.
 * 
 * @author Sandeep (sandeep.visvanathan@cognizant.com)
 * @author Deepthi (deepthi.g2@cognizant.com)
 * @author Sundar (sundarajan.srinivasan@cognizant.com)
 * @author Ramkumar(ramkumar.kandhakumar@cognizant.com)
 */
public class RestUtil {

	/**
	 * Holds LOGGER reference.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(RestUtil.class);

	/**
	 * This is a Util to handle the HTTP or HTTPs REST (GET,POST,DELETE)
	 */

	/**
	 * (gethttps)
	 * 
	 * @param url
	 *            refers to the http url
	 * @param httpDetail
	 *            contains the header params
	 * @return the JSON response etc
	 */
	public static String gethttps(String url, Map<String, String> httpDetail) throws Exception {
		Args.notNull(url, "url");
		Args.notNull(httpDetail, "httpDetail");
		LOGGER.info("URL-{}", url);
		CloseableHttpClient httpclient = client(url);
		RequestConfig config = RequestConfig.custom().build();

		HttpGet request = new HttpGet(url);
		request.setConfig(config);
		for (String key : httpDetail.keySet()) {
			// LOGGER.info("Header : {} = {}", key, httpDetail.get(key));
			request.setHeader(key, httpDetail.get(key));
		}

		HttpResponse response = httpclient.execute(request);

		return getResponse(response);

	}

	/**
	 * (posthttps)
	 * 
	 * @param url
	 *            refers to the http url
	 * @param httpDetail
	 *            contains the header params
	 * @param body
	 *            contains the post body data
	 * @return the redirected URL if 302 or the JSON response etc
	 */

	public static String posthttps(String url, Map<String, String> httpDetail, String body) throws Exception {

		LOGGER.debug("url -" + url);
		LOGGER.debug("httpDetail -" + httpDetail);
		// LOGGER.debug("body -" + body);

		Args.notNull(url, "url");
		Args.notNull(httpDetail, "httpDetail");
		Args.notNull(body, "body");
		CloseableHttpClient httpclient = client(url);
		RequestConfig config = RequestConfig.custom().build();

		HttpPost request = new HttpPost(url);
		request.setConfig(config);
		for (String key : httpDetail.keySet()) {
			// LOGGER.info("Header : {} = {}", key, httpDetail.get(key));
			request.setHeader(key, httpDetail.get(key));
		}

		StringEntity entity = new StringEntity(body);
		request.setEntity(entity);
		HttpResponse response = httpclient.execute(request);
		return getResponse(response);
	}
	
	/**
	 * (posthttps)
	 * 
	 * @param url
	 *            refers to the http url
	 * @param httpDetail
	 *            contains the header params
	 * @return the redirected URL if 302 or the JSON response etc
	 */

	public static String posthttps(String url, Map<String, String> httpDetail) throws Exception {

		LOGGER.debug("url -" + url);
		LOGGER.debug("httpDetail -" + httpDetail);
		// LOGGER.debug("body -" + body);

		Args.notNull(url, "url");
		Args.notNull(httpDetail, "httpDetail");
		CloseableHttpClient httpclient = client(url);
		RequestConfig config = RequestConfig.custom().build();

		HttpPost request = new HttpPost(url);
		request.setConfig(config);
		for (String key : httpDetail.keySet()) {
			// LOGGER.info("Header : {} = {}", key, httpDetail.get(key));
			request.setHeader(key, httpDetail.get(key));
		}

		HttpResponse response = httpclient.execute(request);
		return getResponse(response);
	}

	/**
	 * (puthttps)
	 * 
	 * @param url
	 *            refers to the http url
	 * @param httpDetail
	 *            contains the header params
	 * @param body
	 *            contains the post body data
	 * @return the redirected URL if 302 or the JSON response etc
	 */
	public static String puthttps(String url, Map<String, String> httpDetail, String body) throws Exception {
		Args.notNull(url, "url");
		Args.notNull(httpDetail, "httpDetail");
		Args.notNull(body, "body");
		CloseableHttpClient httpclient = client(url);
		RequestConfig config = RequestConfig.custom().build();

		HttpPut request = new HttpPut(url);
		request.setConfig(config);
		for (String key : httpDetail.keySet()) {
			// LOGGER.info("Header : {} = {}", key, httpDetail.get(key));
			request.setHeader(key, httpDetail.get(key));
		}

		StringEntity entity = new StringEntity(body);
		request.setEntity(entity);
		HttpResponse response = httpclient.execute(request);
		return getResponse(response);
	}

	/**
	 * (deletehttps)
	 * 
	 * @param url
	 *            refers to the http url
	 * @param httpDetail
	 *            contains the header params
	 * @return the JSON response etc
	 */

	public static String deletehttps(String url, Map<String, String> httpDetail) throws Exception {
		Args.notNull(url, "url");
		Args.notNull(httpDetail, "httpDetail");
		CloseableHttpClient httpclient = client(url);
		RequestConfig config = RequestConfig.custom().build();

		HttpDelete request = new HttpDelete(url);
		request.setConfig(config);
		for (String key : httpDetail.keySet()) {
			// LOGGER.info("Header : {} = {}", key, httpDetail.get(key));
			request.setHeader(key, httpDetail.get(key));
		}

		HttpResponse response = httpclient.execute(request);
		return getResponse(response);

	}

	/**
	 * (deletehttps)
	 * 
	 * @param url
	 *            refers to the http url
	 * @param httpDetail
	 *            contains the header params
	 * @return the JSON response etc
	 */
	public static String deletehttps(String url, Map<String, String> httpDetail, String body) throws Exception {
		Args.notNull(url, "url");
		Args.notNull(httpDetail, "httpDetail");
		Args.notNull(body, "body");
		CloseableHttpClient httpclient = client(url);
		RequestConfig config = RequestConfig.custom().build();

		// POST request with method 'DELETE'
		HttpPost request = new HttpPost(url) {
			@Override
			public String getMethod() {
				return HttpMethod.DELETE.name();
			}
		};

		request.setConfig(config);
		for (String key : httpDetail.keySet()) {
			// LOGGER.info("Header : {} = {}", key, httpDetail.get(key));
			request.setHeader(key, httpDetail.get(key));
		}

		StringEntity entity = new StringEntity(body);
		request.setEntity(entity);

		HttpResponse response = httpclient.execute(request);
		return getResponse(response);

	}

	/**
	 * This util is to download a multipart file and copy to the filepath
	 * specified
	 * 
	 * @param url
	 * @param httpDetail
	 * @param filePath
	 * @return true
	 * @throws Exception
	 */

	public static Boolean getMultipart(String url, Map<String, String> httpDetail, File filePath) throws Exception {
		Args.notNull(url, "url");
		Args.notNull(httpDetail, "httpDetail");
		Args.notNull(filePath, "filePath");
		CloseableHttpClient httpclient = client(url);
		RequestConfig config = RequestConfig.custom().build();

		HttpGet request = new HttpGet(url);
		request.setConfig(config);
		for (String key : httpDetail.keySet()) {
			// LOGGER.info("Header : {} = {}", key, httpDetail.get(key));
			request.setHeader(key, httpDetail.get(key));
		}

		HttpResponse response = httpclient.execute(request);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(), new FileOutputStream(filePath));
		return true;

	}

	/**
	 * this Util is to upload a multipart form data(uploading bits for cf apps)
	 * 
	 * @param url
	 * @param httpDetail
	 * @param file
	 * @param boundary
	 * @return
	 * @throws Exception
	 */
	public static String Multipartput(String url, Map<String, String> httpDetail, File file, String boundary)
			throws Exception {
		Args.notNull(url, "url");
		Args.notNull(httpDetail, "httpDetail");
		Args.notNull(file, "file");
		Args.notNull(boundary, "boundary");
		CloseableHttpClient httpclient = client(url);
		RequestConfig config = RequestConfig.custom().build();
		ContentType contentType = ContentType.create(URLConnection.guessContentTypeFromName(file.getName()));
		HttpEntity entity = MultipartEntityBuilder.create().setBoundary(boundary)
				.addBinaryBody("application", file, contentType, file.getName())
				.addPart("resources", new StringBody("[]", ContentType.TEXT_PLAIN)).build();

		HttpPut request = new HttpPut(url);
		for (String key : httpDetail.keySet()) {
			// LOGGER.info("Header : {} = {}", key, httpDetail.get(key));
			request.setHeader(key, httpDetail.get(key));
		}
		request.setConfig(config);
		request.setEntity(entity);
		HttpResponse response = httpclient.execute(request);
		return getResponse(response);
	}

	/**
	 * this method is for creating http client connection
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	private static CloseableHttpClient client(String url) throws Exception {

		CloseableHttpClient httpclient = null;
		if (StringUtils.startsWith(url, "https")) {
			SSLContextBuilder builder = new SSLContextBuilder();
			builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build(),
					SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();

		} else if (StringUtils.startsWith(url, "http")) {

			httpclient = HttpClients.createDefault();

		} else {
			throw new IllegalArgumentException(String.format("Invalid protocol in URL '%s'.", url));
		}

		return httpclient;

	}

	private static String getResponse(HttpResponse response) throws Exception {

		int status = response.getStatusLine().getStatusCode();
		// LOGGER.info("Status-{}", status);
		String responseString = StringUtils.EMPTY;
		if (status == HttpStatus.SC_MOVED_TEMPORARILY) {
			String taskUrl = response.getHeaders(HttpHeaders.LOCATION)[NumberUtils.INTEGER_ZERO].getValue().toString();
			URL url = new URL(taskUrl);
			url = new URL(url.getProtocol(), url.getHost(), 25555, url.getFile());
			responseString = url.toString();

		} else if (status == HttpStatus.SC_NO_CONTENT) {
			responseString = StringUtils.EMPTY;
		} else {
			responseString = EntityUtils.toString(response.getEntity());
		}
		// LOGGER.info("Response-{}", responseString);
		return responseString;
	}

}

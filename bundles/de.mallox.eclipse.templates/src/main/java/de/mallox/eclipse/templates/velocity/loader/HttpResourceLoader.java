package de.mallox.eclipse.templates.velocity.loader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

import java.io.InputStream;
import java.security.AccessControlException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;

import javax.net.ssl.SSLContext;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;

import de.mallox.eclipse.templates.Activator;

/**
 * Source: org.apache.velocity.runtime.resource.loader.URLResourceLoader This is
 * a simple URL-based loader.
 *
 * @author <a href="mailto:geirm@apache.org">Geir Magnusson Jr.</a>
 * @author <a href="mailto:nbubna@apache.org">Nathan Bubna</a>
 * @version $Id: HttpResourceLoader.java 191743 2005-06-21 23:22:20Z dlr $
 * @since 1.5
 */
public class HttpResourceLoader extends ResourceLoader {
	private String[] roots = null;
	protected HashMap<String, String> templateRoots = null;
	private int timeout = -1;
	private SSLConnectionSocketFactory sslsf;
	private CredentialsProvider credentialsProvider;

	/**
	 * @see org.apache.velocity.runtime.resource.loader.ResourceLoader#init(org.apache.commons.collections.ExtendedProperties)
	 */
	public void init(ExtendedProperties pExtendedProperties) {
		log.trace("HttpResourceLoader : initialization starting.");

		roots = pExtendedProperties.getStringArray("root");
		if (log.isDebugEnabled()) {
			for (int i = 0; i < roots.length; i++) {
				log.debug("HttpResourceLoader : adding root '" + roots[i] + "'");
			}
		}

		timeout = pExtendedProperties.getInt("timeout", -1);

		// init the template paths map
		templateRoots = new HashMap<>();

		log.trace("HttpResourceLoader : initialization complete.");

		// Trust everyone
		SSLContext sslcontext;
		try {
			sslcontext = SSLContexts.custom().loadTrustMaterial(new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] pArg0, String pArg1) throws CertificateException {
					return true;
				}
			}).build();

			sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1" }, null,
					SSLConnectionSocketFactory.getDefaultHostnameVerifier());

			credentialsProvider = new BasicCredentialsProvider() {
				@Override
				public Credentials getCredentials(AuthScope pAuthscope) {

					String tResourceName = pAuthscope.getHost() + ":" + pAuthscope.getRealm();
					Credentials tCredentials = Activator.getDefault().getAuthentication(tResourceName);

					if (tCredentials == null) {
						Activator.logInfo("HttpResourceLoader: tPasswordAuthentication  = null");
						throw new RuntimeException("HttpResourceLoader : User cannceled authentication.");
					}

					return tCredentials;
				}

				@Override
				public void clear() {
					super.clear();

					Activator.getDefault().removeAllAuthentications();
				}
			};

		} catch (Exception e) {
			Activator.logError("HttpResourceLoader.init(ExtendedProperties pExtendedProperties): " + e.getMessage(), e);
		}

	}

	/**
	 * Get an InputStream so that the Runtime can build a template with it.
	 *
	 * @param pName
	 *            name of template to fetch bytestream of
	 * @return InputStream containing the template
	 * @throws ResourceNotFoundException
	 *             if template not found in the file template path.
	 */
	public synchronized InputStream getResourceStream(String pName) throws ResourceNotFoundException {
		if (StringUtils.isEmpty(pName)) {
			throw new ResourceNotFoundException("HttpResourceLoader : No template name provided");
		}

		InputStream tInputStream = null;
		Exception tException = null;
		for (int i = 0; i < roots.length; i++) {

			try {
				CloseableHttpClient tHttpclient = HttpClients.custom().setSSLSocketFactory(sslsf)
						.setDefaultCredentialsProvider(credentialsProvider).build();
				try {
					HttpGet httpget = new HttpGet(roots[i] + pName);

					CloseableHttpResponse response = tHttpclient.execute(httpget);
					try {
						HttpEntity entity = response.getEntity();

						if (response.getStatusLine().getStatusCode() != 200) {
							String tStatusMessage = String.valueOf(response.getStatusLine());
							Activator.logInfo(
									"HttpResourceLoader: access '" + roots[i] + pName + "': " + tStatusMessage);

							if (log.isDebugEnabled())
								log.debug("HttpResourceLoader: access '" + roots[i] + pName + "': " + tStatusMessage);

							if (response.getStatusLine().getStatusCode() == 401) {
								/*
								 * Remove wrong credentials:
								 */
								credentialsProvider.clear();
								throw new AccessControlException(
										"Access to '" + roots[i] + pName + "' not permitted: " + tStatusMessage);
							} else if (response.getStatusLine().getStatusCode() == 404) {
								throw new ResourceNotFoundException(
										"Resource '" + roots[i] + pName + "' not found: " + tStatusMessage);
							}
						}

						// save this root for later re-use
						templateRoots.put(pName, roots[i]);

						InputStream tContent = entity.getContent();

						ByteArrayOutputStream tOutputStream = new ByteArrayOutputStream(
								(int) entity.getContentLength());

						/*
						 * 16384 is a fairly arbitrary choice although I tend to
						 * favour powers of 2 to increase the chance of the
						 * array aligning with word boundaries. pihentagy's
						 * answer shows how you can avoid using an intermediate
						 * buffer, but rather allocate an array of the correct
						 * size. Unless you're dealing with large files I
						 * personally prefer the code above, which is more
						 * elegant and can be used for InputStreams where the
						 * number of bytes to read is not known in advance.
						 */
						byte[] tBuffer = new byte[16384];

						int tRead;
						while ((tRead = tContent.read(tBuffer, 0, tBuffer.length)) != -1) {
							tOutputStream.write(tBuffer, 0, tRead);
						}
						tOutputStream.flush();

						tInputStream = new ByteArrayInputStream(tOutputStream.toByteArray());
					} finally {
						response.close();
					}
				} finally {
					tHttpclient.close();
				}
			} catch (IOException e) {
				Activator.logError("HttpResourceLoader: IOException: " + e.getMessage(), e);
				if (log.isDebugEnabled())
					log.debug("HttpResourceLoader: Exception when looking for '" + pName + "' at '" + roots[i] + "'",
							e);

				// only save the first one for later throwing
				if (tException == null) {
					tException = e;
				}
			}
		}

		// if we never found the template
		if (tInputStream == null) {
			String tMessage;
			if (tException == null) {
				tMessage = "HttpResourceLoader : Resource '" + pName + "' not found.";
			} else {
				tMessage = tException.getMessage();
			}
			Activator.logInfo("HttpResourceLoader: " + tMessage);
			// convert to a general Velocity ResourceNotFoundException
			throw new ResourceNotFoundException(tMessage);
		}

		return tInputStream;
	}

	/**
	 * Checks to see if a resource has been deleted, moved or modified.
	 *
	 * @param pResource
	 *            Resource The resource to check for modification
	 * @return boolean True if the resource has been modified, moved, or
	 *         unreachable
	 */
	public boolean isSourceModified(Resource pResource) {
		long tFileLastModified = getLastModified(pResource);
		// if the file is unreachable or otherwise changed
		if (tFileLastModified == 0 || tFileLastModified != pResource.getLastModified()) {
			return true;
		}
		return false;
	}

	/**
	 * Checks to see when a resource was last modified
	 *
	 * @param pResource
	 *            Resource the resource to check
	 * @return long The time when the resource was last modified or 0 if the
	 *         file can't be reached
	 */
	@SuppressWarnings("deprecation")
	public long getLastModified(Resource pResource) {
		// get the previously used root
		String tName = pResource.getName();
		String tRoot = (String) templateRoots.get(tName);

		try {
			CloseableHttpClient tHttpclient = HttpClients.custom().setSSLSocketFactory(sslsf)
					.setDefaultCredentialsProvider(credentialsProvider).build();
			try {
				HttpGet httpget = new HttpGet(tRoot + tName);

				CloseableHttpResponse response = tHttpclient.execute(httpget);

				String value = response.getFirstHeader("last-modified").getValue();
				try {
					return Date.parse(value);
				} catch (Exception e) {
				}
			} finally {
				tHttpclient.close();
			}
		} catch (IOException e) {
			Activator.logError("HttpResourceLoader: IOException: " + e.getMessage(), e);
			if (log.isDebugEnabled())
				log.debug("HttpResourceLoader: Exception when looking for '" + tRoot + tName + "'", e);
		}

		return 0;
	}

	/**
	 * Returns the current, custom timeout setting. If negative, there is no
	 * custom timeout.
	 * 
	 * @since 1.6
	 */
	public int getTimeout() {
		return timeout;
	}
}

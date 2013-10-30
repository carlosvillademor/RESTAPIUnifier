/**
 *
 *  Copyright (c) 2013. All rights reserved.
 *
 *  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 *  This code is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License version 2 only, as
 *  published by the Free Software Foundation.  Oracle designates this
 *  particular file as subject to the "Classpath" exception as provided
 *  by Oracle in the LICENSE file that accompanied this code.
 *
 *  This code is distributed in the hope that it will be useful, but WITHOUT
 *  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 *  version 2 for more details (a copy is included in the LICENSE file that
 *  accompanied this code).
 *
 *  You should have received a copy of the GNU General Public License version
 *  2 along with this work; if not, write to the Free Software Foundation,
 *  Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.neomatrix369.apiworld;

import org.neomatrix369.apiworld.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mani Sarkar
 */
public class APIReader {

    private static final String MSG_ERROR_CONNECTING = ">>> Error connecting to the website: %s";
    private static final String MSG_ERROR_DUE_TO = "Error due to: %s";
    private static final String MSG_INPUT_URL_STRING = ">>> Input URL String: %s";
    private static final String MSG_READING_COMPLETED = ">>> Reading completed...";
    private static final String MSG_CONNECTING_TO_URL = ">>> Connecting to URL: <%s>, this may take a moment.";
    private static final String MSG_READING_RESULTS_RETURNED = ">>> Reading results returned, this may take a moment...";

    private static final Logger logger = LoggerFactory.getLogger(APIReader.class);
    private List<String> lastHttpResult = new ArrayList<String>();
    private URL url;

    private Map<String, String> headers = new HashMap<String, String>();

    public APIReader(UriBuilder uriBuilder) {
        constructUrl(uriBuilder.getFinalURL());
    }

    public APIReader(String url) {
        constructUrl(url);
    }

    //FIXME This constructor is used only in APIReaderTest, but it should probably be the only constructor for this class if we want the class to be easy to test
    public APIReader(URL url) {
        this.url = url;
    }

    public APIReader setHeader(String header, String value) {
        headers.put(header, value);
        return this;
    }

    public String executeGetUrl() throws IOException {
        return executeGetUrl(null);
    }

    public String executePostUrl() throws IOException {
        return executePostUrl(null);
    }

    public String executePostUrl(String urlParameters) throws IOException {

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);
        urlConnection.setInstanceFollowRedirects(false);

        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        urlConnection.setRequestProperty("charset", "utf-8");

        for (Map.Entry<String, String> header : headers.entrySet()) {
            urlConnection.setRequestProperty(header.getKey(), header.getValue());
        }

        // TODO: work with null
        // urlConnection.setRequestProperty("Content-Length", "" +
        // Integer.toString(urlParameters.getBytes().length));
        urlConnection.setUseCaches(false);

        if (urlParameters != null) {
            writeUrlParameters(urlParameters, urlConnection);
        }
        return fireRequest(urlConnection);
    }

    public String executeGetUrl(Map<String, String> requestProperties) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setRequestMethod("GET");
        if (requestProperties != null) {
            for (Map.Entry<String, String> property : requestProperties.entrySet()) {
                urlConnection.setRequestProperty(property.getKey(), property.getValue());
            }
        }

        return fireRequest(urlConnection);
    }

    private void constructUrl(String url) {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            logger.error(String.format(MSG_INPUT_URL_STRING, url));
            logger.error(String.format(MSG_ERROR_DUE_TO, e.getMessage()));
            throw new IllegalArgumentException("Final URL does not exist.");
        }
    }

    private String fireRequest(HttpURLConnection urlConnection) throws IOException {
        try {
            logger.info(String.format(MSG_CONNECTING_TO_URL, url));
            fetchDataFromURL(new InputStreamReader(urlConnection.getInputStream()));
            urlConnection.disconnect();
        } catch (IOException ioException) {
            showMessageDueToIOException(url.toString(), ioException);
            throw ioException;
        }
        return getFetchedResults();
    }

    private void writeUrlParameters(String urlParameters, HttpURLConnection urlConnection) throws IOException {
        logger.info("url parameter: " + urlParameters);
        Writer wr = createWriter(urlConnection.getOutputStream());
        wr.write(urlParameters);
        wr.flush();
        wr.close();
    }

    //access level package for testing purposes
    Writer createWriter(OutputStream outputStream) {
        return new PrintWriter(outputStream);
    }

    private String getFetchedResults() {
        String result = lastHttpResult.toString();
        clearHttpResults();
        while (result.startsWith(Utils.OPENING_BOX_BRACKET) && result.endsWith(Utils.CLOSING_BOX_BRACKET)) {
            result = Utils.dropStartAndEndDelimiters(result);
        }
        return result;
    }

    private List<String> fetchDataFromURL(InputStreamReader isr) throws IOException {
        BufferedReader httpResult = null;
        try {
            httpResult = new BufferedReader(isr);
            logger.info(MSG_READING_RESULTS_RETURNED);

            String inputLine;
            while ((inputLine = httpResult.readLine()) != null) {
                addToLastHttpResults(inputLine);
            }

            logger.info(MSG_READING_COMPLETED);
        } finally {
            if (httpResult != null) {
                httpResult.close();
            }
            logger.info(">>> Connection closed!");
        }

        return lastHttpResult;
    }

    private void showMessageDueToIOException(String urlText, IOException ioe) {
        logger.error(String.format(MSG_ERROR_CONNECTING, urlText));
        logger.error(String.format(MSG_ERROR_DUE_TO, ioe.getMessage()));
    }

    private void clearHttpResults() {
        if (lastHttpResult != null) {
            lastHttpResult.clear();
        }
    }

    private void addToLastHttpResults(String inputLine) {
        lastHttpResult.add(inputLine);
    }

}
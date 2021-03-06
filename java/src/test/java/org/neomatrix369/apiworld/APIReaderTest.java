/**
 * Copyright (c) 2013. All rights reserved.
 * <p/>
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * <p/>
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 * <p/>
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 * <p/>
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.neomatrix369.apiworld;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class APIReaderTest {

    public static final boolean DO_INPUT = true;
    public static final boolean DO_OUTPUT = true;
    public static final boolean USE_CACHES = false;
    public static final boolean INSTANCE_FOLLOW_REDIRECTS = false;

    public static final String UTF_8 = "utf-8";
    public static final String CHARSET = "charset";
    public static final String HEADER_KEY = "headerKey";
    public static final String HEADER_VALUE = "headerValue";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String GET_REQUEST_METHOD = "GET";
    public static final String POST_REQUEST_METHOD = "POST";
    public static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";

    private APIReader apiReader;
    @Mock
    private HttpURLConnection mockConnection;
    @Mock
    private OutputStream mockOutputStream;
    @Mock
    private Writer mockWriter;
    private URLStreamHandler urlStreamHandler = new URLStreamHandler() {
        @Override
        protected URLConnection openConnection(URL url) throws IOException {
            return mockConnection;
        }
    };
    private URL url;

    @Before
    public void setUp() throws MalformedURLException {
        url = new URL("http://restapiunifier.com", "restapiunifier.com", -1, "", urlStreamHandler);
        apiReader = new APIReader(url);
    }

    @Test
    public void should_Send_A_Get_Request() throws Exception {
        //Given
        when(mockConnection.getInputStream()).thenReturn(IOUtils.toInputStream("response"));
        //When
        apiReader.executeGetUrl();
        //Then
        verify(mockConnection).setRequestMethod(GET_REQUEST_METHOD);
    }

    @Test
    public void should_Return_Response_To_Http_Get_Request() throws Exception {
        //Given
        when(mockConnection.getInputStream()).thenReturn(IOUtils.toInputStream("response"));
        //When
        String response = apiReader.executeGetUrl();
        //Then
        assertThat(response, is("response"));
    }

    @Test
    public void should_Return_Response_Without_Delimiters_To_Http_Get_Request() throws Exception {
        //Given
        when(mockConnection.getInputStream()).thenReturn(IOUtils.toInputStream("[[response]]"));
        //When
        String response = apiReader.executeGetUrl();
        //Then
        assertThat(response, is("response"));
    }

    @Test
    public void should_Send_A_Get_Request_With_Request_Properties() throws Exception {
        //Given
        when(mockConnection.getInputStream()).thenReturn(IOUtils.toInputStream("response"));
        Map<String, String> properties = createProperties();
        //When
        apiReader.executeGetUrl(properties);
        //Then
        verify(mockConnection).setRequestProperty("propertyKey1", "propertyValue1");
        verify(mockConnection).setRequestProperty("propertyKey2", "propertyValue2");
    }

    @Test
    public void should_Send_A_Get_Request_With_No_Request_Properties() throws Exception {
        //Given
        when(mockConnection.getInputStream()).thenReturn(IOUtils.toInputStream("response"));
        Map<String, String> properties = new HashMap<>();
        //When
        apiReader.executeGetUrl(properties);
        //Then
        verify(mockConnection, never()).setRequestProperty(anyString(), anyString());
    }

    @Test
    public void should_Disconnect_When_Http_Get_Request_Response_Is_Back() throws IOException {
        //Given
        when(mockConnection.getInputStream()).thenReturn(IOUtils.toInputStream(""));
        //When
        apiReader.executeGetUrl();
        //Then
        verify(mockConnection).disconnect();
    }

    @Test
    public void should_Disconnect_When_Http_Get_Request_Throws_An_Error() throws IOException {
        //Given
        when(mockConnection.getInputStream()).thenReturn(IOUtils.toInputStream(""));
        //When
        try {
            apiReader.executeGetUrl();
        } catch (IOException e) {
            //IOException should be thrown
        } finally {
            //Then
            verify(mockConnection).disconnect();
        }
    }

    @Test(expected = IOException.class)
    public void should_Throw_Exception_When_Connection_Error_On_Get_Request() throws Exception {
        //Given
        when(mockConnection.getInputStream()).thenThrow(IOException.class);
        //When
        apiReader.executeGetUrl(new HashMap<String, String>());
        //Then
        //Exception should be thrown
    }

    @Test
    public void should_Send_A_Post_Request_With_Appropriate_Options_And_Properties_And_Content_Type_And_Charset() throws Exception {
        //Given
        when(mockConnection.getInputStream()).thenReturn(IOUtils.toInputStream("response"));
        apiReader.setHeader(HEADER_KEY, HEADER_VALUE);
        //When
        apiReader.executePostUrl();
        //Then
        verify(mockConnection).setDoOutput(DO_OUTPUT);
        verify(mockConnection).setDoInput(DO_INPUT);
        verify(mockConnection).setInstanceFollowRedirects(INSTANCE_FOLLOW_REDIRECTS);
        verify(mockConnection).setRequestMethod(POST_REQUEST_METHOD);
        verify(mockConnection).setRequestProperty(CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED);
        verify(mockConnection).setRequestProperty(CHARSET, UTF_8);
        verify(mockConnection).setUseCaches(USE_CACHES);
        verify(mockConnection).setRequestProperty(HEADER_KEY, HEADER_VALUE);
    }

    @Test
    public void should_Return_Response_To_Http_Post_Request() throws Exception {
        //Given
        when(mockConnection.getInputStream()).thenReturn(IOUtils.toInputStream("response"));
        //When
        String response = apiReader.executePostUrl();
        //Then
        assertThat(response, is("response"));
    }

    @Test
    public void should_Return_Response_Without_Delimiters_To_Http_Post_Request() throws Exception {
        //Given
        when(mockConnection.getInputStream()).thenReturn(IOUtils.toInputStream("[[response]]"));
        //When
        String response = apiReader.executePostUrl();
        //Then
        assertThat(response, is("response"));
    }

    @Test
    public void should_Fire_Http_Post_Request_With_Appropriate_Url_Parameters() throws Exception {
        //Given
        String urlParameters = "urlParameters";
        when(mockConnection.getInputStream()).thenReturn(IOUtils.toInputStream("response"));
        APIReader apiReader = createAPIReaderWithMockWriter();

        //When
        apiReader.executePostUrl(urlParameters);
        //Then
        verify(mockWriter).write(urlParameters);
        verify(mockWriter).flush();
        verify(mockWriter).close();
    }

    @Test
    public void should_Fire_Http_Post_Request_With_No_Url_Parameters() throws Exception {
        //Given
        String urlParameters = "";
        when(mockConnection.getInputStream()).thenReturn(IOUtils.toInputStream("response"));
        APIReader apiReader = createAPIReaderWithMockWriter();

        //When
        apiReader.executePostUrl(urlParameters);
        //Then
        verifyZeroInteractions(mockWriter);
    }

    @Test
    public void should_Disconnect_When_Http_Post_Request_Response_Is_Back() throws IOException {
        //Given
        when(mockConnection.getInputStream()).thenReturn(IOUtils.toInputStream(""));
        //When
        apiReader.executePostUrl();
        //Then
        verify(mockConnection).disconnect();
    }

    @Test(expected = IOException.class)
    public void should_Throw_Exception_When_Connection_Error_On_Post_Request() throws Exception {
        //Given
        when(mockConnection.getInputStream()).thenThrow(IOException.class);
        //When
        apiReader.executePostUrl();
        //Then
        //Exception should be thrown
    }

    @Test
    public void should_Disconnect_When_Http_Post_Request_Throws_An_Error() throws IOException {
        //Given
        when(mockConnection.getInputStream()).thenThrow(IOException.class);
        //When
        try {
            apiReader.executePostUrl();
        } catch (IOException exception) {
            //IOException should be thrown
        } finally {
            //Then
            verify(mockConnection).disconnect();
        }
    }

    @Test
    public void should_Only_Return_Response_For_Last_Get_Request() throws IOException {
        //Given
        when(mockConnection.getInputStream()).thenReturn(IOUtils.toInputStream("response 1"));
        apiReader.executeGetUrl();
        when(mockConnection.getInputStream()).thenReturn(IOUtils.toInputStream("response 2"));
        //When
        String response = apiReader.executeGetUrl();
        //Then
        assertThat(response, is("response 2"));
    }

    @Test
    public void should_Only_Return_Response_For_Last_Post_Request() throws IOException {
        //Given
        when(mockConnection.getInputStream()).thenReturn(IOUtils.toInputStream("response 1"));
        apiReader.executePostUrl();
        when(mockConnection.getInputStream()).thenReturn(IOUtils.toInputStream("response 2"));
        //When
        String response = apiReader.executePostUrl();
        //Then
        assertThat(response, is("response 2"));
    }

    private Map<String, String> createProperties() {
        Map<String, String> properties = new HashMap<>();
        String propertyKey1 = "propertyKey1";
        String propertyValue1 = "propertyValue1";
        String propertyKey2 = "propertyKey2";
        String propertyValue2 = "propertyValue2";
        properties.put(propertyKey1, propertyValue1);
        properties.put(propertyKey2, propertyValue2);
        return properties;
    }

    private APIReader createAPIReaderWithMockWriter() {
        return new APIReader(url) {
            @Override
            Writer createWriter(OutputStream outputStream) {
                return mockWriter;
            }
        };
    }

}
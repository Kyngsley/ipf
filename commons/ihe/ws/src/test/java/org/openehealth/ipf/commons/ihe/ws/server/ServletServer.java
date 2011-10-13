/*
 * Copyright 2009 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openehealth.ipf.commons.ihe.ws.server;

import javax.servlet.Servlet;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * Simple abstraction of an embedded servlet server (e.g. Jetty or Tomcat).
 * <p>
 * Note: any exceptions thrown are treated as assertion failures.
 * @author Jens Riemschneider
 */
public abstract class ServletServer {
    private Servlet servlet;
    private int port;
    private String contextPath;
    private String servletPath;
    private String contextResource;
    private boolean secure;
    private String keystoreFile;
    private String keystorePass;
    private String truststoreFile;
    private String truststorePass;

    /**
     * Starts the server.
     * <p>
     * Note: any exceptions thrown are treated as assertion failures.
     */
    public abstract void start();
    
    /**
     * Stops the server if it was running.
     * <p>
     * Note: any exceptions thrown are treated as assertion failures.
     */
    public abstract void stop();

    /**
     * @param servletPath
     *          the path of the servlet itself (including patterns if needed).
     */
    public void setServletPath(String servletPath) {
        notNull(servletPath, "servletPath cannot be null");
        this.servletPath = servletPath;
    }

    /**
     * @return the path of the servlet itself (including patterns if needed).
     */
    public String getServletPath() {
        return servletPath;
    }

    /**
     * @param contextPath
     *          the path of the context in which the servlet is running.
     */
    public void setContextPath(String contextPath) {
        notNull(contextPath, "contextPath cannot be null");
        this.contextPath = contextPath;
    }

    /**
     * @return the path of the context in which the servlet is running.
     */
    public String getContextPath() {
        return contextPath;
    }

    /**
     * @param port
     *          the port that the server is started on.
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return the port that the server is started on.
     */
    public int getPort() {
        return port;
    }
    
    /**
     * The servlet to use within the servlet.
     * @param servlet
     *          the servlet.      
     */
    public void setServlet(Servlet servlet) {
        notNull(servlet, "servlet cannot be null");
        this.servlet = servlet;
    }

    /**
     * @return the servlet to use within the servlet.
     */
    public Servlet getServlet() {
        return servlet;
    }

    /**
     * @return <code>true</code> to enable SSL.
     */
    public boolean isSecure() {
        return secure;
    }

    /**
     * @param secure
     *          <code>true</code> to enable SSL.
     */
    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    /**
     * @return key store location.
     */
    public String getKeystoreFile() {
        return keystoreFile;
    }

    /**
     * @param keystoreFile
     *          key store location.
     */
    public void setKeystoreFile(String keystoreFile) {
        this.keystoreFile = keystoreFile;
    }

    /**
     * @return key store password.
     */
    public String getKeystorePass() {
        return keystorePass;
    }

    /**
     * @param keystorePass
     *          key store password.
     */
    public void setKeystorePass(String keystorePass) {
        this.keystorePass = keystorePass;
    }

    /**
     * @return trust store location.
     */
    public String getTruststoreFile() {
        return truststoreFile;
    }

    /**
     * @param truststoreFile
     *          trust store location.
     */
    public void setTruststoreFile(String truststoreFile) {
        this.truststoreFile = truststoreFile;
    }

    /**
     * @return trust store password.
     */
    public String getTruststorePass() {
        return truststorePass;
    }

    /**
     * @param truststorePass
     *          trust store password.
     */
    public void setTruststorePass(String truststorePass) {
        this.truststorePass = truststorePass;
    }

    /**
     * @return location of a spring application context to be started with the web-app.
     */
    public String getContextResource() {
        return contextResource;
    }

    /**
     * @param contextResource
     *          location of a spring application context to be started with the web-app.
     */
    public void setContextResource(String contextResource) {
        this.contextResource = contextResource;
    }
    
    /**
     * @return a free port for testing between 8000-9999.
     */
    public static int getFreePort() {
        int port = 8000;
        boolean portFree = false;
        while (!portFree) {
            port = 8000 + new Random().nextInt(2000);
            portFree = isPortFree(port);
        }
        return port;
    }
    
    private static boolean isPortFree(int port) {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(port);
            return true;
        } 
        catch (IOException e) {
            return false;
        } 
        finally { 
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    return false;
                }
            }
        }
    }
}

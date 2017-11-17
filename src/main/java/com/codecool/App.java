package com.codecool;

import com.codecool.controller.LoginController;
import com.codecool.controller.StaticController;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

    // set routes
        server.createContext("/", new LoginController());
        server.createContext("/static", new StaticController());
        server.createContext("/assets", new StaticController());



        server.setExecutor(null); // creates a default executor
    // start listening
        server.start();
    }
}

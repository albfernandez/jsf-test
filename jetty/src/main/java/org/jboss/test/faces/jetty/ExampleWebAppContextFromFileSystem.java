package org.jboss.test.faces.jetty;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jetty.ee10.webapp.WebAppContext;
import org.eclipse.jetty.server.Server;

public class ExampleWebAppContextFromFileSystem
{
    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8080);

        Path warPath = Paths.get("target/webapps/hello-servlet-5.war").toAbsolutePath().normalize();
        if (!Files.isRegularFile(warPath))
        {
            System.err.println("Unable to find " + warPath + ".  Please build the entire project once first (`mvn clean install` from top of repo)");
            System.exit(-1);
        }

        System.out.println("WAR File is " + warPath);

        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/");
        webapp.setWar(warPath.toUri().toASCIIString());

        server.setHandler(webapp);

        server.start();
        server.join();
    }
}
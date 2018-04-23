/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2017 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.license.dictionary;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.cdi.CDIFraction;
import org.wildfly.swarm.config.logging.Level;
import org.wildfly.swarm.jaxrs.JAXRSArchive;
import org.wildfly.swarm.keycloak.KeycloakFraction;
import org.wildfly.swarm.logging.LoggingFraction;
import org.wildfly.swarm.swagger.SwaggerArchive;
import org.wildfly.swarm.swagger.SwaggerFraction;

public class LicenseDictionaryApp {

    public static void main(String[] args) throws Exception {
        System.out.println("Initializing LicenseDictionaryApp from main");

        // Swarm swarm = createSwarm(args);
        Swarm swarm = new Swarm();
        // swarm.fraction(logging()).start().deploy(deployment(swarm));
        swarm.fraction(LoggingFraction.createDefaultLoggingFraction()).start().deploy(deployment(swarm));
    }

    private static Archive deployment(Swarm swarm) throws Exception {

        SwaggerArchive archive = ShrinkWrap.create(SwaggerArchive.class, "swagger-app.war");
        JAXRSArchive deployment = archive.as(JAXRSArchive.class).addPackage("org.jboss.license.dictionary");

        // JAXRSArchive deployment = swarm.createDefaultDeployment().as(JAXRSArchive.class);
        //
        // SwaggerArchive archive = deployment.as(SwaggerArchive.class);
        archive.setResourcePackages("org.jboss.license.dictionary.endpoint");
        archive.setPrettyPrint(true);
        archive.setTitle("License Dictionary Application");
        // archive.setContextRoot("/rest");

        deployment.addAllDependencies();

        return deployment;
    }

    public static Swarm createSwarm(String... args) throws Exception {
        return new Swarm(args).fraction(new CDIFraction()).fraction(new SwaggerFraction()).fraction(new KeycloakFraction());
    }

    // public static void main(String[] args) throws Exception {
    //
    // Swarm swarm = new Swarm();
    //
    // SwaggerArchive archive = ShrinkWrap.create(SwaggerArchive.class, "swagger-app.war");
    // JAXRSArchive deployment = archive.as(JAXRSArchive.class).addPackage(Main.class.getPackage());
    //
    // // Tell swagger where our resources are
    // archive.setResourcePackages("org.wildfly.swarm.examples.jaxrs.swagger");
    //
    // deployment.addAllDependencies();
    // swarm.fraction(LoggingFraction.createDefaultLoggingFraction()).start().deploy(deployment);
    // }

    private static LoggingFraction logging() {
        String logFile = System.getProperty("user.dir") + File.separator + "target" + File.separator + "debug.log";

        return new LoggingFraction().formatter("default", "%K{level}%d{HH:mm:ss,SSS} %-5p [%c] (%t) %s%e%n")
                .consoleHandler(Level.INFO, "default").fileHandler("FILE", f -> {

                    Map<String, String> fileProps = new HashMap<>();
                    fileProps.put("path", logFile);
                    f.file(fileProps);
                    f.level(Level.DEBUG);
                    f.formatter("%K{level}%d{HH:mm:ss,SSS} %-5p [%c] (%t) %s%e%n");

                }).rootLogger(Level.DEBUG, "FILE", "CONSOLE");
    }

}

package com.lemonsoft.pliker;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static spark.Spark.*;

public class Pliker {
    private static FreeMarkerEngine freeMarkerEngine;

    public static void main(String[] argv) {
        // Load JSON config
        var gson = new Gson();
        PlikerConfiguration config;
        try (var reader = new JsonReader(new FileReader("config.json"))) {
            config = gson.fromJson(reader, PlikerConfiguration.class);
        } catch (IOException e) {
            System.out.println("Unable to read config.json.");
            e.printStackTrace();
            return;
        }
        if (config == null) {
            System.out.println("config.json has invalid structure.");
            return;
        }

        // Spark/Jetty/Freemarker config
        port(config.webPort == 0 ? 8080 : config.webPort);
        if (config.maxUploadSize == 0) config.maxUploadSize = -1;
        var multipartConfigElement = new MultipartConfigElement("plikerTemp", config.maxUploadSize, config.maxUploadSize, 0);
        staticFileLocation("static");
        Configuration freeMarkerConfiguration = new Configuration(Configuration.VERSION_2_3_26);
        freeMarkerConfiguration.setTemplateLoader(new ClassTemplateLoader(Pliker.class, "/"));
        freeMarkerEngine = new FreeMarkerEngine(freeMarkerConfiguration);

        post("/upload", (req, resp) -> {
            if (req.session().attribute("user") == null) return null;
            req.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);
            Part filePart = req.raw().getPart("uploadfile");
            String fileName = filePart.getSubmittedFileName();
            InputStream fileStream = filePart.getInputStream();
            return null;
        });

        get("/login", (request, response) -> {
            if (request.session().attribute("user") == null) {
                return freeMarkerEngine.render(new ModelAndView(null, "login.ftl"));
            } else {
                response.redirect("/");
                return null;
            }
        });
        get("/", (request, response) -> {
            String username = request.session().attribute("user");
            if (username != null) {
                return username;
            }
            response.redirect("/login");
            return null;
        });
        post("/login", (request, response) -> {
            String submittedToken = request.queryParams("token");
            if (submittedToken == null) {
                halt(400);
            }
            String username = config.tokens.get(submittedToken);
            if (username == null) {
                return freeMarkerEngine.render(new ModelAndView(Collections.singletonMap("flashmsg", "Invalid token!"), "login.ftl"));
            }
            request.session().attribute("user", username);
            response.redirect("/");
            return null;
        });

    }
}

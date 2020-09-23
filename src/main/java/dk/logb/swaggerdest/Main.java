package dk.logb.swaggerdest;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import net.minidev.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.stream.Collectors;

enum Version {
    SWAGGER_1_0, OPEN_API_3_0
}

public class Main {
    private static Logger log = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        String json = null;
        try {
            json = getStringFromFile("./src/main/resources/input3_0.json");
            processContract(json);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            json = getStringFromURL("https://gateway.marvel.com/docs/public");
            processContract(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void processContract(String json) {
        Version version = getVersionFromContract(json);
        switch (version) {
            case SWAGGER_1_0:
                processSwagger_1_0(json);
                break;
            case OPEN_API_3_0:
                processOpenAPI_3_0(json);
                break;
        }
    }

    private static void processOpenAPI_3_0(String json) {
        Map<String, String> out = JsonPath.read(json, "$.paths"); //3.0

        long count = out.keySet().stream().count();
        log.info("Resources in total: " + count);
        List<String> collect = out.keySet().stream().sorted().collect(Collectors.toList());
        for (String s : collect) {
            System.out.println(s);
        }
    }

    private static void processSwagger_1_0(String json) {
        //get path objects
        JSONArray out = JsonPath.read(json, "$.apis[?(@.path != null)].path"); //1.0
        log.info("Resources in total: " + out.size());
        for (int i = 0; i < out.size(); i++) {
            System.out.println(out.get(i));
        }
    }

    public static String getStringFromFile(String path) {
        Path path1 = Paths.get(path);
        try {
            String s = Files.readString(path1);
            return s;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static String getStringFromURL(String url) {
        try {
            return new Scanner(new URL(url).openStream(), "UTF-8").useDelimiter("\\A").next();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static Version getVersionFromContract(String json) {

        String swaggerVersion = safeRead(json, "$.swaggerVersion");
        if (swaggerVersion != null && swaggerVersion.trim().equals("1.0")) {
            return Version.SWAGGER_1_0;
        }
        String openAPIVersion = safeRead(json, "$.openapi");
        if (openAPIVersion != null && openAPIVersion.trim().startsWith("3.0")) {
            return Version.OPEN_API_3_0;
        }
        throw new IllegalArgumentException("could not establish version");

    }

    private static String safeRead(String json, String s) {
        try {
            return JsonPath.read(json, s);
        } catch (PathNotFoundException pne) {
            return null;
        }
    }


}

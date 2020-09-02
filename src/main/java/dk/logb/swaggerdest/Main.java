package dk.logb.swaggerdest;

import com.jayway.jsonpath.JsonPath;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        System.out.println(new File(".").getAbsolutePath());
        String file = "./src/main/resources/input.json";
        String json = getString(file);



        Map<String, String> out = JsonPath.read(json, "$.paths");
        long count = out.keySet().stream().count();
        System.out.println(count);
        List<String> collect = out.keySet().stream().sorted().collect(Collectors.toList());
        for(String s : collect) {
            System.out.println(s);
        }
    }

    public static String getString(String path) {
        Path path1 = Paths.get(path);


        try {
            String s = Files.readString(path1);
            return s;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}

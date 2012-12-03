package net.md_5.polymer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import lombok.Data;
import org.yaml.snakeyaml.Yaml;

@Data
public class Configuration {

    private static final transient Yaml yaml = new Yaml();
    /*========================================================================*/
    private String ip = "0.0.0.0";
    private int port = 25565;
    private String motd = "Just another Polymer server";
    private int timeout = 30;
    private int maxPlayers = 20;
    private boolean onlineMode = true;

    private Configuration() {
    }

    public static Configuration load(String file) throws IllegalAccessException, IOException {
        File storage = new File(file);
        storage.getParentFile().mkdir();
        storage.createNewFile();

        Configuration config;

        try (FileReader reader = new FileReader(storage)) {
            config = yaml.loadAs(reader, Configuration.class);
        }

        if (config == null) {
            config = new Configuration();
        }

        try (FileWriter writer = new FileWriter(storage)) {
            writer.write(yaml.dumpAsMap(config));
        }

        return config;
    }
}

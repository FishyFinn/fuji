import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.sakurawald.config.annotation.Documentation;
import io.github.sakurawald.config.model.ConfigModel;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
public class ModuleDocumentationGeneratorTest {

    private StringBuilder processJavaObject(Object obj) {
        StringBuilder sb = new StringBuilder();
        sb.append("""
                # Modules

                _**By default, all the modules are disabled.**_

                _**This page is generated by program. To get more detailed documentation, read [the runtime-control configuration](https://github.com/sakurawald/fuji-fabric/blob/dev/src/main/java/io/github/sakurawald/config/model/ConfigModel.java)**_
                
                """);

        processFields(sb, obj);
        return sb;
    }

    private void processFields(StringBuilder sb, Object obj) {
        Class<?> clazz = obj.getClass();
        for (Class<?> innerClazz : clazz.getDeclaredClasses()) {
            String name = innerClazz.getSimpleName();

            /* insert related comment property */
            if (innerClazz.isAnnotationPresent(Documentation.class)) {
                Documentation annotation = innerClazz.getAnnotation(Documentation.class);
                sb.append("## ").append(name).append(" module");
                sb.append("\n").append(annotation.value().trim()).append("\n\n");
            }
        }
    }

    private boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() || clazz == Boolean.class || clazz == Character.class ||
                Number.class.isAssignableFrom(clazz) || clazz == String.class;
    }

    private void writeToFile(String fileName, Object object) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String filePath = "./fuji-fabric.wiki/" + fileName;
        new File(filePath).getParentFile().mkdirs();

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(object.toString());
            System.out.println("File " + fileName + " has been written successfully.");
        } catch (IOException e) {
            System.err.println("An error occurred while writing file: " + e.getMessage());
        }
    }

    @Test
    void buildModuleDocumentation() {
        writeToFile("Module.md", processJavaObject(new ConfigModel().modules));
    }

}

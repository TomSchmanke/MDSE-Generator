import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import template_data.ControllerDataModel;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class TemplateResolver {

    private final VelocityEngine velocityEngine;
    private final List<String> entityNames = Arrays.asList("Antrag", "Benutzer", "Fahrzeug", "Hersteller");

    public TemplateResolver() {
        velocityEngine = new VelocityEngine();
        Properties velocityProperties = new Properties();
        velocityProperties.put("file.resource.loader.path", "src/main/resources/");
        velocityEngine.init(velocityProperties);
    }

    private void resolveTemplate(VelocityContext velocityContext, String inputTemplate, String outputFile) {
        try {
            Writer writer = new FileWriter(outputFile);
            velocityEngine.mergeTemplate(inputTemplate, "UTF-8", velocityContext, writer);
            writer.flush();
            writer.close();
            System.out.println("Successfully generated " + outputFile);
        } catch (IOException e) {
            System.out.println("Error occurred during merging of template and velocity context " + e);
        }
    }

    public void createControllerFiles() {
        for (String entity: entityNames) {
            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("targetPackagePath" , "TODO");
            velocityContext.put("entitiesPackagePath" , "TODO");
            velocityContext.put("repositoriesPackagePath" , "TODO");

            ControllerDataModel controllerDataModel = new ControllerDataModel(entity, "id");
            velocityContext.put("controller", controllerDataModel);

            resolveTemplate(velocityContext, "controller.vm", entity + "Controller.java");
        }
    }



}

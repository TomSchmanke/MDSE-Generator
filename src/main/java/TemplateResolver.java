import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class TemplateResolver {

    static String inputTemplate = "methode.vm";
    static String annotation = "GetMapping";
    static String crudPath = "/hersteller";
    static String crudParameter = "/{id}";
    static String privatePublic = "public";
    static String returnType = "Hersteller";
    static String methodName = "get";
    static String methodParameters = "@PathVariable int id";
    static List<String> allStatements = Arrays.asList("Optional<Hersteller> ret = repository.findById(id)", "if (ret.isPresent()) return ret.get();", "return null;");
    static String outputFile = "test.java";

    public TemplateResolver() throws IOException {
        VelocityEngine velocityEngine = new VelocityEngine();
        Properties velocityProperties = new Properties();
        velocityProperties.put("file.resource.loader.path", "src/main/resources/");
        velocityEngine.init(velocityProperties);

        VelocityContext context = new VelocityContext();
        context.put("annotation", annotation);
        context.put("crudPath", crudPath);
        context.put("crudParameter", crudParameter);
        context.put("privatePublic", privatePublic);
        context.put("returnType", returnType);
        context.put("methodName", methodName);
        context.put("methodParameters", methodParameters);
        context.put("allStatements", allStatements);

        Writer writer = new FileWriter(outputFile);
        velocityEngine.mergeTemplate(inputTemplate, "UTF-8", context, writer);
        writer.flush();
        writer.close();

        System.out.println("Generated " + outputFile);
    }

}

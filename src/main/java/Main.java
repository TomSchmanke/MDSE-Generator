import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        InitializeProject initializeProject = new InitializeProject();
        String nameOfZip = initializeProject.loadGeneratedFilesFromSpringInitializer();

        if(nameOfZip == null) {
            return;
        }
        initializeProject.unzipFile(nameOfZip, nameOfZip.substring(0, nameOfZip.length() - 3));

        TemplateResolver templateResolver = new TemplateResolver();
        templateResolver.createControllerFiles();
    }
}

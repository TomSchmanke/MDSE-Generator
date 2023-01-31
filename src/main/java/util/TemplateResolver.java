package util;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import template_data.AssociationsModel;
import template_data.ControllerModel;
import template_data.EntityModel;
import template_data.RepositoryModel;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * Class which on initialization creates a velocity engine and context configured for the templates used to create the
 * necessary controllers, entities and repositories out of data given in the structure defined in the template_data
 * directory based on the .vm templates in the resources' directory.
 *
 * @author Tom Schmanke
 * @version 1.0 Initial creation with generation of controller, entities and repositories based on templates
 */
public class TemplateResolver {

    private final VelocityEngine velocityEngine;

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

    /**
     * Method generates a list of RestController .java files based on the controller.vm template and the List of
     * {@link ControllerModel} containing the necessary data
     *
     * @param controllerModels List of {@link ControllerModel} which hold the data which will be used in the generation
     * of the RestController .java files
     * @return List of names of the generated files
     */
    public List<String> createControllerFiles(List<ControllerModel> controllerModels) {
        for (ControllerModel controllerModel : controllerModels) {
            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("targetPackagePath" , "TODO");
            velocityContext.put("entitiesPackagePath" , "TODO");
            velocityContext.put("repositoriesPackagePath" , "TODO");
            velocityContext.put("controller", controllerModel);

            resolveTemplate(velocityContext, "controller.vm", controllerModel.getEntityName() + "Controller.java");
        }
        return controllerModels.stream().map(controllerModel -> controllerModel.getEntityName() + "Controller.java").toList();
    }

    /**
     * Method generates a list of JPA Entities .java files based on the entity.vm template and the List of
     * {@link EntityModel} containing the necessary data
     *
     * @param entityModels List of {@link EntityModel} which hold the data which will be used in the generation
     * of the JPA Entities .java files
     * @return List of names of the generated files
     */
    public List<String> createEntityFiles(List<EntityModel> entityModels, List<AssociationsModel> associationsModels) {
        for (EntityModel entityModel : entityModels) {
            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("targetPackagePath" , "TODO");
            velocityContext.put("entity", entityModel);

            List<AssociationsModel> filteredMTOAssociationsForEntity = associationsModels.stream().filter(associationsModel -> Objects.equals(associationsModel.fkEntity(), entityModel.getEntityName())).toList();
            List<AssociationsModel> filteredOTMAssociationsForEntity = associationsModels.stream().filter(associationsModel -> Objects.equals(associationsModel.referencedEntity(), entityModel.getEntityName())).toList();
            velocityContext.put("mtoAssociations", filteredMTOAssociationsForEntity);
            velocityContext.put("otmAssociations", filteredOTMAssociationsForEntity);

            resolveTemplate(velocityContext, "entity.vm", entityModel.entityName() + ".java");
        }
        return entityModels.stream().map(entityModel -> entityModel.entityName() + ".java").toList();
    }

    /**
     * Method generates a list of JPA Repositories .java files based on the repository.vm template and the List of
     * {@link RepositoryModel} containing the necessary data
     *
     * @param repositoryModels List of {@link RepositoryModel} which hold the data which will be used in the generation
     * of the JPA Repositories .java files
     * @return List of names of the generated files
     */
    public List<String> createRepositoryFiles(List<RepositoryModel> repositoryModels) {
        for (RepositoryModel repositoryModel : repositoryModels) {
            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("targetPackagePath" , "TODO");
            velocityContext.put("entitiesPackagePath" , "TODO");
            velocityContext.put("repository", repositoryModel);

            resolveTemplate(velocityContext, "entity.vm", repositoryModel.repositoryName() + ".java");
        }
        return repositoryModels.stream().map(repositoryModel -> repositoryModel.repositoryName() + "Repository.java").toList();
    }

}

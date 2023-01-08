package util;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import template_data.AssociationsDataModel;
import template_data.ControllerDataModel;
import template_data.EntityDataModel;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class TemplateResolver {

    private final VelocityEngine velocityEngine;

    private List<ControllerDataModel> controllerDataModels;
    private List<EntityDataModel> entityDataModels;
    private List<AssociationsDataModel> associationsDataModels;

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
        for (ControllerDataModel controllerDataModel: controllerDataModels) {
            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("targetPackagePath" , "TODO");
            velocityContext.put("entitiesPackagePath" , "TODO");
            velocityContext.put("repositoriesPackagePath" , "TODO");
            velocityContext.put("controller", controllerDataModel);

            resolveTemplate(velocityContext, "controller.vm", controllerDataModel.getEntityName() + "Controller.java");
        }
    }

    public void createEntityFiles() {
        for (EntityDataModel entityDataModel: entityDataModels) {
            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("targetPackagePath" , "TODO");
            velocityContext.put("entity", entityDataModel);

            List<AssociationsDataModel> filteredMTOAssociationsForEntity = associationsDataModels.stream().filter(associationsDataModel -> Objects.equals(associationsDataModel.fkEntity(), entityDataModel.getEntityName())).toList();
            List<AssociationsDataModel> filteredOTMAssociationsForEntity = associationsDataModels.stream().filter(associationsDataModel -> Objects.equals(associationsDataModel.referencedEntity(), entityDataModel.getEntityName())).toList();
            velocityContext.put("mtoAssociations", filteredMTOAssociationsForEntity);
            velocityContext.put("otmAssociations", filteredOTMAssociationsForEntity);

            resolveTemplate(velocityContext, "entity.vm", entityDataModel.entityName() + ".java");
        }
    }

    public void createRepositoryFiles() {
        // TODO
    }

    public void setControllerDataModels(List<ControllerDataModel> controllerDataModels) {
        this.controllerDataModels = controllerDataModels;
    }

    public void setEntityDataModels(List<EntityDataModel> entityDataModels) {
        this.entityDataModels = entityDataModels;
    }

    public void setAssociationsDataModels(List<AssociationsDataModel> associationsDataModels) {
        this.associationsDataModels = associationsDataModels;
    }

}

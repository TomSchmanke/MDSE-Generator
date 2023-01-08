package util;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import template_data.AssociationsModel;
import template_data.ControllerModel;
import template_data.DataModel;
import template_data.EntityModel;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class TemplateResolver {

    private final VelocityEngine velocityEngine;

    private List<ControllerModel> controllerModels;
    private List<EntityModel> entityModels;
    private List<AssociationsModel> associationsModels;

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
        for (ControllerModel controllerModel : controllerModels) {
            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("targetPackagePath" , "TODO");
            velocityContext.put("entitiesPackagePath" , "TODO");
            velocityContext.put("repositoriesPackagePath" , "TODO");
            velocityContext.put("controller", controllerModel);

            resolveTemplate(velocityContext, "controller.vm", controllerModel.getEntityName() + "Controller.java");
        }
    }

    public void createEntityFiles() {
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
    }

    public void createRepositoryFiles() {
        // TODO
    }

    public void setDataModel(DataModel datamodel) {
        this.controllerModels = datamodel.getControllerDataModels();
        this.entityModels = datamodel.getEntityDataModels();
        this.associationsModels = datamodel.getAssociationsDataModels();
    }

    public void setControllerDataModels(List<ControllerModel> controllerModels) {
        this.controllerModels = controllerModels;
    }

    public void setEntityDataModels(List<EntityModel> entityModels) {
        this.entityModels = entityModels;
    }

    public void setAssociationsDataModels(List<AssociationsModel> associationsModels) {
        this.associationsModels = associationsModels;
    }

}

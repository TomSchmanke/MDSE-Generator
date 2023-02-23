package template_data;

import java.util.List;

/**
 * Data model that is used to create the generated application.
 *
 * @author Laura Schmidt
 * @version 1.0 initial creation
 */
public class DataModel {
    private List<ControllerModel> controllerModels;
    private List<EntityModel> entityModels;
    private List<AssociationsModel> associationsModels;
    private List<RepositoryModel> repositoryModels;

    public List<ControllerModel> getControllerDataModels() {
        return controllerModels;
    }

    public void setControllerDataModels(List<ControllerModel> controllerModels) {
        this.controllerModels = controllerModels;
    }

    public List<EntityModel> getEntityDataModels() {
        return entityModels;
    }

    public void setEntityDataModels(List<EntityModel> entityModels) {
        this.entityModels = entityModels;
    }

    public List<AssociationsModel> getAssociationsDataModels() {
        return associationsModels;
    }

    public void setAssociationsDataModels(List<AssociationsModel> associationsModels) {
        this.associationsModels = associationsModels;
    }

    public List<RepositoryModel> getRepositoryDataModels() {
        return repositoryModels;
    }

    public void setRepositoryDataModels(List<RepositoryModel> repositoryModels) {
        this.repositoryModels = repositoryModels;
    }

}

package template_data;

import java.util.List;

public class DataModel {
    private List<ControllerModel> controllerModels;
    private List<EntityModel> entityModels;
    private List<AssociationsModel> associationsModels;

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
}

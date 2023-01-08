package template_data;

import java.util.List;

public class DataModel {
    private List<ControllerDataModel> controllerDataModels;
    private List<EntityDataModel> entityDataModels;
    private List<AssociationsDataModel> associationsDataModels;

    public List<ControllerDataModel> getControllerDataModels() {
        return controllerDataModels;
    }

    public void setControllerDataModels(List<ControllerDataModel> controllerDataModels) {
        this.controllerDataModels = controllerDataModels;
    }

    public List<EntityDataModel> getEntityDataModels() {
        return entityDataModels;
    }

    public void setEntityDataModels(List<EntityDataModel> entityDataModels) {
        this.entityDataModels = entityDataModels;
    }

    public List<AssociationsDataModel> getAssociationsDataModels() {
        return associationsDataModels;
    }

    public void setAssociationsDataModels(List<AssociationsDataModel> associationsDataModels) {
        this.associationsDataModels = associationsDataModels;
    }
}

package template_data;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public record EntityDataModel(String entityName, String identificationVariable, List<AttributeDataModel> attributeDataModels) {

    public String getEntityName() {
        return entityName;
    }

    public String getIdentificationVariable() {
        return identificationVariable;
    }

    public List<AttributeDataModel> getAttributeDataModels() {
        return attributeDataModels;
    }

    public String getAttributesForConstructorHeader() {
        return attributeDataModels.stream().map(AttributeDataModel::getTypeAndName).collect(Collectors.joining(", "));
    }

    public String getAttributesForConstructorBody() {
        return attributeDataModels.stream().map(AttributeDataModel::getConstructorAssignment).collect(Collectors.joining("\n\t\t"));
    }

    public List<AttributeDataModel> getSimpleAttributes() {
        return attributeDataModels.stream().filter(attributeDataModel -> Arrays.asList("int", "float", "String", "boolean").contains(attributeDataModel.attributeType())).toList();
    }
}

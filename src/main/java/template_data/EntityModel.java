package template_data;

import java.util.List;

/**
 * Record that holds information about JPA-Entities.
 * The record and its methods are used in the entity-base.vm and entity-impl.vm template.
 *
 * @param entityName name of JPA entity
 * @param identificationVariable variable used for identification (normally id)
 * @param attributeModels entity attributes
 *
 * @author Tom Schmanke
 * @version 1.0 initial creation
 */
public record EntityModel(String entityName, String identificationVariable, List<AttributeModel> attributeModels) {

    public String getEntityName() {
        return entityName;
    }

    public String getIdentificationVariable() {
        return identificationVariable;
    }

    public String getIdentificationVariableCamelCase() {
        return identificationVariable.substring(0, 1).toLowerCase() + identificationVariable.substring(1);
    }

    public List<AttributeModel> getAttributeDataModels() {
        return attributeModels;
    }

}

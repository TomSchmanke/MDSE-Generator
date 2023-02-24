package template_data;

import de.arinir.mdsd.metamodell.MDSDMetamodell.DataType;
import de.arinir.mdsd.metamodell.MDSDMetamodell.VisibilityET;

/**
 * Record that holds data of attributes.
 * The record and its methods are used in the entity-base.vm template.
 *
 * @param attributeName name of attribute
 * @param attributeType data type of attribute
 * @param visibility visibility of attribute
 *
 * @author Tom Schmanke
 * @version 1.0 initial creation
 *
 * @author Laura Schmidt
 * @version 1.1 add visibility
 */
public record AttributeModel(String attributeName, DataType attributeType, VisibilityET visibility) {

    public String getAttributeNameCamelCase() {
        return attributeName.substring(0, 1).toLowerCase() + attributeName.substring(1);
    }

    public DataType getAttributeType() {
        return attributeType;
    }

    public VisibilityET getAttributeVisibility() {
        return visibility;
    }

}

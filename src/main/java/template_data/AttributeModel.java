package template_data;

import de.arinir.mdsd.metamodell.MDSDMetamodell.DataType;
import de.arinir.mdsd.metamodell.MDSDMetamodell.VisibilityET;

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

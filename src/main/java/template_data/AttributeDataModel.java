package template_data;

import de.arinir.mdsd.metamodell.MDSDMetamodell.DataType;
import de.arinir.mdsd.metamodell.MDSDMetamodell.VisibilityET;

public record AttributeDataModel(String attributeName, DataType attributeType, VisibilityET visibility) {

    public String getAttributeName() {
        return attributeName;
    }

    public DataType getAttributeType() {
        return attributeType;
    }

    public VisibilityET getAttributeVisibility() {
        return visibility;
    }

    public String getTypeAndName() {
        return attributeType + " " + attributeName;
    }

    public String getConstructorAssignment() {
        return "this." + attributeName + " = " + attributeName + ";";
    }
}

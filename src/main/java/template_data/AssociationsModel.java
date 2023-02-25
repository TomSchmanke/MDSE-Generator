package template_data;

/**
 * Record that holds information about associations between two entities identified via the parameters.
 * The record and its methods are used in the entity-base.vm template.
 *
 * @param fkEntity         name of entity (association end 1)
 * @param fkName           role name of entity (association end 1)
 * @param referencedEntity name of entity (association end 2)
 * @param referencedName   role name of entity (association end 2)
 *
 * @author Tom Schmanke
 * @version 1.0 initial creation
 */
public record AssociationsModel(String fkEntity, String fkName, String referencedEntity, String referencedName) {

    public String getFKEntity() {
        return fkEntity;
    }

    public String getFKName() {
        return fkName;
    }

    public String getFKNameCamelCase() {
        return fkName.substring(0, 1).toLowerCase() + fkName.substring(1);
    }

    public String getReferencedEntity() {
        return referencedEntity;
    }

    public String getReferencedName() {
        return referencedName;
    }

    public String getReferencedNameCamelCase() {
        return referencedName.substring(0, 1).toLowerCase() + referencedName.substring(1);
    }

    public String getMappedByKey() {
        return referencedName.substring(0, 1).toLowerCase() + referencedName.substring(1);
    }

    public String getFKKey() {
        return "FK_" + referencedName();
    }
}

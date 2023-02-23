package template_data;

import de.arinir.mdsd.metamodell.MDSDMetamodell.MultiplicityT;

/**
 * Record that holds information about associations.
 *
 * @param fkEntity name of entity (association end 1)
 * @param fkName role name of entity (association end 1)
 * @param fkMultiplicity multiplicity (association end 1)
 * @param referencedEntity name of entity (association end 2)
 * @param referencedName role name of entity (association end 2)
 * @param referencedMultiplicity multiplicity (association end 2)
 *
 * @author Tom Schmanke
 * @version 1.0 initial creation
 *
 * @author Laura Schmidt
 * @version 2.0 use MultiplicityT instead of own class for multiplicity
 */
public record AssociationsModel(String fkEntity, String fkName, MultiplicityT fkMultiplicity, String referencedEntity, String referencedName, MultiplicityT referencedMultiplicity) {

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

package template_data;

/**
 * Record that holds information for the JPA-Repository creation.
 * The record and its methods are used in the repository-base.vm and repository-impl.vm template.
 *
 * @param repositoryName name of JPARepository
 *
 * @author Tom Schmanke
 * @version 1.0 initial creation
 */
public record RepositoryModel(String repositoryName) {

    public String getRepositoryName() {
        return repositoryName + "Repository";
    }

    public String getEntityName() {
        return repositoryName;
    }

}

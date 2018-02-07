package org.jboss.license.dictionary.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "ProjectVersion")
@Table(name = "project_version", indexes = { @Index(name = "idx_project_version_project", columnList = "project_id") })
@ToString
@EqualsAndHashCode
public class ProjectVersion {

    public static final String SEQUENCE_NAME = "project_version_id_seq";

    @Id
    @GeneratedValue(generator = SEQUENCE_NAME)
    @GenericGenerator(name = SEQUENCE_NAME, strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = SEQUENCE_NAME), @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "100") })
    @Getter
    private Integer id;

    @NotNull
    @Column(name = "scm_url")
    @Getter
    @Setter
    private String scmUrl;

    @NotNull
    @Size(max = 50)
    @Column(name = "scm_revision", length = 50)
    @Getter
    @Setter
    private String scmRevision;

    @ManyToOne
    @JoinColumn(nullable = false, foreignKey = @ForeignKey(name = "fk_project_version_project"))
    @Getter
    @Setter
    private Project project;

    @OneToMany(mappedBy = "projectVersion")
    private Set<ProjectVersion> projectVersions;

    public ProjectVersion() {
        this.projectVersions = new HashSet<ProjectVersion>();
    }

    public void addProjectVersion(ProjectVersion projectVersion) {
        this.projectVersions.add(projectVersion);
    }

}

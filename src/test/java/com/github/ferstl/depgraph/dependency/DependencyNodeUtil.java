package com.github.ferstl.depgraph.dependency;

import org.apache.maven.artifact.Artifact;

import static org.apache.maven.shared.dependency.tree.DependencyNode.OMITTED_FOR_CONFLICT;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class DependencyNodeUtil {

  private DependencyNodeUtil() {
    throw new AssertionError("not instantiable");
  }

  public static DependencyNode createDependencyNodeWithConflict(String groupId, String artifactId, String effectiveVersion) {
    Artifact artifact = createArtifact(groupId, artifactId, effectiveVersion);
    Artifact conflictingArtifact = mock(Artifact.class);
    when(conflictingArtifact.getVersion()).thenReturn(effectiveVersion + "-alpha");

    org.apache.maven.shared.dependency.tree.DependencyNode mavenDependencyNode = new org.apache.maven.shared.dependency.tree.DependencyNode(artifact, OMITTED_FOR_CONFLICT, conflictingArtifact);
    return new DependencyNode(mavenDependencyNode);
  }

  public static DependencyNode createDependencyNode(String groupId, String artifactId, String version) {
    return new DependencyNode(createArtifact(groupId, artifactId, version));
  }

  private static Artifact createArtifact(String groupId, String artifactId, String version) {
    Artifact artifact = mock(Artifact.class);
    when(artifact.getGroupId()).thenReturn(groupId);
    when(artifact.getArtifactId()).thenReturn(artifactId);
    when(artifact.getVersion()).thenReturn(version);
    when(artifact.getScope()).thenReturn("compile");
    return artifact;
  }
}

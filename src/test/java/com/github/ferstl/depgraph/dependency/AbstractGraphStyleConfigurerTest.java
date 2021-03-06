package com.github.ferstl.depgraph.dependency;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import com.github.ferstl.depgraph.graph.Edge;
import com.github.ferstl.depgraph.graph.GraphBuilder;
import com.github.ferstl.depgraph.graph.Node;
import com.github.ferstl.depgraph.graph.TestFormatter;

import static com.github.ferstl.depgraph.dependency.DependencyNodeUtil.createDependencyNode;
import static com.github.ferstl.depgraph.dependency.DependencyNodeUtil.createDependencyNodeWithConflict;
import static com.github.ferstl.depgraph.dependency.NodeIdRenderers.VERSIONLESS_ID;
import static org.junit.Assert.assertThat;

public abstract class AbstractGraphStyleConfigurerTest {

  private GraphStyleConfigurer graphStyleConfigurer;
  private TestFormatter formatter;
  private DependencyNode from;
  private DependencyNode to;
  private String fromId;
  private String toId;
  private DependencyNode toWithConflict;

  @Before
  public void before() {
    this.graphStyleConfigurer = createGraphStyleConfigurer();
    this.formatter = new TestFormatter();
    this.from = createDependencyNode("group1", "artifact1", "version1");
    this.to = createDependencyNode("group2", "artifact2", "version2");
    this.toWithConflict = createDependencyNodeWithConflict("group2", "artifact2", "version2");
    this.fromId = VERSIONLESS_ID.render(this.from);
    this.toId = VERSIONLESS_ID.render(this.to);

  }

  @Test
  public void showGroupIds() {
    // arrange
    GraphBuilder<DependencyNode> graphBuilder = new GraphBuilder<>();
    this.graphStyleConfigurer
        .showGroupIds(true)
        .configure(graphBuilder)
        .useNodeIdRenderer(VERSIONLESS_ID)
        .graphFormatter(this.formatter);


    // act
    graphBuilder.addEdge(this.from, this.to);
    graphBuilder.toString();

    // assert
    assertThat(this.formatter.nodes, Matchers.<Node>containsInAnyOrder(
        new Node<>(this.fromId, getNodeNameForGroupIdOnly("group1"), this.from),
        new Node<>(this.toId, getNodeNameForGroupIdOnly("group2"), this.to)));

    assertThat(this.formatter.edges, Matchers.containsInAnyOrder(new Edge(this.fromId, this.toId, getEdgeNameForNonConflictingVersion("version2"))));
  }

  @Test
  public void showArtifactIds() {
    // arrange
    GraphBuilder<DependencyNode> graphBuilder = new GraphBuilder<>();
    this.graphStyleConfigurer
        .showArtifactIds(true)
        .configure(graphBuilder)
        .useNodeIdRenderer(VERSIONLESS_ID)
        .graphFormatter(this.formatter);

    // act
    graphBuilder.addEdge(this.from, this.to);
    graphBuilder.toString();

    // assert
    assertThat(this.formatter.nodes, Matchers.<Node>containsInAnyOrder(
        new Node<>(this.fromId, getNodeNameForArtifactIdOnly("artifact1"), this.from),
        new Node<>(VERSIONLESS_ID.render(this.to), getNodeNameForArtifactIdOnly("artifact2"), this.to)));

    assertThat(this.formatter.edges, Matchers.containsInAnyOrder(new Edge(this.fromId, this.toId, getEdgeNameForNonConflictingVersion("version2"))));
  }

  @Test
  public void showVersionsOnNodes() {
    // arrange
    GraphBuilder<DependencyNode> graphBuilder = new GraphBuilder<>();
    this.graphStyleConfigurer
        .showVersionsOnNodes(true)
        .configure(graphBuilder)
        .useNodeIdRenderer(VERSIONLESS_ID)
        .graphFormatter(this.formatter);

    // act
    graphBuilder.addEdge(this.from, this.to);
    graphBuilder.toString();

    // assert
    assertThat(this.formatter.nodes, Matchers.<Node>containsInAnyOrder(
        new Node<>(this.fromId, getNodeNameForVersionOnly("version1"), this.from),
        new Node<>(this.toId, getNodeNameForVersionOnly("version2"), this.to)));

    assertThat(this.formatter.edges, Matchers.containsInAnyOrder(new Edge(this.fromId, this.toId, getEdgeNameForNonConflictingVersion("version2"))));
  }

  @Test
  public void showVersionsOnNodesWithConflict() {
    // arrange
    GraphBuilder<DependencyNode> graphBuilder = new GraphBuilder<>();
    this.graphStyleConfigurer
        .showVersionsOnNodes(true)
        .configure(graphBuilder)
        .useNodeIdRenderer(VERSIONLESS_ID)
        .graphFormatter(this.formatter);

    // act
    graphBuilder.addEdge(this.from, this.toWithConflict);
    graphBuilder.toString();

    // assert
    assertThat(this.formatter.nodes, Matchers.<Node>containsInAnyOrder(
        new Node<>(this.fromId, getNodeNameForVersionOnly("version1"), this.from),
        new Node<>(this.toId, getNodeNameForVersionOnly("version2-alpha"), this.to)));

    assertThat(this.formatter.edges, Matchers.containsInAnyOrder(new Edge(this.fromId, this.toId, getEdgeNameForConflictingVersion("version2", false))));
  }

  @Test
  public void showVersionsOnEdgesWithoutConflict() {
    // arrange
    GraphBuilder<DependencyNode> graphBuilder = new GraphBuilder<>();
    this.graphStyleConfigurer
        .showVersionsOnEdges(true)
        .configure(graphBuilder)
        .useNodeIdRenderer(VERSIONLESS_ID)
        .graphFormatter(this.formatter);

    // act
    graphBuilder.addEdge(this.from, this.to);
    graphBuilder.toString();

    // assert
    assertThat(this.formatter.nodes, Matchers.<Node>containsInAnyOrder(
        new Node<>(this.fromId, getEmptyNodeName(), this.from),
        new Node<>(this.toId, getEmptyNodeName(), this.to)));

    assertThat(this.formatter.edges, Matchers.containsInAnyOrder(new Edge(this.fromId, this.toId, getEdgeNameForNonConflictingVersion("version2"))));
  }

  @Test
  public void showVersionsOnEdgesWithConflict() {
    // arrange
    GraphBuilder<DependencyNode> graphBuilder = new GraphBuilder<>();
    this.graphStyleConfigurer
        .showVersionsOnEdges(true)
        .configure(graphBuilder)
        .useNodeIdRenderer(VERSIONLESS_ID)
        .graphFormatter(this.formatter);

    // act
    graphBuilder.addEdge(this.from, this.toWithConflict);
    graphBuilder.toString();

    // assert
    assertThat(this.formatter.nodes, Matchers.<Node>containsInAnyOrder(
        new Node<>(this.fromId, getEmptyNodeName(), this.from),
        new Node<>(this.toId, getEmptyNodeName(), this.toWithConflict)));

    assertThat(this.formatter.edges, Matchers.containsInAnyOrder(new Edge(this.fromId, this.toId, getEdgeNameForConflictingVersion("version2", true))));
  }

  @Test
  public void showAll() {
    // arrange
    GraphBuilder<DependencyNode> graphBuilder = new GraphBuilder<>();
    this.graphStyleConfigurer
        .showGroupIds(true)
        .showArtifactIds(true)
        .showVersionsOnNodes(true)
        .showVersionsOnEdges(true)
        .configure(graphBuilder)
        .useNodeIdRenderer(VERSIONLESS_ID)
        .graphFormatter(this.formatter);

    // act
    graphBuilder.addEdge(this.from, this.toWithConflict);
    graphBuilder.toString();

    // assert
    assertThat(this.formatter.nodes, Matchers.<Node>containsInAnyOrder(
        new Node<>(this.fromId, getNodeNameForAllAttributes("group1", "artifact1", "version1"), this.from),
        new Node<>(this.toId, getNodeNameForAllAttributes("group2", "artifact2", "version2-alpha"), this.to)));

    assertThat(this.formatter.edges, Matchers.containsInAnyOrder(new Edge(this.fromId, this.toId, getEdgeNameForConflictingVersion("version2", true))));
  }

  @Test
  public void showNothing() {
    // arrange
    GraphBuilder<DependencyNode> graphBuilder = new GraphBuilder<>();
    this.graphStyleConfigurer.configure(graphBuilder)
        .useNodeIdRenderer(VERSIONLESS_ID)
        .graphFormatter(this.formatter);

    // act
    graphBuilder.addEdge(this.from, this.to);
    graphBuilder.toString();

    // assert
    assertThat(this.formatter.nodes, Matchers.<Node>containsInAnyOrder(
        new Node<>(this.fromId, getEmptyNodeName(), this.from),
        new Node<>(this.toId, getEmptyNodeName(), this.to)));

    assertThat(this.formatter.edges, Matchers.containsInAnyOrder(new Edge(this.fromId, this.toId, "")));
  }

  protected abstract GraphStyleConfigurer createGraphStyleConfigurer();

  protected abstract String getNodeNameForGroupIdOnly(String groupId);

  protected abstract String getNodeNameForArtifactIdOnly(String artifactId);

  protected abstract String getNodeNameForVersionOnly(String version);

  protected abstract String getEdgeNameForNonConflictingVersion(String version);

  protected abstract String getEdgeNameForConflictingVersion(String conflictingVersion, boolean showVersion);

  protected abstract String getNodeNameForAllAttributes(String groupId, String artifactId, String version);

  protected abstract String getEmptyNodeName();
}

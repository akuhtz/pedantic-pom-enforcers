package ch.sferstl.maven.pomenforcer;

import java.util.Collection;

import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import ch.sferstl.maven.pomenforcer.util.CommaSeparatorUtils;
import ch.sferstl.maven.pomenforcer.util.EnforcerRuleUtils;

public class CompoundPedanticEnforcer extends AbstractPedanticEnforcer {

  /** See {@link PedanticPomSectionOrderEnforcer#setSectionPriorities(String)}.*/
  private String pomSectionPriorities;

  /** See {@link PedanticModuleOrderEnforcer#setIgnoredModules(String)}. */
  private String moduleOrderIgnores;

  /** See {@link PedanticDependencyOrderEnforcer#setOrderBy(String)}.*/
  private String dependenciesOrderBy;

  /** See {@link PedanticDependencyOrderEnforcer#setGroupIdPriorities(String)}.*/
  private String dependenciesGroupIdPriorities;

  /** See {@link PedanticDependencyOrderEnforcer#setArtifactIdPriorities(String)}.*/
  private String dependenciesArtifactIdPriorities;

  /** See {@link PedanticDependencyOrderEnforcer#setScopePriorities(String)}.*/
  private String dependenciesScopePriorities;

  /** See {@link PedanticDependencyManagementOrderEnforcer#setOrderBy(String)}.*/
  private String dependencyManagementOrderBy;

  /** See {@link PedanticDependencyManagementOrderEnforcer#setGroupIdPriorities(String)}.*/
  private String dependencyManagementGroupIdPriorities;

  /** See {@link PedanticDependencyManagementOrderEnforcer#setArtifactIdPriorities(String)}.*/
  private String dependencyManagementArtifactIdPriorities;

  /** See {@link PedanticDependencyManagementOrderEnforcer#setScopePriorities(String)}.*/
  private String dependencyManagementScopePriorities;

  /** See {@link PedanticPluginManagementOrderEnforcer#setOrderBy(String)}.*/
  private String pluginManagementOrderBy;

  /** See {@link PedanticPluginManagementOrderEnforcer#setGroupIdPriorities(String)}.*/
  private String pluginManagementGroupIdPriorities;

  /** See {@link PedanticPluginManagementOrderEnforcer#setArtifactIdPriorities(String)}.*/
  private String pluginManagementArtifactIdPriorities;

  /** Collection of enforcers to execute. */
  private final Collection<PedanticEnforcerRule> enforcers;

  private final PropertyInitializationVisitor propertyInitializer;

  public CompoundPedanticEnforcer() {
    this.enforcers = Sets.newLinkedHashSet();
    this.propertyInitializer = new PropertyInitializationVisitor();
  }

  public void setEnforcers(String enforcers) {
    CommaSeparatorUtils.splitAndAddToCollection(enforcers, this.enforcers, new Function<String, PedanticEnforcerRule>() {
      @Override
      public PedanticEnforcerRule apply(String input) {
        return PedanticEnforcerRule.valueOf(input);
      }
    });
  }

  @Override
  public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {
    MavenProject project = EnforcerRuleUtils.getMavenProject(helper);
    Document pom = XmlParser.parseXml(project.getFile());
    doEnforce(helper, pom);
  }

  @Override
  protected void doEnforce(EnforcerRuleHelper helper, Document pom) throws EnforcerRuleException {
    for (PedanticEnforcerRule pedanticEnforcer : this.enforcers) {
      AbstractPedanticEnforcer rule = pedanticEnforcer.createEnforcerRule();
      rule.accept(this.propertyInitializer);
      rule.doEnforce(helper, pom);
    }
  }

  @Override
  protected void accept(PedanticEnforcerVisitor visitor) {
    visitor.visit(this);
  }

  private class PropertyInitializationVisitor implements PedanticEnforcerVisitor {

    @Override
    public void visit(PedanticPomSectionOrderEnforcer sectionOrderEnforcer) {
      if (!Strings.isNullOrEmpty(CompoundPedanticEnforcer.this.pomSectionPriorities)) {
        sectionOrderEnforcer.setSectionPriorities(CompoundPedanticEnforcer.this.pomSectionPriorities);
      }
    }

    @Override
    public void visit(PedanticModuleOrderEnforcer moduleOrderEnforcer) {
      if (!Strings.isNullOrEmpty(CompoundPedanticEnforcer.this.moduleOrderIgnores)) {
        moduleOrderEnforcer.setIgnoredModules(CompoundPedanticEnforcer.this.moduleOrderIgnores);
      }
    }

    @Override
    public void visit(PedanticDependencyManagementOrderEnforcer dependencyManagementOrderEnforcer) {
      if (!Strings.isNullOrEmpty(CompoundPedanticEnforcer.this.dependencyManagementOrderBy)) {
        dependencyManagementOrderEnforcer.setOrderBy(
            CompoundPedanticEnforcer.this.dependencyManagementOrderBy);
      }
      if (!Strings.isNullOrEmpty(CompoundPedanticEnforcer.this.dependencyManagementGroupIdPriorities)) {
        dependencyManagementOrderEnforcer.setGroupIdPriorities(
            CompoundPedanticEnforcer.this.dependencyManagementGroupIdPriorities);
      }
      if (!Strings.isNullOrEmpty(CompoundPedanticEnforcer.this.dependencyManagementArtifactIdPriorities)) {
        dependencyManagementOrderEnforcer.setArtifactIdPriorities(
            CompoundPedanticEnforcer.this.dependencyManagementArtifactIdPriorities);
      }
      if (!Strings.isNullOrEmpty(CompoundPedanticEnforcer.this.dependencyManagementScopePriorities)) {
        dependencyManagementOrderEnforcer.setScopePriorities(
            CompoundPedanticEnforcer.this.dependencyManagementScopePriorities);
      }
    }

    @Override
    public void visit(PedanticDependencyOrderEnforcer dependencyOrderEnforcer) {
      if (!Strings.isNullOrEmpty(CompoundPedanticEnforcer.this.dependenciesOrderBy)) {
        dependencyOrderEnforcer.setOrderBy(CompoundPedanticEnforcer.this.dependenciesOrderBy);
      }
      if (!Strings.isNullOrEmpty(CompoundPedanticEnforcer.this.dependenciesGroupIdPriorities)) {
        dependencyOrderEnforcer.setGroupIdPriorities(CompoundPedanticEnforcer.this.dependenciesGroupIdPriorities);
      }
      if (!Strings.isNullOrEmpty(CompoundPedanticEnforcer.this.dependenciesArtifactIdPriorities)) {
        dependencyOrderEnforcer.setArtifactIdPriorities(
            CompoundPedanticEnforcer.this.dependenciesArtifactIdPriorities);
      }
      if (!Strings.isNullOrEmpty(CompoundPedanticEnforcer.this.dependenciesScopePriorities)) {
        dependencyOrderEnforcer.setScopePriorities(CompoundPedanticEnforcer.this.dependenciesScopePriorities);
      }
    }

    @Override
    public void visit(PedanticPluginManagementOrderEnforcer pluginManagementOrderEnforcer) {
      if (!Strings.isNullOrEmpty(CompoundPedanticEnforcer.this.pluginManagementOrderBy)) {
        pluginManagementOrderEnforcer.setOrderBy(CompoundPedanticEnforcer.this.pluginManagementOrderBy);
      }
      if (!Strings.isNullOrEmpty(CompoundPedanticEnforcer.this.dependenciesGroupIdPriorities)) {
        pluginManagementOrderEnforcer.setGroupIdPriorities(
            CompoundPedanticEnforcer.this.pluginManagementGroupIdPriorities);
      }
      if (!Strings.isNullOrEmpty(CompoundPedanticEnforcer.this.pluginManagementArtifactIdPriorities)) {
        pluginManagementOrderEnforcer.setArtifactIdPriorities(
            CompoundPedanticEnforcer.this.pluginManagementArtifactIdPriorities);
      }
    }

    @Override
    public void visit(CompoundPedanticEnforcer compoundEnforcer) {
      // nothing to do.
    }

  }

}
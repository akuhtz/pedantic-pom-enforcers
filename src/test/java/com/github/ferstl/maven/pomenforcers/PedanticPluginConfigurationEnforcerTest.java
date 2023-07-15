/*
 * Copyright (c) 2012 - 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.ferstl.maven.pomenforcers;

import java.util.Collections;
import org.junit.Test;
import com.github.ferstl.maven.pomenforcers.model.DependencyModel;
import com.github.ferstl.maven.pomenforcers.model.PluginModel;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * JUnit tests for {@link PedanticPluginConfigurationEnforcer}.
 */
public class PedanticPluginConfigurationEnforcerTest extends AbstractPedanticEnforcerTest<PedanticPluginConfigurationEnforcer> {

  @Override
  PedanticPluginConfigurationEnforcer createRule() {
    return new PedanticPluginConfigurationEnforcer();
  }

  @Override
  @Test
  public void getDescription() {
    assertThat(this.testRule.getDescription(), equalTo(PedanticEnforcerRule.PLUGIN_CONFIGURATION));
  }

  @Override
  @Test
  public void accept() {
    PedanticEnforcerVisitor visitor = mock(PedanticEnforcerVisitor.class);
    this.testRule.accept(visitor);

    verify(visitor).visit(this.testRule);
  }

  @Test
  public void defaultSettingsCorrect() {
    addPlugin(false, false, false);

    executeRuleAndCheckReport(false);
  }

  @Test
  public void allowedUnmanagedConfiguration() {
    this.testRule.setManageConfigurations(false);
    addPlugin(false, true, false);

    executeRuleAndCheckReport(false);
  }

  @Test
  public void forbiddenUnmanagedConfiguration() {
    this.testRule.setManageConfigurations(true);
    addPlugin(false, true, false);

    executeRuleAndCheckReport(true);
  }

  @Test
  public void allowedUnmanagedDependencies() {
    this.testRule.setManageDependencies(false);
    addPlugin(false, false, true);

    executeRuleAndCheckReport(false);
  }

  @Test
  public void forbiddenUnmanagedDependencies() {
    this.testRule.setManageDependencies(true);
    addPlugin(false, false, true);

    executeRuleAndCheckReport(true);
  }

  @Test
  public void allowedManagedVersion() {
    this.testRule.setManageVersions(false);
    addPlugin(true, false, false);

    executeRuleAndCheckReport(false);
  }

  @Test
  public void forbiddenManagedVersion() {
    this.testRule.setManageVersions(true);
    addPlugin(true, false, false);

    executeRuleAndCheckReport(true);
  }

  @Test
  public void allowedProjectVersion1() {
    this.testRule.setAllowUnmanagedProjectVersions(true);
    PluginModel plugin = addPlugin(false, false, false);
    when(plugin.getVersion()).thenReturn("${project.version}");

    executeRuleAndCheckReport(false);
  }

  @Test
  public void allowedProjectVersion2() {
    PluginModel plugin = addPlugin(false, false, false);
    when(plugin.getVersion()).thenReturn("${version}");

    executeRuleAndCheckReport(false);
  }

  @Test
  public void forbiddenProjectVersion() {
    this.testRule.setAllowUnmanagedProjectVersions(false);
    PluginModel plugin = addPlugin(false, false, false);
    when(plugin.getVersion()).thenReturn("${project.version}");

    executeRuleAndCheckReport(true);
  }

  @Test
  public void allowedVersionWithProps() {
    this.testRule.setAllowedUnmanagedProjectVersionProps("some.version");
    addPlugin(false, false, false);

    executeRuleAndCheckReport(false);
  }

  @Test
  public void allowedVersionWithDisabledProps1() {
    this.testRule.setManageVersions(false);
    this.testRule.setAllowedUnmanagedProjectVersionProps("some.version");
    addPlugin(true, false, false);

    executeRuleAndCheckReport(false);
  }

  @Test
  public void allowedVersionWithDisabledProps2() {
    this.testRule.setAllowUnmanagedProjectVersions(false);
    this.testRule.setAllowedUnmanagedProjectVersionProps("some.version");
    addPlugin(false, false, false);

    executeRuleAndCheckReport(false);
  }

  @Test
  public void forbiddenVersionWithCustomProps1() {
    this.testRule.setAllowedUnmanagedProjectVersionProps("some.version");
    addPlugin(true, false, false);

    executeRuleAndCheckReport(true);
  }

  @Test
  public void forbiddenVersionWithCustomProps2() {
    this.testRule.setAllowedUnmanagedProjectVersionProps("some.version");
    PluginModel plugin = addPlugin(true, false, false);
    when(plugin.getVersion()).thenReturn("${project.version}");

    executeRuleAndCheckReport(true);
  }

  @Test
  public void allowedVersionWithActiveCustomProps1() {
    this.testRule.setAllowedUnmanagedProjectVersionProps("some.version");
    PluginModel plugin = addPlugin(true, false, false);
    when(plugin.getVersion()).thenReturn("${some.version}");

    executeRuleAndCheckReport(false);
  }
  @Test
  public void allowedVersionWithActiveCustomProps2() {
    this.testRule.setAllowedUnmanagedProjectVersionProps("some.version,some.other.version");
    PluginModel plugin = addPlugin(true, false, false);
    when(plugin.getVersion()).thenReturn("${some.other.version}");

    executeRuleAndCheckReport(false);
  }

  private PluginModel addPlugin(boolean withVersion, boolean withConfiguration, boolean withDependencies) {
    PluginModel plugin = mock(PluginModel.class);

    when(plugin.getGroupId()).thenReturn("a.b.c");
    when(plugin.getArtifactId()).thenReturn("a");

    if (withVersion) {
      when(plugin.getVersion()).thenReturn("1.0");
    }

    if (withConfiguration) {
      when(plugin.isConfigured()).thenReturn(true);
    }

    if (withDependencies) {
      when(plugin.getDependencies()).thenReturn(
          Collections.singletonList(new DependencyModel("x.y.z", "z", "1.0", null, null, null)));
    }

    this.testRule.getProjectModel().getPlugins().add(plugin);
    return plugin;
  }
}

/*
 * Copyright (c) 2012 - 2023 the original author or authors.
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

import java.util.Collection;
import java.util.Collections;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import com.github.ferstl.maven.pomenforcers.model.DependencyModel;


/**
 * This enforcer makes sure that all artifacts in your dependency management are
 * ordered. The ordering can be defined by any combination of <code>scope</code>,
 * <code>groupId</code> and <code>artifactId</code>. Each of these attributes
 * may be given a priority.
 * <pre>
 * ### Example
 *     &lt;rules&gt;
 *       &lt;dependencyManagementOrder implementation=&quot;com.github.ferstl.maven.pomenforcers.PedanticDependencyManagementOrderEnforcer&quot;&gt;
 *         &lt;!-- order by scope, groupId and artifactId (default) --&gt;
 *         &lt;orderBy&gt;scope,groupId,artifactId&lt;/orderBy&gt;
 *         &lt;!-- runtime scope should occur before provided scope --&gt;
 *         &lt;scopePriorities&gt;compile,runtime,provided&lt;/scopePriorities&gt;
 *         &lt;!-- all group IDs starting with com.myproject and com.mylibs should occur first --&gt;
 *         &lt;groupIdPriorities&gt;com.myproject,com.mylibs&lt;/groupIdPriorities&gt;
 *         &lt;!-- all artifact IDs starting with commons- and utils- should occur first --&gt;
 *         &lt;artifactIdPriorities&gt;commons-,utils-&lt;/artifactIdPriorities&gt;
 *       &lt;/dependencyManagementOrder&gt;
 *     &lt;/rules&gt;
 * </pre>
 *
 * @id {@link PedanticEnforcerRule#DEPENDENCY_MANAGEMENT_ORDER}
 * @since 1.0.0
 */
@Named("dependencyManagementOrder")
public class PedanticDependencyManagementOrderEnforcer extends AbstractPedanticDependencyOrderEnforcer {

  @Inject
  public PedanticDependencyManagementOrderEnforcer(final MavenProject project, final ExpressionEvaluator helper) {
    super(project, helper);
  }

  @Override
  protected PedanticEnforcerRule getDescription() {
    return PedanticEnforcerRule.DEPENDENCY_MANAGEMENT_ORDER;
  }

  @Override
  protected void accept(PedanticEnforcerVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  protected Collection<DependencyModel> getDeclaredDependencies() {
    return getProjectModel().getManagedDependencies();
  }

  @Override
  protected Collection<Dependency> getMavenDependencies(MavenProject project) {
    DependencyManagement dependencyManagement = project.getDependencyManagement();
    if (dependencyManagement != null) {
      return dependencyManagement.getDependencies();
    } else {
      return Collections.emptyList();
    }
  }

  @Override
  protected void reportError(ErrorReport report, Collection<DependencyModel> resolvedDependencies, Collection<DependencyModel> sortedDependencies) {

    report.addLine("Your dependency management has to be ordered this way:")
        .emptyLine()
        .addDiffUsingToString(resolvedDependencies, sortedDependencies, "Actual Order", "Required Order");
  }
}

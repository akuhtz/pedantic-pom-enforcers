/*
 * Copyright (c) 2012 by The Author(s)
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
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.model.Dependency;

import com.github.ferstl.maven.pomenforcers.artifact.ArtifactSorter;
import com.github.ferstl.maven.pomenforcers.artifact.DependencyElement;
import com.github.ferstl.maven.pomenforcers.artifact.DependencyMatcher;
import com.github.ferstl.maven.pomenforcers.util.CommaSeparatorUtils;
import com.google.common.base.Function;
import com.google.common.collect.Sets;


public abstract class AbstractPedanticDependencyOrderEnforcer extends AbstractPedanticEnforcer {

  private final ArtifactSorter<Dependency, DependencyElement> artifactSorter;

  public AbstractPedanticDependencyOrderEnforcer() {
    Set<DependencyElement> orderBy = Sets.newLinkedHashSet();
    orderBy.add(DependencyElement.SCOPE);
    orderBy.add(DependencyElement.GROUP_ID);
    orderBy.add(DependencyElement.ARTIFACT_ID);
    this.artifactSorter = new ArtifactSorter<>();
    this.artifactSorter.orderBy(orderBy);
  }

  /**
   * Comma-separated list of dependency elements that defines the ordering.
   * @param dependencyElements Comma-separated list of dependency elements that defines the ordering.
   * @configParam
   * @default scope,groupId,artifactId
   */
  public void setOrderBy(String dependencyElements) {
    Set<DependencyElement> orderBy = Sets.newLinkedHashSet();
    Function<String, DependencyElement> transformer = new Function<String, DependencyElement>() {
      @Override
      public DependencyElement apply(String input) {
        return DependencyElement.getByElementName(input);
      }
    };
    CommaSeparatorUtils.splitAndAddToCollection(dependencyElements, orderBy, transformer);
    this.artifactSorter.orderBy(orderBy);
  }

  /**
   * Comma-separated list of group IDs that should be listed first in the
   * dependencies declaration. All group IDs that <strong>start with</strong>
   * any of the priorized group IDs in the given list, are required to be
   * located first in the dependencies section.
   *
   * @param groupIds Comma separated list of group IDs.
   * @configParam
   * @default n/a
   */
  public void setGroupIdPriorities(String groupIds) {
    LinkedHashSet<String> groupIdPriorities = Sets.newLinkedHashSet();
    CommaSeparatorUtils.splitAndAddToCollection(groupIds, groupIdPriorities);
    this.artifactSorter.setPriorities(DependencyElement.GROUP_ID, groupIdPriorities);
  }

  /**
   * Comma-separated list of artifact IDs that should be listed first in the
   * dependencies declaration. All artifact IDs that <strong>start with</strong>
   * any of the priorized IDs in the given list, are required to be located
   * first in the dependencies section.
   *
   * @param artifactIds Comma separated list of artifact IDs.
   * @configParam
   * @default n/a
   */
  public void setArtifactIdPriorities(String artifactIds) {
    LinkedHashSet<String> artifactIdPriorities = Sets.newLinkedHashSet();
    CommaSeparatorUtils.splitAndAddToCollection(artifactIds, artifactIdPriorities);
    this.artifactSorter.setPriorities(DependencyElement.ARTIFACT_ID, artifactIdPriorities);
  }

  /**
   * Comma-separated list of scopes that should be listed first in the
   * dependencies declaration. All scopes that equal any of the scopes in the
   * given list, are required to be located first in the dependencies section.
   *
   * @param scopes Comma separated list of scopes.
   * @configParam
   * @default n/a
   */
  public void setScopePriorities(String scopes) {
    LinkedHashSet<String> scopePriorities = Sets.newLinkedHashSet();
    CommaSeparatorUtils.splitAndAddToCollection(scopes, scopePriorities);
    this.artifactSorter.setPriorities(DependencyElement.SCOPE, scopePriorities);
  }

  public ArtifactSorter<Dependency, DependencyElement> getArtifactSorter() {
    return this.artifactSorter;
  }

  protected Collection<Dependency> matchDependencies(
      final Collection<Dependency> subset,
      final Collection<Dependency> superset,
      final EnforcerRuleHelper helper) {
    return new DependencyMatcher(superset, helper).match(subset);
  }
}
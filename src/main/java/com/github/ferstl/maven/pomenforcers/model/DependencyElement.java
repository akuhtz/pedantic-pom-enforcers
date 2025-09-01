/*
 * Copyright (c) 2012 - 2025 the original author or authors.
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
package com.github.ferstl.maven.pomenforcers.model;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import com.github.ferstl.maven.pomenforcers.priority.PriorityOrdering;
import com.github.ferstl.maven.pomenforcers.priority.PriorityOrderingFactory;
import com.google.common.collect.Maps;
import static com.github.ferstl.maven.pomenforcers.model.functions.StringStartsWithEquivalence.stringStartsWith;
import static java.util.Objects.requireNonNull;


public enum DependencyElement implements PriorityOrderingFactory<String, DependencyModel>, Function<DependencyModel, String> {
  GROUP_ID("groupId") {
    @Override
    public PriorityOrdering<String, DependencyModel> createPriorityOrdering(Collection<String> priorityCollection) {
      return new PriorityOrdering<>(priorityCollection, this, stringStartsWith());
    }

    @Override
    public String apply(DependencyModel input) {
      return input.getGroupId();
    }
  },

  ARTIFACT_ID("artifactId") {
    @Override
    public PriorityOrdering<String, DependencyModel> createPriorityOrdering(Collection<String> priorityCollection) {
      return new PriorityOrdering<>(priorityCollection, this, stringStartsWith());
    }

    @Override
    public String apply(DependencyModel input) {
      return input.getArtifactId();
    }
  },

  SCOPE("scope") {
    @Override
    public PriorityOrdering<String, DependencyModel> createPriorityOrdering(Collection<String> priorityCollection) {
      return new PriorityOrdering<>(priorityCollection, this);
    }

    @Override
    public String apply(DependencyModel input) {
      return input.getScope().getScopeName();
    }
  };

  private static final Map<String, DependencyElement> elementMap;

  static {
    elementMap = Maps.newLinkedHashMap();
    for (DependencyElement element : values()) {
      elementMap.put(element.getElementName(), element);
    }
  }

  public static DependencyElement getByElementName(String elementName) {
    requireNonNull(elementName, "Element name is null");

    DependencyElement result = elementMap.get(elementName);
    if (result == null) {
      throw new IllegalArgumentException("No dependency element with name " + elementName);
    }

    return result;
  }

  private final String elementName;

  DependencyElement(String elementName) {
    this.elementName = elementName;
  }

  public String getElementName() {
    return this.elementName;
  }
}

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
package ch.sferstl.maven.pomenforcer.reader;

import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.model.Dependency;
import org.w3c.dom.Document;

import com.google.common.collect.Lists;
import com.thoughtworks.xstream.XStream;

public class DeclaredDependencyManagementReader extends AbstractPomSectionReader<List<Dependency>> {

  private static final String DEPENDENCY_MANAGEMENT_XPATH = "/project/dependencyManagement/dependencies";
  private static final String DEPENDENCIES_ALIAS = "dependencies";
  private static final String DEPENDENCY_ALIAS = "dependency";

  public DeclaredDependencyManagementReader(Document pom) {
    super(pom);
  }

  @Override
  protected XPathExpression createXPathExpression(XPath xpath) throws XPathExpressionException {
    return xpath.compile(DEPENDENCY_MANAGEMENT_XPATH);
  }

  @Override
  protected void configureXStream(XStream xstream) {
    xstream.alias(DEPENDENCIES_ALIAS, List.class);
    xstream.alias(DEPENDENCY_ALIAS, Dependency.class);
    xstream.omitField(DefaultArtifact.class, "exclusions");
  }

  @Override
  protected List<Dependency> getUndeclaredSection() {
    return Lists.newArrayList();
  }

}

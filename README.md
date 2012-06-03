# Pedantic POM Enforcers
*- One does not simply write a POM file*


Have you ever experienced symptoms like headaches, unfocused anger or a feeling of total resignation when looking at a Maven project where everybody adds and changes stuff just as they need? Do people call you a "POM-Nazi" if you show them how to setup proper and well organized projects?

If so, the *Pedantic POM Enforcers* are absolutely the thing you need!


### What are the Pedantic POM Enforcers?
The *Pedantic POM Enforcers* consist of serveral Maven enforcer rules that help you keep your project setup consistent and organized. At the current state, the available enforcer rules ensure that your POM files are organized in a well-defined manner and that your `<modules>`/`<dependencyManagement>`/`<dependencies>`/`<pluginManagement>` sections are sorted in a reasonable way.

Rules that take care of where to put version numbers, plugin configurations and so on will follow in the next releases.

### How to use the Pedantic POM Enforcers
To activate the *Pedantic POM enforcers*, just declare them in the configuration of the `maven-enforcer-plugin`. The simplest way of doing this is by using the `CompoundPedanticEnforcer`, which is able to aggregate all choosen enforcer rules. The compound enforcer is also more efficient than using the single enforcer rules separately.

    <build>
      <plugins>
        <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-enforcer-plugin</artifactId>
          <version>1.0.1</version>
          <configuration>
            <rules>
              <compound implementation="ch.sferstl.maven.pomenforcer.CompoundPedanticEnforcer">
                <!-- Enforcers -->
                <enforcers>POM_SECTION_ORDER,MODULE_ORDER,DEPENDENCY_MANAGEMENT_ORDER,DEPENDENCY_ORDER,PLUGIN_MANAGEMENT_ORDER</enforcers>
              
                <!-- Further configuration of the declared enforcer rules -->
                ...
              </compound>
            </rules>
            <fail>true</fail>
          </configuration>
          <dependencies>
            <dependency>
              <groupId>ch.sferstl.maven.enforcer</groupId>
              <artifactId>pedantic-pom-enforcers</artifactId>
              <version>${pedantic-pom-enforcers.version}</version>
            </dependency>
          </dependencies>
        </plugin>
        ...
      </plugins>
    </build>

### Available Enforcer Rules
These are the currently available *Pedantic POM Enforcers*. The ID is required by the `CompoundPedanticEnforcer`'s configuration.

| Enforcer Rule | ID | Description |
| ------------- | --- | ----------- |
| `PedanticPomSectionOrderEnforcer` | `POM_SECTION_ORDER` | Enforces that your POM sections are in order, e.g. 1: `<modelVersion>`, 2: `<groupId>` 3: `<artifactId>`, ... |
| `PedanticModuleOrderEnforcer` | `MODULE_ORDER` | Enforces that the declared `<modules>` are ordered alphabetically. |
| `PedanticDependencyManagementOrderEnforcer` | `DEPENDENCY_MANAGEMENT_ORDER` | Enforces that the dependencies in your `<dependencyManagement>` are ordered. |
| `PedanticDependencyOrderEnforcer` | `DEPENDENCY_ORDER` | Enforces that your `<dependencies>` are ordered.|
| `PedanticPluginManagementOrderEnforcer` | `PLUGIN_MANAGEMENT_ORDER` | Enforces that the plugins in your `<pluginManagement>` are ordered. |
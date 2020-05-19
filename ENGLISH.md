# mybatis-dynamic-plugin
[中文](./README.md) | [English](./ENGLISH.md)
<br>
<br>
Introduction
* Provide fast generation of domain (entity), repository (dao), service, controller and CRUD operations
* Note: My Batis 3 Dynamic Sql is adopted by default, and mapper.xml is banned, use reference[官方](https://mybatis.org/mybatis-dynamic-sql/)
* Note: But you can use Lombok Plugin and Domain Plugin separately, replace the target Runtime in generator Config.xml with My Batis 3 or My Batis 3 Simple
### needToAddDependencies
* spring-boot-starter
* mybatis-spring-boot-starter
* mybatis-dynamic-sql (Optional, only use Lombok Plugin and Domain Plugin without adding)
* mybatis-generator-maven-plugin (addInPlugins)
### use
* noteTheDefaultUseMyBatis3DynamicSql
1. createADatabaseUsingMySQLAsAnExample
2. createTable [sys_user.sql](docs/sys_user.sql) 
3. Create generator Config.xml in the resources directory, refer to: [generatorConfig.xml](docs/generatorConfig.xml)
   * LombokPlugin (lombok configuration)
      *  data : theDefaultValueIsFalse,contain getter,setter,toString,equalsAndHashCode,requiredArgsConstructor
      *  getter : theDefaultValueIsFalse
      *  setter : theDefaultValueIsFalse
      *  toString : theDefaultValueIsFalse
      *  equalsAndHashCode : theDefaultValueIsFalse
      *  builder : theDefaultValueIsFalse
      *  noArgsConstructor : theDefaultValueIsFalse
      *  allArgsConstructor : theDefaultValueIsFalse
      *  requiredArgsConstructor : theDefaultValueIsFalse
   * DomainPlugin (entityClassConfiguration)
      *  serializable (whetherToInheritSerializable) : theDefaultValueIsFalse
      *  json(useJsonFramework) :theDefaultValueIsNull,Optional fastjon and jackson, jackson supports bean registration configuration, if the way of bean is null
      *  dateSerialize (dateSerialization) : 默认值false 不生成
   * RepositoryPlugin (dataAccessLayerDAO)
      *  database (Specify database: ignore case, support most databases) : defaultValueMySQL 
      *  repository (Basic warehousing, Mybatis Repository is expandable by default) : theDefaultValueIsNull
   * ServicePlugin (serviceLayer)
      *  disable (whetherToDisable) : The default value is false to disable all functions of ServicePlugin
      *  targetProject (generatePathExample:src/main/java) : noDefaultValueCannotBeNull
      *  targetPackage (generateThePackageExample:org.example.service) : noDefaultValueCannotBeNull
      *  basicService (Basic Service can be implemented by yourself, provided by default BaseService) : noDefault  
      *  basicServiceImpl (The basic Service Impl can be implemented by yourself, and the Base Service Impl is provided by default) : noDefault
   * ControllerPlugin (controller)
      *  disable (whetherToDisable) : false or delete this property directly to disable all functions of Controller Plugin
      *  targetProject (generatePathExample:src/main/java) : cannotBeNull
      *  targetPackage (generateThePackageExample:org.example.controller) : cannotBeNull
      *  rest (isItRestController) : true is @RestController false directly delete this property as @Controller
      *  basicController (The basic controller can be implemented by itself, and the BaseController is provided by default (The Chinese characters are used by default, if you need to provide language inheritance rewriting)) : noDefault
4. addDependency
     * If you only use Domain Plugin and Lombok Plugin, no need to add, see the next step 
     ```xml
        <dependency>
            <groupId>com.github.uinios</groupId>
            <artifactId>mybatis-dynamic-spring-boot-starter</artifactId>
            <version>1.0.2</version>
        </dependency>
      ```
5. configurePlugin
      ```xml
      <build>
              <plugins>
                  <plugin>
                      <groupId>org.springframework.boot</groupId>
                      <artifactId>spring-boot-maven-plugin</artifactId>
                  </plugin>
                  <plugin>
                      <groupId>org.mybatis.generator</groupId>
                      <artifactId>mybatis-generator-maven-plugin</artifactId>
                      <version>1.4.0</version>
                      <executions>
                          <execution>
                              <id>Generate MyBatis Artifacts</id>
                              <phase>deploy</phase>
                              <goals>
                                  <goal>generate</goal>
                              </goals>
                          </execution>
                      </executions>
                      <configuration>
                          <verbose>true</verbose>
                          <overwrite>true</overwrite>
                      </configuration>
                      <dependencies>
                          <dependency>
                              <groupId>mysql</groupId>
                              <artifactId>mysql-connector-java</artifactId>
                              <version>8.0.18</version>
                          </dependency>
                           <dependency>
                              <groupId>com.github.uinios</groupId>
                              <artifactId>mybatis-dynamic-plugin</artifactId>
                              <version>1.0.2</version>
                            </dependency>
                      </dependencies>
                  </plugin>
              </plugins>
          </build>
      ```
6. generate
 * idea turnRight maven->plugins->mybatis-generator->mybatis-generator:generator clickToExecute  
 * or mvn mybatis-generator:generator  
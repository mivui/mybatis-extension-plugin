# mybatis-dynamic-plugin
[中文](./ZH_CN.md) | [English](./README.md)
* Introduction
  * Provide Entity, Dao extension。
  * Provide Service, Controller generation。
  * It is recommended to use mybatis-dynamic-sql to provide better functional support,mybatis-dynamic-sqlUse reference[Official](https://mybatis.org/mybatis-dynamic-sql/)
------
##### Add dependency (example)
```xml
     <!-- The first time you need to download -->
    <!-- <dependencies>
        <dependency>
            <groupId>com.github.uinios</groupId>
            <artifactId>mybatis-dynamic-plugin</artifactId>
            <version>1.5.1</version>
        </dependency>
    </dependencies> -->

    <build>
        <plugins>
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
                    <!--mysql As an example-->
                    <dependency>
                        <groupId>mysql</groupId>
                        <artifactId>mysql-connector-java</artifactId>
                        <version>8.0.18</version>
                    </dependency>
                    <!--mybatis-dynamic-plugin-->
                    <dependency>
                        <groupId>com.github.uinios</groupId>
                        <artifactId>mybatis-dynamic-plugin</artifactId>
                        <version>1.5.1</version>
                    </dependency>
                </dependencies>
            </plugin>
        </plugins>
    </build>
```
------
#### Plugin usage
> Possible dependencies
```xml
 <dependency>
    <groupId>com.github.uinios</groupId>
    <artifactId>mybatis-dynamic-spring-boot-starter</artifactId>
    <version>1.5.1</version>
 </dependency>
```

> LombokPlugin (lombok generation)
  
| property | defaults | introduction |
|---------|--------|---------|
| data | false | Contains getter,setter,toString,equalsAndHashCode,requiredArgsConstructor |
| getter | false | getter |
| setter | false | getter |
| toString | false | toString |
| equalsAndHashCode | false | equalsAndHashCode |
| builder | false | builder |
| noArgsConstructor | false | noArgsConstructor |
| allArgsConstructor | false | allArgsConstructor |
| requiredArgsConstructor | false | requiredArgsConstructor |

##### examples
```xml
 <plugin type="com.github.uinios.mybatis.plugin.LombokPlugin">
    <property name="data" value="true"/>
    <property name="builder" value="true"/>
    <property name="noArgsConstructor" value="true"/>
    <property name="allArgsConstructor" value="true"/>
 </plugin>
```
------

> DomainPlugin (Entity class extension)

| property | defaults | introduction |
|---------|--------|---------|
| serializable | false | Whether to implement Serializable |
| dateSerialize | false | Date serialization only supports JDK 8 date types |
| json | null | Date deserialization only supports JDK 8 date types(options: fastjon , jackson ) |

##### Examples
```xml
 <plugin type="com.github.uinios.mybatis.plugin.DomainPlugin">
    <property name="serializable" value="true"/>
    <property name="dateSerialize" value="true"/>
    <property name="json" value="jackson"/>
 </plugin>
```
------
> RepositoryPlugin (Data Access Object DAO Extension,Only support[mybatis-dynamic-sql](https://mybatis.org/mybatis-dynamic-sql/))

| property | defaults | introduction |
|---------|--------|---------|
| repository | null | Dao parent, By default, Mybatis Repository needs to add dependencies:mybatis-dynamic-spring-boot-starter |
| uuid | null | New primary key uuid configuration(Options: true , false , uuid) true is 36 bits uuid , false is null, uuid is a 32-bit uuid  |
| mysql | false |  mysql paging requires special handling  |
| separationPackage | false |  Whether to split Dynamic Sql into separate packages  |
##### examples
```xml
 <!-- Only supports mybatis-dynamic-sql -->
 <plugin type="com.github.uinios.mybatis.plugin.RepositoryPlugin">
   <property name="uuid" value="true"/>
   <property name="mysql" value="true"/>
   <property name="separationPackage" value="true"/>
   <property name="repository" value="com.github.uinios.mybatis.basic.repository.MybatisRepository"/>
 </plugin>
```
------
> ServicePlugin (Service generation)

| property | defaults | introduction |
|---------|--------|---------|
| targetProject | null | Generate path example:src/main/java |
| targetPackage | null | Generate package path example:org.example.service |
| basicService | null | Service interface parent class (note:If using[mybatis-dynamic-sql](https://mybatis.org/mybatis-dynamic-sql/) By default, Base Service needs to add dependencies mybatis-dynamic-spring-boot-starter) |
| basicServiceImpl | null | ServiceImplParent (note:If using[mybatis-dynamic-sql](https://mybatis.org/mybatis-dynamic-sql/) By default, Base Service Impl needs to add dependency mybatis-dynamic-spring-boot-starter) |
 ##### examples
 ```xml
  <plugin type="com.github.uinios.mybatis.plugin.ServicePlugin">
     <property name="targetProject" value="src/main/java"/>
     <property name="targetPackage" value="org.example.service"/>
     <property name="basicService" value="com.github.uinios.mybatis.basic.service.BaseService"/>
     <property name="basicServiceImpl" value="com.github.uinios.mybatis.basic.service.BaseServiceImpl"/>
  </plugin>
 ```
------
> ControllerPlugin (Controller generate)

| property | defaults | introduction |
|---------|--------|---------|
| targetProject | null | Generate path example:src/main/java |
| targetPackage | null | Generate package path example:org.example.controller |
| rest | false | true is @RestController,false is @Controller |
| respond | null | Return to result set,copy [Respond](./docs/Respond.java)Into this project (Note: If you use[mybatis-dynamic-sql](https://mybatis.org/mybatis-dynamic-sql/) By default Respond needs to add dependenciesmybatis-dynamic-spring-boot-starter)  |
 ##### Examples
 ```xml
  <plugin type="com.github.uinios.mybatis.plugin.ControllerPlugin">
      <property name="rest" value="true"/>
      <property name="targetProject" value="src/main/java"/>
      <property name="targetPackage" value="org.example.controller"/>
      <property name="respond" value="com.github.uinios.mybatis.basic.io.Respond"/>
  </plugin>
 ```
------      
> generate
 * Idea opens the right side maven->plugins->mybatis-generator->mybatis-generator:generator Click to execute
 * Or mvn mybatis-generator:generator  
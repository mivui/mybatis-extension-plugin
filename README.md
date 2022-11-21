# mybatis-extension-plugin
English |  [中文](./ZH_CN.md) 
* For ease of use, align with the main version number of mybatis-generator
* Introduction
  * Support Mybatis3 and mybatis-dynamic-sql
  * Note: You need to learn the use of mybatis-generator-maven-plugin first http://mybatis.org/generator/running/runningWithMaven.html
  * Note: You need to learn the configuration of the generated file first http://mybatis.org/generator/configreference/xmlconfig.html
  * Provide Entity, Dao extension
  * Provide Service, Controller generation
------
##### Add dependency (example)
```xml
    <dependencies>
        <dependency>
            <groupId>io.github.mioxs</groupId>
            <artifactId>mybatis-extension-plugin</artifactId>
            <version>1.4.2</version>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
    </dependencies>

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
                        <version>8.0.21</version>
                    </dependency>
                    <!--mybatis-extension-plugin-->
                    <dependency>
                        <groupId>io.github.mioxs</groupId>
                        <artifactId>mybatis-extension-plugin</artifactId>
                        <version>1.4.2</version>
                    </dependency>
                </dependencies>
            </plugin>
        </plugins>
    </build>
```
------
#### Plugin usage

> LombokPlugin (lombok generation)
  
| property | defaults | introduction |
|---------|--------|---------|
| data | false | include getter,setter,toString,equalsAndHashCode,requiredArgsConstructor |
| getter | false | getter |
| setter | false | getter |
| toString | false | toString |
| equalsAndHashCode | false | equalsAndHashCode |
| builder | false | builder |
| noArgsConstructor | false | noArgsConstructor |
| allArgsConstructor | false | allArgsConstructor |
| requiredArgsConstructor | false | requiredArgsConstructor |

##### Examples
* The \<plugin> element is a child element of the \<context> element. Any number of plugins can be specified in the context.

```xml

<plugin type="io.github.mioxs.mybatis.LombokPlugin">
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
| json | null | Date deserialization only supports JDK8 date types (options: fastJson, jackson) |

##### Examples
* The \<plugin> element is a child element of the \<context> element. Any number of plugins can be specified in the context.

```xml

<plugin type="io.github.mioxs.mybatis.DomainPlugin">
    <property name="serializable" value="true"/>
    <property name="dateSerialize" value="true"/>
    <property name="json" value="jackson"/>
</plugin>
```
------
> RepositoryPlugin (Data Access Object DAO Extension)

| property | defaults | introduction |
|---------|--------|---------|
| suppressAllComments | false | Whether to remove the generated comments |
| repository | null | Dao parent class, default is null |
##### Examples
* The \<plugin> element is a child element of the \<context> element. Any number of plugins can be specified in the context.

```xml

<plugin type="io.github.mioxs.mybatis.RepositoryPlugin">
    <property name="suppressAllComments" value="true"/>
    <property name="repository" value="org.example.MybatisRepository"/>
</plugin>
```
------
> ServicePlugin (Service generation)

| property | defaults | introduction |
|---------|--------|---------|
| targetProject |  null  | Generate path example: src/main/java |
| targetPackage |  null  | generate package path example: org.example.service |
| basicService |  null  | service interface parent class default is null |
| basicServiceImpl |  null  | serviceImpl parent class, default is null |
 ##### Examples
 *The \<plugin> element is a child element of the \<context> element. Any number of plugins can be specified in the context.

 ```xml

<plugin type="io.github.mioxs.mybatis.ServicePlugin">
    <property name="targetProject" value="src/main/java"/>
    <property name="targetPackage" value="org.example.service"/>
    <property name="basicService" value="org.example.BaseService"/>
    <property name="basicServiceImpl" value="org.example.BaseServiceImpl"/>
</plugin>
 ```
------
> ControllerPlugin (Controller generation)

| property | defaults | introduction |
|---------|--------|---------|
| targetProject |  null   | Generate path example: src/main/java |
| targetPackage |  null  | Generate package path example: org.example.controller |
| rest |  false  | true for @RestController, false for @Controller |
| respond |  null  | The controller returns the result set. The default is null |
##### Examples
* The \<plugin> element is a child element of the \<context> element. Any number of plugins can be specified in the context.

 ```xml

<plugin type="io.github.mioxs.mybatis.ControllerPlugin">
    <property name="targetProject" value="src/main/java"/>
    <property name="targetPackage" value="org.example.controller"/>
    <property name="rest" value="true"/>
    <property name="respond" value="org.example.Respond"/>
</plugin>
 ```
##### Generate impressions (partial code)
```java
@Slf4j
@RestController
@RequestMapping("example")
public class ExampleController {

    @Autowired
    private ExampleService exampleService;
    
    @GetMapping("findAll")
    public Respond findAll() {
       return null;
    }
    //...
}
```
------      
> Generate
 * Idea opens the right side  maven->plugins->mybatis-generator->  mybatis-generator:generator Click to execute 
 * Or  mvn mybatis-generator:generator 

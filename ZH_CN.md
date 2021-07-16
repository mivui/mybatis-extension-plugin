# mybatis-extension-plugin
中文 | [English](./README.md)
* 为了方便使用,将和mybatis-generator主版本号对齐
* 简介
  * 支持 Mybatis3 和 mybatis-dynamic-sql
  * 注意:需要先学习mybatis-generator-maven-plugin的使用 http://mybatis.org/generator/running/runningWithMaven.html
  * 注意:需要先学习生成文件的配置 http://mybatis.org/generator/configreference/xmlconfig.html
  * 提供 Entity,Dao 扩展。
  * 提供 Service,Controller 生成。
------
##### 添加依赖(示例)
```xml
    <dependencies>
        <dependency>
            <groupId>com.github.uinio</groupId>
            <artifactId>mybatis-extension-plugin</artifactId>
            <version>1.4.0</version>
            <scope>runtime</scope>
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
                        <groupId>com.github.uinio</groupId>
                        <artifactId>mybatis-extension-plugin</artifactId>
                        <version>1.4.0</version>
                    </dependency>
                </dependencies>
            </plugin>
        </plugins>
    </build>
```
------
#### 插件使用

> LombokPlugin (lombok生成)
  
| 属性 | 默认值 | 简介 |
|---------|--------|---------|
| data | false | 包含getter,setter,toString,equalsAndHashCode,requiredArgsConstructor |
| getter | false | getter |
| setter | false | getter |
| toString | false | toString |
| equalsAndHashCode | false | equalsAndHashCode |
| builder | false | builder |
| noArgsConstructor | false | noArgsConstructor |
| allArgsConstructor | false | allArgsConstructor |
| requiredArgsConstructor | false | requiredArgsConstructor |

##### 示例
* \<plugin>元素是\<context>元素的子元素。可以在上下文中指定任意数量的插件。
```xml
 <plugin type="com.github.uinio.mybatis.LombokPlugin">
    <property name="data" value="true"/>
    <property name="builder" value="true"/>
    <property name="noArgsConstructor" value="true"/>
    <property name="allArgsConstructor" value="true"/>
 </plugin>
```
------

> DomainPlugin (实体类扩展)

| 属性 | 默认值 | 简介 |
|---------|--------|---------|
| serializable | false | 是否实现Serializable |
| dateSerialize | false | 日期序列化只支持JDK8日期类型 |
| json | null | 日期反序列化只支持JDK8日期类型(选项: fastJson , jackson ) |

##### 示例
* \<plugin>元素是\<context>元素的子元素。可以在上下文中指定任意数量的插件。
```xml
 <plugin type="com.github.uinio.mybatis.DomainPlugin">
    <property name="serializable" value="true"/>
    <property name="dateSerialize" value="true"/>
    <property name="json" value="jackson"/>
 </plugin>
```
------
> RepositoryPlugin (数据访问对象DAO扩展)

| 属性 | 默认值 | 简介 |
|---------|--------|---------|
| suppressAllComments | false | 是否去掉生成的注释 |
| repository | null | Dao父类,默认为null |
##### 示例
* \<plugin>元素是\<context>元素的子元素。可以在上下文中指定任意数量的插件。
```xml
 <plugin type="com.github.uinio.mybatis.RepositoryPlugin">
   <property name="suppressAllComments" value="true"/>
   <property name="repository" value="org.example.MybatisRepository"/>
 </plugin>
```
------
> ServicePlugin (Service生成)

| 属性 | 默认值 | 简介 |
|---------|--------|---------|
| targetProject |  null  | 生成路径例如:src/main/java |
| targetPackage |  null  | 生成所在包路径例如:org.example.service |
| basicService |  null  | service接口父类, 默认为null |
| basicServiceImpl |  null  | serviceImpl父类, 默认为null |
 ##### 示例
 * \<plugin>元素是\<context>元素的子元素。可以在上下文中指定任意数量的插件。
 ```xml
  <plugin type="com.github.uinio.mybatis.ServicePlugin">
     <property name="targetProject" value="src/main/java"/>
     <property name="targetPackage" value="org.example.service"/>
     <property name="basicService" value="org.example.BaseService"/>
     <property name="basicServiceImpl" value="org.example.BaseServiceImpl"/>
  </plugin>
 ```
------
> ControllerPlugin (Controller生成)

| 属性 | 默认值 | 简介 |
|---------|--------|---------|
| targetProject |  null   | 生成路径例如:src/main/java |
| targetPackage |  null  | 生成所在包路径例如:org.example.controller |
| rest |  false  | true为@RestController,false为@Controller |
| respond |  null  | controller返回结果集 默认为null |
##### 示例
* \<plugin>元素是\<context>元素的子元素。可以在上下文中指定任意数量的插件。
 ```xml
  <plugin type="com.github.uinio.mybatis.ControllerPlugin">
      <property name="targetProject" value="src/main/java"/>
      <property name="targetPackage" value="org.example.controller"/>
      <property name="rest" value="true"/>
      <property name="respond" value="org.example.Respond"/>
  </plugin>
 ```
##### 生成展示(部份代码)
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
> 生成
 * idea打开右侧 maven->plugins->mybatis-generator->  mybatis-generator:generator 点击执行  
 * 或  mvn mybatis-generator:generator  
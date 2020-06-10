# mybatis-dynamic-plugin
[中文](./ZH_CN.md) | [English](./README.md)
* 简介
  * 提供 Entity,Dao 扩展。
  * 提供 Service,Controller 生成。
  * 建议使用mybatis-dynamic-sql提供更好功能支持,mybatis-dynamic-sql使用参考[官方](https://mybatis.org/mybatis-dynamic-sql/)
------
##### 添加依赖(示例)
```xml
     <!-- 第一次使用需下载 -->
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
#### 插件使用
> 可能使用到的依赖
```xml
 <dependency>
    <groupId>com.github.uinios</groupId>
    <artifactId>mybatis-dynamic-spring-boot-starter</artifactId>
    <version>1.5.1</version>
 </dependency>
```

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
```xml
 <plugin type="com.github.uinios.mybatis.plugin.LombokPlugin">
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
| json | null | 日期反序列化只支持JDK8日期类型(选项: fastjon , jackson ) |

##### 示例
```xml
 <plugin type="com.github.uinios.mybatis.plugin.DomainPlugin">
    <property name="serializable" value="true"/>
    <property name="dateSerialize" value="true"/>
    <property name="json" value="jackson"/>
 </plugin>
```
------
> RepositoryPlugin (数据访问对象DAO扩展,只支持[mybatis-dynamic-sql](https://mybatis.org/mybatis-dynamic-sql/))

| 属性 | 默认值 | 简介 |
|---------|--------|---------|
| repository | null | Dao父类, 默认提供MybatisRepository需添加依赖:mybatis-dynamic-spring-boot-starter |
| uuid | null | 新增主键uuid配置(选项: true , false , uuid) true为36位uuid , false为null , uuid为32位uuid  |
| mysql | false |  mysql分页需要特殊处理  |
| separationPackage | false |  是否拆分将DynamicSql拆分为单独的包  |
##### 示例
```xml
 <!-- 只支持mybatis-dynamic-sql -->
 <plugin type="com.github.uinios.mybatis.plugin.RepositoryPlugin">
   <property name="uuid" value="true"/>
   <property name="mysql" value="true"/>
   <property name="separationPackage" value="true"/>
   <property name="repository" value="com.github.uinios.mybatis.basic.repository.MybatisRepository"/>
 </plugin>
```
------
> ServicePlugin (Service生成)

| 属性 | 默认值 | 简介 |
|---------|--------|---------|
| targetProject | null | 生成路径例如:src/main/java |
| targetPackage | null | 生成所在包路径例如:org.example.service |
| basicService | null | service接口父类 (注意:如果使用[mybatis-dynamic-sql](https://mybatis.org/mybatis-dynamic-sql/) 默认提供BaseService需添加依赖mybatis-dynamic-spring-boot-starter) |
| basicServiceImpl | null | serviceImpl父类 (注意:如果使用[mybatis-dynamic-sql](https://mybatis.org/mybatis-dynamic-sql/) 默认提供BaseServiceImpl需添加依赖mybatis-dynamic-spring-boot-starter) |
 ##### 示例
 ```xml
  <plugin type="com.github.uinios.mybatis.plugin.ServicePlugin">
     <property name="targetProject" value="src/main/java"/>
     <property name="targetPackage" value="org.example.service"/>
     <property name="basicService" value="com.github.uinios.mybatis.basic.service.BaseService"/>
     <property name="basicServiceImpl" value="com.github.uinios.mybatis.basic.service.BaseServiceImpl"/>
  </plugin>
 ```
------
> ControllerPlugin (Controller生成)

| 属性 | 默认值 | 简介 |
|---------|--------|---------|
| targetProject | null | 生成路径例如:src/main/java |
| targetPackage | null | 生成所在包路径例如:org.example.controller |
| rest | false | true为@RestController,false为@Controller |
| respond | null | 返回结果集,复制[Respond](./docs/Respond.java)此类到项目中 (注意:如果使用[mybatis-dynamic-sql](https://mybatis.org/mybatis-dynamic-sql/) 默认提供Respond需添加依赖mybatis-dynamic-spring-boot-starter)  |
| zh_cn | false | 对中文更好的扩展请设置为true  |
##### 示例
 ```xml
  <plugin type="com.github.uinios.mybatis.plugin.ControllerPlugin">
      <property name="rest" value="true"/>
      <property name="zh_cn" value="true"/>
      <property name="targetProject" value="src/main/java"/>
      <property name="targetPackage" value="org.example.controller"/>
      <property name="respond" value="com.github.uinios.mybatis.basic.io.Respond"/>
  </plugin>
 ```
------      
> 生成
 * idea打开右侧maven->plugins->mybatis-generator->mybatis-generator:generator 点击执行  
 * 或mvn mybatis-generator:generator  
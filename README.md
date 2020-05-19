# mybatis-dynamic-plugin
[中文](./README.md) | [English](./ENGLISH.md)
<br>
<br>
简介
* 提供快速生成domain(entity),repository(dao),service,controller和CRUD操作
* 注意:默认采用MyBatis3DynamicSql,mapper.xml被取缔，使用参考[官方](https://mybatis.org/mybatis-dynamic-sql/)
* 注意:但可以单独使用LombokPlugin和DomainPlugin,将generatorConfig.xml中的targetRuntime替换为MyBatis3或MyBatis3Simple
### 需添加依赖
* spring-boot-starter
* mybatis-spring-boot-starter
* mybatis-dynamic-sql (可选,只使用LombokPlugin和DomainPlugin无需添加)
* mybatis-generator-maven-plugin (在plugins中添加)
### 使用
* 注意：生成默认使用MyBatis3DynamicSql
1. 创建数据库以MySQL为例
2. 创建表 [sys_user.sql](docs/sys_user.sql) 
3. 在resources目录下创建generatorConfig.xml,参考: [generatorConfig.xml](docs/generatorConfig.xml)
   * LombokPlugin (lombok配置)
      *  data : 默认值false 不生成,包含getter,setter,toString,equalsAndHashCode,requiredArgsConstructor
      *  getter : 默认值false 不生成
      *  setter : 默认值false 不生成
      *  toString : 默认值false 不生成
      *  equalsAndHashCode : 默认值false 不生成
      *  builder : 默认值false 不生成
      *  noArgsConstructor : 默认值false 不生成
      *  allArgsConstructor : 默认值false 不生成
      *  requiredArgsConstructor : 默认值false 不生成
   * DomainPlugin (实体类配置)
      *  serializable (是否继承Serializable) : 默认值false 不生成
      *  json(使用json框架) :默认值为null,可选fastjon和jackson,jackson支持Bean注册配置,如果以bean的方式为null即可
      *  dateSerialize (日期序列化) : 默认值false 不生成
   * RepositoryPlugin (数据访问层DAO)
      *  database (指定数据库:忽略大小写,支持大部分数据库) : 默认值MySQL 
      *  repository (基础仓储,默认提供MybatisRepository)可扩展) : 无默认值 
   * ServicePlugin (服务层)
      *  disable (是否禁用) : 默认值false 禁用ServicePlugin所有功能
      *  targetProject (生成路径例如:src/main/java) : 无默认值 不能为null
      *  targetPackage (生成所在包例如:org.example.service) : 无默认值 不能为null
      *  basicService (基础Service可自己实现,默认提供BaseService) : 无默认值  
      *  basicServiceImpl (基础ServiceImpl可自己实现,默认提供BaseServiceImpl) : 无默认值
   * ControllerPlugin (控制器)
      *  disable (是否禁用) : false或者直接删除此属性禁用ControllerPlugin所有功能
      *  targetProject (生成路径例如:src/main/java) : 不能为null
      *  targetPackage (生成所在包例如:org.example.controller) : 不能为null
      *  rest (是否是@RestController) : true为@RestController false直接删除此属性为@Controller
      *  basicController (基础basicController可自己实现,默认提供BaseController) : 无默认值
4. 添加依赖
     * 如果只使用DomainPlugin和LombokPlugin无需添加，查看下一步内容  
     ```xml
        <dependency>
            <groupId>com.github.uinios</groupId>
            <artifactId>mybatis-dynamic-design</artifactId>
            <version>1.0.2</version>
        </dependency>
      ```
5. 配置插件
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
                              <artifactId>mybatis-dynamic-design</artifactId>
                              <version>1.0.2</version>
                            </dependency>
                      </dependencies>
                  </plugin>
              </plugins>
          </build>
      ```
6. 生成
 * idea打开右侧maven->plugins->mybatis-generator->mybatis-generator:generator 点击执行  
 * 或mvn mybatis-generator:generator  
# idatrix-db

#`1`.目的
    ①.数据服务的代理中间层
    ②.提供dubbo接口给各个子应用调用

#`2`.项目框架
    ①.Springboot 2.1.2..RELEASE 
    ②.Es 5.6.10
    ③.Mybatis + Mysql
    ④.Dubbo 2.6
    ⑤.Hadoop 2.7.3
    ⑥.Phoenix 4.10.0-HBase-1.2
    ⑦.HBase-client 1.3.0
    ⑧.Hive 1.2.1.spark2
    
#`3`.项目组成
    ①.idatrix-db-api
        此模块定义：项目对外提供的接口（dubbo）
    ②.idatrix-db-dto
        此模块定义：项目内容部持久对象、业务对象、常量
    ③.idatrix-db-dto
        Mybatis:mapper接口及mapper.xml        
    ④.idatrix-db-core
        DB直接操作数据服务器的服务 
    ⑤.idatrix-db-proxy
        DB操作服务代理层：条件判断、权限判断，统一封装对外接口，数据库操作服务层。项目启动类。 
        
#`4`.配置文件
    ①.主配置文件：idatrix-db-proxy/application.yml                    --可以根据环境修改。需动态修改。
    ②.自定义配置文件：idatrix-db-proxy/application-config.yml          --自定义可配置文件。需动态修改。   
    ③.监控配置文件：idatrix-db-proxy/metrics.properties                --需动态修改。
    ④.hadoop环境配置文件：core-site.xml、hdfs-site.xml、hbase-site.xml、hive-site.xml   --需动态替换文件。
    ⑤.日志配置文件：idatrix-db-proxy/logback.xml                       --需动态修改，日志路径及级别
    ⑥.mybatis配置：idatrix-job-manager/application-mybatis.yml        --mybatis配置。mybatis和分页配置。打包在jar内容，固定值不用修改。   
    
#`5`.项目启动主类          
    idatrix-es-web
    com.ys.idatrix.db.DbApplication
    注：本地IDE中运行需要注释掉 idatrex-db-proxy/pom.xm 中 <build> 中的 <resources> 部分，否则运行不起来。  

#`6`.打包运行
    取消 idatrex-db-proxy/pom.xm 中 <build> 中的 <resources> 部分的注释，否则需动态修改的配置文件打包在jar内。
    配置文件与jar分离打包（推荐）- zip 包
    ①.直接maven执行package命令打包运行
    ②.生成压缩包：db-proxy.zip。解压目录结构：
         a.db-proxy.jar
         b.config 文件夹（内部配置文件可手动调整参数）
    ③.java命令运行：
        java -Xbootclasspath/a:./config -jar db-proxy.jar  
        命令说明：
            a.-Xbootclasspath/a:要在-jar之前
            b.-Xbootclasspath/a:和后面的参数之间不能有空格
            c."./config"是配置文件的路径
            d.可配置多个文件路径。文件路径之间使用分隔符（win下为分号，linux下为冒号）

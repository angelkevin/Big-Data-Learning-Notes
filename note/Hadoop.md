# Hadoop

- 广义上来说，Hadoop通常是指一个更广泛的概念——Hadoop生态圈。
- 狭义上说，Hadoop指Apache这款开源框架，它的核心组件有：
  - HDFS（分布式文件系统）：解决海量数据存储
  - YARN（作业调度和集群资源管理的框架）：解决资源任务调度
  - MAPREDUCE（分布式运算编程框架）：解决海量数据计算

# Hadoop特性优点

- 扩容能力（Scalable）：Hadoop是在可用的计算机集群间分配数据并完成计算任务的，这些集群可用方便的扩展到数以千计的节点中。

- 成本低（Economical）：Hadoop通过普通廉价的机器组成服务器集群来分发以及处理数据，以至于成本很低。

- 高效率（Efficient）：通过并发数据，Hadoop可以在节点之间动态并行的移动数据，使得速度非常快。

- 可靠性（Rellable）：能自动维护数据的多份复制，并且在任务失败后能自动地重新部署（redeploy）计算任务。所以Hadoop的按位存储和处理数据的能力值得人们信赖。
  

# Hadoop集群中Hadoop都需要启动哪些进程，他们的作用分别是什么？

- namenode =>HDFS的守护进程，负责维护整个文件系统，存储着整个文件系统的元数据信息，image+edit log
- datanode =>是具体文件系统的工作节点，当我们需要某个数据，namenode告诉我们去哪里找，就直接和那个DataNode对应的服务器的后台进程进行通信，由DataNode进行数据的检索，然后进行具体的读/写操作
- secondarynamenode =>一个守护进程，相当于一个namenode的元数据的备份机制，定期的更新，和namenode进行通信，将namenode上的image和edits进行合并，可以作为namenode的备份使用
- resourcemanager =>是yarn平台的守护进程，负责所有资源的分配与调度，client的请求由此负责，监控nodemanager
- nodemanager => 是单个节点的资源管理，执行来自resourcemanager的具体任务和命令
- DFSZKFailoverController高可用时它负责监控NN的状态，并及时的把状态信息写入ZK。它通过一个独立线程周期性的调用NN上的一个特定接口来获取NN的健康状态。FC也有选择谁作为Active NN的权利，因为最多只有两个节点，目前选择策略还比较简单（先到先得，轮换）。
- JournalNode 高可用情况下存放namenode的editlog文件
  

# HDFS写数据流程

1）客户端通过Distributed FileSystem模块向namenode请求上传文件，namenode检查目标文件是否已存在，父目录是否存在。

2）namenode返回是否可以上传。

3）客户端请求第一个 block上传到哪几个datanode服务器上。

4）namenode返回3个datanode节点，分别为dn1、dn2、dn3。

5）客户端通过FSDataOutputStream模块请求dn1上传数据，dn1收到请求会继续调用dn2，然后dn2调用dn3，将这个通信管道建立完成。

6）dn1、dn2、dn3逐级应答客户端。

7）客户端开始往dn1上传第一个block（先从磁盘读取数据放到一个本地内存缓存），以packet为单位（大小为64k），dn1收到一个packet就会传给dn2，dn2传给dn3；dn1每传一个packet会放入一个应答队列等待应答。

8）当一个block传输完成之后，客户端再次请求namenode上传第二个block的服务器。

# HDFS小文件问题及解决方案

1.**Hadoop Archive**: Hadoop Archive或者HAR，是一个高效地将小文件放入HDFS块中的文件存档工具，它能够将多个小文件打包成一个HAR文件，这样在减少namenode内存使用的同时，仍然允许对文件进行透明的访问。

2.**sequence file** ：sequence file由一系列的二进制key/value组成，如果为key小文件名，value为文件内容，则可以将大批小文件合并成一个大文件

3.**CombineFileInputFormat**：CombineFileInputFormat是一种新的inputformat，用于将多个文件合并成一个单独的split

# Hadoop读数据流程

1）客户端通过Distributed FileSystem向namenode请求下载文件，namenode通过查询元数据，找到文件块所在的datanode地址。

2）挑选一台datanode（就近原则，然后随机）服务器，请求读取数据。

3）datanode开始传输数据给客户端（从磁盘里面读取数据输入流，以packet为单位来做校验,大小为64k）。

4）客户端以packet为单位接收，先在本地缓存，然后写入目标文件。

# SecondaryNameNode的作用

它的职责是**合并NameNode的edit logs到fsimage文件**

# MapReduce

MapReduce的思想核心是“分而治之”，适用于大量复杂的任务处理场景（大规模数据处理场景）。

Map负责“分”，即把复杂的任务分解为若干个“简单的任务”来并行处理。可以进行拆分的前提是这些小任务可以并行计算，彼此间几乎没有依赖关系。

Reduce负责“合”，即对map阶段的结果进行全局汇总。

# Combiner

每一个map都可能会产生大量的本地输出，Combiner的作用就是对map端的输出先做一次合并，以减少在map和reduce节点之间的数据传输量，以提高网络IO性能。

# partitioner

在进行MapReduce计算时，有时候需要把最终的输出数据分到不同的文件中，比如按照省份划分的话，需要把同一省份的数据放到一个文件中；按照性别划分的话，需要把同一性别的数据放到一个文件中。负责实现划分数据的类称作Partitioner。

# MapReduce的执行流程

- Map阶段
  - 第一阶段是把输入目录下文件按照一定的标准逐个进行逻辑切片，形成切片规划。默认情况下，Split size = Block size。每一个切片由一个MapTask处理。（getSplits）
  - 第二阶段是对切片中的数据按照一定的规则解析成<key,value>对。默认规则是把每一行文本内容解析成键值对。key是每一行的起始位置(单位是字节)，value是本行的文本内容。（TextInputFormat）
  - 第三阶段是调用Mapper类中的map方法。上阶段中每解析出来的一个<k,v>，调用一次map方法。每次调用map方法会输出零个或多个键值对。
  - 第四阶段是按照一定的规则对第三阶段输出的键值对进行分区。默认是只有一个区。分区的数量就是Reducer任务运行的数量。默认只有一个Reducer任务。
  - 第五阶段是对每个分区中的键值对进行排序。首先，按照键进行排序，对于键相同的键值对，按照值进行排序。比如三个键值对<2,2>、<1,3>、<2,1>，键和值分别是整数。那么排序后的结果是<1,3>、<2,1>、<2,2>。如果有第六阶段，那么进入第六阶段；如果没有，直接输出到文件中。
  - 第六阶段是对数据进行局部聚合处理，也就是combiner处理。键相等的键值对会调用一次reduce方法。经过这一阶段，数据量会减少。本阶段默认是没有的。

- reduce阶段
  - 第一阶段是Reducer任务会主动从Mapper任务复制其输出的键值对。Mapper任务可能会有很多，因此Reducer会复制多个Mapper的输出。
  - 第二阶段是把复制到Reducer本地数据，全部进行合并，即把分散的数据合并成一个大的数据。再对合并后的数据排序。
  - 第三阶段是对排序后的键值对调用reduce方法。键相等的键值对调用一次reduce方法，每次调用会产生零个或者多个键值对。最后把这些输出的键值对写入到HDFS文件中。

# MapReduce的shuffle阶段

每一个Mapper进程都有一个环形的内存缓冲区，用来存储Map的输出数据，这个内存缓冲区的默认大小100MB，当数据达到阙值0.8，也就是80MB的时候，一个后台的程序就会把数据溢写到磁盘中。在将数据溢写到磁盘的过程中要经过复杂的过程，首先要将数据进行分区排序（按照分区号如0，1，2），分区完以后为了避免Map输出数据的内存溢出，可以将Map的输出数据分为各个小文件再进行分区，这样map的输出数据就会被分为了具有多个小文件的分区已排序过的数据。然后将各个小文件分区数据进行合并成为一个大的文件（将各个小文件中分区号相同的进行合并）。

# MapReduce程序在yarn上的执行流程

一：客户端向集群提交一个任务，该任务首先到ResourceManager中的ApplicationManager;

二：ApplicationManager收到任务之后，会在集群中找一个NodeManager，并在该NodeManager所在DataNode上启动一个AppMaster进程，该进程用于进行任务的划分和任务的监控；

三：AppMaster启动起来之后，会向ResourceManager中的ApplicationManager注册其信息（目的是与之通信）；

四：AppMaster向ResourceManager下的ResourceScheduler申请计算任务所需的资源；

五：AppMaster申请到资源之后，会与所有的NodeManager通信要求它们启动计算任务所需的任务（Map和Reduce）；

六：各个NodeManager启动对应的容器用来执行Map和Reduce任务；

七：各个任务会向AppMaster汇报自己的执行进度和执行状况，以便让AppMaster随时掌握各个任务的运行状态，在某个任务出了问题之后重启执行该任务；

八：在任务执行完之后，AppMaster向ApplicationManager汇报，以便让ApplicationManager注销并关闭自己，使得资源得以回收；


# 小文件优化

1、从源头干掉，也就是在hdfs上我们不存储小文件，也就是数据上传hdfs的时候我们就合并小文件

2、在FileInputFormat读取入数据的时候我们使用实现类CombineFileInputFormat读取数据，在读取数据的时候进行合并。

# 数据倾斜问题优化

1、既然默认的是hash算法进行分区，那我们自定义分区，修改分区实现逻辑，结合业务特点，使得每个分区数据基本平衡

2、既然有默认的分区算法，那么我们可以修改分区的键，让其符合hash分区，并且使得最后的分区平衡，比如在key前加随机数n-key。

3、既然reduce处理慢，我们可以增加reduce的内存和vcore呀，这样挺高性能就快了，虽然没从根本上解决题，但是还有效果

4、既然一个reduce处理慢，那我们可以增加reduce的个数来分摊一些压力呀，也不能根本解决问题，还是有一定的效果。

## 1.如何能够让Map执行效率最高
尽量减少环形缓冲区flush的次数（减少IO 的使用）

调大环形缓冲区的大小，将100M调更大。

调大环形缓冲区阈值大的大小。

对Map输出的数据进行压缩。（数据在压缩和解压的过程中会消耗CPU）

## 2.如何能够让Reduce执行效率最高
尽量减少环形缓冲区flush的次数（减少IO 的使用）

尽量将所有的数据写入内存，在内存中进行计算。


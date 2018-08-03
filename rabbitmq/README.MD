1. p2ps：一条消息只能被一个接收端接收确认，多个消费者竞争获取消息。

sender-》queue-》receiver 发送端直接将消息发送到queue，消费者直接从queue消费信息

queue：消息存储


2. pub/sub：所有消费者可以接收相同的信息，

使用Exchanges交换机，发送者只负责将消息发送到交换机，不关心消息存储

Exchanges：一方面接收发送者消息，另一方面将信息发送到队列

Exchanges type（direct、fanout、topic、headers）决定怎么处理消息

附：默认使用 “” 空串指代默认exchange，消息通过默认exchange路由到指定的队列。

临时队列：每次连接时使用空队列，

每次连接时，创建随机临时队列；每次断开连接时，删除队列。

>direct: 将接收到的信息发送至绑定的队列上指定routekey的队列。根据routekey routing路由

>fanout：将接收到的信息广播发送至所有它知道的队列

rabbitmqctl list_exchanges：amqp.* 默认创建的exchange

>topic：特定标识route_key消息将被发送至绑定匹配route_key的队列。



注意：

和direct的区别是，route_key的使用；

topic使用的route_key：‘.’分割的一些列关键词，最对255 bytes

topic队列绑定route_key可以使用通配符匹配

* 匹配一个词组；#匹配一个或者多个词组

*.beijing.#：匹配第二个单词为beijing的route_key


附：topic exchange可以模拟其它exchange












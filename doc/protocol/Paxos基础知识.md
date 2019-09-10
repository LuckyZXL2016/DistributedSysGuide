## Paxos协议/算法是分布式系统中比较重要的协议.
Google Chubby的作者Mike Burrows说过这个世界上只有一种一致性算法，那就是Paxos，其它的算法都是残次品。

## 算法内容
Paxos在原作者的《Paxos Made Simple》中内容是比较精简的：
> **Phase 1**  
>
> (a) A proposer selects a proposal number n and sends a prepare request with number n to a majority of acceptors.  
> 
> (b) If an acceptor receives a prepare request with number n greater than that of any prepare request to which it has already responded, then it responds to the request with a promise not to accept any more proposals numbered less than n and with the highest-numbered pro-posal (if any) that it has accepted.  
>
> **Phase 2**  
>
> (a) If the proposer receives a response to its prepare requests (numbered n) from a majority of acceptors, then it sends an accept request to each of those acceptors for a proposal numbered n with a value v , where v is the value of the highest-numbered proposal among the responses, or is any value if the responses reported no proposals.  
>
> (b) If an acceptor receives an accept request for a proposal numbered n, it accepts the proposal unless it has already responded to a prepare request having a number greater than n.  
<br>
Paxos流程图可概括为：

![image](/pic/paxos/paxos-flow.png)
<br>

## 实例及详解
Paxos中有三类角色Proposer、Acceptor及Learner，主要交互过程在Proposer和Acceptor之间。
Proposer与Acceptor之间的交互主要有4类消息通信，如下图：

![image](/pic/paxos/paxos-messages.png)

这4类消息对应于paxos算法的两个阶段4个过程：
- phase 1
  - a) proposer向网络内超过半数的acceptor发送prepare消息
  - b) acceptor正常情况下回复promise消息
- phase 2
  - a) 在有足够多acceptor回复promise消息时，proposer发送accept消息
  - b) 正常情况下acceptor回复accepted消息
  
以下图中基本只画出proposer与一个acceptor的交互。时间标志T2总是在T1后面。propose number简称N。

情况之一如下图：

![image](/pic/paxos/paxos-e1.png]

A3在T1发出accepted给A1，然后在T2收到A5的prepare，在T3的时候A1才通知A5最终结果(税率10%)。这里会有两种情况：

- A5发来的N5小于A1发出去的N1，那么A3直接拒绝(reject)A5
- A5发来的N5大于A1发出去的N1，那么A3回复promise，但带上A1的(N1, 10%)
这里可以与paxos流程图对应起来，更好理解。acceptor会记录(MaxN, AcceptN, AcceptV)。

A5在收到promise后，后续的流程可以顺利进行。但是发出accept时，因为收到了(AcceptN, AcceptV)，所以会取最大的AcceptN对应的AcceptV，例子中也就是A1的10%作为AcceptV。如果在收到promise时没有发现有其他已记录的AcceptV，则其值可以由自己决定。

针对以上A1和A5冲突的情况，最终A1和A5都会广播接受的值为10%。

其实4个过程中对于acceptor而言，在回复promise和accepted时由于都可能因为其他proposer的介入而导致特殊处理。所以基本上看在这两个时间点收到其他proposer的请求时就可以了解整个算法了。例如在回复promise时则可能因为proposer发来的N不够大而reject：

![image](/pic/paxos/paxos-e2.png)

如果在发accepted消息时，对其他更大N的proposer发出过promise，那么也会reject该proposer发出的accept，如图：

![image](/pic/paxos/paxos-e3.png)

这个对应于Phase 2 b)：
> it accepts the proposal unless it has already responded to a prepare request having a number greater than n.

## 总结
Leslie Lamport没有用数学描述Paxos，但是他用英文阐述得很清晰。将Paxos的两个Phase的内容理解清楚，整个算法过程还是不复杂的。

至于Paxos中一直提到的一个全局唯一且递增的proposer number，其如何实现，引用如下：
> 如何产生唯一的编号呢？在《Paxos made simple》中提到的是让所有的Proposer都从不相交的数据集合中进行选择，例如系统有5个Proposer，则可为每一个Proposer分配一个标识j(0~4)，则每一个proposer每次提出决议的编号可以为5*i + j(i可以用来表示提出议案的次数)

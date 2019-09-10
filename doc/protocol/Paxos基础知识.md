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

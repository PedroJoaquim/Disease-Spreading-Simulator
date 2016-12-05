# Disease-Spreading-Simulator

Disease spreading simulator that implements three different immunization startegies:

-Random: Where a randomly selectected group of people are vaccinated

-Most Popular Friend: An initial small randomly selected group of people are vaccinated and then each person indicates its most popular friend to vaccination (highest degree)

-Acquaintance Immunization: based on this [work](https://arxiv.org/abs/cond-mat/0207387), it first selects a a group of nodes, that could be the entire graph, and each of the individuals in the group select a random neighbor for immunization. Here the initial group is not vaccinated. 

Developed with [ParaGraph](https://github.com/PedroJoaquim/ParaGraph)

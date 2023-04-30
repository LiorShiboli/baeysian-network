# baeysian-network
## implementing baeysian network in java
while knowing usually this type of algorithm would be either used in a library in python or written efficiently in c or c++, this excersize was very useful in understanding the how and why of the algorithm.<br />
to add to that this was a very useful example in how without enough care for oop in java things can get messy quick,while in the end the code came out relatively clean there are a lot of things to be improved on future oop projects
## input output<br />
 this implementation uses an XML file describing the network and an input and output files in text forms(assignment specs).<br />
<br />
 the input consists of several lines the first being the XML describing the network and the rest consisting of probabilities to compute and how.<br />
 probabilities to compute will be written as P(< variable 1>=< outcome 1>|< variable 2>=< outcome 2>,< variable 3>=< outcome 3> etc) to denote probability of variable 1 being outcome 1 given the other variables are their corresponding outcomes.<br />
 then write ,< 1-3> to specify the algorithm used where 1 is the naive calculation, 2 is variable elimination and 3 is variable elimination with an added heuristic

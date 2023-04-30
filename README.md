# baeysian-network
implementing bayesian network in java<br />
this implementation uses an XML file describing the network and an input and output files in text forms(assignment specs)
the input consists of several lines the first being the XML describing the network and the rest consisting of probabilities to compute and how
probabilities to compute will be written as P(<variable 1>=<outcome 1>|<variable 2>=<outcome 2>,<variable 3>=<outcome 3> etc) to denote probability of variable 1 being outcome 1 given the other variables are their corresponding outcomes
then write ,<1-3> to specify the algorithm used where 1 is the naive calculation, 2 is variable elimination and 3 is variable elimination with an added heuristic

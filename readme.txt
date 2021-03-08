Runner.java is a command line application for computing the MAST between the two trees located in the 'inputTrees' folder.

----------O(n^2)-------------
The O(n^2) algorithm is contained in the 'n_squared' package.
MAST.java contains methods for computing the MAST between two rooted binary trees, each given as a Phylogeny.
	getMASTsize(tree1, tree2) computes the size of the MAST between tree1 and tree2.
	getMAST(tree1, tree2) computes the MAST between tree1 and tree2, returning it as a Phylogeny tree.
	getMastBackTrack(tree1, tree2) computes the MAST between tree1 and tree2, using backtracking, returning it as a Phylogeny tree.

----------O(nlogn)------------
The O(nlogn) algorithm is contained in the 'nlogn' package.
MAST.java contains methods for computing the MAST between two rooted binary trees, each given as a Phylogeny.
	getMAST(tree1, tree2, recursive) computes the MAST between tree1 and tree2, returning a pair of the Phylogeny tree and the size. The last parameter determines whether it should be computed recursively or iteratively.
	getMASTUsingMLIS(tree1, tree2) computes the MAST between tree1 and tree2, using an algorithm for the Longest Increasing Subsequence problem, returning it as a Phylogeny tree. The input trees should be of a topology where each internal node has at least one leaf as a child.

----------Naive algorithm-----------
A naive algorithm is contained in the 'naive' package.
MAST.java contains a method for computing the MAST between two rooted binary trees, each given as a Phylogeny.
	getMAST(tree1, tree2) computes the MAST between tree1 and tree2, returning it as a Phylogeny tree.

----------Utilities-----------
The 'Utilities' package contains
	A constant time algorithm for computing a Least Common Ancestor.
	An O(nlogn) time algorithm for the Longest Increasing Subsequence problem.
	A linear time algorithm for inducing a subtree, given an ordered set of leaves.
	Tools for parsing newick files to Phylogeny trees, writing Phylogeny trees to newick files, genegating Phylogeny trees etc.
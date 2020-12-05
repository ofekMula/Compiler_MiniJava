## assumptions:
# 1. this script is in the same folder as mjavac.jar
# 2. AST folder for .xml files is in ../examples/ast
# 3. Llvm Folder for .ll files is in ../examples/LLVM

## results:
# if the script & your code are working properly
# you should see the names of all of the tests with "success"

# this tester is using "diff-llvm" command to compare your program's results
# if a test failed, the tester stops and the "diff" results are found in file "res"
# if your program prints to screen, the prints appear in file "log"

COMMAND="java -jar mjavac.jar unmarshal compile"
FILEPATH="../examples/ast"
OUTPUTPATH="."
LLVMPATH="../examples/LLVM"
function testLlvmGenerator()
{
	XMLFILE=$1
	OUTPUTFILE=$2
	LLVMFILE=$3
	$COMMAND $FILEPATH/$XMLFILE.xml $OUTPUTPATH/$OUTPUTFILE.ll > ./log
	llvm-diff $LLVMPATH/$LLVMFILE.ll $OUTPUTPATH/$OUTPUTFILE.ll > ./res
	if [ $? -eq 0 ]
	then
		echo $LLVMFILE.ll $OUTPUTFILE.ll Success
	else
		echo $LLVMFILE.ll $OUTPUTFILE.ll Fail
	fi
}

#ex2 examples in yotam's git:
testLlvmGenerator BinarySearch.java binarySearch BinarySearch
testLlvmGenerator BinaryTree.java binaryTree BinaryTree
testLlvmGenerator BubbleSort.java bubbleSort BubbleSort
testLlvmGenerator Factorial.java factorial Factorial
testLlvmGenerator LinearSearch.java linearSearch LinearSearch
testLlvmGenerator LinkedList.java linkedlist LinkedList
testLlvmGenerator QuickSort.java quickSort QuickSort
testLlvmGenerator TreeVisitor.java treeVisitor TreeVisitor
#demos from recitations:
testLlvmGenerator And.java and And
testLlvmGenerator CompoundExpr.java compundExpr CompoundExpr
testLlvmGenerator If.java iff If
testLlvmGenerator Simple simple Simple
testLlvmGenerator SimpleExpr.java simexpr SimpleExpr
testLlvmGenerator varType.java varType varType
testLlvmGenerator Arrays.java arr Arrays
testLlvmGenerator Classes classes Classes
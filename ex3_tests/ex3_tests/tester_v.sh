
## assumptions:
# 1. in order to reach the compiled jar you need to run ../../mjavac
# 2. tests folder 'Test' has xml and 'Test.res' file (expected result)


## results:
# if the script & your code are working properly
# you should see the names of all of the tests with "Success"


COMMAND="java -jar ../../mjavac.jar unmarshal semantic"
FILEPATH="."
function test()
{
	XMLFILE=$1
	TESTFOLDER="valid"
	echo "Running Test:" $XMLFILE
	result_file=$TESTFOLDER/$XMLFILE.our-result
	log_file=$TESTFOLDER/$XMLFILE.log
	file = "ok.txt"
	$COMMAND $TESTFOLDER/$XMLFILE $result_file > $log_file
	diff -w $result_file $TESTFOLDER/$file
	if [ $? -eq 0 ]
	then
		echo Success
	else
		echo Fail
	fi
}

test And.xml
test and_short_circuit_arr.xml
test and_short_circuit_arr_backward.xml
test Arrays.xml
test array_access_in_access.xml
test array_iota_print_all.xml
test array_iota_print_all_oob.xml
test array_iota_simple.xml
test array_iota_simple_neg_size.xml
test array_iota_simple_oob.xml
test AssignmentValid.java.xml
test BinarySearch.xml
test BinaryTree.xml
test binary_search.xml
test binary_tree.xml
test BubbleSort.xml
test bubble_sort.test xml
test Classes.xml
test CompoundExpr.xml
test empty_vtable.xml
test Factorial.java.xml
test field_related_sibling_class.xml
test field_unrelated_sibling_class.xml
test field_with_if_and_assign_array_statements.xml
test field_with_sysout_and_array_length_statements.xml
test formal_param_with_if_and_assign_array_statements.xml
test If.xml
test InitVarValid.java.xml
test LinearSearch.xml
test linear_search.xml
test LinkedList.xml
test linked_list.xml
test method_related_sibling_class.xml
test method_unrelated_sibling_class.xml
test method_with_new_object.xml
test method_with_ref_id.xml
test method_with_sysout_statement.xml
test method_with_this.xml
test OwnerExprValid.java.xml
test partial_overrides.xml
test QuickSort.xml
test quick_sort.xml
test Rule11Valid.xml
test Rule13Valid.xml
test Rule16Valid.xml
test Rule18Valid.xml
test Rule25Valid.xml
test Rule6Valid.xml
test Rule6Valid_covariance.xml
test Rule6Valid_covariance_b.xml
test Rule6Valid_covariance_c.xml
test Rule8Valid.xml
test Rule9Valid.xml
test Simple.xml
test SimpleExpr.xml
test simple_if.xml
test simple_if_and.xml
test simple_print.xml
test simple_print_mult.xml
test subclass_fields.xml
test thisDotMethod.xml
test TreeVisitor.xml
test TreeVisitorBrotherMethod.xml
test TreeVisitorChildMethod.xml
test TreeVisitorFieldRename.xml
test TreeVisitorMethodRename.xml
test tree_visitor.xml
test VarType.xml

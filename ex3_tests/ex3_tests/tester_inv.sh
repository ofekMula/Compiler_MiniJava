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
	TESTFOLDER="invalid"
	echo "Running Test:" $XMLFILE
	result_file=$TESTFOLDER/$XMLFILE.our-result
	log_file=$TESTFOLDER/$XMLFILE.log
	file = "error.txt"
	$COMMAND $TESTFOLDER/$XMLFILE $result_file > $log_file
	diff -w $result_file $TESTFOLDER/$file
	if [ $? -eq 0 ]
	then
		echo Success
	else
		echo Fail
	fi
}

#ex3 examples in yotam's git:
test AssignmentInvalid.java.xml
test field_with_local_var_shadowing_Error_15.xml
test field_with_while_and_assign_statements_Error15.xml
test formal_param_with_while_and_assign_statements_Error15.xml
test InitVarInvalid.java.xml
test InitVarInvalid2.java.xml
test local_var_with_if_and_assign_array_statements_Error15.xml
test local_var_with_while_and_assign_statements_Error15.xml
test method_and_variable_with_the_same_name_Error15.xml
test method_with_while_and_assign_statements_Error15.xml
test OwnerExprInvalid.java.xml
test Rule10Invalid.xml
test Rule10Invalid_b.xml
test Rule11Invalid_not_declared_in_owner.xml
test Rule11Invalid_not_declared_in_owner2.xml
test Rule11Invalid_wrong_params.xml
test Rule11Invalid_wrong_params2.xml
test Rule11Invalid_wrong_params3.xml
test Rule11Invalid_wrong_params4.xml
test Rule12Invalid_a.xml
test Rule12Invalid_b.xml
test Rule13Invalid_a.xml
test Rule13Invalid_b.xml
test Rule13Invalid_c.xml
test Rule14Invalid_a.xml
test Rule14Invalid_b.xml
test Rule14Invalid_c.xml
test Rule16Invalid_a.xml
test Rule16Invalid_b.xml
test Rule16Invalid_c.xml
test Rule16Invalid_call_return.xml
test Rule17Invalid_if.xml
test Rule17Invalid_while.xml
test Rule18Invalid_a.xml
test Rule18Invalid_b.xml
test Rule18Invalid_c.xml
test Rule18Invalid_d.xml
test Rule18Invalid_e.xml
test Rule18Invalid_non_exist.xml
test Rule1Invalid_a.xml
test Rule1Invalid_b.xml
test Rule20Invalid.xml
test Rule21invalid_a.xml
test Rule21invalid_b.xml
test Rule21invalid_c.xml
test Rule21invalid_d.xml
test Rule21invalid_e.xml
test Rule22Invalid_a.xml
test Rule22Invalid_b.xml
test Rule23Invalid_a.xml
test Rule23Invalid_b.xml
test Rule23Invalid_b.xml
test Rule23Invalid_c.xml
test Rule24Invalid_formals.xml
test Rule24Invalid_variables.xml
test Rule25Invalid_a.xml
test Rule25Invalid_b.xml
test Rule25Invalid_nonexisting_param.xml
test Rule2Invalid.xml
test Rule2Invalid_b.xml
test Rule3Invalid.xml
test Rule3Invalid_b.xml
test Rule4Invalid.xml
test Rule4Invalid_b.xml
test Rule4Invalid_c.xml
test Rule5Invalid.xml
test Rule6Invalid_covariance_return.xml
test Rule6Invalid_formals.xml
test Rule6Invalid_formals_class.xml
test Rule6Invalid_formal_types.xml
test Rule6Invalid_ret.xml
test Rule6Invalid_ret_indirect.xml
test Rule8Invalid.xml
test Rule8Invalid_b.xml
test Rule9Invalid.xml
test variable_and_method_with_the_same_name_Error15.xml
test VarTypeBad.xml

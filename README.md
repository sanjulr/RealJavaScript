***RealJavaScript***
--------------
RJS (RealJavaScript) is the **REAL** Scripting language built for Java.

 - RJS enables dynamic scripting in Java.
 - Dynamic debugging in Java.
 - Built on pure Java.
 - Supports Android platform too.
 - Things you thought can be done only on compile time can now be done in run time.
 

***How to use RealJavaScript?***
---------------------
1) Include the RealJavaScript.jar as a library to your project.
2) Import com.zero1.realjavascript.RJSBridge to your Java class.
3) Use the RJSBridge.interpret() function to invoke RJS commands.
***Example***:
```
import com.zero1.realjavascript.RJSBridge;

public class MyJavaClass {

	int a, b = 10;

	public static void main(String[] args) {
		MyJavaClass myJavaClass = new MyJavaClass();
		myJavaClass.doTheMagic();
	}

	public void doTheMagic() {
		// RJS Script to print Hello World! Output: Hello World!
		RJSBridge.interpret(this, "call java.lang.System.out.println \"Hello World!\"");

		// RJS Script to interact with your class fields. Output: 15
		RJSBridge.interpret(this, "assign 5 to a and call java.lang.System.out.println (compute a+b)");

		// RJS Script to interact with your class methods. Output: Hello Sanju
		RJSBridge.interpret(this, "call printHello \"Sanju\"");
	}

	void printHello(String name) {
		System.out.println("Hello " + name);
	}
}
```

***RealJavaScript Syntax and Commands***
-----------------------

**GENERAL:**

 - All RJS Commands are case insensitive.
>  ***Example***: `call helloWorld` is the same as `CALL helloWorld`.
> Both will call the method helloWorld from the calling object.

 - Multiple commands can be executed in a linear fashion by separating the commands by using the ***AND*** keyword.
>  ***Example***: `call helloWorld and call java.lang.System.out.println
> 123` will execute the commands separated by and keyword from left to
> right. So at first, `call helloWorld` is executed and then `call
> java.lang.System.out.println 123` is executed.

 - Objects/Fields/Classes/Functions created using RJS commands can be accessed by prefixing the ***&*** symbol.
>  ***Example***:  `create java.util.Date date and call
> java.lang.System.out.println &date` will print the *date* object in
> the system console.

 - An object's method can be accessed by using the dot operator '***.***'.
>  ***Example***: `create java.util.Date date and call
> java.lang.System.out.println &date.getTime` will call *getTime*
> function of the created object named *date*.

 - In RJS, string appending is similar to how it is in Java. The plus operator ***+*** is used for appending.
>  ***Example***: `call java.lang.System.out.println "The current time
> is "+(create java.util.Date date)` will print the string *"The current
> time is Sat Nov 19 23:09:35 IST 2016"* in the system console.

 - The plus operator ***+*** can also be used for appending part of command or keyword or text as well.
>  ***Example***: Consider we have a method named getFalString which
> returns the string "fal" `public void getFalString(){ return "fal" }`
> now in RJS, `if ((call getFalString)+se) then {call print 1} else
> {call print 0}` will print the integer value *0* in the system
> console. In the *If* condition, the string returned from the method
> *getFalString* is appened with *se* which makes it *false* as a whole and thus the *else* part is executed. The strings when combined if
> makes sense, then it is understood by RJS. This type of appending is
> possible in RJS.

 - RJS Commands can be nested within another command. If your nested command needs to execute only one RJS command, then enclose your command within the ***(*** and ***)*** braces. If your nested command needs to execute more than one RJS command, then enclose your commands within the ***{*** and ***}*** braces.
>  ***Example***:  
>  ***Nested single command***: `call
> java.lang.System.out.println (compute 10*(call funcToAdd2Nos (compute
> -1-3),9))` will execute `call compute -1-3` first and the result *-4* and *9* is passed as arguments for the function *funcToAdd2Nos* and
> the result which is *5* is executed with *compute* command. So here,
> the integer value *50* is printed in the system console. The execution
> order is from the inner most nested command to the outer command. If
> the command inside the ***( )*** braces, if the execution results in a
> value, it is returned to the outer command.  
> 
> ***Nested multiple commands***:  `call java.lang.System.out.println {create
> java.util.Date date and create java.lang.String dateString = call
> &date.toString and call &dateString.length}` will execute commands
> from left to right inside the ***{ }*** braces. Here an object named
> *date* is created for the class *java.util.Date* and another object named *dateString* of type *java.lang.String* is created and is
> assigned with the *date* object's *toString* method execution's result
> and then *dateString* object's *length* method is executed which is
> the last statement and this value is returned. So the integer value
> *11* is printed in the system console which is the length of the *java.lang.String* object named *dateString*. For commands inside ***{ }*** braces, the last statement if is a value is returned to the outer
> command. In the above example, `call &dateString.length` is the last
> statement and thus the result of this command is returned to the outer
> command.

 - *null* keyword can be used in RJS Script just the same way it is used in Java.
>  ***Example***: `java.lang.String myString=null;`  `call
> java.lang.System.out.println (is myString==null)` will print *true* in
> the system console.

 - *this* keyword in RJS Script refers to the object passed to RJSBridge. The passed object is used for operations ***this*** is used in RJS.
> Example: `call this.print this.myVariable` will call the the method
> named *print* by passing *myVariable* as parameter for that method.
> Here, both the method *print* and the variable *myVariable* belong to
> the object that was passed to RJSBridge. So ***this*** keyword *refers
> to the object that was passed to RJSBridge* and not the conventional
> Java's ***this***.

 - Numbers, Strings and boolean can be defined as done in Java.
>  ***Example***:   **Numbers**: `compute 1+a`, Here *1* is considered
> as *java.lang.Integer*. `compute 1.5+a`, here *1.5* is considered as
> *java.lang.Float*.  **Strings**: `call java.lang.System.out.println "Hello World!"`, in RJS, anything that is specified within double
> quotes is considered as java.lang.String.  **Boolean**: `if (is 2>1)
> then {true} else {false}`. Here *true* is considered as *boolean true*
> and *false* is considered as *boolean false*.

 - Logical Operations can be done through RJS.
>  ***Example***:  ***AND***: `call java.lang.System.out.println (is
> true && (is 1==2))` will print false as the second evaluation fails.
> In AND operation, if one of the evaluation results in false, then the
> other evaluations are not checked.  ***OR*** `call
> java.lang.System.out.println (is true || (is 1==2))` will print true
> and the second evaluation is not checked at all since this this an OR
> operation.

 - To execute Logical operations for more than 2 evaluations, use nesting.
>  ***Example***: `call java.lang.System.out.println (is (is (is 8>3) &&
> (is 2==2)) && (is 6<6))` will print false as 6<6 evaluation fails.
> This checks for 3 evaluations and only if all 3 are true, the result
> is true.


**CALL:**

 - Used to call a method.
> ***Example***: `call helloWorld` will call the function named *helloWorld* from the calling object.

 - Can call methods with parameters too.
>  ***Example***: `call funcToAdd2Nos 10,12` will call the function named
> *funcToAdd2Nos* by passing two integer parameters *10* and *12*. The parameters are seperated using the ***,*** symbol.

 - Returns object if the method returns anything.
> ***Example***: `call print (call funcToAdd2Nos 10,12)` will call the function named *print* by passing the result received by calling
> *funcToAddNos* *10*,*12* which will be *22*. So *22* is passed to the *print*
> function.

 - Can call an object's method or a static method too.
> ***Example***: `call java.lang.System.out.println "Hello World!"` will call j*ava.lang.System.out's println* method by passing the string.
> *"Hello World"*.

 - Supports polymorphism.
>  ***Example***: `call exit` and `call exit 1`. Here `call exit` calls the function named *exit* which does not accept any parameter. Whereas `call exit 1` calls the function named *exit* which accepts one integer parameter.

 - Negation of a function can be done. Used to negate the return value.
>  ***Example***: `call java.lang.System.out.println -(call funcToAdd2Nos
> 10,12)` will print *-22* in the console.

 - Logical NOT on a function result can be done.
> ***Example***: `call java.lang.System.out.println !false` will print *true* in the console.

 - Parameters can be variables, objects and direct numbers or strings
   too.
> ***Example***: `call java.lang.System.out.println myVariable` will call *println* function of *java.lang.System.out* by passing the variable named.
> *myVariable*.

**COMPUTE:**

 - Used to perform mathematical calculations.
> ***Example***: `compute 5 + 7` adds the numbers *5* and *7* and returns *12*.

 - Supports passing variables and direct numbers as parameters.
>  ***Example***: `compute 5 + myVariable` adds the number *5* with the value
> stored in the variable *myVariable* and returns the computational
> result.

 - Performs calculation using BODMAS technique.
>  ***Example***: `compute "a+b*c/(-4/b-1)"` will return the result based on
> BODMAS technique.

 - Can calculate more than 2 variables/numbers at the same time.
> ***Example***: `compute "a*a+b*b+2*a*b"` will perform the computation based
> on BODMAS technique. To perform computations on more than 2
> variables/numbers, enclose the computation within double quotes.

 - Supports Addition, Subtraction, Multiplication and Division.
> ***Example***: 
> *Addition*: `compute 3+5` will return the value *8*.
> *Subtraction*: `compute 3-5` will return the value *-2*.
> *Multiplication*: `compute 3*5` will return the value *15*.
> *Division*: `compute 3/5` will return the value *0.6*.
	
**TO:**

 - Used to set the result of an operation to a variable or object.
> ***Example***: `call funcToAdd2Nos 5, (compute 3+7) to b` will call the method named *funcToAdd2Nos* by passing values *5* and the
> computational result by calling `compute 3+7` and the result is
> assigned to the variable named *b*. So in this example, *15* is
> assigned to *b*.

**ASSIGN:**

 - Used to assign a value (variable or object or direct number/string)
   to another variable or object.
> ***Example***: `assign 5 to d` will assign the value *5* to the variable
> named *d*.

**CREATE:**

 - Used to create an object.
>  ***Example***: `create java.util.Date date` will create a *java.util.Date*
> object with the name *date*.

 - Can create local objects and other class' objects as well.
>  ***Example***: `create Tester tester` will create an object for the local
> class *Tester* with the name *tester*.

 - Created object can be assigned to other objects too.
>  ***Example***: `create java.util.Date date to myDateVariable` will create a
> *java.util.Date* object with the name *date* and will be assigned to preexisting *java.util.Date* object named *myDateVariable*.

 - Created objects can also be accessed within RJS script.
>  ***Example***: `create java.util.Date date` creates a *java.util.Date*
> object named *date*. This object is created inside RJS. All objects
> created using RJS can be accessed by prefixing the ***&*** to the
> created object's name. Like, `call java.lang.System.out.println &date` will
> pass the *date* object to *java.lang.System.out.println* which will print the
> result in the system console.

 - Constructor polymorphism supported.
>  ***Example***: `create java.util.Date date` will create a *java.util.Date*
> object by passing no parameters. Whereas, `create java.util.Date date
> 1479396430482` will create a *java.util.Date* object by passing one
> long parameter.

**IS:**

 - Used to perform boolean operations.
>  ***Example***: `is a>b` will evaluate if the value in *a* is greater than
> the value in *b*. The result will be a boolean *true* or *false*.

 - Supports Equal To, Not Equal To, Greater Than, Lesser Than, Greater
   Than or Equal To, Lesser Than or Equal To.
> ***Example***: 
>  Equal To: `is 5 == 3` will return boolean *false*.
> Not Equal To: `is 5 != 3` will return boolean *true*.
> *Greater Than*: `is 5 > 3` will return boolean *true*.
> *Lesser Than*: `is 5 < 3` will return boolean *false*.
> *Greater Than or Equal To*: `is 5 >= 3` will return boolean *true*.
> *Lesser Than or Equal To*: `is 5 <= 3` will return boolean *false*.
	
**IF, THEN, ELSE:**

 - Used to perform branching executions.
> ***Example***: `if (is a>b) then {call java.lang.System.out.println \"Hello\"} else {call java.lang.System.out.println \"Bye\"}` will
> evaluate `is a>b` and if the result is *true*, the ***then*** part
> will be executed. If the result is *false*, the ***else*** part will
> be executed.

 - Else part is not mandatory.
>  ***Example***: `if (is a>b) then {call java.lang.System.out.println
> "Hello"}` will evaluate `is a>b` and if the result is *true*, the
> ***then*** part will be executed. If the result is *false*, nothing is executed.

**CLASS:**

 - Used to create an RJSClass.
>  ***Example***: `class myRJSClass fields <? java.lang.String
> string1,string2,string3="Sanju"; java.lang.Integer num = 123 ?>
> methods <? myMethod <@ java.lang.String name @> {call
> java.lang.System.out.println $name}; myMethod <@ java.lang.String
> name, java.lang.Integer sno @> {call java.lang.System.out.println
> ($sno)+") "+($name)} ?>` will create a RJS class named *myRJSClass*
> with three *java.lang.String* objects namely *string1*, *string2* and
> *string3* and one *java.lang.Integer* object named *num*. Here, the variable named *string3* is assigned with the string *"Sanju"* and the
> variable named *num* is assigned with the value *123*. Two methods
> named *myMethod* is created; one method accepts only one
> *java.lang.String* object and another method accepts one *java.lang.String* object and one *java.lang.Integer* object. The first method calls the *println* method of *java.lang.System.out* by
> passing the *java.lang.String* object passed to it while invoking
> *myMethod*. The second method calls the *println* method of *java.lang.System.out* by passing the *java.lang.String* and *java.lang.Integer* objects passed to it while invoking *myMethod*.

 - RJSClass can have it's own fields.
>  ***Example***: `class myClass2 fields <? java.lang.String s1 ?>` creates a
> RJS class named *myClass2* with a field named *s1* which is of type
> *java.lang.String*. Fields need to be mentioned after the ***fields*** keyword and the fields are to be declared/defined within the ***<?
> ?>*** braces.
> RJS fields can be accessed within RJS methods of the same class without using the & symbol. `class myRJSClass fields <? java.lang.String name = "Sanjeevi" ?> methods <? printMyName {call java.lang.System.out.println "Hello "+ (name)} ?>` here a class named *myRJSClass* is created with one field named *name* and is assigned the string value *"Sanjeevi"* and the RJS method named *printMyName* can access the RJS field *name* without the *&* symbol. In this example, since we are doing string appending operation, the RJS field *name* is enclosed within *( )* braces. Multiple fields of the same type can be declared/defined using comma ***,*** to separate each field. Example: `java.lang.String string1, string2` will create two *java.lang.String* objects named *string1*, *string2* . Multiple fields of different types need to be declared/defined using semi colon ***;*** as separator. Example: `java.lang.String string1; java.lang.Integer num` will create one *java.lang.String* object named *string1* and one *java.lang.Integer* object named *num*.

 - Fields can be preassigned.
>  ***Example***: `class myClass2 fields <? java.lang.String s1 = "Sanju" ?>`
> will create a RJS class named *myClass2* with a field named *s1* of
> type *java.lang.String* and is assigned the string value *"Sanju"*. RJS fields need to be assigned using the ***=*** symbol while creating them.

 - RJSClass can have it's own methods.
>  ***Example***: `class myClass3 methods <? myMethod {call
> java.lang.System.out.println \"Hello World\"} ?>` will create a class
> named *myClass3* with one method named *myMethod* which accepts no
> parameters. Multiple RJS Methods can be defined using the semi colon ***;*** as separator. Example: `myMethod1 {call
> java.lang.System.out.println "No parameters"}; myMethod2 <@
> java.lang.Integer num @> {call java.lang.System.out.println $num} ?>`. will create two methods named *myMethod1* and *myMethod2*.

 - Methods can have arguments.
>  ***Example***: `class myClass3 methods myMethod <@ java.lang.String
> firstName @> {call java.lang.System.out.println "Hello "+($firstName)}
> ?>` will create a class named *myClass3* with one method named
> *myMethod* which accepts one *java.lang.String* as parameter. The parameter *firstName* is accessed inside the method named *myMethod*
> by prefixing the parameter name with ***$***. RJS Methods may not accept any parameters. Example: `myMethod {call java.lang.System.out.println "No parameters"}`.

 - Methods will return if it has anything to return.
>  ***Example***: `class myClass3 methods <? getGreatestNum <@
> java.lang.Integer num1, java.lang.Integer num2 @> { if (is
> $num1>$num2) then {$num1} else {$num2} } ?>` will create a class named
> *myClass3* which has one method named *getGreatestNum* which accepts two *java.lang.Integer* arguments. The method evaluates which integer
> is greater and returns the greatest integer. The last statement in any
> method if is a value, is the value that is returned. Otherwise *null*
> is returned.

 - Objects can be created for RJSClasses.
>  ***Example***: `create &myClass3 myObject and call java.lang.System.out.println
> (call &myObject.getGreatestNum 2,5)` will create an object named *myObject* for
> the RJSClass *myClass3*. The method *getGreatestNum* is called by
> using the RJSClass's object named *myObject*.

 - Methods support polymorphism.
>  ***Example***: `class myRJSClass methods <? myMethod {call
> java.lang.System.out.println "No parameters"}; myMethod <@
> java.lang.Integer num @> {call java.lang.System.out.println $num} ?>`
> will create a RJSClass named *myRJSClass* with two methods named
> *myMethod*. `create &myRJSClass myObject and call &myObject.myMethod` prints the string *"No parameters"* in the system's console. `create &myRJSClass myObject and call &myObject.myMethod 4` will print the integer *4* in the system's console.

**LOOP:**

Consider the following array for the examples listed below:
`java.lang.String[] myArray = {"String 1", "String 2", "String 3", "String 4", "String 5"};`

 - Used to iterate over a array.
>  ***Example***: `loop myArray<> {call java.lang.System.out.println
> myArray<#>}` will iterate over the array named *myArray* from 0 to
> length of the array. The ***#*** represents the index of iteration.
> This will print *String1, String2, String3, String4, String 5* in the
> system's console.

 - Used to iterate over a given number range.
>  ***Example***: `loop myArray<1..3> {call java.lang.System.out.println
> myArray<#>}` will iterate over the array named *myArray* from the
> index *1 to 3*, which includes both indexes *1 and 3*. The range is
> indicated using ***..*** symbol. This will print *String2, String 3,
> String 4* in the system's console.

 - Can loop by stride.
>  ***Example***: `loop myArray<2> {call java.lang.System.out.println
> myArray<#>}` will iterate over the array named *myArray* from index *0
> to length* of the array by taking a *step of 2* to iterate over the
> array. This will print *String 1, String 3, String 5* in the system's
> console.

 - Reverse looping is also supported.
>  ***Example***: `loop myArray<3..0> {call java.lang.System.out.println
> myArray<#>}` will iterate over the loop named *myArray* from index *3
> to 0*. This will print *String 4, String 3, String 2, String 1* in the
> system's console.

 - Ranged looping with stride is also supported.
>  ***Example***: `loop myArray<2..l, 2> {call
> java.lang.System.out.println myArray<#>}` will iterate over the array
> named myArray from index *2 to length* of the array which is
> represented by the letter ***l*** with stride *2*. This will print
> *String 3, String 5* in the sytem's console.

**AS:**

 - Used to perform type casting.
>  ***Example***:  `call java.lang.System.out.println (call (create
> java.util.Date date as java.lang.String).getClass)` will cast the
> created *java.util.Date* object named *date* as *java.lang.String*
> object. Thus, *class java.lang.String* is printed in the system's
> console.

 - Type cast will be performed only if type casting is valid as per Java
   standards.
>   ***Example***: `create java.util.Date date as java.lang.Integer` is
> invalid.

 - Casting to java.lang.String performs Object’s *toString* operation.
>  ***Example***: `call java.lang.System.out.println (create java.util.Date
> date 1479547474671 as java.lang.String)` will cast the created
> *java.util.Date* object named *date* to *java.lang.String* and will print *Sat Nov 19 14:54:34 IST 2016* in the system's console.

**ARRAYS:**

 - Arrays can be created with RJS.
>  ***Example***: `create java.lang.Integer array=1:2:3:4:5 and loop
> &array<> {call java.lang.System.out.println &array<#>}` will create an
> *java.lang.Integer* array with the values *1, 2, 3, 4, 5*. The array values are to be separated by the symbol ***:***. This will print the
> integers *1, 2, 3, 4, 5* in the system's console.

 - A particular index of an array can be accessed using the <index> braces.
> ***Example***: `call java.lang.System.out.println myArray<2>` will print the value at index number *2* in the array object *myArray*.

**VAR:**

 - Used to declare a variable or a function.
>  ***Example***: `var myVar = 5` will create a RJS variable named myVar
> with the value *5*.  `var myFunction = {call
> java.lang.System.out.println "Hello World!"}` will create a RJS
> function named *myFunction* which when called will print the string
> *"Hello World"* in the system console.

 - Can declare an array of variables and functions.
>  ***Example***: `var myRJSArray = "Sanjeevi":10:{compute 10+3}`

 - RJS Functions supports polymorphism.
>  ***Example***: `var myRJSFunction = {call
> java.lang.System.out.println "Hello World"}, myRJSFunction = <@
> java.lang.Integer num @> {call java.lang.System.out.println $num}`
> creates two functions named myRJSFunction. The first method accepts no
> parameters whereas the second method accepts one integer parameter.
> `call &myRJSFunction` will print the string *"Hello World"* in the
> system console. `call &myRJSFunction 3` will print the integer *3* in
> the system console.

 - It is type independent.
>  ***Example***: `var myVariable=123` will initialize the variable
> named *myVariable* with the integer *123*. `assign (create
> java.util.Date date) to &myVariable and call
> java.lang.System.out.println &myVariable` will create a new
> *java.util.Date* object named *date* and it is assigned to the RJS variable named *myVariable*. So the current date is printed in the
> system console.

 - A var array can consist of both variables and functions.
>  ***Example***: `var myRJSArray = "Sanjeevi":10:{compute 10+3}` is
> valid. But `var myRJSArray = "Sanjeevi":10:{call
> java.lang.System.out.println (compute 10+3)}` is **invalid** because
> the function declared in the RJS array does not return a value. All
> RJS array functions should return a value.

 - It is auto type casted to it’s assigned value’s type.
>  ***Example***: `var myVariable=123 and call
> java.lang.System.out.println (call &myVariable.getClass)` will print
> *class java.lang.Integer* in the system console. `var myVariable=123 and assign (create java.util.Date date) to &myVariable and call
> java.lang.System.out.println (call &myVariable.getClass)` will print
> *class java.util.Date* in the system console.

**INTERFACE:**

 - Objects for interfaces can be created through RJS. Interface object creation in RJS is similar to RJS Class declaration.
>  ***Example***: `create android.view.View$OnClickListener onClickListener <? onClick <@ android.view.View view @> {call java.lang.System.out.println "Hello World"} ?>` will create an object for the interface *android.view.View.OnClickListener*. The method named *onClick(android.view.View)* for the interface *android.view.View.OnClickListener* is overridden with RJS code which would print the string *"Hello World"* on the system console whenever the click event happens for that view in this example.

 - All abstract methods of an interface need not mandatorily be overrided while creating an object for the interface.
> ***Example***: `create java.util.List list <? size {call java.lang.System.out.println 0} ?>` will create an object for the interface *java.util.List* but overrides only one method named *size()*. However, note that other method calls will fail except for the overridden methods for that interface as other methods have not been defined.
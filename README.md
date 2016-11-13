                ## RealJavaScript Commands

	**CALL:**
		Used to call a method.
		Can call methods with parameters too.
		Returns object if the method returns anything.
		Can call an object's method or a static method too.
		Supports polymorphism.
		Negate function available. Used to negate the return value.
		Parameters can be variables, objects and direct numbers or strings too.

	**COMPUTE:**
		Used to perform mathematical calculations.
		Supports passing variables and direct numbers as parameters.
		Performs calculation using BODMAS technic.
		Can calculate more than 2 variables/numbers at the same time.
		Supports Addition, Subtraction, Multiplication and Division.
	
	**TO:**
		Used to set the result of an operation to a variable or object.

	**ASSIGN:**
		Used to assign a value (variable or object or direct number/string) to another variable or object.

	**CREATE:**
		Used to create an object.
		Can create local objects and other class' objects as well.
		Created object can be assigned to other objects too.
		Created objects can also be accessed within RJS script.
		Constructor polymorphism supported.
	
	**IF, THEN, ELSE:**
		Used to perform branching executions.
		Else part is not mandatory.
		
	**this:**
		The object passed to RJSBridge is used for operations.

	**CLASS:**
		Used to create an RJSClass.
		RJSClass can have it's own fields.
		Fields can be pre assigned.
		RJSClass can have it's own methods.
		Methods can have arguments.
		Methods will return if it has anything to return.
		Objects can be created for RJSClasses.
		Methods support polymorphism.

	**LOOP:**
		Used to iterate over a array.
		Used to iterate over a given number range.
		Can loop by stride.
		Reverse looping is also supported.
		Ranged looping is also supported.

	**IS:**
		Used to perform boolean operations.
		Supports Equal To, Not Equal To, Greater Than, Lesser Than, Greater Than or Equal To, Lesser Than or Equal To.

	**AS:**
		Used to perform type casting.
		Type cast will be performed only if type casting is valid as per Java standards.
		Casting to String performs Object’s toString operation.

	**VAR:**
		Used to declare a variable or a function.
		Can declare an array of variables and functions.
		It is type independant.
		A var array can consist of both variables and functions.
		It is auto type casted to it’s assigned value’s type.

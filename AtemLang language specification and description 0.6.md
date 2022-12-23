
# AtemLang language specification and description

AtemLang is a semi interpretive language running on the JVM platform. It is a dynamic, flexible, and interesting programming language. It is easy to call java libraries and is simpler to use than Lisp. Its name comes from the reverse order of meta。

source code website:https://github.com/TKT2016/AtemLang

## example HelloWorld

```java  {.line-numbers}
package examples.stdsamples;
require atem.lang.Core;
import java.lang;
// show Hello
println "Hello World! ";

// function reference
var println2 = System.out.println;

// lambda
var hello = { println2 "Hello:"+$1 +",Waclome to "+$2 };
hello "Jack" 2088;

// macro (define my for each)
macro my_for_each (item) in (list) (body)
{
    var count = list.count();
    var i=0;
    while(i<count)
    {
        item = list.get.(i);
        body();
        i=i+1;
    }
}

my_for_each (var item) in [1,3,5]{
        println ("element = "+item);
} ;

```
running output
```
Hello World! 
Hello:Jack,Waclome to 2088
element = 1
element = 3
element = 5
```

## keywords

```java  {.line-numbers}
import require if else package return while true false null function macro var break
```

special identifier

```java  {.line-numbers}
new default self
```
## comment

Single line comment: the contents after double slash * *//* * will be ignored.
Multiline comments: start with/* and end with */.
comment is same as Java 。

```java  {.line-numbers}
// Single line comment

/* 
	Multiline comments 
*/
```
## identifier
Atem case sensitive。

## package
The format is package <package name>, which defines the package name of the current source file. The package statement must be written at the beginning of the source file.
It's same as Java。

```java  {.line-numbers}
package examples.stdsamples;
```


## import

The format is import <package name>. All public classes in the package are imported. The import statement must be written after the package statement.
All types in the Java language import package need to have a ". *" at the end, but not AtemLang.

```java  {.line-numbers}
import java.lang;
```

## require

The format is require <type name>. All public static programs of this type and this type are imported. The require statement must be written after the package statement.

Similar to Java import statements "import static ...";

```java  {.line-numbers}
require atem.lang.Core;
```


## Literal quantity and data type

In programming languages, const values are generally called literals, such as 3.14.

### 布尔（Boolean）

Corresponding boolean(java.lang.Boolean)。

```java  {.line-numbers}
true
false
```

###  整数（Integer）

Corresponding 32-bit integer(java.lang.Integer)。

```java  {.line-numbers}
1200
```

###  浮点数（Float）

对应32位浮点数(java.lang.Float).
```java  {.line-numbers}
3.14
```

###  字符串（String）
Corresponding String class(java.lang.String)。
```java  {.line-numbers}
"Hello"
```

### 列表（List） 

对应类List(atem.lang.List)。

```java  {.line-numbers}
[40, 100, 1, 5, 25, 10]
```

### Map（Map） 

Corresponding Map class(atem.lang.Map).

```java  {.line-numbers}
[1:"A",2:"B"]
```

### 动态对象 (Dynamic)

Corresponding Dynamic class (atem.lang.Dynamic).

```java  {.line-numbers}
{name:"John", age:18, eyeColor:"blue"}
```

### null 

```java  {.line-numbers}
null
```

## 运算符

The operators of Atem include add, sub, mul and div, compare operator and logical operator.

**运算符**

| 符号 | 意义                                |  
| ---- | ----------------------------------- |
| +    | add     | 
| -    | sub    |
| *    | mul | 
| /    | div   |
| >    | gt  |
| >=   | ge            | 
| <    | lt          | 
| <=   | le                  | 
| ==   | eq                     | 
| !=   | neq                         | 
| &&   | and                    | 
| &#124;&#124;   | or                       | 
| !   | not                        | 

In order to simplify the language, there are no operators such as ++, --,+=  in the AtemLang .

```java  {.line-numbers}
package examples.stdsamples;
require atem.lang.Core;

println (3+4 );
println (3 -4 );
println (3*4 );
println (3 /4 );
println (3.0 /4 );

println (3> 4 );
println (3>= 4 );
println (3< 4 );
println (3<= 4 );
println (3 == 4 );
println (3 != 4 );

println ( true && false );
println ( true || false );
println ( !true );
```

## Variable

### Variable declaration assignment 
Variables are "containers" for storing information. Declare the variable in the format of var  variable name , and then assign the initial value with the equal sign. If the initial value is not assigned, the variable value is null.

```java  {.line-numbers}
package examples.stdsamples;
require atem.lang.Core;

var a="Hello";
println a;
var b;
println (b==null);
```
A statement can only declare one variable.

### Global variable
Variables declared outside the function in the source file are global variables.

### Local variable
Variables declared within a function or macro are local variables and can only be used within this function or macro.

### Macro statement variable
Variables defined in macro call statements are macro statement variables, which can only be used by functions or lambda expressions in this statement.

## String

 **General usage**

```java  {.line-numbers}
var area = "Xi Jiang";
```
**Get a character**
Use charAt to access a character in a string. The index of the string starts from 0, which means that the index value of the first character is 0, the second is 1, and so on.

```java  {.line-numbers}
var ch = area.charAt(6);
```

**Escape character**

```java  {.line-numbers}
var str= "A\bB\fC\nD\rF\tE\\G\'H\"I\0JKL";
println str;
```
**Escape character table**
| character | meaning                                | ASCII Code |
| -------- | ----------------------------------- | ------------------- |
| \b       | BackSpace(BS)     | 008                 |
| \f       | Page change(FF)    | 012                 |
| \n       | Line change(LF)  | 010                 |
| \r       | Enter (CR)    | 013                 |
| \t       | TAB (HT)   | 009                 |
| \\       | ''\'              | 092                 |
| \'       | '          | 039                 |
| \"       | "                  | 034                 |
| \0       | Null character (NUL)                     | 000                 |

**String length**
You can use the length method to calculate the length of a string.
```java  {.line-numbers}
println (str.length ());
```

## AtemObject class
AtmeObject is the base class of the atem related classes. It only defines a read-only prototype. Dynamic and List in atem.lang package inherit it.

## Statement

The statement ends with a semicolon.

```java  {.line-numbers}
x = 5 + 6;
y = x * 10;
```

### Conditional statement

The following conditional statements can be used:
- Use 'if' to specify the block of code to execute if the condition is true;
- Use 'else' to specify which block of code to execute if the same condition is false;
- Use 'else if' to specify the new condition to be tested if the first condition is false;

Note: The AtemLang language does not have a switch statement.

```java  {.line-numbers}
package examples.stdsamples;
require atem.lang.Core;

var time =16;

if (time < 18) {
    println  "Good day";
}

if (time < 10) {
    println  "Good day";
 } else {
    println  "Good evening";
 }

 if (time < 10) {
     println  "Good morning";
  } else if (time < 18) {
     println  "Good day";
  } else {
     println  "Good evening";
  }
```

### Loop statement

AtemLang has only the while statement for its loop.

#### While循环

```java  {.line-numbers}
package examples.stdsamples;
require atem.lang.Core;

var i=0;
while (i < 10) {
    println  ("数字是 " + i );
    i=i+1;
}
```

#### forloop

The forloop macro is defined in Core, mimicking the forloop in other languages.
The format used is:
```java  {.line-numbers}
forloop Initializes statement 
condition function 
updates function loop 
body function ;
```
When used, the initialization statement is a single statement, all surrounded by parentheses. The following are functions, usually lambda function expressions.
Because forloop is a macro, when it is called, the declared variable scope is inside the statement.
The example used is this:
```java  {.line-numbers}
forloop (var i=0) {i<8} {i=i+1}{
    println("i="+i);
};
```

### Break out of the loop (break)

```java  {.line-numbers}
var k=0;
while ( k< 10) {
    println  ("数字 k= " + k );
    k=k+1;
    if(k==5)
        break;
}
```

You can also use break in the forloop macro to break out of the loop.
```java  {.line-numbers}
forloop (var i=0) {i<8} {i=i+1}{
    println("i="+i);
    if(i==6)
        break;
};
```

**Note:** AtemLang language does not continue.


## Type and member and variable assignment

```java  {.line-numbers}
package examples.stdsamples;
require atem.lang.Core;
import java.lang;
import javax.swing;

var sys = System;
println sys;

var err = sys.err;
println err;
err.println "err println";

var errshow = err.println;
println errshow;
errshow "errshow";

var newFrame = JFrame.new;

var frame1 = newFrame ();
frame1.setBounds 600 600 300 200;
frame1.setTitle "JFrame 1";
frame1.setDefaultCloseOperation WindowConstants.HIDE_ON_CLOSE;
frame1.setVisible true ;

var frame2 = newFrame "JFrame 2";
frame2.setBounds 300 300 300 300;
frame2.setDefaultCloseOperation WindowConstants.EXIT_ON_CLOSE;
frame2.setVisible true;
```

### A variable is assigned as a type

```java  {.line-numbers}
var sys = System;
println sys;
```

### A variable is assigned as a field

```java  {.line-numbers}
var err = sys.err;
println err;
```

###  A variable is assigned as a function

```java  {.line-numbers}
var errshow = err.println;
println errshow;
```

### A variable is assigned as a constructor
The constructor has no function name. The Atem language uses new to indicate the constructor and < type name >.new to get the constructor.

```java  {.line-numbers}
var newFrame = JFrame.new;
```

## Function
Functions are made up of reusable blocks of code.

### Function definition
The definition format begins with the keyword function, followed by the function name, and so on.

```java  {.line-numbers}
function *functionname*(argument1,argument2....)
{
  *// 执行代码*
}
```
When the function is called, the code inside the function is executed.

### return
A return statement is always used in a function, either to return a value of the type specified by the method (which is always determined) or to end the execution of the method (just a return statement).

**Arguments are value passing, and changing the value of an argument within a function does not affect the actual value of the original argument.**

#### No return value
```java  {.line-numbers}
function noReturn(a)
{
   if(a>100)
   	return;
  println a;
}
noReturn 10;
```

#### A function that returns a value
Sometimes, we want a function to return a value to a value.
When a return statement is used, the function stops execution and returns the specified value.
**example**
Calculate the product of two numbers and return the result:
```java  {.line-numbers}
function myFunction2(a,b)
{
  return a*b;
}
println ( myFunction2 3 4);
```

### Function call
When a function is called, you can pass values to it, which are called arguments.
These arguments can be used in functions. Variables and parameters must appear in a consistent order. The first variable is the given value of the first parameter passed, and so on.

```java  {.line-numbers}
package examples.stdsamples;
require atem.lang.Core;
require atem.lang.SimpleDialogs;

function myFunction(name,job){
    alert("Welcome " + name + ", the " + job);
}

myFunction("Harry Potter","Wizard");
```
This example will show "Welcome Harry Potter, the Wizard" 。


#### Call a function with arguments
There are two ways to call a function, the S-expression call and the dot parentheses call. The first form is natural and simple, and the second is compact and suitable for chain calls.

**1 Called as an s-expression**
The first is the name of the function, followed by an argument, separated by a space, in the format
```java  {.line-numbers}
myFunction argument1 argument2 ...
```
Such as the myFunction2
```java  {.line-numbers}
myFunction2 3 4
```

**2 Call as dot parentheses**
The first is the function name, followed by a period, followed by parentheses containing arguments separated by commas.
```java  {.line-numbers}
myFunction .( argument1 , argument2 ...)
```
Such as the myFunction2
```java  {.line-numbers}
myFunction2.(3 ,4)
```

You can pass as many arguments as you want, separated by commas (,) :
```
myFunction(*argument1,argument2*)
```
When you declare functions, declare arguments as variables:

```java  {.line-numbers}
function myFunction(var1,var2)
{
*代码*
}
```

#### A call to a function without arguments
Parameterless function calls are special and follow the two forms described above. The second method also serves to keep the call compact.

**1 Called as an s-expression**
The first is the name of the function, followed by a pair of parentheses, in the format
```java  {.line-numbers}
myFunction ()
```

**2 Call as dot parentheses**
The first is the function name, followed by a period, followed by a pair of parentheses.
```java  {.line-numbers}
myFunction .()
```

### self function
The name of the first argument to a function is self, which is always self function.

### The AtemObject member is assigned to self function
When an AtmeObject instance calls a member after it has been assigned to self, the first argument is automatically assigned to that instance. When you pass an argument using this member, the first argument, self, is not passed.

```java  {.line-numbers}
package examples.stdsamples;
require atem.lang.Core;

var dynamic1 ={ name:"Tom" };

function ShowName(self)
{
    println self.name;
}
ShowName(dynamic1);

dynamic1.show = ShowName;
dynamic1.show ();
```

## Lamda expression

A Lambda expression, also known as a closure, is actually an anonymous function. Using Lambda expressions makes your code more compact.
A Lambda can be defined with a pair of braces.

### Parameterless Lambda

```java  {.line-numbers}
 var lambda1 ={ println "Hello" };
 lambda1 ();
```

### Lambda default return
When there is only one expression in the body of a Lambda function, then you do not need to use return; the expression is returned by default.
```java  {.line-numbers}
 var lambda2 ={ 990 };
 println (lambda2 ());
```

### Lambda with arguments
In the body of a Lambda function, the character is "$", or an identifier beginning with "$", or a number beginning with "$" is a Lambda argument. Calls are made in the order sorted by argument. For example, defining $a,$b and $a,$c are actually the same.

```java  {.line-numbers}
 var lambda3 ={ println $ };
 lambda3 "HELLO";

 var lambda4 ={ println $2 };
 lambda4 "GOOD";

 var lambda5 ={ println $a+$b };
 lambda5 "A" "B";

 var lambda6 ={ println $a+$c };
 lambda6 "A" "B";
```

If there is a return statement in the Lambda defined in the macro call statement, the return of the function in which the macro call is made is raised.


## Macro
Macro format is more flexible, using macros can be very convenient to extend the syntax structure.

### Definition format
The definition format begins with the keyword macro, followed by the macro name, and can be followed by either a constituent identifier or a parenthesis symbol. The following identifier is part of the macro, and the parenthes-containing identifier is the macro's argument. A return statement in the body of a macro method cannot have a return value.

```java  {.line-numbers}
macro MacroName id1|(param1) ...
{
  // 执行代码
}
```

The format of the call and defined macros is the same, the identifiers are the same, but the arguments are replaced with arguments.

example:Define your own if-then

```java  {.line-numbers}
package examples.stdsamples;
require atem.lang.Core;

macro myif (condition) then (thenbody)
{
    if(condition ())
    {
        thenbody();
    }
}

myif (2>1) then { println "2>1" };
```
As shown above, a myif macro is defined, with condition and thenbody as arguments to the macro.

### Macro call
```java  {.line-numbers}
myif (2>1) then { println "2>1" };
```
The call, which replaces condition and thenbody with actual arguments, takes the form of a statement.

### Macro calls the return statement of the argument Lambda expression
If you define a macro with an executable parameter, the macro method body implements the call to this executable parameter.
There is another in-function implementation that calls the macro statement and implements a Lambda expression as an argument to the argument that contains a return statement.

After executing the return statement to the Lambda expression argument, the program returns not only from the macro, but also from the function where the macro call statement resides. If the return statement has a return value, the return value is also the return value of the function where the macro call statement resides.

**example** choose
Part 1 defines a macro choose;
Part 2 defines a function called test_choose;
Part 3 implements a call function statement.
The output is
```
test_choose return resut is 10
10
```
As you can see, in the return statement in the choose of the function test_choose, the return value is also the return value of the function test_choose.

```java  {.line-numbers}
macro choose (condition) then  (thenbody) that (thatbody)
{
     if(condition ())
     {
        thenbody();
     }
    else
    {
        thatbody();
    }
}

function test_choose(value )
{
     choose(value>0) then
     {
            println ("test_choose return resut is"+value);
            return value;
     }
     that
      {
           println ("test_choose return resut is0");
            return 0;
      };
}

println ( test_choose  (10));

```
### Macro calls a real parameter

The variable used in the macro call is passed by reference, and if the macro changes its value, the value of the variable will change after the macro call is complete.
**Arguments are passed by reference, and modifying the value of an argument within a function affects the actual value of the original argument .**

**example:changeValue**
```java  {.line-numbers}
macro macro_changeValue (a) with (b)
{
    a=b;
}

function function_changeValue (a,b)
{
    a=b;
}

var var1 =100;
println ("step 1 var1="+var1);
macro_changeValue var1 with 990;
println ("step 2 var1="+var1);
function_changeValue  var1 800;
println ("step 3 var1="+var1);
```
runnig output:
```
step 1 var1=100
step 2 var1=990
step 3 var1=990
```
As you can see, macro calls change the value of the variable, function calls do not.

### Variable arguments declared by the macro call
Variable arguments declared by the macro call
**example**
```java  {.line-numbers}
macro eachLoop (item) in (list) (body)
{
    var count = list.count();
    var i=0;
    while(i<count)
    {
        item = list.get.(i);
        body();
        i=i+1;
    }
}

eachLoop (var item) in [1,3,5,7,9]{
        println ("element = "+item);
} ;

//println item;
```
The item declared in the eachLoop call statement can only be used in the current statement. If you uncomment the last statement, an error will be reported because the item use returns the statement in which the macro call has been exceeded.


## Exception handling

Exception handling is not a core part of the Atem statement, but is implemented with several macros. Exception handling is defined in three macros in atem.lang.Core:throw,try catch,try catch finally。
**example**
```java  {.line-numbers}
package examples.stdsamples;
require atem.lang.Core;
import java.lang;

try {
    println "step1";
     var ex1 = Exception.new "Test Throw Exception 1";
     throw ex1;
     println "step2";
}
catch
{
    println "catch Exception";
}
;

try {
    println "step1";
     throw (Exception.new "Test Throw Exception 2");
     println "step2";
}
catch (var e)
{
    println (e.getMessage());
}
;

try {
    println "step1";
     throw ( Exception.new "Test Throw Exception 3");
     println "step2";
}
catch (var e)
{
    println (e.getMessage());
}
finally
{
    println "finally ";
}
;

try {
    println "step1";
     throw (Exception.new "Test Throw Exception 4");
     println "step2";
}
catch
{
   println "catch Exception 4";
}
finally
{
    println "finally 2";
}
;
```

### Throw exception

throw the exception with the Throw macro. The variable to throw must be an instance of java.lang.Exception or a subclass of it.

### Catch exception
catch exceptions and handle try catches. Unlike java, you can only have 1 catch section.

### finally
The finally keyword is used to create a block of code to execute after the try block.
The code in the finally block is always executed regardless of whether an exception occurs.
In a finally block, you can run end-of-life statements such as cleanup types.
The finally block appears at the end of the catch block.

## Other important methods
### These are in the Core Class.

```java  {.line-numbers}
println  (  "STRING".getClass ()  );
println (isInstanceof  "STRING"  String);
println (isUndefined   "STRING"  );
println (isNull    "STRING"  );
println (isNotNull     "STRING"  );
```

### getClass (Gets the type of the object)
We're using the getClass method that java object instances have.
```java  {.line-numbers}
println  (  "STRING".getClass ()  );
```

### isInstanceof (Whether to inherit from)
It works the same as the java keyword instanceof.

```java  {.line-numbers}
println (isInstanceof "STRING"  String);
```

### isUndefined (Undefined or not)

```java  {.line-numbers}
println (isUndefined "STRING");
```

### isNull (null or not)

```java  {.line-numbers}
println (isNull "STRING");
```

### isNotNull 不为null
```java  {.line-numbers}
println (isNotNull "STRING");
```

## List
List class is a special type that can store more than one value at a time.

### Initializer list

```java  {.line-numbers}
package examples.stdsamples;
require atem.lang.Core;

var emptys =[];
var cars = ["Saab", "Volvo", "BMW"];
```

### Gets the element by index number
Reference a list element by referring to the * index number *, 0 for the first in the list, 1 for the second in the list, and so on.

```java  {.line-numbers}
println ( cars.get 0);
```

### Change list elements by index number

```java  {.line-numbers}
cars.set  1 "Opel";
println ( cars.get 1);
```

###  Number of list elements
Use the count method to get the number

```java  {.line-numbers}
println (cars.count ());
```

### Add element
Use the add method to add a new element at the end of the list, which increases the number of elements by one.

```java  {.line-numbers}
cars.add "DaZhong";
println (cars.count ());
```

### Iterate through the list with the foreach macro
There are two foreach macros defined in the Core class. The main difference is that the second one has an index part, from which you can get the index number of the current loop.

```java  {.line-numbers}
foreach(var ele) in cars 
{ 
    println ele
};

foreach(var ele) in cars index (var index)
{ 
    println ("the "+index+" is "+ ele)
};

```

## Map
Map class is a collection of key-value pairs. Each element in the Map contains a key object and a value object. Where key objects are not allowed to be duplicated, value objects can be duplicated, and value objects can also be of type Map, just as elements in an array can also be arrays.

### Initialize Map

#### Create a Map without default values

```java  {.line-numbers}
var mapNoDefault =[ "A":100,"B":200 ];
```

#### Create a Map with default values
The default value is used to return the default value if the Map does not contain a key. default key values are represented by the default key.

```java  {.line-numbers}
var map =[ "A":100,"B":200,default:-1];
```
#### Create an empty Map
```java  {.line-numbers}
var emptyMap = Map.new () ;
```

### Map correlation method

| Method description     |       Method name | 
| :----------- | :------------- | 
| Whether there is a default value |   hasDefault() |  
| Get the default value   | defaultValue() |   
| Set key   | set( key, value) |   
| Get the value with the key   | get(key) |   
| Whether it contains a bond   | contains(key) |   
| Elements count   |  count() |   
| A list of all the keys   | keys() |   
| Remove key values with keys   |   remove(key) |   
| Clear elements   |  clear() |   

**example**
```java  {.line-numbers}
var opfns=[
    "+":{$1+$2} ,
    "-":{$1-$2} ,
    "*":{$1*$2} ,
    default:{$1/$2}
];
println( opfns.get.("&&") 100 4 );
```

## Dynamic class

```java  {.line-numbers}
package examples.stdsamples;
import atem.lang;
require atem.lang.Core;

//Defines a class that contains the attributes name and age
var d1 ={ name:"Tom" ,age:10 };
//The name of d1 is displayed
println d1.name;
//Displays the age of d1
println d1.age;
//Displays high for d1 and returns undefined because this property was not added
println d1.high;
//Add a new property directly with the assignment expression
d1.country="JiaLeBi";
println d1.country;

// Define a dynamic class without attributes
var d2 = Dynamic.new ();
println d2;
d2.name = "Jan";
println d2.name;
```

### Defines a dynamic class whose class contains attributes

```java  {.line-numbers}
var d1 ={ name:"Tom" ,age:10 };
```

### Add a new property and get the property

```java  {.line-numbers}
d1.country="JiaLeBi";
println d1.country;
```

### Define a dynamic class without attributes

```java  {.line-numbers}
var d2 = Dynamic.new ();
```


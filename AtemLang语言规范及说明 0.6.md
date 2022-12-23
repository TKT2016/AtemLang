

# AtemLang语言规范及说明

AtemLang是运行在JVM平台上的半解释性的语言，它是动态的、灵活的、有趣的编程语言，易于调用java库，比Lisp使用起来更简单。它的名字来源 meta的倒序。

开源网址:https://github.com/TKT2016/AtemLang

## 例子 HelloWorld

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
运行输出
```
Hello World! 
Hello:Jack,Waclome to 2088
element = 1
element = 3
element = 5
```

## 关键字

```java  {.line-numbers}
import require if else package return while true false null function macro var break
```

特殊标识符

```java  {.line-numbers}
new default self
```
## 注释

单行注释: 以双斜杠 **//** 后的内容将会被忽略。
多行注释:以 /* 开始 ,以 */ 结束。
注释和 Java一样。

```java  {.line-numbers}
// 单行注释

/* 
	多行注释 
*/
```
## 标识符
Atem 对大小写是敏感的。

## package语句
格式是 package <包名称>，定义当前源文件的包名称。package语句必须写在源文件开头。
这个和 Java语言一样。

```java  {.line-numbers}
package examples.stdsamples;
```


## import 语句

格式是 import  <包名称>，导入的是这个包内所有的公共类。import语句必须写在package语句后面。

Java语言导入包内所有类型需要末尾是".*"，Atem不需要。

```java  {.line-numbers}
import java.lang;
```

## require 语句

格式是 require  <类型名称>，导入的是这个类型的所有公共的静态的程序以及这个类型。require 语句必须写在package语句后面。

类似于 Java 导入语句 "import static ...";

```java  {.line-numbers}
require atem.lang.Core;
```


## 字面量和数据类型

在编程语言中，一般固定值称为字面量，如 3.14。

### 布尔（Boolean）

对应boolean类型(java.lang.Boolean)。

```java  {.line-numbers}
true
false
```

###  整数（Integer）

对应32位整数(java.lang.Integer)。

```java  {.line-numbers}
1200
```

###  浮点数（Float）

对应32位浮点数(java.lang.Float).

```java  {.line-numbers}
3.14
```

###  字符串（String）

对应String类(java.lang.String)。

```java  {.line-numbers}
"Hello"
```

### 列表（List） 

对应类List(atem.lang.List)。

```java  {.line-numbers}
[40, 100, 1, 5, 25, 10]
```

### Map（Map） 

对应类Map(atem.lang.Map).

```java  {.line-numbers}
[1:"A",2:"B"]
```

### 动态对象 (Dynamic)

对应类Dynamic(atem.lang.Dynamic).

```java  {.line-numbers}
{name:"John", age:18, eyeColor:"blue"}
```

### null 

```java  {.line-numbers}
null
```

## 运算符

Atem的运算符只有加减乘除、比较运算符、逻辑运算符。

**运算符**

| 符号 | 意义                                |  
| ---- | ----------------------------------- |
| +    | 加     | 
| -    | 减    |
| *    | 乘 | 
| /    | 除   |
| >    | 大于  |
| >=   | 大于等于            | 
| <    | 小于          | 
| <=   | 小于等于                  | 
| ==   | 等于                     | 
| !=   | 不等于                         | 
| &&   | 与                    | 
| &#124;&#124;   | 或                       | 
| !   | 非                        | 

为了保持语言简洁，Atem语言中没有 ++ ，--， +=  等等这类运算符。

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

## 变量

### 变量声明赋值 
变量是用于存储信息的"容器"。声明变量用 var <变量名称> 格式，后面可以用等于号赋初值，如果不赋初值的话，该变量值为null。

```java  {.line-numbers}
package examples.stdsamples;
require atem.lang.Core;

var a="Hello";
println a;
var b;
println (b==null);
```
一条语句只能声明一个变量。

### 全局变量
在源文件内函数外声明的变量是全局变量。

### 局部变量
在函数或宏内部声明的变量是局部变量，只能在这个函数或者宏内使用。

### 宏语句变量
在宏调用语句内定义的变量是宏语句变量,变量只能由这个语句内的函数或者lambda表达式使用。

## 字符串

 **一般用法**

```java  {.line-numbers}
var area = "Xi Jiang";
```
**获取某个字符**
使用charAt来访问字符串中的某个字符。字符串的索引从 0 开始，这意味着第一个字符索引值为 0,第二个为 1, 以此类推。

```java  {.line-numbers}
var ch = area.charAt(6);
```

**转义符**

```java  {.line-numbers}
var str= "A\bB\fC\nD\rF\tE\\G\'H\"I\0JKL";
println str;
```
**转义符表**
| 转义字符 | 意义                                | ASCII码值（十进制） |
| -------- | ----------------------------------- | ------------------- |
| \b       | 退格(BS) ，将当前位置移到前一列     | 008                 |
| \f       | 换页(FF)，将当前位置移到下页开头    | 012                 |
| \n       | 换行(LF) ，将当前位置移到下一行开头 | 010                 |
| \r       | 回车(CR) ，将当前位置移到本行开头   | 013                 |
| \t       | 水平制表(HT) 跳到下一个TAB位置 | 009                 |
| \\       | 代表一个反斜线字符''\'              | 092                 |
| \'       | 代表一个单引号（撇号）字符          | 039                 |
| \"       | 代表一个双引号字符                  | 034                 |
| \0       | 空字符(NUL)                         | 000                 |

**字符串长度**
可以使用方法length 来计算字符串的长度。
```java  {.line-numbers}
println (str.length ());
```

## AtemObject类
AtmeObject是atem相关类的基类，它只定义了一个只读的prototype。atem.lang包中的Dynamic、List都继承它。

## 语句

语句用分号结尾。

```java  {.line-numbers}
x = 5 + 6;
y = x * 10;
```

### 条件语句

可使用如下条件语句：
- 使用 `if` 来规定要执行的代码块，如果指定条件为 true;
- 使用 `else` 来规定要执行的代码块，如果相同的条件为 false;
- 使用 `else if` 来规定要测试的新条件，如果第一个条件为 false;

注:AtemLang语言没有switch语句。

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

### 循环语句

AtemLang的循环语句只有while语句。

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

Core中定义forloop宏，模仿其它语言的for循环。
使用格式是

```java  {.line-numbers}
forloop 初始化语句 条件函数 更新函数 循环体函数 ;
```
使用的时候，初始化语句是一个语句，所有要用括号包围起来，后面的几个都是函数，一般用lambda函数表达式。
因为forloop是一个宏，所以调用它的时候，声明的变量作用域是在这个语句内。
使用的例子是这样的：
```java  {.line-numbers}
forloop (var i=0) {i<8} {i=i+1}{
    println("i="+i);
};
```


### 跳出循环(break)

```java  {.line-numbers}
var k=0;
while ( k< 10) {
    println  ("数字 k= " + k );
    k=k+1;
    if(k==5)
        break;
}
```

在宏forloop中也可以使用break跳出循环。
```java  {.line-numbers}
forloop (var i=0) {i<8} {i=i+1}{
    println("i="+i);
    if(i==6)
        break;
};
```

**注:** AtemLang语言里没有continue.


## 类型与成员变量赋值

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

### 变量赋值为类型

```java  {.line-numbers}
var sys = System;
println sys;
```

### 变量赋值为字段

```java  {.line-numbers}
var err = sys.err;
println err;
```

### 变量赋值为函数

```java  {.line-numbers}
var errshow = err.println;
println errshow;
```

### 变量赋值为构造函数
构造函数是没有函数名称的，Atem语言用new来指示构造函数，用<类型名称>.new 来获取构造函数。

```java  {.line-numbers}
var newFrame = JFrame.new;
```

## 函数
函数是由可重复使用的代码块。

### 函数定义

定义格式是，前面以关键字  function开头，后面跟着函数名称等。

```java  {.line-numbers}
function *functionname*(argument1,argument2....)
{
  *// 执行代码*
}
```

当调用该函数时，会执行函数内的代码。

### return
return语句总是用在函数中，有两个作用，一个是返回方法指定类型的值（这个值总是确定的），一个是结束方法的执行（仅仅一个return语句）。
**实参是值传递，在函数内修改参数的值不会影响到原参数实际的值。**

#### 无返回值
```java  {.line-numbers}
function noReturn(a)
{
   if(a>100)
   	return;
  println a;
}
noReturn 10;
```

#### 有返回值的函数

有时，我们会希望函数将值返回值。
在使用 return 语句时，函数会停止执行，并返回指定的值。

**实例**

计算两个数字的乘积，并返回结果：

```java  {.line-numbers}
function myFunction2(a,b)
{
  return a*b;
}
println ( myFunction2 3 4);
```

### 函数调用
在调用函数时，您可以向其传递值，这些值被称为参数。
这些参数可以在函数中使用。变量和参数必须以一致的顺序出现。第一个变量就是第一个被传递的参数的给定的值，以此类推。

```java  {.line-numbers}
package examples.stdsamples;
require atem.lang.Core;
require atem.lang.SimpleDialogs;

function myFunction(name,job){
    alert("Welcome " + name + ", the " + job);
}

myFunction("Harry Potter","Wizard");
```

上面的例子会提示 "Welcome Harry Potter, the Wizard" 。


#### 调用有参函数

有两种方法调用函数,S表达式形式调用和点括号形式调用，第一种形式自然简单，第二种紧凑，适合链式调用。

**1 S表达式形式调用**

第一个是函数名，后面跟着参数，前后用空格隔开，格式是
```java  {.line-numbers}
myFunction argument1 argument2 ...
```
比如上面的myFunction2
```java  {.line-numbers}
myFunction2 3 4
```

**2 点括号形式调用**

第一个是函数名，后面跟着点号，再后面跟着括号，括号内是若干参数，参数以逗号隔开。
```java  {.line-numbers}
myFunction .( argument1 , argument2 ...)
```
比如上面的myFunction2
```java  {.line-numbers}
myFunction2.(3 ,4)
```

您可以传递任意多的参数，由逗号 (,) 分隔：
```
myFunction(*argument1,argument2*)
```
当您声明函数时，请把参数作为变量来声明：

```java  {.line-numbers}
function myFunction(var1,var2)
{
*代码*
}
```

#### 无参函数的调用
无参函数调用比较特殊，按照上述两种形式调用，第二种方式作用同样是让调用紧凑。

**1 S表达式形式调用**

第一个是函数名，后面跟着一对括号，格式是
```java  {.line-numbers}
myFunction ()
```


**2 点括号形式调用**
第一个是函数名，后面跟着点号，再后面跟着一对括号。
```java  {.line-numbers}
myFunction .()
```

### self函数
函数的第一个参数的名称是self的，都是self函数。

### AtemObject成员赋值为self函数

某个AtmeObject实例在某个成员赋值为self函数后再调用这个成员时，第一个参数会自动赋值为这个实例,掉用这个成员传参时，第一个参数self不需要传递。

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


## Lamda表达式

Lambda表达式，也可称为闭包，实际上它是一个匿名的函数。使用Lambda表达式可以使代码变的更加简洁紧凑。

用一对大括号就可以定义一个Lambda。

### 无参Lambda

```java  {.line-numbers}
 var lambda1 ={ println "Hello" };
 lambda1 ();
```

### Lambda默认返回
Lambda函数体只有一个表达式时，那么不需要用return，默认返回这个表达式。
```java  {.line-numbers}
 var lambda2 ={ 990 };
 println (lambda2 ());
```

### 带参数的Lambda

在Lambda函数体中，字符为"$"，或者以"$"开头的标识符，或者以"$"开头的数字是Lambda的参数。调用时的顺序按按参数排序后的顺序。例如定义 $a,$b 和$a,$c实际是一样的。

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

在macro调用语句中定义的Lambda中如果有return语句，则会引发macro调用所在函数的return。


## 宏
宏的格式比较灵活，利用宏可以很方便的扩展语法结构。

### 定义格式
定义格式是，前面以关键字  macro 开头，后面跟着macro名称，之后可以跟着若个组成标识符或者括号加表示符号。后面的标识符是宏的组成部分，括号包含标识符则是宏的参数。在宏方法体中的return语句不能有返回值。

```java  {.line-numbers}
macro MacroName id1|(param1) ...
{
  // 执行代码
}
```

调用和定义的宏的格式是一样的，标识符相同，但是参数要换成实参。

例子:定义自己的if-then

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

如上所示，定义了一个myif宏，condition和thenbody都是宏的参数。
### 宏调用
```java  {.line-numbers}
myif (2>1) then { println "2>1" };
```
调用的时候要把condition和thenbody替换成实际参数，调用的形式像一条语句。

### 宏调用实参Lambda表达式的return语句
假如定义了一个宏，宏带有一个可执行的参数，宏方法体内实现对这个可执行参数的调用。
又有一个函数内实现调用了这个宏的语句，并且实现了一个Lambda表达式作为这个参数的实参，这个Lambda表达式存在return语句。
程序在执行到这个Lambda表达式实参的的return语句后，程序不止从宏返回，而且会从这个宏调用语句所在的函数返回；如果return语句有返回值，返回值也是宏调用语句所在函数的返回值。
**实例** choose
第1部分定义了一个宏choose;
第2部分定义了一个函数test_choose;
第3部分实现了一个调用函数语句.
输出结果是
```
test_choose返回值是10
10
```
可以看到，在函数test_choose中的choose里的return语句，返回值也作为函数test_choose的返回值。
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
            println ("test_choose返回值是"+value);
            return value;
     }
     that
      {
           println ("test_choose返回值是0");
            return 0;
      };
}

println ( test_choose  (10));

```
### 宏调用实参变量

在宏调用中使用的变量是引用传递，如果宏修改了它的值，则在宏调用完成后变量的值会改变。
**实参是引用传递，在函数内修改参数的值会影响到原参数实际的值。**

**实例:changeValue**
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
执行后输出:
```
step 1 var1=100
step 2 var1=990
step 3 var1=990
```
可以看出宏调用会改变变量的值，函数调用不会。
### 宏调用声明的变量实参
如果在宏调用语句中声明一个变量作为实参，这个变量的作用域只能在这条语句内。
**实例**
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
在eachLoop调用语句中声明的item只能在当前语句中使用，如果取消注释最后一条语句，则会编译报错，因为item使用返回已经超出的宏调用所在语句。


## 异常处理

异常处理不是Atem语句的核心部分，而是用几个宏来实现。异常处理都定义在atem.lang.Core中的三个宏中:throw,try catch,try catch finally。
**实例**
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

### 抛出异常

抛出异常用throw宏。throw的变量必须是java.lang.Exception或者它的子类的实例。

### 捕捉异常

捕捉异常并处理用try catch。catch部分只能有1个，这和java不同。

### finally

finally 关键字用来创建在 try 代码块后面执行的代码块。
无论是否发生异常，finally 代码块中的代码总会被执行。
在 finally 代码块中，可以运行清理类型等收尾善后性质的语句。
finally 代码块出现在 catch 代码块最后。

## 其它重要方法
### 这几个在Core类中。

```java  {.line-numbers}
println  (  "STRING".getClass ()  );
println (isInstanceof  "STRING"  String);
println (isUndefined   "STRING"  );
println (isNull    "STRING"  );
println (isNotNull     "STRING"  );
```

### getClass 获取对象的类型
这里用的还是java对象实例都有有的getClass方法。
```java  {.line-numbers}
println  (  "STRING".getClass ()  );
```



### isInstanceof 是否继承于
和java关键字instanceof作用一致。

```java  {.line-numbers}
println (isInstanceof "STRING"  String);
```

### isUndefined 是否未定义

```java  {.line-numbers}
println (isUndefined "STRING");
```

### isNull 是否为null

```java  {.line-numbers}
println (isNull "STRING");
```

### isNotNull 不为null
```java  {.line-numbers}
println (isNotNull "STRING");
```

## List

列表是一种特殊的类型，它能够一次存放一个以上的值。

### 初始化列表

```java  {.line-numbers}
package examples.stdsamples;
require atem.lang.Core;

var emptys =[];
var cars = ["Saab", "Volvo", "BMW"];
```

### 通过索引号获取元素

通过引用*索引号*来引用某个数组元素，0对应列表中第一个，1对应列表中第二个，其它以此类推。

```java  {.line-numbers}
println ( cars.get 0);
```

### 通过索引号改变列表元素

```java  {.line-numbers}
cars.set  1 "Opel";
println ( cars.get 1);
```

###  列表元素个数

用count方法获取个数

```java  {.line-numbers}
println (cars.count ());
```

### 添加元素

用add方法在列表末尾添加新元素，添加后元素个数也会增加1个。

```java  {.line-numbers}
cars.add "DaZhong";
println (cars.count ());
```

### 用foreach宏遍历列表

在Core类中定义了两个foreach宏，主要区别是第二个多了index部分，可以从index获取当前循环的索引号。

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

Map 是一种键-值对（key-value）集合，Map 集合中的每一个元素都包含一个键对象和一个值对象。其中，键对象不允许重复，而值对象可以重复，并且值对象还可以是 Map 类型的，就像数组中的元素还可以是数组一样。

### 初始化Map

#### 创建无默认值的Map

```java  {.line-numbers}
var mapNoDefault =[ "A":100,"B":200 ];
```

#### 创建有默认值的Map

默认值的作用是在取值时，如果Map中不含某个键，则返回默认值。默认键值用default键表示。

```java  {.line-numbers}
var map =[ "A":100,"B":200,default:-1];
```
#### 创建空的Map
```java  {.line-numbers}
var emptyMap = Map.new () ;
```

### Map相关方法

| 方法说明     |       方法名称 | 
| :----------- | :------------- | 
| 是否有默认值 |   hasDefault() |  
| 获取默认值   | defaultValue() |   
| 设置键值   | set( key, value) |   
| 用键获取值   | get(key) |   
| 是否含有键   | contains(key) |   
| 个数   |  count() |   
| 所有键的列表   | keys() |   
| 用键移除键值   |   remove(key) |   
| 清空   |  clear() |   

**实例**
```java  {.line-numbers}
var opfns=[
    "+":{$1+$2} ,
    "-":{$1-$2} ,
    "*":{$1*$2} ,
    default:{$1/$2}
];
println( opfns.get.("&&") 100 4 );
```

## Dynamic 动态类

```java  {.line-numbers}
package examples.stdsamples;
import atem.lang;
require atem.lang.Core;

//定义一个类,包含属性name和age
var d1 ={ name:"Tom" ,age:10 };
//显示 d1的name
println d1.name;
//显示 d1的age
println d1.age;
//显示 d1的high,因为没有添加这个属性，所以返回undefined
println d1.high;
//添加一个新的属性，直接用赋值表达式
d1.country="JiaLeBi";
println d1.country;

// 定义一个无属性的动态类
var d2 = Dynamic.new ();
println d2;
d2.name = "Jan";
println d2.name;
```

### 定义一个类包含属性的动态类

```java  {.line-numbers}
var d1 ={ name:"Tom" ,age:10 };
```

### 添加一个新的属性和获取属性

```java  {.line-numbers}
d1.country="JiaLeBi";
println d1.country;
```

### 定义一个无属性的动态类

```java  {.line-numbers}
var d2 = Dynamic.new ();
```


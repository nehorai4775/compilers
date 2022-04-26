package VMcompiler
import java.io.File

var asmFile: File? = null
var FileName: String? = null
fun label(line : List<String>)
{
    asmFile?.appendText("(${line[1]})\n")
}
fun goto(line: List<String>)
{
    asmFile?.appendText("@${line[1]}\n")
    asmFile?.appendText("0;JMP\n")
}
fun ifGoto(line : List<String>)
{
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("M=M-1\n")
    asmFile?.appendText("A=M\n")
    asmFile?.appendText("D=M\n")
    asmFile?.appendText("@${line[1]}\n")
    asmFile?.appendText("D;JNE\n")
}
fun call(line : List<String>)
{
    val functionName = line[1]
    val numArgs = line[2].toInt() +5
    asmFile?.appendText("@return-address${functionName}\n")
    asmFile?.appendText("D=A\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("A=M\n")
    asmFile?.appendText("M=D\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("M=M+1\n")
    pushLable("LCL")
    pushLable("ARG")
    pushLable("THIS")
    pushLable("THAT")

    asmFile?.appendText("@SP\n")
    asmFile?.appendText("D=M\n")
    asmFile?.appendText("@${numArgs}\n")

    asmFile?.appendText("D=D-A\n")
    asmFile?.appendText("@ARG\n")
    asmFile?.appendText("M=D\n")

    asmFile?.appendText("@SP\n")
    asmFile?.appendText("D=M\n")
    asmFile?.appendText("@LCL\n")
    asmFile?.appendText("M=D\n")

    goto(line)
    asmFile?.appendText("(return-address${functionName})\n")
}

fun pushLable(s: String) {
    asmFile?.appendText("@${s}\n")
    asmFile?.appendText("D=M\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("A=M\n")
    asmFile?.appendText("M=D\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("M=M+1\n")


}

fun function(line : List<String>)
{
    val funcName = line[1]
    asmFile?.appendText("(${funcName})\n")
    asmFile?.appendText("@${line[2]}\n")
    asmFile?.appendText("D=A\n")


    val loopEnd = "${funcName}_loopEnd"
    val loopStart = "${funcName}_loopStart"

    asmFile?.appendText("@${loopEnd}\n")
    asmFile?.appendText("D;JEQ\n")
    asmFile?.appendText("(${loopStart})\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("A=M\n")
    asmFile?.appendText("M=0\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("M=M+1\n")
    asmFile?.appendText("@${loopStart}\n")
    asmFile?.appendText("D=D-1;JNE\n")
    asmFile?.appendText("(${loopEnd})\n")
}
fun returnFunc()
{
    asmFile?.appendText("@LCL\n")
    asmFile?.appendText("D=M\n")

    asmFile?.appendText("@5\n")
    asmFile?.appendText("A=D-A\n")
    asmFile?.appendText("D=M\n")
    asmFile?.appendText("@13\n")
    asmFile?.appendText("M=D\n")

    asmFile?.appendText("@SP\n")
    asmFile?.appendText("M=M-1\n")
    asmFile?.appendText("A=M\n")
    asmFile?.appendText("D=M\n")
    asmFile?.appendText("@ARG\n")
    asmFile?.appendText("A=M\n")
    asmFile?.appendText("M=D\n")

    asmFile?.appendText("@ARG\n")
    asmFile?.appendText("D=M\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("M=D+1\n")

    asmFile?.appendText("@LCL\n")
    asmFile?.appendText("M=M-1\n")
    asmFile?.appendText("A=M\n")
    asmFile?.appendText("D=M\n")
    asmFile?.appendText("@THAT\n")
    asmFile?.appendText("M=D\n")

    asmFile?.appendText("@LCL\n")
    asmFile?.appendText("M=M-1\n")
    asmFile?.appendText("A=M\n")
    asmFile?.appendText("D=M\n")
    asmFile?.appendText("@THIS\n")
    asmFile?.appendText("M=D\n")

    asmFile?.appendText("@LCL\n")
    asmFile?.appendText("M=M-1\n")
    asmFile?.appendText("A=M\n")
    asmFile?.appendText("D=M\n")
    asmFile?.appendText("@ARG\n")
    asmFile?.appendText("M=D\n")

    asmFile?.appendText("@LCL\n")
    asmFile?.appendText("M=M-1\n")
    asmFile?.appendText("A=M\n")
    asmFile?.appendText("D=M\n")
    asmFile?.appendText("@LCL\n")
    asmFile?.appendText("M=D\n")

    asmFile?.appendText("@13\n")
    asmFile?.appendText("A=M\n")
    asmFile?.appendText("0;JMP\n")
}

fun endFile()
{
    asmFile?.appendText("(end)\n")
    asmFile?.appendText("@end\n")
    asmFile?.appendText("0;JMP\n")

}
fun handlePush(offset : String, base : Int)
{
    var loc = (Integer.parseInt(offset) + base).toString()
    asmFile?.appendText("@" + loc + "\n")
    asmFile?.appendText("D=M\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("A=M\n")
    asmFile?.appendText("M=D\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("M=M+1\n")
}



fun popTemp(offset : String, base : Int)
{
    asmFile?.appendText("@SP\n");
    asmFile?.appendText("A=M-1\n");
    asmFile?.appendText("D=M\n");


    var loc = (Integer.parseInt(offset) + base).toString()
    asmFile?.appendText("@" + loc+ "\n")

    asmFile?.appendText("M=D\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("M=M-1\n")

}


fun handleOffsetPush(offset : String, base : Int)
{

    var loc = (Integer.parseInt(offset) + base).toString()
    asmFile?.appendText("@" + loc + "\n")
    asmFile?.appendText("D=M\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("A=M\n")
    asmFile?.appendText("M=D\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("M=M+1\n")
}



fun add()
{
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("A=M-1\n")
    asmFile?.appendText("D=M\n")
    asmFile?.appendText("A=A-1\n")
    asmFile?.appendText("M=D+M\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("M=M-1\n")
}
fun sub()
{
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("A=M-1\n")
    asmFile?.appendText("D=M\n")
    asmFile?.appendText("A=A-1\n")
    asmFile?.appendText("M=M-D\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("M=M-1\n")
}
fun neg()
{
    asmFile?.appendText("//--negate--\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("A=M-1\n")
    asmFile?.appendText("M=-M\n")

}
fun eq(j:Int)
{
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("A=M-1\n")
    asmFile?.appendText("D=M\n")
    asmFile?.appendText("A=A-1\n")
    asmFile?.appendText("D=D-M\n")
    asmFile?.appendText("@TRUE_$j\n")
    asmFile?.appendText("D;JEQ\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("A=M-1\n")
    asmFile?.appendText("A=A-1\n")
    asmFile?.appendText("M=0\n")
    asmFile?.appendText("@FALSE_$j\n")
    asmFile?.appendText("D=1;JMP\n")
    asmFile?.appendText("(TRUE_$j) //label \n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("A=M-1\n")
    asmFile?.appendText("A=A-1\n")
    asmFile?.appendText("M=-1\n")
    asmFile?.appendText("(FALSE_$j) //label \n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("M=M-1\n")


}
fun gt(j:Int)
{
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("A=M-1\n")
    asmFile?.appendText("D=M\n")
    asmFile?.appendText("A=A-1\n")
    asmFile?.appendText("D=M-D\n")
    asmFile?.appendText("@TRUE_$j\n")
    asmFile?.appendText("D;JGT\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("A=M-1\n")
    asmFile?.appendText("A=A-1\n")
    asmFile?.appendText("M=0\n")
    asmFile?.appendText("@FALSE_$j\n")
    asmFile?.appendText("D=1;JMP\n")
    asmFile?.appendText("(TRUE_$j) //label \n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("A=M-1\n")
    asmFile?.appendText("A=A-1\n")
    asmFile?.appendText("M=-1\n")
    asmFile?.appendText("(FALSE_$j) //label \n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("M=M-1\n")

}
fun lt(j:Int)
{
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("A=M-1\n")
    asmFile?.appendText("D=M\n")
    asmFile?.appendText("A=A-1\n")
    asmFile?.appendText("D=M-D\n")
    asmFile?.appendText("@TRUE_$j\n")
    asmFile?.appendText("D;JLT\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("A=M-1\n")
    asmFile?.appendText("A=A-1\n")
    asmFile?.appendText("M=0\n")
    asmFile?.appendText("@FALSE_$j\n")
    asmFile?.appendText("D=1;JMP\n")
    asmFile?.appendText("(TRUE_$j) //label \n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("A=M-1\n")
    asmFile?.appendText("A=A-1\n")
    asmFile?.appendText("M=-1\n")
    asmFile?.appendText("(FALSE_$j) //label \n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("M=M-1\n")

}
fun and()
{
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("A=M-1\n")
    asmFile?.appendText("D=M\n")
    asmFile?.appendText("A=A-1\n")
    asmFile?.appendText("M=D&M\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("M=M-1\n")
}
fun or()
{
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("A=M-1\n")
    asmFile?.appendText("D=M\n")
    asmFile?.appendText("A=A-1\n")
    asmFile?.appendText("M=D|M\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("M=M-1\n")
}
fun not()
{
    asmFile?.appendText("//--Not--\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("A=M-1\n")
    asmFile?.appendText("M=!M\n")

}


fun pop(line : List<String>,fileName : String)
{
    //take caer of the case where the pop is static, temp and pointer
    if (line[1] == "static") 
    {
        popStatic(line,fileName);
       // handleOffsetPop(line[2], 16)
    } else if (line[1] == "temp") 
    {
        popTemp(line[2], 5)
    } else if (line[1] == "pointer") 
    {
        popPointer(line[2])
    } 
    else if (line[1] == "local") 
    {
        popLocal(Integer.parseInt(line[2]))
    } 
    else if(line[1]=="argument")
    {
        popArgument(Integer.parseInt(line[2]))
    }
    else if(line[1]=="this")
    {
        popThis(Integer.parseInt(line[2]))
    }
    else if(line[1]=="that")
    {
        popThat(Integer.parseInt(line[2]))
    }

}

fun popStatic(line: List<String>, fileName : String) {

    asmFile?.appendText("@SP\n");
    asmFile?.appendText("A=M-1\n");
    asmFile?.appendText("D=M\n");
    asmFile?.appendText("@"+ fileName+ "."+line[2]+"\n");
    asmFile?.appendText("M=D\n");
    asmFile?.appendText("@SP\n");
    asmFile?.appendText("M=M-1\n");
}

fun popPointer(pointer:String)
{
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("A=M-1\n")
    asmFile?.appendText("D=M\n")
    if(pointer=="0")
    {
        asmFile?.appendText("@THIS\n")
    }
    else if(pointer=="1")
    {
        asmFile?.appendText("@THAT\n")
    }
    asmFile?.appendText("M=D\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("M=M-1\n")
}
fun popLocal(n:Int)
{
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("A=M-1\n")
    asmFile?.appendText("D=M\n")
    asmFile?.appendText("@LCL\n")
    asmFile?.appendText("A=M\n")
    for (i in 0 until n)
    {
        asmFile?.appendText("A=A+1\n")
    }
    asmFile?.appendText("M=D\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("M=M-1\n")
}
fun popArgument(n:Int)
{
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("A=M-1\n")
    asmFile?.appendText("D=M\n")
    asmFile?.appendText("@ARG\n")
    asmFile?.appendText("A=M\n")
    for (i in 0 until n)
    {
        asmFile?.appendText("A=A+1\n")
    }
    asmFile?.appendText("M=D\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("M=M-1\n")
}
fun popThis(n:Int)
{
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("A=M-1\n")
    asmFile?.appendText("D=M\n")
    asmFile?.appendText("@THIS\n")
    asmFile?.appendText("A=M\n")
    for (i in 0 until n)
    {
        asmFile?.appendText("A=A+1\n")
    }
    asmFile?.appendText("M=D\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("M=M-1\n")
}
fun popThat(n:Int)
{
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("A=M-1\n")
    asmFile?.appendText("D=M\n")
    asmFile?.appendText("@THAT\n")
    asmFile?.appendText("A=M\n")
    for (i in 0 until n)
    {
        asmFile?.appendText("A=A+1\n")
    }
    asmFile?.appendText("M=D\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("M=M-1\n")
}



fun push(line : List<String>,count:Int, fileName : String)
{
    if(line[1]=="constant")
    {
        asmFile?.appendText("@${line[2]}\n")
        asmFile?.appendText("D=A\n")
        asmFile?.appendText("@SP\n")
        asmFile?.appendText("A=M\n")
        asmFile?.appendText("M=D\n")
        asmFile?.appendText("@SP\n")
        asmFile?.appendText("M=M+1\n")
    }
    else if(line[1]=="local")
    {
        pushLocal(Integer.parseInt(line[2]))
    }
    else if(line[1]=="static")
    {
        pushStatic(Integer.parseInt(line[2]),count,fileName)
    }
    else if(line[1]=="temp")
    {
        pushTemp(Integer.parseInt(line[2]))
    }
    else if(line[1]=="pointer")
    {
        pushPointer(line[2])

    }
    else if(line[1]=="argument")
    {
        pushArgument(Integer.parseInt(line[2]))
    }
    else if(line[1]=="this")
    {
        pushThis(Integer.parseInt(line[2]))
    }
    else if(line[1]=="that")
    {
        pushThat(Integer.parseInt(line[2]))
    }
}
fun pushThat(n:Int)
{
    asmFile?.appendText("@${n}\n")
    asmFile?.appendText("D=A\n")
    asmFile?.appendText("@THAT\n")
    asmFile?.appendText("A=M+D\n")
    asmFile?.appendText("D=M\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("A=M\n")
    asmFile?.appendText("M=D\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("M=M+1\n")
}
fun pushThis(n:Int)
{
    asmFile?.appendText("@${n}\n")
    asmFile?.appendText("D=A\n")
    asmFile?.appendText("@THIS\n")
    asmFile?.appendText("A=M+D\n")
    asmFile?.appendText("D=M\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("A=M\n")
    asmFile?.appendText("M=D\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("M=M+1\n")
}
fun pushArgument(n:Int)
{
    asmFile?.appendText("@${n}\n")
    asmFile?.appendText("D=A\n")
    asmFile?.appendText("@ARG\n")
    asmFile?.appendText("A=M+D\n")
    asmFile?.appendText("D=M\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("A=M\n")
    asmFile?.appendText("M=D\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("M=M+1\n")
}

fun pushLocal(n:Int)
{
    asmFile?.appendText("@${n}\n")
    asmFile?.appendText("D=A\n")
    asmFile?.appendText("@LCL\n")
    asmFile?.appendText("A=M+D\n")
    asmFile?.appendText("D=M\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("A=M\n")
    asmFile?.appendText("M=D\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("M=M+1\n")
}

fun pushStatic(n:Int,count:Int,fileName : String)
{
    asmFile?.appendText("@${fileName}.${n}\n")
    asmFile?.appendText("D=M\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("A=M\n")
    asmFile?.appendText("M=D\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("M=M+1\n")
}
fun pushTemp(n:Int)
{
    asmFile?.appendText("@${n}\n")
    asmFile?.appendText("D=A\n")
    asmFile?.appendText("@5\n")
    asmFile?.appendText("A=A+D\n")
    asmFile?.appendText("D=M\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("A=M\n")
    asmFile?.appendText("M=D\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("M=M+1\n")
}
fun pushPointer(n:String)
{
    if(n=="0")
    {
        asmFile?.appendText("@THIS\n")
    }
    else if(n=="1")
    {
        asmFile?.appendText("@THAT\n")
    }
    asmFile?.appendText("D=M\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("A=M\n")
    asmFile?.appendText("M=D\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("M=M+1\n")
}
//Yedidya Marashe 213661499
//Nehorai Cohen 325356814
//150060.01.5782.01, 150060.01.5782.48    \
package VMcompiler
import java.io.File
import java.io.InputStream
var asmFile: File? = null

fun main(args: Array<String>) {

    print(args[0]);
    val vmFiles = setUpFiles(args[0]);
    iterateFiles(vmFiles);
}

fun setUpFiles(dirName : String) : List<File>
{
    //iterate through the directory and find the two .vm files
    val dir = File(dirName);
    val vmFiles = dir.listFiles().filter { it.extension == "vm" };    
    val lastDirName = dirName.substring(dirName.lastIndexOf("/") + 1);
    println("lastDirName: $lastDirName");
    //take the last dir name and create new file in the dir with the name of the dir .asm
    asmFile = File(dirName+"/"+lastDirName + ".asm");
    asmFile?.createNewFile();
    return vmFiles;
}

fun iterateFiles(vmFiles: List<File>)
{

    //for every file in the vm list filse open it for reading,
     // itarate through the lines and write to the asm file
   vmFiles.forEach {
        val inputStream: InputStream = File(it.toString()).inputStream()

        //print the file name to the asm file
        var fileName = it.toString().substring(it.toString().lastIndexOf("/") + 1);
        fileName = fileName.substring(0, fileName.lastIndexOf("."));
        asmFile?.appendText("//"+fileName + "\n");
        inputStream.bufferedReader().useLines { lines -> lines.forEach{
                //it is a line from the vm file.             
                parseLine(it);
                } };
        endFile();
        inputStream.close(); 
    } 
}



fun parseLine(lineString : String)
{

    //TODO: if there is any error return the Line number with error message
    val line = lineString.split(" ");

    asmFile?.appendText("//${lineString}\n")
    when(line[0])
    {
        "add" -> add(line);
        "sub" -> sub(line);
        "neg" -> neg(line);
        "eq" -> eq(line);
        "gt" -> gt(line);
        "lt" -> lt(line);
        "and" -> and(line);
        "or" -> or(line);
        "not" -> not(line);
        "push" -> push(line);
        "pop" -> pop(line);
    }
    asmFile?.appendText("\n");
}

fun endFile()
{
    asmFile?.appendText("(end)\n");
    asmFile?.appendText("@end\n");
    asmFile?.appendText("0;JMP\n");

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

fun handleOffsetPop(offset : String, base : Int)
{
    var loc = (Integer.parseInt(offset) + base).toString()
    asmFile?.appendText("@" + loc+ "\n")
    asmFile?.appendText("D=A\n")
    asmFile?.appendText("@14\n")
    asmFile?.appendText("M=D\n")
}



fun push(line : List<String>)
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
    else if(line[1]=="static")
    {
        handleOffsetPush(line[2], 16)
    }
    else if(line[1]=="temp")
    {
        handleOffsetPush(line[2], 5)
    }
    else if(line[1]=="pointer")
    {
        handleOffsetPush(line[2], 3)

    }
    else {
        //TODO: handle this
    }
}
fun pop(line : List<String>)
{
    //take caer of the case where the pop is static, temp and pointer
    if (line[1] == "static") 
    {
        handleOffsetPop(line[2], 16)
    } else if (line[1] == "temp") 
    {
        handleOffsetPop(line[2], 5)
    } else if (line[1] == "pointer") 
    {
        handleOffsetPop(line[2], 3)
    } 
    else {}
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("AM=M-1\n")
    asmFile?.appendText("D=M\n")
    asmFile?.appendText("@14\n")
    asmFile?.appendText("A=M\n")
    asmFile?.appendText("M=D\n")
}
fun add(line : List<String>)
{
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("A=M-1\n")
    asmFile?.appendText("D=M\n")
    asmFile?.appendText("A=A-1\n")
    asmFile?.appendText("D=D+M\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("M=M-1\n")
}
fun sub(line : List<String>)
{
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("AM=M-1\n")
    asmFile?.appendText("D=M\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("AM=M-1\n")
    asmFile?.appendText("D=D-M\n")
    asmFile?.appendText("M=D\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("M=M+1\n")
}
fun neg(line : List<String>)
{
    asmFile?.appendText("//--negate--\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("AM=M-1\n")
    asmFile?.appendText("M=-M\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("M=M+1\n")
}
fun eq(line : List<String>)
{
    
}
fun gt(line : List<String>)
{
}
fun lt(line : List<String>)
{
}
fun and(line : List<String>)
{
    asmFile?.appendText("//--And--\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("AM=M-1\n")
    asmFile?.appendText("D=M\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("AM=M-1\n")
    asmFile?.appendText("D=D&M\n")
    asmFile?.appendText("M=D\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("M=M+1\n")
}
fun or(line : List<String>)
{

    asmFile?.appendText("//--Or--\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("AM=M-1\n")
    asmFile?.appendText("D=M\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("AM=M-1\n")
    asmFile?.appendText("D=D|M\n")
    asmFile?.appendText("M=D\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("M=M+1\n")
}
fun not(line : List<String>)
{
    asmFile?.appendText("//--Not--\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("AM=M-1\n")
    asmFile?.appendText("M=!M\n")
    asmFile?.appendText("@SP\n")
    asmFile?.appendText("M=M+1\n")
}


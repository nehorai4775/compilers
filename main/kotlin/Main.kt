//Yedidya Marashe 213661499
//Nehorai Cohen 325356814
//150060.01.5782.01, 150060.01.5782.48    \
import java.io.File
import java.io.InputStream
import VMcompiler.*

fun main(args: Array<String>) {

    println(args[0])
    val vmFiles = setUpFiles(args[0])
    iterateFiles(vmFiles)
}

fun setUpFiles(dirName : String) : List<File>
{
    //iterate through the directory and find the two .vm files
    val dir = File(dirName)
    val vmFiles = dir.listFiles().filter { it.extension == "vm" }
    val lastDirName = dirName.substring(dirName.lastIndexOf("/") + 1)
    println("lastDirName: $lastDirName")
    //take the last dir name and create new file in the dir with the name of the dir .asm
    asmFile = File("$dirName/$lastDirName.asm")
    FileName = lastDirName
    asmFile?.createNewFile()
    return vmFiles
}

fun iterateFiles(vmFiles: List<File>)
{

    //for every file in the vm list filse open it for reading,
     // itarate through the lines and write to the asm file
   vmFiles.forEach {
        val inputStream: InputStream = File(it.toString()).inputStream()

        //print the file name to the asm file
        var fileName = it.toString().substring(it.toString().lastIndexOf("/") + 1)
        fileName = fileName.substring(0, fileName.lastIndexOf("."))
        asmFile?.appendText("//"+fileName + "\n")
        inputStream.bufferedReader().useLines { lines -> lines.forEach{
                //it is a line from the vm file.             
            parseLine(it)
                } }
       endFile()
        inputStream.close()
    } 
}


var count=0
fun parseLine(lineString : String)
{

    //TODO: if there is any error return the Line number with error message
    val line = lineString.split(" ")

    asmFile?.appendText("//${lineString}\n")
    count++
    when(line[0])
    {
        "add" -> add()
        "sub" -> sub()
        "neg" -> neg()
        "eq" -> eq(count)
        "gt" -> gt(count)
        "lt" -> lt(count)
        "and" -> and()
        "or" -> or()
        "not" -> not()
        "push" -> push(line)
        "pop" -> pop(line)
        "label" -> label(line)
        "goto" -> goto(line)
        "if-goto" -> ifGoto(line)
        "call" -> call(line)
        "function" -> function(line)
        "return" -> returnFunc()

    }
    asmFile?.appendText("\n")
}

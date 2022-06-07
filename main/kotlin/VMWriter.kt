import java.io.File
enum class Segment{CONSTANT, ARGUMENT, LOCAL, STATIC, THIS, THAT, POINTER, TEMP, NONE }
enum class com{ADD,SUB,NEG,EQ,GT,LT,AND,OR,NOT}
class VMWriter (var vmFile:File){


    fun writePush(seg:Segment,index:Int){
        vmFile?.appendText("push ${seg.toString().lowercase()} $index\n")
    }
    fun writePop(seg:Segment,index:Int){
        vmFile?.appendText("pop ${seg.toString().lowercase()} $index\n")
    }
    fun writeLabel(label:String){
        vmFile?.appendText("label $label\n")
    }
    fun writeGoto(label: String){
        vmFile?.appendText("goto $label\n")
    }
    fun writeIf(label: String){
        vmFile?.appendText("if-goto $label\n")
    }
    fun writeCall(className:String,funcName:String,nargs:Int){
        vmFile?.appendText("call $className.$funcName $nargs\n")
    }
    fun writeFunction(currentClass:String, name:String,nLocals:Int){
        vmFile?.appendText("function $currentClass.$name $nLocals\n")
    }
    fun writeReturn(){
        vmFile?.appendText("return\n\n")
    }

    fun writeVM(command :String){
        vmFile?.appendText(command + "\n")
    }





}
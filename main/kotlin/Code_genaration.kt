import java.io.File

enum class Tag {
    CLASS, CLASS_VAR_DEC, SUBROUTINE_DEC, PARAMETER_LIST,
    SUBROUTINE_BODY, VAR_DEC, STATEMENTS, LET_STATEMENT,
    IF_STATEMENT, WHILE_STATEMENT, DO_STATEMENT, RETURN_STATEMENT,
    EXPRESSION, TERM, EXPRESSION_LIST, KEYWORD, SYMBOL,IDENTIFIER,INTEGERCONSTANT,STRINGCONSTANT;

    override fun toString(): String {
        return super.toString().lowercase().replace(Regex("_.")) {
            it.value[1].uppercase()
        }
    }
}

enum class Keyword {
    CLASS, CONSTRUCTOR, FUNCTION, METHOD,
    FIELD, STATIC, VAR, INT,
    CHAR, BOOLEAN, VOID, TRUE,
    FALSE, NULL, THIS, LET,
    DO, IF, ELSE, WHILE,
    RETURN;

    override fun toString(): String {
        return super.toString().toLowerCase()
    }
}


enum class Keyword2 {
    CLASS, CONSTRUCTOR, FUNCTION, METHOD,
    FIELD, STATIC, VAR, INT,
    CHAR, BOOLEAN, VOID, TRUE,
    FALSE, NULL, THIS, LET,
    DO, IF, ELSE, WHILE,
    RETURN;

    override fun toString(): String {
        return super.toString().toLowerCase()
    }
}

//fun main(args: Array<String>) {
//    setUpFiles3(args[0])
//}

/*fun setUpFiles3(dirName: String) {

    val dir = File(dirName)
    val xmlFiles = dir.listFiles().filter { it.extension == "xml" }
    for (file in xmlFiles) {
        var newfileName = file.toString().substring(file.toString().lastIndexOf("\\"))
        newfileName = newfileName.toString().substring(0, newfileName.toString().lastIndexOf("."))
        var vmFile = File("$dirName\\$newfileName" + ".vm")
        vmFile?.createNewFile()
        var vmwriter = VMWriter(vmFile)
        createVm(vmwriter, file)

    }
}*/

class VM {
    private var xmlFile: Xml
    private var writer: VMWriter
    private var currentClass: String
    private var table: symboleTable
    private var whileCount: Int
    private var ifCount: Int


    constructor(filePath: String, xmlFile: Xml) {
        this.xmlFile = xmlFile
        var f = File(filePath.subSequence(0, filePath.length - 5).toString() + ".vm")
        f.createNewFile()
        f.writeText("") // Empty the file
        writer = VMWriter(f)
        table = symboleTable()
        currentClass = ""
        whileCount = 0
        ifCount = 0
    }

    fun vm() {
        if (xmlFile.hasNext()) {
            whileCount = 0
            ifCount = 0
            compileClass()
        } else {
            println("No class found")
        }

    }

    private fun compileClass() {

        xmlFile.next() // class

        currentClass = xmlFile.getRemoveNextTokenContent() // class name

        xmlFile.next() // {

        while (xmlFile.hasNext() && xmlFile.nextTokenType() == Tag.KEYWORD && xmlFile.nextTokenContent() == Keyword.STATIC.toString()
                .lowercase() || xmlFile.nextTokenContent() == Keyword.FIELD.toString().lowercase()
        ) {
            compileClassVarDec()
        }

        while (xmlFile.hasNext() && xmlFile.nextTokenType() == Tag.KEYWORD && xmlFile.nextTokenContent() == Keyword.CONSTRUCTOR.toString()
                .lowercase() || xmlFile.nextTokenContent() == Keyword.FUNCTION.toString()
                .lowercase() || xmlFile.nextTokenContent() == Keyword.METHOD.toString().lowercase()
        ) {
            compileSubroutineDec()
        }


        xmlFile.next() // }
    }

    private fun compileSubroutineDec() {
        table.startSubroutine()

        var funcKind = xmlFile.getRemoveNextTokenContent() // function/ctor/method

        var returnType = xmlFile.getRemoveNextTokenContent() //function type

        var functionName = xmlFile.getRemoveNextTokenContent() //function name

        xmlFile.next() // (

        if (funcKind == "method") table.define("this", currentClass, Segment.ARGUMENT)

        compileParameterList()

        xmlFile.next() // )

        compileSubroutineBody(funcKind, returnType, functionName)
    }

    private fun compileSubroutineBody(funcKind: String, returnType: String, functionName: String) {

        xmlFile.next() // {
        var count = 0

        while (xmlFile.hasNext() && xmlFile.nextTokenType() == Tag.KEYWORD && xmlFile.nextTokenContent() == Keyword.VAR.toString()
                .lowercase()
        ) {
            compileVarDec()
        }

        writer.writeFunction(currentClass, functionName, table.VarCount(Segment.LOCAL))
        when (funcKind) {
            "constructor" -> {
                writer.writePush(Segment.CONSTANT, table.VarCount(Segment.THIS))
                writer.writeCall("Memory", "alloc", 1)
                writer.writePop(Segment.POINTER, 0)
            }

            "method" -> {
                writer.writePush(Segment.ARGUMENT, 0)
                writer.writePop(Segment.POINTER, 0)
            }
        }

        compileStatements()

        xmlFile.next() // }
    }

    private fun compileStatements() {
        while (xmlFile.hasNext() && xmlFile.nextTokenType() == Tag.KEYWORD && xmlFile.nextTokenContent() == Keyword.LET.toString()
                .lowercase() || xmlFile.nextTokenContent() == Keyword.IF.toString()
                .lowercase() || xmlFile.nextTokenContent() == Keyword.WHILE.toString()
                .lowercase() || xmlFile.nextTokenContent() == Keyword.DO.toString()
                .lowercase() || xmlFile.nextTokenContent() == Keyword.RETURN.toString().lowercase()
        ) {
            when (xmlFile.nextTokenContent()) {
                Keyword.LET.toString().lowercase() -> compileLet()
                Keyword.IF.toString().lowercase() -> compileIf()
                Keyword.WHILE.toString().lowercase() -> compileWhile()
                Keyword.DO.toString().lowercase() -> compileDo()
                Keyword.RETURN.toString().lowercase() -> compileReturn()
            }
        }
    }

    private fun compileReturn() {
        xmlFile.next() // return

        if (!(xmlFile.nextTokenType() == Tag.SYMBOL && xmlFile.nextTokenContent() == ";")) {
            compileExpression()
        } else {
            writer.writePush(Segment.CONSTANT, 0)
        }

        xmlFile.next() // ;

        writer.writeReturn()
    }

    private fun compileDo() {
        xmlFile.next() // do

        var first = currentClass
        var second = xmlFile.getRemoveNextTokenContent() // name
        var isMethod = true

        if (xmlFile.nextTokenType() == Tag.SYMBOL && xmlFile.nextTokenContent() == ".") {
            isMethod = false
            xmlFile.next() // .

            first = second
            second = xmlFile.getRemoveNextTokenContent() // name
        }

        var count = 0
        if (isMethod) {
            writer.writePush(Segment.POINTER, 0)
            count++

        } else {
            // class not object
            if (table.KindOf(first) != Segment.NONE) {
                writer.writePush(table.KindOf(first), table.IndexOf(first))
                first = table.TypeOf(first)
                count++
            }
        }

        xmlFile.next() // (

        count += compileExpressionList()

        xmlFile.next() // )


        xmlFile.next() // ;

        writer.writeCall(first, second, count)

        writer.writePop(Segment.TEMP, 0)
    }

    private fun compileWhile() {
        var _whileCount = whileCount
        whileCount++
        xmlFile.next() // while

        xmlFile.next() // (

        writer.writeLabel("WHILE$_whileCount")
        compileExpression()

        writer.writeVM("not")
        writer.writeIf("WHILE_END$_whileCount")

        xmlFile.next() // )

        xmlFile.next() // {

        compileStatements()

        xmlFile.next() // }

        writer.writeGoto("WHILE$_whileCount")
        writer.writeLabel("WHILE_END$_whileCount")
    }

    private fun compileIf() {
        var _ifCount = ifCount
        ifCount++

        xmlFile.next() // if


        xmlFile.next() // (

        compileExpression()

        xmlFile.next() // )
        writer.writeVM("not")
        writer.writeIf("FALSE$_ifCount")

        xmlFile.next() // {

        compileStatements()

        xmlFile.next() // }

        if (xmlFile.nextTokenType() == Tag.KEYWORD && xmlFile.nextTokenContent() == "else")
            writer.writeGoto("ELSE$_ifCount")

        writer.writeLabel("FALSE$_ifCount")

        if (xmlFile.nextTokenType() == Tag.KEYWORD && xmlFile.nextTokenContent() == "else") {

            xmlFile.next() // else

            xmlFile.next() // {

            compileStatements()

            xmlFile.next() // }
            writer.writeLabel("ELSE$_ifCount")
        }


    }

    private fun compileLet() {
        var flag=false
        var index=0


        xmlFile.next() // let

        var name=xmlFile.getRemoveNextTokenContent() // name

        if (xmlFile.nextTokenContent() == "[") {
            xmlFile.next() // [
            flag=true

             compileExpression()
            writer.writePush(table.KindOf(name),table.IndexOf(name))
            writer.writeVM("add")

            xmlFile.next() // ]
        }

        xmlFile.next() // =


        compileExpression()

        xmlFile.next() // ;
        if (!flag)
        {
            writer.writePop(table.KindOf(name),table.IndexOf(name))
        }
        else
        {
            writer.writePop(Segment.TEMP, 0)
            writer.writePop(Segment.POINTER, 1)

            writer.writePush(Segment.TEMP, 0)
            writer.writePop(Segment.THAT, 0)
        }
    }

    private fun compileExpression() {

        compileTerm()

        while (xmlFile.nextTokenContent() in listOf(
                "+",
                "-",
                "*",
                "/",
                "&lt;",
                "&gt;",
                "=",
                "&amp;",
                "&amp;&amp;",
                "|"
            )
        ) {
             var op = xmlFile.getRemoveNextTokenContent() // + or -

            val m = mapOf("+" to "add",
                "-" to "sub",
                "*" to "call Math.multiply 2",
                "/" to "call Math.divide 2",
                "&amp;" to "and",
                "|" to "or",
                "&lt;" to "lt",
                "&gt;" to "gt",
                "=" to "eq")

            compileTerm()
            writer.writeVM(m.get(op).toString())

        }


    }

    private fun compileTerm() {

        var content = xmlFile.nextTokenContent()

        when (xmlFile.nextTokenType()) {
            Tag.INTEGERCONSTANT -> {
                xmlFile.next() // value
                writer.writePush(Segment.CONSTANT, content.toInt())
            }
            Tag.STRINGCONSTANT -> {
                xmlFile.next() // value
                writer.writePush(Segment.CONSTANT,content.length)
                writer.writeCall("String", "new", 1)
                for (letter in content){
                    writer.writePush(Segment.CONSTANT,letter.toInt())
                    writer.writeCall("String", "appendChar", 2)

                }
            }
            Tag.KEYWORD -> {
                xmlFile.next() // keyword
                when(content){
                    "false"->writer.writePush(Segment.CONSTANT, 0)
                    "true"->{writer.writePush(Segment.CONSTANT, 0)
                            writer.writeVM("not")}
                    "this"->writer.writePush(Segment.POINTER, 0)
                    "null"->writer.writePush(Segment.CONSTANT, 0)
                }
            }
            Tag.IDENTIFIER -> {
                var con = xmlFile.getRemoveNextTokenContent() // name
                when (xmlFile.nextTokenContent()) {
                    "(" -> {
                        xmlFile.next() // (
                        var count=compileExpressionList()
                        xmlFile.next() // )
                        var first = currentClass
                        var second = con // name
                        writer.writePush(Segment.POINTER, 0)
                        writer.writeCall(first, second, count+1)
                    }
                    "[" -> {
                        xmlFile.next()
                        compileExpression()
                        writer.writePush(table.KindOf(con),table.IndexOf(con))
                        writer.writeVM("add")
                        writer.writePop(Segment.POINTER, 1)

                        writer.writePush(Segment.THAT, 0)
                        xmlFile.next() // ]
                    }
                    "." -> {
                        xmlFile.next()
                        var second =  xmlFile.getRemoveNextTokenContent() // name


                        xmlFile.next() // (

                        var count=compileExpressionList()

                        xmlFile.next() // )
                        var first = con
                        if (table.KindOf(con) != Segment.NONE) {
                            writer.writePush(table.KindOf(first), table.IndexOf(first))
                            first = table.TypeOf(first)
                            count++
                        }

                        writer.writeCall(first, second, count)
                    }
                    else -> {
                        writer.writePush(table.KindOf(con), table.IndexOf(con))
                    }
                }
            }
            Tag.SYMBOL -> {
                xmlFile.next() // value
                when (content) {
                    "(" -> {
                        compileExpression()
                        xmlFile.next() // )
                    }
                    "-" -> {
                        compileTerm()
                        writer.writeVM("neg")
                    }
                    "~" -> {
                        compileTerm()
                        writer.writeVM("not")
                    }
                }

            }
        }
    }

    private fun compileExpressionList() :Int {
        var count = 0
        var tagElement = getTag(xmlFile.nextStringLine()) // name or )
        while (tagElement.first != Tag.SYMBOL || tagElement.second != ")") {
            compileExpression()
            count++
            tagElement = getTag(xmlFile.nextStringLine())
            while (tagElement.first == Tag.SYMBOL && tagElement.second == ",") {
                xmlFile.next() // ,
                compileExpression() // ex
                count++

                tagElement = getTag(xmlFile.nextStringLine())

            }
            tagElement = getTag(xmlFile.nextStringLine())
        }
        return count
    }

    private fun compileVarDec() {
        xmlFile.next() // var

        var type=xmlFile.getRemoveNextTokenContent() // type

        var name=xmlFile.getRemoveNextTokenContent() // name
        table.define(name,type, Segment.LOCAL)


        while (xmlFile.hasNext() && xmlFile.nextTokenContent() == ",") {
            xmlFile.next() // ,

            var name=xmlFile.getRemoveNextTokenContent() // name
            table.define(name,type, Segment.LOCAL)
        }

        xmlFile.next() // ;



    }

    private fun compileParameterList() {


        if (xmlFile.hasNext() && xmlFile.nextTokenContent() != ")") {
            var type=xmlFile.getRemoveNextTokenContent() // type

            var name=xmlFile.getRemoveNextTokenContent() // name
            table.define(name,type, Segment.ARGUMENT)

            while (xmlFile.hasNext() && xmlFile.nextTokenContent() == ",") {
                xmlFile.next() // ,

                var type=xmlFile.getRemoveNextTokenContent() // type

                var name=xmlFile.getRemoveNextTokenContent() // name
                table.define(name,type, Segment.ARGUMENT)
            }
        }
    }

    private fun compileClassVarDec() {

        var seg =xmlFile.getRemoveNextTokenContent() // field/static

        var type=xmlFile.getRemoveNextTokenContent() // type

        var name=xmlFile.getRemoveNextTokenContent() // name
        when(seg){
            "field"->table.define(name,type, Segment.THIS)
            "static"->table.define(name,type, Segment.STATIC)

        }


        while (xmlFile.hasNext() && xmlFile.nextTokenContent() == ",") {
            xmlFile.next() // ,

            var name=xmlFile.getRemoveNextTokenContent() // name
            when(seg){
                "field"->table.define(name,type, Segment.THIS)
                "static"->table.define(name,type, Segment.STATIC)

            }
        }

        xmlFile.next() // ;
    }


    fun getTag(tag: String): Pair<Tag, String> {
        val tagType = tag.split(" ")[0].subSequence(1, tag.split(" ")[0].length - 1).toString()
        val TAG = Tag.valueOf(tagType.uppercase())
        var tagName = tag.split(" ")[1]
        if (TAG == Tag.STRINGCONSTANT)
            tagName = tag.substring(tag.indexOf(">") + 1, tag.lastIndexOf("<"))
        return Pair(TAG, tagName)
    }
}


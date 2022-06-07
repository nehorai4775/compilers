
/*enum class Tag {
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

class Parser {
    private var xmlFile: Xml
    private var writer: Writer = Writer()
    private var spaceCounter = 0

    constructor(xmlFile: Xml) {
        this.xmlFile = xmlFile
        writer = Writer()
    }

    fun parse() {
        if (xmlFile.hasNext()) {
            val classElemnt = xmlFile.next()
            compileClass(classElemnt)
        } else {
            println("No class found")
        }

    }

    private fun compileClass(classElemnt: String) {
        val tag = Tag.CLASS
        writer.writeTag(tag, spaceCounter)
        spaceCounter++

        var tagElement = getTag(xmlFile.next()) // class
        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)

        tagElement = getTag(xmlFile.next()) // class name
        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)

        tagElement = getTag(xmlFile.next()) // {
        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)

        while (xmlFile.hasNext() && xmlFile.nextTokenType() == TokenType.keyword && xmlFile.nextTokenContent() == Keyword.STATIC.toString()
                .lowercase() || xmlFile.nextTokenContent() == Keyword.FIELD.toString().lowercase()
        ) {
            compileClassVarDec()
        }

        while (xmlFile.hasNext() && xmlFile.nextTokenType() == TokenType.keyword && xmlFile.nextTokenContent() == Keyword.CONSTRUCTOR.toString()
                .lowercase() || xmlFile.nextTokenContent() == Keyword.FUNCTION.toString()
                .lowercase() || xmlFile.nextTokenContent() == Keyword.METHOD.toString().lowercase()
        ) {
            compileSubroutineDec(xmlFile.next())
        }


        tagElement = getTag(xmlFile.next()) // }
        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)

        spaceCounter--
        writer.writeEnd(tag, spaceCounter)
    }

    private fun compileSubroutineDec(next: String) {
        val tag = Tag.SUBROUTINE_DEC
        writer.writeTag(tag, spaceCounter)
        spaceCounter++

        var tagElement = getTag(next) // function
        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)

        tagElement = getTag(xmlFile.next()) //function type
        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)

        tagElement = getTag(xmlFile.next()) // function name
        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)


        tagElement = getTag(xmlFile.next()) // (
        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)

        compileParameterList()

        tagElement = getTag(xmlFile.next()) // )
        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)



        compileSubroutineBody()



        spaceCounter--
        writer.writeEnd(tag, spaceCounter)
    }

    private fun compileSubroutineBody() {
        val tag = Tag.SUBROUTINE_BODY
        writer.writeTag(tag, spaceCounter)
        spaceCounter++

        var tagElement = getTag(xmlFile.next()) // {
        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)


        while (xmlFile.hasNext() && xmlFile.nextTokenType() == TokenType.keyword && xmlFile.nextTokenContent() == Keyword.VAR.toString()
                .lowercase()
        ) {
            compileVarDec()
        }

        compileStatements()





        tagElement = getTag(xmlFile.next()) // }
        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)
        spaceCounter--
        writer.writeEnd(tag, spaceCounter)
    }

    private fun compileStatements() {
        val tag = Tag.STATEMENTS
        writer.writeTag(tag, spaceCounter)
        spaceCounter++

        while (xmlFile.hasNext() && xmlFile.nextTokenType() == TokenType.keyword && xmlFile.nextTokenContent() == Keyword.LET.toString()
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
        spaceCounter--
        writer.writeEnd(tag, spaceCounter)
    }

    private fun compileReturn() {
        val tag = Tag.RETURN_STATEMENT
        writer.writeTag(tag, spaceCounter)
        spaceCounter++

        var tagElement = getTag(xmlFile.next()) // return
        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)


        if(!(xmlFile.nextTokenType() == TokenType.symbol && xmlFile.nextTokenContent() == ";")){
            compileExpression()
        }

        tagElement = getTag(xmlFile.next()) // ;
        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)


        spaceCounter--
        writer.writeEnd(tag, spaceCounter)
    }

    private fun compileDo() {
        val tag = Tag.DO_STATEMENT
        writer.writeTag(tag, spaceCounter)
        spaceCounter++

        var tagElement = getTag(xmlFile.next()) // do
        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)

        tagElement = getTag(xmlFile.next()) // name
        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)

        if (xmlFile.nextTokenType() == TokenType.symbol && xmlFile.nextTokenContent() == ".") {
            tagElement = getTag(xmlFile.next()) // .
            writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)

            tagElement = getTag(xmlFile.next()) // name
            writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)
        }

        tagElement = getTag(xmlFile.next()) // (
        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)

        compileExpressionList()

        tagElement = getTag(xmlFile.next()) // )
        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)


        tagElement = getTag(xmlFile.next()) // ;
        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)


        spaceCounter--
        writer.writeEnd(tag, spaceCounter)
    }

    private fun compileWhile() {
        val tag = Tag.WHILE_STATEMENT
        writer.writeTag(tag, spaceCounter)
        spaceCounter++

        var tagElement = getTag(xmlFile.next()) // while
        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)

        tagElement = getTag(xmlFile.next()) // (
        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)


        compileExpression()

        tagElement = getTag(xmlFile.next()) // )
        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)

        tagElement = getTag(xmlFile.next()) // {
        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)

        compileStatements()

        tagElement = getTag(xmlFile.next()) // }
        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)


        spaceCounter--
        writer.writeEnd(tag, spaceCounter)
    }

    private fun compileIf() {
        val tag = Tag.IF_STATEMENT
        writer.writeTag(tag, spaceCounter)
        spaceCounter++

        var tagElement = getTag(xmlFile.next()) // if
        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)


        tagElement = getTag(xmlFile.next()) // (
        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)

        compileExpression()

        tagElement = getTag(xmlFile.next()) // )
        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)

        tagElement = getTag(xmlFile.next()) // {
        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)

        compileStatements()

        tagElement = getTag(xmlFile.next()) // }
        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)

        if(xmlFile.nextTokenType() == TokenType.keyword && xmlFile.nextTokenContent() == "else"){
            tagElement = getTag(xmlFile.next()) // else
            writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)

            tagElement = getTag(xmlFile.next()) // {
            writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)

            compileStatements()

            tagElement = getTag(xmlFile.next()) // }
            writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)
        }

        spaceCounter--
        writer.writeEnd(tag, spaceCounter)
    }

    private fun compileLet() {
        val tag = Tag.LET_STATEMENT
        writer.writeTag(tag, spaceCounter)
        spaceCounter++

        var tagElement = getTag(xmlFile.next()) // let
        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)

        tagElement = getTag(xmlFile.next()) // name
        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)

        if (xmlFile.nextTokenContent() == "[") {
            tagElement = getTag(xmlFile.next()) // [
            writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)

            compileExpression()

            tagElement = getTag(xmlFile.next()) // ]
            writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)
        }

        tagElement = getTag(xmlFile.next()) // =
        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)


        compileExpression()

        tagElement = getTag(xmlFile.next()) // ;
        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)



        spaceCounter--
        writer.writeEnd(tag, spaceCounter)
    }

    private fun compileExpression() {
        val tag = Tag.EXPRESSION
        writer.writeTag(tag, spaceCounter)
        spaceCounter++

        compileTerm()

        while (xmlFile.nextTokenContent() in listOf("+", "-", "*", "/","&lt;","&gt;","=","&amp;","&amp;&amp;","|")) {
            val tagElement = getTag(xmlFile.next()) // + or -
            writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)

            compileTerm()
        }

        spaceCounter--
        writer.writeEnd(tag, spaceCounter)
    }

    private fun compileTerm() {
        val tag = Tag.TERM
        writer.writeTag(tag, spaceCounter)
        spaceCounter++

        var tagElement = getTag(xmlFile.next())

        when (tagElement.first) {
            Tag.INTEGERCONSTANT -> {
                writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)
            }
            Tag.STRINGCONSTANT -> {
                writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)
            }
            Tag.KEYWORD -> {
                writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)
            }
            Tag.IDENTIFIER -> {
                writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)
                tagElement = getTag(xmlFile.nextStringLine()) // name
                when (tagElement.second) {
                    "(" -> {
                        tagElement = getTag(xmlFile.next())
                        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)
                        compileExpressionList()
                        tagElement = getTag(xmlFile.next()) // )
                        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)
                    }
                    "[" -> {
                        tagElement = getTag(xmlFile.next())
                        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)
                        compileExpression()
                        tagElement = getTag(xmlFile.next()) // ]
                        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)
                    }
                    "." -> {
                        tagElement = getTag(xmlFile.next())
                        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)
                        tagElement = getTag(xmlFile.next()) // name
                        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)


                        tagElement = getTag(xmlFile.next()) // (
                        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)

                        compileExpressionList()

                        tagElement = getTag(xmlFile.next()) // )
                        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)

                    }
                    else -> {
                        ;
                    }
                }
            }
            Tag.SYMBOL -> {
                when (tagElement.second) {
                    "(" -> {
                        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)
                        compileExpression()
                        tagElement = getTag(xmlFile.next()) // )
                        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)
                    }
                    "-" -> {
                        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)
                        compileTerm()
                    }
                    "~" -> {
                        writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)
                        compileTerm()
                    }
                }

            }
        }

        spaceCounter--
        writer.writeEnd(tag, spaceCounter)
    }

    private fun compileExpressionList() {
        val tag = Tag.EXPRESSION_LIST
        writer.writeTag(tag, spaceCounter)
        spaceCounter++

        var tagElement = getTag(xmlFile.nextStringLine()) // name or )
        while (tagElement.first != Tag.SYMBOL || tagElement.second != ")") {
            compileExpression()

            tagElement = getTag(xmlFile.nextStringLine())
            while (tagElement.first == Tag.SYMBOL && tagElement.second == ",") {
                tagElement = getTag(xmlFile.next()) // ,
                writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)
                compileExpression() // ex
                tagElement = getTag(xmlFile.nextStringLine())
            }
                tagElement = getTag(xmlFile.nextStringLine())

        }

        spaceCounter--
        writer.writeEnd(tag, spaceCounter)
    }

    private fun compileVarDec() {
            val tag = Tag.VAR_DEC
            writer.writeTag(tag, spaceCounter)
            spaceCounter++

            var tagElement = getTag(xmlFile.next()) // var
            writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)

            tagElement = getTag(xmlFile.next()) // type
            writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)

            tagElement = getTag(xmlFile.next()) // name
            writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)


            while (xmlFile.hasNext() && xmlFile.nextTokenContent() == ",") {
                tagElement = getTag(xmlFile.next()) // ,
                writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)

                tagElement = getTag(xmlFile.next()) // name
                writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)
            }

            tagElement = getTag(xmlFile.next()) // ;
            writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)



            spaceCounter--
            writer.writeEnd(tag, spaceCounter)
        }

        private fun compileParameterList() {
            val tag = Tag.PARAMETER_LIST
            writer.writeTag(tag, spaceCounter)
            spaceCounter++

            if(xmlFile.hasNext() && xmlFile.nextTokenContent() != ")") {
                var tagElement = getTag(xmlFile.next()) // type
                writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)

                tagElement = getTag(xmlFile.next()) // name
                writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)

                while (xmlFile.hasNext() && xmlFile.nextTokenContent() == ",") {
                    tagElement = getTag(xmlFile.next()) // ,
                    writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)

                    tagElement = getTag(xmlFile.next()) // type
                    writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)

                    tagElement = getTag(xmlFile.next()) // name
                    writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)
                }
            }


            spaceCounter--
            writer.writeEnd(tag, spaceCounter)
        }

        private fun compileClassVarDec() {
            val tag = Tag.CLASS_VAR_DEC
            writer.writeTag(tag, spaceCounter)
            spaceCounter++

            var tagElement = getTag(xmlFile.next())
            writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)

            tagElement = getTag(xmlFile.next())
            writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)

            tagElement = getTag(xmlFile.next())
            writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)

            while (xmlFile.hasNext() && xmlFile.nextTokenType() == TokenType.symbol && xmlFile.nextTokenContent() == ",") {
                tagElement = getTag(xmlFile.next()) // ,
                writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)

                tagElement = getTag(xmlFile.next()) // name
                writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)
            }

            tagElement = getTag(xmlFile.next()) // ;
            writer.writeTagWithContect(tagElement.first, tagElement.second, spaceCounter)


            spaceCounter--
            writer.writeEnd(tag, spaceCounter)
        }


        fun getTag(tag: String): Pair<Tag, String> {
            val tagType = tag.split(" ")[0].subSequence(1, tag.split(" ")[0].length - 1).toString()
            val TAG = Tag.valueOf(tagType.uppercase())
            var tagName = tag.split(" ")[1]
            if (TAG == Tag.STRINGCONSTANT)
                tagName = tag.substring(tag.indexOf(">") + 1, tag.lastIndexOf("<"))
            return Pair(TAG, tagName)
        }


    }*/
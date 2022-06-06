import java.io.*

enum class TokenType {
    keyword, identifier, symbol, stringConstant , integerConstant
}


class Xml {

    var position: Int = 0
    var fileContentList = ArrayList<String>()

    constructor(xmlFileName: String) {
        //read the file into content
        readXml( xmlFileName)
        //set the position to 0
        position = 0
    }

    constructor() {}

    fun readXml(xmlFileName: String)  {
        val xmlFile = File(xmlFileName)
        Writer().createFile(File(xmlFileName.subSequence(0, xmlFileName.length - 5).toString() + ".xml"))
        //read from file like a text file
        val fileReader = FileReader(xmlFile)
        val bufferedReader = BufferedReader(fileReader)
        for (line in bufferedReader.lines()) {
            fileContentList.add(line)
        }
    }

    fun next(): String {
        var next = fileContentList[position]
        if (next == null) {
            return ""
        }
        position++
        return next
    }

    fun hasNext(): Boolean {
        return position < fileContentList.size
    }


    fun nextTokenType(): TokenType {

        var node = fileContentList[position]
        var tokenTypestring = node.subSequence(node.indexOf("<") + 1, node.indexOf(">")).toString()

        return TokenType.valueOf(tokenTypestring)
    }

    fun nextTokenContent() :String
    {
        var node = fileContentList[position]
        var content = node.subSequence(node.indexOf(">") + 1, node.lastIndexOf("<")).toString()
        if(content.contains(" "))
        {
            content = content.substring(1)
            if(content.contains(" "))
            {
                content = content.substring(0, content.indexOf(" "))
            }
        }
        return content
    }

    fun nextStringLine(): String {
        var next = fileContentList[position]
        return next
    }
}

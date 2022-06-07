//Yedidya Marashe 213661499
//Nehorai Cohen 325356814
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import java.io.FileInputStream
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import java.io.InputStream
import java.io.PrintWriter
import java.lang.Character.digit

var xmlFile: File? = null
val symbols1: List<String> =
    listOf("{", "}", "(", ")", "[", "]", ".", ",", ";", "+", "-", "*", "/", "&", "|", "<", ">", "=", "~")
val keyword: List<String> = listOf(
    "class",
    "constructor",
    "function",
    "method",
    "field",
    "static",
    "var",
    "int",
    "char",
    "boolean",
    "void",
    "true",
    "false",
    "null",
    "this",
    "let",
    "do",
    "if",
    "else",
    "while",
    "return"
)
fun main(args: Array<String>){
    setUpFiles2(args[0])
}


fun setUpFiles2(dirName: String) {

    val dir = File(dirName)
    val jackFiles = dir.listFiles().filter { it.extension == "jack" }
    for (file in jackFiles) {
        var newfileName = file.toString().substring(file.toString().lastIndexOf("\\"))
        newfileName = newfileName.toString().substring(0, newfileName.toString().lastIndexOf("."))
        //take the last dir name and create new file in the dir with the name of the dir .xml
        xmlFile = File("$dirName\\$newfileName"+"T.xml")
        xmlFile?.createNewFile()
        iterateFiles2(file)
        //val xml = Xml("$dirName\\$newfileName"+"T.xml")
       // val preser = Parser(xml)
       // preser.parse()
    }
}

fun iterateFiles2(jackFile: File) {
    //jackFiles.forEach {
    val inputStream: FileInputStream = File(jackFile.toString()).inputStream()
    val inputString = inputStream.bufferedReader().use { jackFile.readText() }

    createXml(inputString)

}

fun createXml(input: String) {
    val docBuilder: DocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
    val doc: Document = docBuilder.newDocument()
    val rootElement: Element = doc.createElement("tokens")
    var i = 0
    var flag = 1


    var temp: String
    while (i < input.length) {
        flag = 1
        if (input[i].isDigit()) {
            var temp = input[i].toString()
            i++
            while (input[i].isDigit()) {

                temp += input[i].toString()
                i++
            }
            val integer: Element = doc.createElement("integerConstant")
            integer.appendChild(doc.createTextNode(temp.toString()))
            rootElement.appendChild(integer)

        } else if (input[i].equals('"')) {
            i++
            temp = input[i].toString()
            while (!input[i].equals('"')) {

                temp += input[i]
                i++
            }
            i++
            val string: Element = doc.createElement("stringConstant")
            string.appendChild(doc.createTextNode(temp.toString()))
            rootElement.appendChild(string)
        } else if (input[i].equals('/')) {
            i++
            if (input[i].equals('/')) {
                i++
                while (!input[i].equals('\n')) {
                    i++
                }
            } else if (input[i].equals('*')) {
                i++
                while (flag == 1) {
                    i++
                    if (input[i].equals('*')) {
                        i++
                        if (input[i].equals('/'))
                            flag = 0
                            i++
                    }
                }
            } else {
                val symbol: Element = doc.createElement("symbol")
                symbol.appendChild(doc.createTextNode("/"))
                rootElement.appendChild(symbol)
                i++
            }

        } else if (symbols1.contains(input[i].toString())) {
            if(input[i].equals('<')){
                val symbol: Element = doc.createElement("symbol")
                symbol.appendChild(doc.createTextNode("&gt"))
                rootElement.appendChild(symbol)
                i++
            }
            else if(input[i].equals('>')){
                val symbol: Element = doc.createElement("symbol")
                symbol.appendChild(doc.createTextNode("&lt"))
                rootElement.appendChild(symbol)
                i++
            }else if(input[i].equals('"')){
                val symbol: Element = doc.createElement("symbol")
                symbol.appendChild(doc.createTextNode("&quot"))
                rootElement.appendChild(symbol)
                i++
            }else if(input[i].equals('&')){
                val symbol: Element = doc.createElement("symbol")
                symbol.appendChild(doc.createTextNode("&amp"))
                rootElement.appendChild(symbol)
                i++
            }
            else {
                val symbol: Element = doc.createElement("symbol")
                symbol.appendChild(doc.createTextNode(input[i].toString()))
                rootElement.appendChild(symbol)
                i++
            }
        } else if (input[i].isLetter() or input[i].equals("_")) {
            temp = input[i].toString()
            i++
            while (input[i].isLetter() or input[i].equals("_") or input[i].isDigit()) {
                temp += input[i].toString()
                i++
            }
            if (keyword.contains(temp)) {
                val keyword1: Element = doc.createElement("keyword")
                keyword1.appendChild(doc.createTextNode(temp.toString()))
                rootElement.appendChild(keyword1)
            } else {
                val identifier: Element = doc.createElement("identifier")
                identifier.appendChild(doc.createTextNode(temp.toString()))
                rootElement.appendChild(identifier)

            }
        }

        else{
            i++
        }

    }


    // Now, "add" the root node to the XML document in memory
    doc.appendChild(rootElement)

    val transformer: Transformer = TransformerFactory.newInstance().newTransformer()


    transformer.transform(DOMSource(doc), StreamResult(xmlFile))

}




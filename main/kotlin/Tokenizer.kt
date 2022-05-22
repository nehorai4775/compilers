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
val symbols1:List<String> = listOf( "{","}","(",")","[","]",".",",",";","+","-","*","/","&","|","<",">","=","~")

fun main(args: Array<String>) {
    val jackFiles = setUpFiles2(args[0])

    iterateFiles2(jackFiles)


}

fun setUpFiles2(dirName: String): List<File> {
    //iterate through the directory and find the two .vm files
    val dir = File(dirName)
    val jackFiles = dir.listFiles().filter { it.extension == "jack" }
    val lastDirName = dirName.substring(dirName.lastIndexOf("\\") + 1)
    println("lastDirName: $lastDirName")
    //take the last dir name and create new file in the dir with the name of the dir .xml
    val xmlFile = File("$dirName\\$lastDirName+T.xml")
    val FileName = lastDirName
    xmlFile?.createNewFile()
    return jackFiles
}

fun iterateFiles2(jackFiles: List<File>) {
    jackFiles.forEach {
        val inputStream: FileInputStream = File(it.toString()).inputStream()
        val inputString = inputStream.bufferedReader().use { it.readText() }

                createXml(inputString)

        }

    }



fun createXml(input: String) {
    val docBuilder: DocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
    val doc: Document = docBuilder.newDocument()
    val rootElement: Element = doc.createElement("tokens")
    var i=0
    var flag=1


    var temp:String
    while(i<input.length){
        flag=1
        if (input[i].isDigit()) {
            var temp = input[i].toString()
            i++
            while (input[i].isDigit()){

                temp+=input[i].toString()
                i++
            }
            val integer: Element = doc.createElement("integerConstant")
            integer.appendChild(doc.createTextNode(temp.toString()))
            rootElement.appendChild(integer)

        }
        else if(input[i].equals('"')){
            i++
            temp=input[i].toString()
            while (!input[i].equals('"')){

                temp+=input[i]
                i++
            }
            i++
            val string: Element = doc.createElement("stringConstant")
            string.appendChild(doc.createTextNode(temp.toString()))
            rootElement.appendChild(string)
        }
        else if(input[i].equals('/')){
            i++
            if(input[i].equals('/')){
                i++
                while(!input[i].equals('\n')){
                    i++
                }
            }
            else if(input[i].equals('*')){
                i++
                while (flag==1){
                    i++
                    if(input[i].equals('*')){
                        i++
                        if(input[i].equals('/'))
                            flag=0
                    }
                }
            }
            else{
                val symbol: Element = doc.createElement("symbol")
                symbol.appendChild(doc.createTextNode("/"))
                rootElement.appendChild(symbol)
            }

        }
        else if(symbols1.contains(input[i].toString())) {
            val symbol: Element = doc.createElement("symbol")
            symbol.appendChild(doc.createTextNode(input[i].toString()))
            rootElement.appendChild(symbol)
        }
        i++

    }




    // Now, "add" the root node to the XML document in memory
    doc.appendChild(rootElement)

    val transformer: Transformer = TransformerFactory.newInstance().newTransformer()


    transformer.transform(DOMSource(doc), StreamResult("C:\\Devo\\compilers\\src\\main\\kotlin\\kotlin+T.xml"))

}




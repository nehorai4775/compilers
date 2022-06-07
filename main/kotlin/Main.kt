import java.io.File

//main function
fun main(args: Array<String>) {
    //input from user to get the file location
    println("Enter the file location: ")
    val fileLocation = readLine()!!
    //get only the files that end with T.xml
    val files = File (fileLocation).listFiles()
        files.forEach {
        if (it.name.endsWith("T.xml")) {
            //get the file path
            val filePath = it.absolutePath
            //create a new instance of the xml class
            val xml = Xml(filePath)
            //val preser = Parser(xml)
            //preser.parse()
            val compiler = VM(filePath, xml)
            compiler.vm()

            //parse the xml file using the parser class

        }
    }

       // setUpFiles2(args[0])


}
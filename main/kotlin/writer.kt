import java.io.File
var file: File? = null

//write xml file class
class Writer {

    fun createFile(xmlFile: File) {
        xmlFile.createNewFile()
        file = xmlFile
    }
    //append to xml file
    fun append(text: String) {
        file?.appendText(text)
    }

    fun writeTag(tag: Tag,space: Int) {
        var line = ""
        for (i in 0 until space) {
            line += "  "
        }
        line += "<" + tag.name.lowercase() + ">"
        file?.appendText("$line\n")

    }

    fun writeEnd(tag: Tag,space: Int) {
        var line = ""
        for (i in 0 until space) {
            line += "  "
        }
        line += "</" + tag.name.lowercase() + ">"
        file?.appendText("$line\n")
    }

    fun writeTagWithContect(tagOn: Tag,content : String , counter: Int) {
        var line = ""
        for (i in 0 until counter) {
            line += "  "
        }
        line += "<" + tagOn.name.lowercase() + "> " + content + " </" + tagOn.name.lowercase() + ">"
        file?.appendText("$line\n")
    }
}
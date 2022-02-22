//Yedidya Marashe 213661499


//150060.01.5782.01, 150060.01.5782.48    


import java.io.File
import java.io.InputStream

var asmFile: File? = null

fun main(args: Array<String>) {

    val vmFiles = setUpFiles(args[0]);
    iterateFiles(vmFiles,args[0]); 
}
fun setUpFiles(dirName : String) : List<File>
{
    //iterate through the directory and find the two .vm files
    val dir = File(dirName);
    val vmFiles = dir.listFiles().filter { it.extension == "vm" };    
    val lastDirName = dirName.substring(dirName.lastIndexOf("/") + 1);

    //take the last dir name and create new file in the dir with the name of the dir .asm
    asmFile = File(dirName+"/"+lastDirName + ".asm");
    asmFile?.createNewFile();
    return vmFiles;
}
fun iterateFiles(vmFiles: List<File>, dirName: String)
{

    var totalSales : Double = 0.0;
    var totalBuys : Double = 0.0;

    //for every file in the vm list filse open it for reading,
    // itarate through the lines and write to the asm file
    vmFiles.forEach {
        val inputStream: InputStream = File(it.toString()).inputStream()

        //print the file name to the asm file
        var fileName = it.toString().substring(it.toString().lastIndexOf("/") + 1);
        fileName = fileName.substring(0, fileName.lastIndexOf("."));
        asmFile?.appendText(fileName + "\n");

        inputStream.bufferedReader().useLines { lines -> lines.forEach{
                //it is a line from the vm file.             
                  val prices = parseLine(it);
                  totalSales += prices.first;
                  totalBuys += prices.second;
                } };
        inputStream.close(); }
    
    asmFile?.appendText("TOTAL BUY: $totalBuys\n");
    asmFile?.appendText("TOTAL CELL: $totalSales\n");   
}
fun parseLine(lineString : String) : Pair<Double,Double>
{
    val line = lineString.split(" ");
    var sellPrice : Double = 0.0;
    var buyPrice : Double = 0.0;

    //if the line start with cell goto HandleSell, if start with buy goto HandleBuy
    when(line[0])
    {
        "cell" -> sellPrice+= HandleSell(line[1], line[2].toInt(), line[3].toDouble());
        "buy" -> buyPrice += HandleBuy(line[1], line[2].toInt(), line[3].toDouble());
    }
    return Pair(sellPrice, buyPrice);
}
fun HandleBuy(productName: String,amount:Int,price:Double) : Double
{
    asmFile?.appendText("### BUY $productName ###\n");
    asmFile?.appendText((amount * price).toString() + "\n");    
    return amount * price;
}
fun HandleSell(productName: String,amount:Int,price:Double) : Double
{
    asmFile?.appendText("$$$ CELL $productName $$$\n");
    asmFile?.appendText((amount * price).toString() + "\n");
    return amount * price;
}

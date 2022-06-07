
 class table(_name: String, _type: String, _seg: Segment, _index:Int) {

       var name=_name
        var type=_type
        var kind=_seg
       var index=_index


}
class symboleTable(){
    var fild=0
    var static1=0
    var arg=0
    var locals=0
    var classtables= mutableMapOf<String,table>();
    var subtables= mutableMapOf<String,table>();

    fun startSubroutine(){
        locals=0
        arg=0
        subtables= mutableMapOf<String,table>();


    }
    fun define(name:String, type:String, segment: Segment){
        when(segment){
            Segment.ARGUMENT->{
                var tempTable = table(name, type, segment,arg++)
                subtables[name] = tempTable}
            Segment.LOCAL->{ var tempTable = table(name, type, segment,locals++)
                subtables[name] = tempTable}
            Segment.STATIC->{var tempTable = table(name, type, segment,static1++)
                classtables[name] = tempTable}
            Segment.THIS->{var tempTable = table(name, type, segment,fild++)
                classtables[name] = tempTable}
        }

    }

     fun VarCount(segment: Segment): Int {
         when(segment){
             Segment.ARGUMENT->{
                 return arg;}
             Segment.LOCAL->{ return locals}
             Segment.STATIC->{return static1}
             Segment.THIS->{return fild}
         }
         return -1
    }

    fun KindOf(name:String):Segment{
        return classtables[name]?.kind ?: subtables[name]?.kind ?: Segment.NONE
    }

    fun TypeOf(name:String):String{
        return classtables[name]?.type ?: subtables[name]?.type ?: ""
    }

    fun IndexOf(name:String):Int{
        return classtables[name]?.index ?: subtables[name]?.index ?: -1
    }
}
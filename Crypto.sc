object Crypto {
  val alphabets = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toList
  def rotate(str:List[Char],num:Int) : List[Char] = (str,num) match {
    case (_,x) if x == 0 => str
    case (x::xs, n) => rotate(xs ++ List(x), n - 1)
  }
  rotate(alphabets,3)
  def makeKey(k:Int):List[(Char,Char)] =alphabets.zip(rotate(alphabets,k))

  makeKey(5)

  def lookUp(char:Char,mapping:List[(Char,Char)]):Char = mapping.find(a => a._1.equals(char)).map(e => e._2).getOrElse(char)

  lookUp('A',makeKey(5))

  def encipher(offset:Int,key:Char):Char= lookUp(key,makeKey(offset))

  encipher(5,'C')

  def normalize(str:String):String=str.replaceAll("[^a-zA-Z0-9]","").toUpperCase()

  normalize("July 4th !")

  def encipehrStr(index:Int,str:String):String={
    normalize(str).toList.map(x=>encipher(index,x)).mkString
  }
  encipehrStr(5,"July 4th !")


  def reverseKey(maping:List[(Char,Char)]):List[(Char,Char)]={
  maping.map(x=>(x._2,x._1)).toList
  }

reverseKey(makeKey(5))

/*
  def deciper(Offset:Int,char:Char):Char={
    makeKey(Offset).find(x=>if(x._2.equals(char)))
  }
  deciper(5,'H')*/
}
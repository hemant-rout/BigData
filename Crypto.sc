object Crypto {
  val alphabets = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toList

  def rotate(str: List[Char], num: Int): List[Char] = (str, num) match {
    case (_, x) if x == 0 => str
    case (x :: xs, n) => rotate(xs ++ List(x), n - 1)
  }

  rotate(alphabets, 3)

  def makeKey(k: Int): List[(Char, Char)] = alphabets.zip(rotate(alphabets, k))

  makeKey(5)

  def lookUp(char: Char, mapping: List[(Char, Char)]): Char = mapping.find(a => a._1.equals(char)).map(e => e._2).getOrElse(char)

  lookUp('A', makeKey(5))

  def encipher(offset: Int, key: Char): Char = lookUp(key, makeKey(offset))

  encipher(5, 'C')

  def normalize(str: String): String = str.replaceAll("[^a-zA-Z0-9]", "").toUpperCase()

  normalize("July 4th !")

  def encipehrStr(index: Int, str: String): String = {
    normalize(str).toList.map(x => encipher(index, x)).mkString
  }

  encipehrStr(5, "July 4th !")


  def reverseKey(maping: List[(Char, Char)]): List[(Char, Char)] = {
    maping.map(x => (x._2, x._1))
  }

  reverseKey(makeKey(5))

  def deciper(Offset: Int, char: Char): Char ={ makeKey(Offset).find(a => a._2.equals(char)).map(e => e._1).getOrElse(char)}
   deciper(5, 'F')


   def normalize1(str: String): String = str.replaceAll("[^A-Z0-9 ]", "")

   def deciperStr(Offset: Int, str: String): String =normalize(str).toList.map(x=>deciper(5,x)).mkString

   deciperStr(2, "OZQD4YM")



  def contains(mainStr:String,findStr:String):Boolean={
    if(mainStr.contains(findStr)) true else false
  }

  contains("Example","amp")
  contains("Example","xml")

 /* def candidates(str:String):List[(Int,String)]=
  {
    (1 to 26).toList.map(index=>(index,deciperStr(index,str))).filter(y=>y._2.contains("THE") ||y._2.contains("AND") ).toList
  }
  candidates("DGGADBCOOCZYMJHZYVMTOJOCZHVS")*/


  /*deciperStr(3,"DGGADBCOOCZYMJHZYVMTOJOCZHVS")
  (1 to 26).toList.map(index=>(index,deciperStr(index,"DGGADBCOOCZYMJHZYVMTOJOCZHVS")))*/


  contains("asdf","asdf")

  def cube(x: Int) :  Int = x * x * x
  def sum(f:Int => Int)(a: Int, b: Int) =
    if(a> b) 0
  else f(a) + (a+1, b)

sum(cube)(1, 10)
  
  
  
  
  //Links
  
  Unit Testing
http://www.martinfowler.com/bliki/SelfTestingCode.html
http://www.martinfowler.com/bliki/UnitTest.html
http://www.thoughtworks.com/insights/blog/guidelines-structuring-automated-tests
http://c2.com/cgi/wiki?ArrangeActAssert
http://www.martinfowler.com/bliki/AssertionFreeTesting.html
http://www.thoughtworks.com/insights/blog/write-better-tests-5-steps
http://www.martinfowler.com/articles/nonDeterminism.html
http://www.martinfowler.com/tags/testing.html
        
Pair Programming
http://martinfowler.com/bliki/PairProgrammingMisconceptions.html
http://agilerichmond.com/attachments/article/82/Pair%20Programming%20Handout.pdf

Refactoring
https://www.youtube.com/watch?v=vqEg37e4Mkw
http://martinfowler.com/articles/workflowsOfRefactoring/
https://www.youtube.com/watch?v=vqEg37e4Mkw

Code Smells
http://martinfowler.com/bliki/CodeSmell.html
http://www.industriallogic.com/wp-content/uploads/2005/09/smellstorefactorings.pdf

OO Concepts and Design
http://www.objectmentor.com/resources/articles/inheritanceVsDelegation
http://www.objectmentor.com/resources/articles/ocp.pdf
http://www.objectmentor.com/resources/articles/Principles_and_Patterns.pdf
http://martinfowler.com/articles/designDead.html

Mocking (TestDouble)
http://gojko.net/2009/10/23/mockito-in-six-easy-examples/
http://docs.mockito.googlecode.com/hg/latest/org/mockito/Mockito.html
https://code.google.com/p/powermock/
http://martinfowler.com/articles/mocksArentStubs.html
http://martinfowler.com/articles/modernMockingTools.html
http://martinfowler.com/articles/injection.html

Functional Testing
https://code.google.com/p/selenium/wiki/GettingStarted
https://selenium-release.storage.googleapis.com/2.42/selenium-server-standalone-2.42.2.jar

Good Reads
http://martinfowler.com/articles/useOfMetrics.html
http://www.javaworld.com/article/2073723/core-java/why-getter-and-setter-methods-are-evil.html
http://www.javaworld.com/article/2072302/core-java/more-on-getters-and-setters.html
http://martinfowler.com/ieeeSoftware/coupling.pdf


Books Must Read  Refactoring,  Test Driven Development

Commenting code does it make sense!!
https://blog.codinghorror.com/coding-without-comments/

Some hilarious comments found in code 
http://stackoverflow.com/questions/184618/what-is-the-best-comment-in-source-code-you-have-ever-encountered

POJO vs. NOJO (All about the misunderstanding)
http://puttingtheteaintoteam.blogspot.in/2008/10/utils-classes-and-nojos.html
http://www.martinfowler.com/bliki/POJO.html (the one who coined the term)

A complete example with OO design guidelines
https://github.com/priyaaank/MarsRover (Check the blog links with the source code)

My Java blog (will be posting more on coding and other standards soon)
http://novice2wise.blogspot.in/ (Can check ENUM blog and CI blog for now)

Amazing series of blog’s on OO design
http://priyaaank.tumblr.com/post/95095165285/decoding-thoughtworks-coding-problems

This link contains a series of links pointing to blogs which can help to make your basics super strong. A step towards being a strong programmer.
https://shirishpadalkar.wordpress.com/2014/05/30/recommended-reading-developers-interview-at-thoughtworks/

Stay in touch with technology, get the reviews by the experienced developers, Architects and scientists on this link  
https://www.thoughtworks.com/radar 

For Developers interested in functional programming.

Scala and Functional Programming from basics to advance… sure you’ll just enjoy reading these. 
These are the best books In which you can invest.
http://underscore.io/training/courses/essential-scala/  (Basic)
http://www.amazon.in/Functional-Programming-Scala-Paul-Chiusano/dp/1617290653 (Intermediate)
http://underscore.io/training/courses/advanced-scala/ (Advance)

Haskell getting familiar 
https://www.youtube.com/watch?v=AOl2y5uW0mA&list=PLtRG9GLtNcHBv4cuh2w1cz5VsgY6adoc3
http://learnyouahaskell.com/chapters
best book so far I took on Haskell http://haskellbook.com/

Nice blogs on Haskell by Gabriel Gonzalez  http://www.haskellforall.com/ 

Get all the papers published by Simon Peton jones one of the creator of GHC
http://research.microsoft.com/en-us/um/people/simonpj/Papers/papers.html

Category theory Lectures for programmers (very interesting) - Bartosz Milewski 
https://www.youtube.com/watch?v=I8LbkfSSR58&list=PLbgaMIhjbmEnaH_LTkxLI7FMa2HsnawM_

  
  
  
  
}

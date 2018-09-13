package recfun
import common._

object Main {
  def main(args: Array[String]) {
    println("Pascal's Triangle")
    for (row <- 0 to 10) {
      for (col <- 0 to row)
        print(pascal(col, row) + " ")
      println()
    }
  }

  /**
   * Exercise 1
   */
  def pascal(c: Int, r: Int): Int = {
    if(c == r || c == 0) 1
    else pascal(c-1, r-1) + pascal(c, r-1)
  }


  /**
   * Exercise 2
   */
  def balance(chars: List[Char]): Boolean = {
    def mapParentheses(p: Char): Int =
      if(p == '(') 1
      else if(p == ')') -1
      else 0
    def loop(sum: Int, chars: List[Char]): Boolean =
      if(sum < 0) false
      else if(chars.isEmpty) sum == 0
      else loop(sum + mapParentheses(chars.head), chars.tail)
    loop(0, chars)
  }

  /**
   * Exercise 3
   */
  def countChange(money: Int, coins: List[Int]): Int ={
    def hdCoin = coins.head
    if(coins.isEmpty) 0
    else if(money == 0) 1
    else if (money >= hdCoin) countChange(money-hdCoin, coins) + countChange(money, coins.tail)
    else countChange(money, coins.tail)
  }
}

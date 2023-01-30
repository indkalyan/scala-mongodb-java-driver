(1 to 5).map(_ * 2)


(1 to 5).reduceLeft(_ + _)

List("hello ","world ", "how ","are ","you!").reduceLeft(_ + _)

(1 to 5) filter {
  _ % 2 == 0
} map {
  _ * 2
}
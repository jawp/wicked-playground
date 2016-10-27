package howitworks.cats

import cats.data.{OptionT, Xor}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}


class TransformersDemo extends wp.Spec {

  "motivation" in {

    //given monad inside monad
    val x: Future[Option[String]] = Future{Some("Nested")}
    val y: Future[Option[String]] = Future{Some("Monads")}

    //and standard way of "composing" nested monads
    val xy = for {
      x: Option[String] <- x
      y: Option[String] <- y
    } yield {
      for {
        x: String <- x
        y: String <- y
      } yield s"$x $y"
    }

    //it works, but it is hard write code such way
    Await.result(xy, 500 millis).value mustBe "Nested Monads"

    //Let's rewrite it using Monad Transformers.
    import cats.instances.future._ //cats.Functor[Future] is needed

    //less caracters, looks cleaner, but result type is different
    val xyT: OptionT[Future, String] = for {
      x <- OptionT(x)
      y <- OptionT(y)
    } yield s"$x $y"

    //this is how you can get wrapped monad out of this transofmer
    val xy2 = xyT.value

    //and works as expected
    Await.result(xy2, 500 millis).value mustBe "Nested Monads"


    //Now lets see some more complex example when dealing with Future[Option[A]]

    //given a functinon f: A => Future[Option[A]]
    //it's not important what it does, just focus on signature
    def distributionOfLetters(s: String): Future[Option[String]] = Future{
      if(s == "") None
      else Some (
        s.foldLeft(Map[Char, Int]()) { case (acc, c) => acc.updated(c, 1 + acc.getOrElse(c, 0)) }
          .toList
          .sortBy(_._1)
          .mkString(s"distribution of letters of '$s' is: ", ", ", "")
      )
    }

    //as well, given some for comprehension when second value depends on first one
    //note how ugly it is:
    import cats.instances.future._
    import cats.instances.option._
    import cats.syntax.traverse._

    val stringAndDescription: Future[Option[(String, String)]] = for {
      x: Option[String] <- x
      tmp1: Option[Future[Option[String]]] = x.map(x => distributionOfLetters(x))
      tmp2: Future[Option[Option[String]]] = tmp1.sequenceU
      tmp3: Future[Option[String]] = tmp2.map(_.flatten)
      desc: Option[String] <- tmp3
    } yield {
      for {
        x <- x
        desc <- desc
      } yield (x, desc)
    }

    //it works
    Await.result(stringAndDescription, 500 millis).value mustBe ("Nested","distribution of letters of 'Nested' is: (N,1), (d,1), (e,2), (s,1), (t,1)")

    //Ok, let's rewrite it using monad transformers:
    val stringAndDescription2: OptionT[Future, (String, String)] = for {
      x <- OptionT(x)
      desc <- OptionT(distributionOfLetters(x))
    } yield(x,desc)

    //and it works like previously
    Await.result(stringAndDescription2.value, 500 millis).value mustBe ("Nested","distribution of letters of 'Nested' is: (N,1), (d,1), (e,2), (s,1), (t,1)")

  }
  //  "WriterT " in {
  //    val a: Future[Writer[String, Int]] = Future(Writer("Got 1", 1))
  //    val b: Future[Writer[String, Int]] = Future(Writer("Got 2", 2))
  //
  //
  //    val c: WriterT[Future, String, Int] = for {
  //      a <- aT
  //      a <- aT
  //    } yield (a, a)
  //
  //    Await.result(aa.value, 500 millis) mustBe (1,1)
  //
  //  }
  //  "stacking more monads" in {
  //
  //
  //    val a: Future[Writer[String, Option[Int]]] = Future(Writer("Got some 1", Some(1)))
  //
  //    val aa = for {
  //      a <- WriterT[Future, String, Option[Int]]
  //    } yield a
  //
  ////    val aa2 = for {
  ////
  ////    }
  //
  //  }


  "more complex example - reset password" ignore {
    //TODO: finish it

    /**
      * This real life scenario when user want's to reset his password.
      * 1. First we must obtain user's backup email for password resetting (let's assume this is how this app works)
      * 2. There may or may not be such backup email.
      * 3. When user has backup email - generate new random password and chang it in system ...
      * 4. ... then send resetpassword link to this email address
      * 5. If user has no backup email - display appropriate message
      * 6. Make it all async
      *
      *
      */

    type Email = String
    type UserId = Int
    type Error = String
    type Password = String

    //this emulates the system
    object database {
      val userIds: List[UserId] =  List(1,2,3)
      val backupEmails: Map[UserId, Email] = Map(1 -> "john@hotmail.com", 2 -> "bob@gmail.com") //user 3 hasn't set backup email
      private var passwords: Map[UserId, Password] = Map(1 -> "secre1", 2-> "secretto", 3-> "forgetmenot")

      def resetPassword(userId: UserId): Password = synchronized {
        val newPassword = "changeMe"
        passwords = passwords.updated(userId, newPassword)
        newPassword
      }
    }

    def getBackupEmail(userId: UserId): Future[Xor[Error, Option[Email]]] = Future{
      if(!database.userIds.contains(userId))
        Xor.Left(s"No such userId $userId")
      else
        Xor.right(database.backupEmails.get(userId))
    }

    def resetPassword(userId: UserId): Future[Xor[Error, Password]] = Future{
      if(!database.userIds.contains(userId))
        Xor.left(s"No such userId $userId")
      else Xor.right(database.resetPassword(userId))
    }

    def sendEmailWithPasswordResetLink(email: Email, userId: UserId, password: String) = Future {
      println (s"Click below link in order to reset password. https://resetpassword.com/resetpassword?ui=$userId&pw=$password")
    }

    //    //main program for resetting password
    //    def main(userId: UserId) = {
    //      for {
    //        _ <- OptionT(getBackupEmail(userId))
    //      } yield ()
    //    }

  }
}

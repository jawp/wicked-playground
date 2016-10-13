package scalatagsext
package generic

import java.util.Objects._


trait ExtAttrs {

  object cls {
    def :=[Builder, T](v: T*)(implicit ev: ClassValue[Builder, T]) = {
      requireNonNull(v)
      ClassModifier[Builder, T](v, ev)
    }
    def empty[Builder](implicit ev: ClassValue[Builder, String]) = this := ""
  }

}


trait ClassValue[Builder, T] {
  def apply(t: Builder, v: T*)
}

case class ClassModifier[Builder, T](v: Seq[T], ev: ClassValue[Builder, T]) extends scalatags.generic.Modifier[Builder] {
  override def applyTo(t: Builder): Unit = {
    ev.apply(t, v: _*)
  }
}

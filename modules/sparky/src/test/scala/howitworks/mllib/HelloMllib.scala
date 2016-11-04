package howitworks.mllib

import com.holdenkarau.spark.testing.SharedSparkContext
import org.apache.spark.mllib.linalg.distributed.{CoordinateMatrix, IndexedRow, IndexedRowMatrix, MatrixEntry}
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD

class HelloMllib extends wp.SparkySpec {

  //https://spark.apache.org/docs/latest/mllib-data-types.html

  import org.apache.spark.mllib.linalg.{Vector, Vectors, Matrix, Matrices }
  import org.apache.spark.mllib.linalg.distributed.{RowMatrix}

  "Local Vectors" in {

    //indexes start from 0
    //values must be doubles
    //MLllibs vectors are local and stored in single machine
    //they can be either dense or spare
    //vector represent point in N-dimentional space where N i size of the vector
    //matrix - list of vectors of the same size

    //create dense vector
    val v0: Vector = Vectors.dense(44.0, 0.0, 55.0)

    v0(0) mustBe 44.0
    v0(1) mustBe 0.0
    v0(2) mustBe 55.0

    //create sparse vector
    val v1: Vector = Vectors.sparse(
      size = 3,
      indices = Array(0, 2),
      values = Array(44.0, 55.0)
    )

    val v2 = Vectors.sparse(
      size = 3,
      elements = Seq((0, 44.0), (2, 55.0))
    )

    v0 mustBe v1
    v0 mustBe v2

    v0.toString mustBe "[44.0,0.0,55.0]"
    v1.toString mustBe "(3,[0,2],[44.0,55.0])"

    //LabeledPoints - label is a double
    val lp0 = LabeledPoint(1.0, v0)
    lp0.label mustBe 1.0
    lp0.features mustBe v0

  }

  "Local matrices" in {
    val m0 = Matrices.dense(
      numRows = 3,
      numCols = 2,
      values = Array(
        1, 3, 5, // first column
        2, 4, 6  // second column
      )
    )

    m0.toString() mustBe "1.0  2.0  \n" +
                         "3.0  4.0  \n" +
                         "5.0  6.0  "

   //m0(ith-row, jth-col) (inaczej niz w ukladzie wspolrzednych)
    m0(0,0) mustBe 1.0
    m0(1,0) mustBe 3.0
    m0(2,0) mustBe 5.0

    m0(0,1) mustBe 2.0
    m0(1,1) mustBe 4.0
    m0(2,1) mustBe 6.0


    m0.isTransposed mustBe false

    val m0T = m0.transpose
    m0T(0,0) mustBe 1.0
    m0T(0,1) mustBe 3.0
    m0T(0,2) mustBe 5.0


//    sparse matrices - efficient for column slicing and computing meatrix vector products

    val m1 = Matrices.sparse(
      5,4,
      Array(0,0,1,2,2),
      Array(1,3),
      Array(34,55)
    )

    m1.toString(100, 100) mustBe "5 x 4 CSCMatrix\n" +
                                 "(1,1) 34.0\n" +
                                 "(3,2) 55.0"

  }

  "Distributed matricies" in {
    //distributed are stored in one or more RDDs
    //3 types:
    //(1)RowMatrix (indices are ints, values are doubles)
    //(2)IndexRowMatrix (indices are longs, values are doubles, rows are extra indexed)
    //(3)CoordinateMatrix
    //Convertions require shuffling

    //(1) most basic types
    //rows are for example feature vectors

    val rows: RDD[Vector] = sc.parallelize(Seq(
      Vectors.dense(1,4),
      Vectors.dense(2,5),
      Vectors.dense(3,6)
    ))

    val mat = new RowMatrix(rows)

    mat.numRows mustBe 3 //the same as amount of vectors
    mat.numCols mustBe 2 //the same as amount of elements in each vector

    //IndexedRowMatrix - meaningful row indices
    val rows2: RDD[IndexedRow] = sc.parallelize(Array(
      IndexedRow(0, Vectors.dense(1,2)),
      IndexedRow(1, Vectors.dense(5,5)),
      IndexedRow(2, Vectors.dense(7,8))
    ))

    val idxMat: IndexedRowMatrix = new IndexedRowMatrix(rows2)

    //coordinate matrix - when both dimentions are huge and matrix is very sparse

    val entries = sc.parallelize(Array(
      MatrixEntry(0, 0, 9.0),
      MatrixEntry(1, 1, 8.0),
      MatrixEntry(2, 1, 6.0)
    ))

    val coordMatrix = new CoordinateMatrix(entries)
  }
}

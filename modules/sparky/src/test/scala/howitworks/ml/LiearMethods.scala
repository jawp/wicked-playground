package howitworks.ml


class LiearMethods extends wp.SparkySpec {

  //Linear methods:
  // - Logistic Regression (classification)
  // - Linear Least Squares (regression)
  // - reqularizatin (for LR and LLS)

  //pipelines
  // - Elastic Net - convex combination of L1 and L2 regularization terms
  //  - if alfa is 0 -than it's Ridge Regression - https://en.wikipedia.org/wiki/Tikhonov_regularization
  //  - if alfa is 1 -than it's Lasso Regression - https://en.wikipedia.org/wiki/Lasso_(statistics)

  //Logistic Regression supported algorithms:
  // - mini-batch gradient descent
  // - L-BFGS (reccomended for faster convervence)

  //Regularization:
  // - L1  -  Ridge Regressin (penalizes beta by the square of their magnitude)
  // - L2  - Lasso Regression (penalizes beta by their absolute values)
  // - combination  of L1, L2 - Convex Net

  "it must work" in {


  }
}

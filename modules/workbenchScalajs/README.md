This project wires scalajs, scalatags and scalarx libs. 

 
My opinionated conclusion after few weeks using scalajs, scalatags and scalarx.

2016-10-13:
- scalajs - quite mature library, it's safe to use it on production.
- scalatags - nice and easy to use but not mature yet. Not all HTML model ported in here, pending PRs might suggest that author is engaged in other work, missing tags, often strings instead of typed objects: `cls := "class1 class2"`.
- scalarx - very cool it has great potential, man can very fast write dynamic web pagees. But: the library is still experimental, has not stable API, has not solved problem of leaking cpu/memory and needs extra code to write/maintain bolierplate binding logic between scalatags and scalarx.
  
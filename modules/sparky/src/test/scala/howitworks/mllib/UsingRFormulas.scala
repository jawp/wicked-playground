package howitworks.mllib


import com.holdenkarau.spark.testing.SharedSparkContext
import org.apache.spark.ml.feature.RFormula
import org.apache.spark.sql.{DataFrame, SQLContext}

import scalaz.syntax.id._

class UsingRFormulas extends wp.Spec with SharedSparkContext {

  //using RFormula notation it is easy to just by writing simple string
  //  select columns and transform them into feature vector and label

  "it must work" in {
    //given
    val sqlContext = new SQLContext(sc)
    import sqlContext.implicits._

    val crimes: DataFrame = sqlContext
      .read
      .format("com.databricks.spark.csv")
      .option("delimiter", ",")
      .option("header", "true")
      .option("inferSchema", "true")
      .load("modules/sparky/data/UScrime2-colsLotsOfNAremoved.csv")
    //.drop("community")   //string
    //.drop("OtherPerCap") //string
    //end of given

    //cols in csv:
    //community,population,householdsize,racepctblack,racePctWhite,racePctAsian,racePctHisp,agePct12t21,agePct12t29,agePct16t24,agePct65up,numbUrban,pctUrban,medIncome,pctWWage,pctWFarmSelf,pctWInvInc,pctWSocSec,pctWPubAsst,pctWRetire,medFamInc,perCapInc,whitePerCap,blackPerCap,indianPerCap,AsianPerCap,OtherPerCap,HispPerCap,NumUnderPov,PctPopUnderPov,PctLess9thGrade,PctNotHSGrad,PctBSorMore,PctUnemployed,PctEmploy,PctEmplManu,PctEmplProfServ,PctOccupManu,PctOccupMgmtProf,MalePctDivorce,MalePctNevMarr,FemalePctDiv,TotalPctDiv,PersPerFam,PctFam2Par,PctKids2Par,PctYoungKids2Par,PctTeen2Par,PctWorkMomYoungKids,PctWorkMom,NumIlleg,PctIlleg,NumImmig,PctImmigRecent,PctImmigRec5,PctImmigRec8,PctImmigRec10,PctRecentImmig,PctRecImmig5,PctRecImmig8,PctRecImmig10,PctSpeakEnglOnly,PctNotSpeakEnglWell,PctLargHouseFam,PctLargHouseOccup,PersPerOccupHous,PersPerOwnOccHous,PersPerRentOccHous,PctPersOwnOccup,PctPersDenseHous,PctHousLess3BR,MedNumBR,HousVacant,PctHousOccup,PctHousOwnOcc,PctVacantBoarded,PctVacMore6Mos,MedYrHousBuilt,PctHousNoPhone,PctWOFullPlumb,OwnOccLowQuart,OwnOccMedVal,OwnOccHiQuart,RentLowQ,RentMedian,RentHighQ,MedRent,MedRentPctHousInc,MedOwnCostPctInc,MedOwnCostPctIncNoMtg,NumInShelters,NumStreet,PctForeignBorn,PctBornSameState,PctSameHouse85,PctSameCity85,PctSameState85,LandArea,PopDens,PctUsePubTrans,ViolentCrimesPerPop

    val formula: RFormula = new RFormula()
      .setFormula("ViolentCrimesPerPop ~ householdsize + PctLess9thGrade + pctWWage")
      .setFeaturesCol("features")
      .setLabelCol("label")


    val crimesRForumlated = formula.fit(crimes).transform(crimes)

    crimesRForumlated
      .select("ViolentCrimesPerPop", "householdsize", "PctLess9thGrade", "pctWWage", "features", "label")
//      .show(10)

    //label - this can be the value which we want to predict when building a model

    //    +-------------------+-------------+---------------+--------+----------------+-----+
    //    |ViolentCrimesPerPop|householdsize|PctLess9thGrade|pctWWage|        features|label|
    //    +-------------------+-------------+---------------+--------+----------------+-----+
    //    |                0.2|         0.33|            0.1|    0.72| [0.33,0.1,0.72]|  0.2|
    //    |               0.67|         0.16|           0.14|    0.72|[0.16,0.14,0.72]| 0.67|
    //    |               0.43|         0.42|           0.27|    0.58|[0.42,0.27,0.58]| 0.43|
    //    |               0.12|         0.77|           0.09|    0.89|[0.77,0.09,0.89]| 0.12|
    //    |               0.03|         0.55|           0.25|    0.72|[0.55,0.25,0.72]| 0.03|
    //    |               0.14|         0.28|           0.13|    0.68|[0.28,0.13,0.68]| 0.14|
    //    |               0.03|         0.39|           0.29|     0.5| [0.39,0.29,0.5]| 0.03|
    //    |               0.55|         0.74|           0.96|    0.44|[0.74,0.96,0.44]| 0.55|
    //    |               0.53|         0.34|           0.52|    0.47|[0.34,0.52,0.47]| 0.53|
    //    |               0.15|          0.4|           0.04|    0.59| [0.4,0.04,0.59]| 0.15|
    //    +-------------------+-------------+---------------+--------+----------------+-----+
  }
}

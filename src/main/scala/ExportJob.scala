import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
import org.apache.spark.sql.functions.{col, collect_list, concat, count, month, year}

object ExportJob {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .master("local[*]")
      .appName("ExportJob")
      .getOrCreate()

    val df : DataFrame = spark.read
      .parquet("C:\\Users\\Consultant\\Documents\\Final HW\\Amenities.parquet")
    val df2 : DataFrame = spark.read.
      parquet("C:\\Users\\Consultant\\Documents\\Final HW\\Properties.parquet")
    val df3 : DataFrame = df.join(df2, df("property_id") === df2 ("property_id"), "inner")
      .where(df2("active") === "true")
      .drop(df2("property_id")).drop(df2("discovered_dt")).drop(df2("active"))
    val out_1 : DataFrame = df3
      .groupBy("property_id")
      .agg(concat(collect_list("amenity_id"))as "amenities")
      .coalesce(1)

    val df4 : DataFrame = df.join(df2, df("property_id") === df2 ("property_id"), "inner")
      .drop(df2("property_id")).drop(df("amenity_id")).drop(df2("active"))
    val df5 : DataFrame = df4
      .groupBy("property_id", "discovered_dt")
      .count()
    val df6 : DataFrame = df5
      .orderBy("discovered_dt")
      .groupBy("discovered_dt")
      .agg(year(col("discovered_dt")) as "year",
        month(col("discovered_dt"))as "month",
        count("discovered_dt") as "count")
      .coalesce(1)

    val out_2 : DataFrame = df6
      .groupBy("year", "month")
      .sum("count") as "count"

    out_1.write
      .option("header", "true")
      .mode("overwrite")
      .json("C:\\Users\\Consultant\\Documents\\Final HW\\Output1")

    out_2.write
      .option("header","true")
      .mode("overwrite")
      .csv("C:\\Users\\Consultant\\Documents\\Final HW\\Output2")

    spark.stop()
  }
}

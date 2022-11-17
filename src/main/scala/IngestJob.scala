import org.apache.spark.sql.functions.{explode, split}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.types.{BooleanType, DateType, IntegerType, StructField, StructType}

object  IngestJob {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .master("local[*]")
      .appName("IngestJob")
      .getOrCreate()

    val propertiesschema = StructType(Array(
      StructField("property_id", IntegerType, true),
      StructField("active", BooleanType, true),
      StructField("discovered_dt", DateType, true)
    ))

    val df : DataFrame = spark.read
      .option("multiline","true")
      .text("C:\\Users\\Consultant\\Downloads\\airdna-spark-use-case\\airdna-spark-use-case\\amenities.txt")

    import spark.implicits._
    val df2 = df.map(f => {
      val elements = f.getString(0).split(" ")
      (elements(0), elements(1))
    })

    val amenities_id : DataFrame = df2.toDF("property_id": String, "amenity_id": String)
      .withColumn("property_id", 'property_id.cast("Int"))
      .withColumn("amenity_id", explode(split($"amenity_id", "[|]")))
      //.write.parquet("C:\\Users\\Consultant\\Documents\\Final HW\\Amenities.parquet")

    val properties_df: DataFrame = spark.read
      .schema(propertiesschema)
      .json("C:\\Users\\Consultant\\Downloads\\airdna-spark-use-case\\airdna-spark-use-case\\properties.json")
      //.write.parquet("C:\\Users\\Consultant\\Documents\\Final HW\\Properties.parquet")

    amenities_id.write.mode("overwrite").parquet("C:\\Users\\Consultant\\Documents\\Final HW\\Amenities.parquet")
    properties_df.write.mode("overwrite").parquet("C:\\Users\\Consultant\\Documents\\Final HW\\Properties.parquet")

    spark.stop()
  }
}

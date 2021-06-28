import org.litote.kmongo.* //NEEDED! import KMongo extensions

data class Jedi(val name: String, val age: Int)

fun main(){
    val DB_URL = "localhost"
    val DB_PORT = "27017"
    val connStr = "mongodb://" + DB_URL + ":" + DB_PORT
    val client = KMongo.createClient(connStr) //get com.mongodb.MongoClient new instance
    val database = client.getDatabase("keker") //normal java driver usage
    val col = database.getCollection<Jedi>("jedikek") //KMongo extension method
//here the name of the collection by convention is "jedi"
//you can use getCollection<Jedi>("otherjedi") if the collection name is different

    col.insertOne(Jedi("Luke Skywalker", 19))
    col.insertOne(Jedi("Yoda", 1119))

    val yoda : Jedi? = col.findOne(Jedi::name eq "Yoda")

    println(yoda)
}

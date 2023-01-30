package com.rhdzmota.examples.mongodb

import com.mongodb.client.{MongoCollection, MongoDatabase}
import com.mongodb.{MongoClient, MongoClientSettings, MongoClientURI}
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.pojo.PojoCodecProvider
import org.bson.codecs.{DecoderContext, DocumentCodec}
import org.bson.{BsonArray, BsonDocument, BsonDocumentReader, Document}

import scala.collection.JavaConverters._
import scala.io.Source

object Clone {
  def main(args: Array[String]): Unit = {

    val client: MongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017/"))
    System.setProperty("org.mongodb.async.type", "netty")
    val db: MongoDatabase = client.getDatabase("mygrocerylist")
      .withCodecRegistry(fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
        fromProviders(PojoCodecProvider.builder().automatic(true).build())))
    val collection: MongoCollection[Document] = db.getCollection("people")
    insertItems(client, collection)

  }

  def insertItems(client: MongoClient, collection: MongoCollection[Document]): Unit = {

    val bstring = Source.fromFile("src/main/resources/people.json")
      .mkString
    val codec: DocumentCodec = new DocumentCodec
    bstring.charAt(0) match {
      case '[' => collection.insertMany(BsonArray.parse(bstring).getValues.asScala
        .map(doc => {
          codec.decode(new BsonDocumentReader(doc.asDocument()), DecoderContext.builder.build())
        }).asJava)
      case _ => collection.insertOne(codec.decode(new BsonDocumentReader(BsonDocument.parse(bstring)), DecoderContext.builder.build()))

    }
  }

}

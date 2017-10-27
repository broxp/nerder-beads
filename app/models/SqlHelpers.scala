package models

import java.sql.Connection

import play.api.db.Database

object SqlHelpers {

  def initDB(db : Database, colors : Color*) = {
    db.withConnection { c =>
      SqlHelpers.exec(c, "drop table col if exists")
      SqlHelpers.exec(c, "create table col (code varchar(10) primary key, r int, " +
        "g int, b int, name varchar(50))")
      for(col <- colors) {
        SqlHelpers.exec(c, s"insert into col values (?,?,?,?,?)",
          col.code, col.r, col.g, col.b, col.name)
      }
      val res = SqlHelpers.query(c, "select * from col")
      val cols = (1 to res.getMetaData.getColumnCount).map{ res.getMetaData.getColumnName(_) }.toList
      while(res.next) {
        println(cols.map{ x => x + ": " + res.getObject(x)})
      }
      println("done")
    }
  }

  def exec(c : Connection, s : String, params : Any*) = {
    val stm = c.prepareStatement(s)
    for((item, i) <- params.zipWithIndex) {
      stm.setObject(i + 1, item.asInstanceOf[AnyRef])
    }
    stm.execute
  }

  def query(c : Connection, s : String) = {
    c.prepareStatement(s).executeQuery
  }
}

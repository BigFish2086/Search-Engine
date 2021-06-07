var express = require('express');
const mysql = require('mysql');
const natural = require('natural');
var router = express.Router();

const con = mysql.createConnection({
  connectionLimit: 100,
  host: "127.0.0.1",
  port: "3306",
  database: "engine",
  user: "root",
  password: "",
  charset : 'utf8',
  debug: false
});

/* GET home page. */
router.get('/', function(req, res, next) {
  let word = req.query.search;
  if(word) {
    return res.redirect(`results?search=${word}&page=1`);
  } else {
    return res.render('index');
  }
});

// select * from indexed GROUP by word having COUNT(word) > 30
// art content user zoom ahmed
router.get('/results', function(req, res, next) {
  let word = String(req.query.search).toLowerCase();
  let stword = natural.PorterStemmer.stem(word);
  
  let notFoundObj = {
    state: "NOT FOUND",
    message: "Please Check your spelling .."
  };

  let sql = `SELECT LOG((Select Count(*) From Indexed) / (1 + (SELECT Count(*) from indexed where Word = ?))) as idf`;
  con.query(sql, [stword], (err, idfObj) => {
    if(err) throw err;
    let sql2 = `select Word, C.Title, I.URL, I.Content, I.TF from indexed as I, crawled as C where Word = ? and C.URL = I.URL Order by I.TF * ? DESC`;
    con.query(sql2, [stword, parseFloat(idfObj[0].idf)], (err2, rows) => {
      if(err2) throw err2;
      //res.send({retData: rows});
      let gtotalPages = parseInt(Math.ceil(rows.length / 10.0));
      if(gtotalPages) {
        res.render("results", {gtotalPages: gtotalPages, retData: rows});
      } else {
        res.render("results", {gtotalPages: 1, notFoundObj: [notFoundObj]});
      }
    });
  });
});

module.exports = router;

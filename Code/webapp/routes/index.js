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
router.get('/results', function(req, res, next) {
  let word = String(req.query.search).toLowerCase();
  let stword = natural.PorterStemmer.stem(word);
  
  let sql = `SET @res1 = (SELECT LOG((Select Count(*) From Indexed) / (SELECT Count(*) from indexed as I, crawled as C where Word = ? and C.URL = I.URL))); select Word, C.Title, I.URL, I.Content, I.TF from indexed as I, crawled as C where Word = ? and C.URL = I.URL Order by I.TF * @res1 DESC;`;
  
  let notFoundObj = {
    state: "NOT FOUND",
    message: "Please Check your spelling .."
  };

  con.query(sql, [stword, stword], (err, rows) => {
    if(err) throw err;
    //res.send({retData: rows});
    let gtotalPages = parseInt(Math.ceil(rows.length / 10.0));
    if(gtotalPages) {
      res.render("results", {gtotalPages: gtotalPages, retData: rows});
    } else {
      res.render("results", {gtotalPages: 1, notFoundObj: [notFoundObj]});
    }
  });
});

module.exports = router;

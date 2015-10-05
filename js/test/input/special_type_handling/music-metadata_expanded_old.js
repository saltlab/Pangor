var fs = require('graceful-fs'), path = require('path'), configuration_handler = require('../../lib/handlers/configuration-handler'), LastfmAPI = require('lastfmapi'), mm = require('musicmetadata'), io = require('../../lib/utils/setup-socket').io;
dbschema = require('../../lib/utils/database-schema') , Album = dbschema.Album , Artist = dbschema.Artist , Track = dbschema.Track , async = require('async');
var config = configuration_handler.initializeConfiguration();
var SUPPORTED_FILETYPES = "m4a";
var dir = path.resolve(config.musicpath);
var walk = function(dir, done) {
  var results = [];
  fs.readdir(dir, function(err, list) {
  if (err) 
    return done(err);
  var i = 0;
  (function next() {
  var file = list[i++];
  if (!file) 
    return done(null, results);
  file = dir + '/' + file;
  fs.stat(file, function(err, stat) {
  if (stat && stat.isDirectory()) {
    walk(file, function(err, res) {
  results = results.concat(res);
  next();
});
  } else {
    var ext = file.split(".");
    ext = ext[ext.length - 1];
    if (ext === SUPPORTED_FILETYPES) {
      results.push(file);
    }
    next();
  }
});
})();
});
};
var setupParse = function(req, res, serveToFrontEnd, results) {
  if (results && results.length > 0) {
    var i = 0;
    async.each(results, function(file, callback) {
  doParse(req, res, file, serveToFrontEnd, function() {
  var perc = (i++) / results.length * 100 >> 0;
  io.sockets.emit('progress', {
  msg: perc});
  callback();
});
}, function(err) {
  Album.findAll({
  include: [Track, Artist]}).success(function(albums) {
  res.json(albums);
});
});
  }
  if (!results) {
    console.log('no results!');
    res.json({
  "result": "none"});
  }
};
var doParse = function(req, res, file, serveToFrontEnd, callback) {
  var parser = new mm(fs.createReadStream(file));
  var result = null;
  parser.on('metadata', function(md) {
  result = md;
});
  parser.on('done', function(err) {
  if (err) {
    console.log("err", err);
  } else {
    var trackName = "Unknown Title", trackNo = "", albumName = "Unknown Album", genre = "Unknown", artistName = "Unknown Artist", year = "";
    if (result) {
      if ((result.title)) {
        trackName = result.title.replace(/\\/g, '');
      } else {
        trackName = '';
      }
      if ((result.track.no)) {
        trackNo = result.track.no;
      } else {
        trackNo = '';
      }
      if ((result.album)) {
        albumName = result.album.replace(/\\/g, '');
      } else {
        albumName = '';
      }
      if ((result.artist[0])) {
        artistName = result.artist[0].replace(/\\/g, '');
      } else {
        artistName = '';
      }
      if ((result.year)) {
        year = Date(result.year).getFullYear();
      } else {
        year = 0;
      }
      if (result.genre !== undefined) {
        var genrelist = result.genre;
        if (genrelist.length > 0 && genrelist !== "") {
          genre = genrelist[0];
        }
      }
    }
    getAdditionalDataFromLastFM(albumName, artistName, function(cover) {
  if (cover === '' || cover === null) {
    cover = '/music/css/img/nodata.jpg';
  }
  var albumData = {
  'title': albumName, 
  'posterURL': cover, 
  'year': year};
  var artistData = {
  'name': artistName};
  Artist.findOrCreate(artistData, artistData).complete(function(err, artist) {
  Album.findOrCreate({
  'title': albumName}, albumData).complete(function(err, album) {
  album.setArtist(artist).complete(function(err) {
  album.createTrack({
  'title': trackName, 
  'order': trackNo, 
  'filePath': file}).complete(function(err) {
  callback();
});
});
});
});
});
  }
});
};
getAdditionalDataFromLastFM = function(album, artist, callback) {
  var lastfm = new LastfmAPI({
  'api_key': "36de4274f335c37e12395286ec6e92c4", 
  'secret': "1f74849490f1872c71d91530e82428e9"});
  var cover = '/music/css/img/nodata.jpg';
  lastfm.album.getInfo({
  'artist': artist, 
  'album': album}, function(err, album) {
  if (err) {
    callback(cover);
  }
  if (album !== undefined && album.image[0] !== undefined && album.image[0] !== null) {
    cover = album.image[3]["#text"];
    if (cover !== '') {
      callback(cover);
    } else {
      callback(cover);
    }
  }
});
};
exports.loadData = function(req, res, serveToFrontEnd) {
  walk(dir, function(err, results) {
  setupParse(req, res, serveToFrontEnd, results);
});
};

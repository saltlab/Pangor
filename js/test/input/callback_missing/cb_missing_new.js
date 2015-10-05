/* Callback Missing: Callback is missing on one path.
 * Output: CB_MISSING_CALLBACK (callback) */

getAlbums = function(callback){
    console.log('Looking for stored albums.');
  setTimeout(function(){
        db.query('SELECT * FROM albums ORDER BY album asc', {
            album   : String,
            artist  : String,
            year    : Number,
            genre   : String,
            cover   : String
        },  
        function(err, rows) {
            if(err) console.log('Database error: ' + err);

            if (rows !== undefined && rows !== null ){
                if(rows.length > 0){ 
                    console.log('Found albums...');
                    callback(rows);
                } else {
                    console.log('No albums found... Indexing.');
                    callback(null);
                }   
            } else {
                console.log('No albums found... Indexing.');
                callback(null);
            }   
        }); 
  },300);
}
